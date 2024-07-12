package com.jitsi.meetingpoc.service;

import com.jitsi.meetingpoc.AESHelper;
import com.jitsi.meetingpoc.model.JitsiUser;
import com.jitsi.meetingpoc.model.Meeting;
import com.jitsi.meetingpoc.repository.MeetingRepository;
import com.jitsi.meetingpoc.repository.UserRepository;
import com.jitsi.meetingpoc.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.jitsi.meetingpoc.constants.Constants.FORWARD_SLASH;
import static com.jitsi.meetingpoc.constants.Constants.TIME_ZONE;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AESHelper aesHelper;
    @Value("${app.jitsi-base-url}")
    private String jitsiBaseUrl;
    @Value("${app.frontend-meet-url}")
    private String frontendMeetUrl;

    /**
     * Create a new meeting immediately.
     *
     * @param description The description of the meeting.
     * @return The created meeting.
     */
    public Meeting createMeeting(String description) {
        // Generate a unique meeting URL (you can customize this logic)
        String meetingUrl = jitsiBaseUrl + Utils.generateMeetingID();

        Meeting meeting = new Meeting();
        meeting.setDescription(description);
        meeting.setMeetingUrl(meetingUrl);
        meeting.setTime(LocalDateTime.now());
        meeting.setIsMeetingOn(true);
        meeting.setLocked(false);
        return meetingRepository.save(meeting);
    }

    public List<Meeting> getAllMeetings() {

        return meetingRepository.findAllByOrderByTimeDesc();
    }

//    private Pageable getPageableObject() {
//        if (null == page)
//            page = 0;
//
//        if (null == size)
//            size = Integer.MAX_VALUE;
//
//        if (null == sortField || sortField.isEmpty())
//            sortField = "createdAt";
//
//        Sort.Direction direction = null;
//        if (null == sortOrder)
//            direction = Sort.Direction.DESC;
//        else if (sortOrder.equalsIgnoreCase("ASC"))
//            direction = Sort.Direction.ASC;
//        else
//            direction = Sort.Direction.DESC;
//
//        return PageRequest.of(0, size, direction, "time");
//    }

    /**
     * Schedule a meeting to occur at a specified time.
     *
     * @param description The description of the meeting.
     * @param time        The scheduled time for the meeting.
     * @return The scheduled meeting.
     */
    public Meeting scheduleMeeting(String description, LocalDateTime time) {
        // Convert time to IST
        LocalDateTime istTime = time.atZone(ZoneId.of(TIME_ZONE)).toLocalDateTime();

        String uniqueMeetingId = UUID.randomUUID().toString();
        String meetingUrl = jitsiBaseUrl + uniqueMeetingId;

        Meeting meeting = new Meeting();
        meeting.setDescription(description);
        meeting.setMeetingUrl(meetingUrl);
        meeting.setTime(istTime); // Set the time in IST
        meeting.setIsMeetingOn(false);
        meeting.setLocked(true);

        return meetingRepository.save(meeting);
    }

    /**
     * Join a meeting by its ID.
     *
     * @param meetingId The ID of the meeting to join.
     * @return The meeting URL if the meeting is not locked, else an error message.
     */
    public String joinMeeting(int meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));
        if (meeting.isLocked()) {
            throw new RuntimeException("Meeting is locked until the scheduled time.");
        }
        return meeting.getMeetingUrl();
    }

    /**
     * Periodically check and unlock meetings that are scheduled to start.
     */
    @Scheduled(fixedRate = 60000) // Check every minute
    public void unlockMeetings() {
        List<Meeting> meetings = meetingRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (Meeting meeting : meetings) {
            if (meeting.isLocked() && now.isAfter(meeting.getTime())) {
                meeting.setLocked(false);
                meetingRepository.save(meeting);
            }
        }
    }

    public List<String> invitePeople(List<String> emails, Integer meetingId) {
        List<String> meetLink = new ArrayList<>();
        Optional<Meeting> meetingOptional = meetingRepository.findById(meetingId);
        if (meetingOptional.isPresent()) {
            Meeting meeting = meetingOptional.get();
            List<JitsiUser> users = userRepository.findAllByEmailIn(emails);
            if (!CollectionUtils.isEmpty(users)) {
                meeting.setJitsiUsers(users);
                meetingRepository.save(meeting);
                meetLink = users
                        .stream()
                        .map(jitsiUser -> {
                            try {
                                return jitsiUser.getName() + " -> " + frontendMeetUrl + aesHelper.encrypt(String.valueOf(jitsiUser.getUserId())) + FORWARD_SLASH + meeting.getMeetingId();
                            } catch (Exception e) {
                                System.out.println("Exception for " + jitsiUser.getEmail() + " : " + e.getMessage());
                            }
                            return "";
                        })
                        .collect(Collectors.toList());
            }
        }
        return meetLink;
    }

    public String getMeet(String encryptedId, Integer meetId) throws Exception {
        String result;
        Integer userId = Integer.valueOf(aesHelper.decrypt(encryptedId));
        if (userRepository.existsById(userId)) {
            if (userRepository.isExistsByMeetIdAndUserId(meetId, userId) > 0) {
                Optional<Meeting> meetingOptional = meetingRepository.findById(meetId);
                if (meetingOptional.isPresent()) {
                    result = meetingOptional.get().getMeetingUrl();
                } else {
                    throw new Exception("Meeting data not found");
                }
            } else {
                throw new Exception("User not authorized to join meet");
            }
        } else {
            throw new Exception("Invalid User");
        }
        return result;
    }

    public List<JitsiUser> getAllUsers() {
        return userRepository.findAll();
    }
}
