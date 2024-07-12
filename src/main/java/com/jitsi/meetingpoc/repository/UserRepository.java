package com.jitsi.meetingpoc.repository;

import com.jitsi.meetingpoc.model.JitsiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<JitsiUser,Integer> {
    List<JitsiUser> findAllByEmailIn(List<String> emails);
    @Query(value = "SELECT COUNT(meeting_meeting_id) FROM meeting_jitsi_users WHERE meeting_meeting_id = :meetId AND jitsi_users_user_id = :userId", nativeQuery = true)
    Long isExistsByMeetIdAndUserId(Integer meetId, Integer userId);
}
