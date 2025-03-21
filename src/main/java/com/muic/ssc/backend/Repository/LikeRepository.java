package com.muic.ssc.backend.Repository;

import com.muic.ssc.backend.Entity.Image;
import com.muic.ssc.backend.Entity.Like;
import com.muic.ssc.backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndImage(User user, Image image);
    int countByImage(Image image);
    void deleteByUserAndImage(User user, Image image);
}

