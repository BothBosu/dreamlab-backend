package com.muic.ssc.backend.Service;

import org.springframework.stereotype.Service;
import com.muic.ssc.backend.Entity.Image;
import com.muic.ssc.backend.Entity.User;
import com.muic.ssc.backend.Entity.Like;
import com.muic.ssc.backend.Repository.ImageRepository;
import com.muic.ssc.backend.Repository.UserRepository;
import com.muic.ssc.backend.Repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    @Autowired private LikeRepository likeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ImageRepository imageRepository;

    public long getLikeCount(Long imageId) {
        Image image = imageRepository.findById(imageId).orElseThrow();
        return likeRepository.countByImage(image);
    }

    @Transactional // ✅ This enables delete operations to work correctly
    public void toggleLike(Long imageId, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Image image = imageRepository.findById(imageId).orElseThrow();

        if (likeRepository.existsByUserAndImage(user, image)) {
            likeRepository.deleteByUserAndImage(user, image); // Unlike
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setImage(image);
            likeRepository.save(like); // Like
        }
    }
}
