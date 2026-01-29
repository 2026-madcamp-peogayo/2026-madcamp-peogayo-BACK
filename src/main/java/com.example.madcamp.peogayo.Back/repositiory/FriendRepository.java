package com.example.madcamp.peogayo.Back.repositiory;

import com.example.madcamp.peogayo.Back.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT f FROM Friend f " +
            "WHERE (f.requester.id = :userId OR f.receiver.id = :userId) " +
            "AND f.status = 'ACCEPTED'")
    List<Friend> findFriendsByUserId(@Param("userId") Long userId);


    @Query("SELECT f FROM Friend f " +
            "WHERE (f.requester.id = :id1 AND f.receiver.id = :id2) " +
            "OR (f.requester.id = :id2 AND f.receiver.id = :id1)")
    List<Friend> findFriendship(@Param("id1") Long id1, @Param("id2") Long id2);
}
