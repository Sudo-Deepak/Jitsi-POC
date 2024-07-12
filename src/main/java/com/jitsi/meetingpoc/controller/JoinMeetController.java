package com.jitsi.meetingpoc.controller;

import com.jitsi.meetingpoc.model.JitsiUser;
import com.jitsi.meetingpoc.service.MeetingService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("*")
public class JoinMeetController {
    private final MeetingService meetingService;

    public JoinMeetController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping("/join-meet/{encryptedId}/{meetId}")
    public String getMeet(@PathVariable String encryptedId, @PathVariable Integer meetId, HttpServletResponse response) throws Exception {
        return meetingService.getMeet(encryptedId, meetId);
    }

    @GetMapping("/users")
    public List<JitsiUser> getAllUsers() {
        return meetingService.getAllUsers();
    }
}
