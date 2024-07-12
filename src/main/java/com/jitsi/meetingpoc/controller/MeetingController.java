package com.jitsi.meetingpoc.controller;

import com.jitsi.meetingpoc.model.Meeting;
import com.jitsi.meetingpoc.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.jitsi.meetingpoc.constants.URLConstants.MeetingURLConstants.*;

@RestController
@RequestMapping(MEETING_BASE_URL)
@CrossOrigin(value = {"http://192.168.105.220:4200/", "http://localhost:4200/"})
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @PostMapping(CREATE)
    public Meeting createMeeting(@RequestBody String description) {
        return meetingService.createMeeting(description);
    }

    @GetMapping()
    public List<Meeting> getAllMeetings() {
        return meetingService.getAllMeetings();
    }

    @PostMapping(SCHEDULE)
    public Meeting scheduleMeeting(@RequestParam String description, @RequestParam String time) {
        LocalDateTime meetingTime = LocalDateTime.parse(time);
        return meetingService.scheduleMeeting(description, meetingTime);
    }

    @GetMapping(JOIN_MEETING)
    public String joinMeeting(@PathVariable int meetingId) {
        return meetingService.joinMeeting(meetingId);
    }

    @GetMapping(UPDATE_MEETING)
    public void update() {
        meetingService.unlockMeetings();
    }

    @PatchMapping(INVITE)
    public List<String> invitePeople(@RequestBody List<String> emails, @RequestParam Integer meetingId) {
        return meetingService.invitePeople(emails, meetingId);
    }
}

