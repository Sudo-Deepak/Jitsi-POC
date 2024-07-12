package com.jitsi.meetingpoc.repository;

import com.jitsi.meetingpoc.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting,Integer> {
    List<Meeting> findAllByOrderByTimeDesc();
}
