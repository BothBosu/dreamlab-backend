package com.muic.ssc.backend.Service;

import com.muic.ssc.backend.Entity.Image;
import com.muic.ssc.backend.Entity.User;
import com.muic.ssc.backend.Repository.ImageRepository;
import com.muic.ssc.backend.Repository.LikeRepository;
import com.muic.ssc.backend.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String region;

    private S3Client s3Client;

    // Map of file extensions to MIME types
    private static final Map<String, String> CONTENT_TYPES = new HashMap<>();

    static {
        CONTENT_TYPES.put(".jpg", "image/jpeg");
        CONTENT_TYPES.put(".jpeg", "image/jpeg");
        CONTENT_TYPES.put(".png", "image/png");
        CONTENT_TYPES.put(".gif", "image/gif");
        CONTENT_TYPES.put(".bmp", "image/bmp");
        CONTENT_TYPES.put(".webp", "image/webp");
        CONTENT_TYPES.put(".svg", "image/svg+xml");
        CONTENT_TYPES.put(".tiff", "image/tiff");
        CONTENT_TYPES.put(".tif", "image/tiff");
    }

    @PostConstruct
    public void initS3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public Image saveFileWithUser(MultipartFile file, Long userId) {
        try {
            String fileName = file.getOriginalFilename();
            Path tempFile = Files.createTempFile("upload-", fileName);
            file.transferTo(tempFile);

            String s3Url = uploadToS3(tempFile, fileName, file.getContentType());

            return saveImageUrlForUser(s3Url, fileName, userId);

        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public Image saveImageFromUrl(String imageUrl, String description, Long userId) {
        try {
            // Determine file extension and content type
            String extension = determineFileExtension(imageUrl);
            String contentType = getContentTypeFromExtension(extension);

            // Generate a unique filename for S3
            String fileName = UUID.randomUUID().toString() + extension;

            // Download the image to a temporary file
            Path tempFile = downloadImageFromUrl(imageUrl);

            // Upload the temp file to S3 with correct content type
            String s3Url = uploadToS3(tempFile, fileName, contentType);

            // Clean up the temp file
            Files.deleteIfExists(tempFile);

            // Save the S3 URL in the database
            return saveImageUrlForUser(s3Url, description, userId);

        } catch (Exception e) {
            throw new RuntimeException("Could not process the image from URL. Error: " + e.getMessage());
        }
    }

    private String getContentTypeFromExtension(String extension) {
        return CONTENT_TYPES.getOrDefault(extension.toLowerCase(), "image/jpeg");
    }

    private Path downloadImageFromUrl(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        // Create a temporary file with a simple prefix
        Path tempFile = Files.createTempFile("img-", null);

        try (InputStream in = connection.getInputStream()) {
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        return tempFile;
    }

    private String determineFileExtension(String imageUrl) {
        // Remove query parameters for extension detection
        String urlPath = imageUrl;
        int queryIndex = urlPath.indexOf('?');
        if (queryIndex > 0) {
            urlPath = urlPath.substring(0, queryIndex);
        }

        // Try to extract extension from URL path
        int dotPos = urlPath.lastIndexOf('.');
        if (dotPos > 0 && dotPos < urlPath.length() - 1) {
            String extension = urlPath.substring(dotPos).toLowerCase();
            // Limit extension length to prevent issues
            if (extension.length() <= 5) {
                return extension;
            }
        }

        // Default to .jpg if we can't determine
        return ".jpg";
    }

    public Image saveImageUrlForUser(String imageUrl, String name, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Image image = new Image();
        image.setInputPrompt(name);
        image.setUrl(imageUrl);
        image.setUser(user);
        image.setPublic(false);

        return imageRepository.save(image);
    }

    private String uploadToS3(Path filePath, String fileName, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)  // Set the content type
                .build();

        try {
            PutObjectResponse response = s3Client.putObject(putObjectRequest, filePath);
            if (response.sdkHttpResponse().isSuccessful()) {
                return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
            } else {
                throw new RuntimeException("Failed to upload image to S3");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to S3", e);
        }
    }

    public List<Image> getAllImages() {
        return imageRepository.findByIsPublicTrue();
    }

    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }

    public List<Image> getImagesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return imageRepository.findByUser(user);
    }

    @Transactional
    public void deleteImage(Long id) {
        Optional<Image> imageOptional = imageRepository.findById(id);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();

            // First, delete all likes associated with this image
            likeRepository.deleteByImageId(id);

            // Then delete the image from S3
            deleteFromS3(image.getUrl());

            // Finally, delete the image record from the database
            imageRepository.deleteById(id);
        } else {
            throw new RuntimeException("Image not found with id " + id);
        }
    }

    private void deleteFromS3(String imageUrl) {
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    public Image updateImageVisibility(Long imageId, String username, boolean isPublic) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        if (!image.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to update this image");
        }

        image.setPublic(isPublic);
        return imageRepository.save(image);
    }

}