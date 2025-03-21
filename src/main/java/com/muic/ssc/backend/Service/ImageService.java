package com.muic.ssc.backend.Service;

import com.muic.ssc.backend.Entity.Image;
import com.muic.ssc.backend.Entity.User;
import com.muic.ssc.backend.Repository.ImageRepository;
import com.muic.ssc.backend.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String region;

    private S3Client s3Client;

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

            String s3Url = uploadToS3(tempFile, fileName);

            return saveImageUrlForUser(s3Url, fileName, userId);

        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public Image saveImageUrlForUser(String imageUrl, String name, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Image image = new Image();
        image.setInputPrompt(name);
        image.setUrl(imageUrl);
        image.setUser(user);

        return imageRepository.save(image);
    }

    private String uploadToS3(Path filePath, String fileName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
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
        return imageRepository.findAll();
    }

    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }

    public List<Image> getImagesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return imageRepository.findByUser(user);
    }

    public void deleteImage(Long id) {
        Optional<Image> imageOptional = imageRepository.findById(id);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            deleteFromS3(image.getUrl());
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
}
