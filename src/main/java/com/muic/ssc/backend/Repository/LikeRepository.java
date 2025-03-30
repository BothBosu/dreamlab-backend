package com.muic.ssc.backend.Repository;

import com.muic.ssc.backend.Entity.Image;
import com.muic.ssc.backend.Entity.Like;
import com.muic.ssc.backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndImage(User user, Image image);
    int countByImage(Image image);
    void deleteByUserAndImage(User user, Image image);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.image.id = :imageId")
    void deleteByImageId(@Param("imageId") Long imageId);
}