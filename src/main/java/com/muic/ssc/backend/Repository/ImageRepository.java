package com.muic.ssc.backend.Repository;

import com.muic.ssc.backend.Entity.Image;
import com.muic.ssc.backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUser(User user);
    List<Image> findByIsPublicTrue();
}