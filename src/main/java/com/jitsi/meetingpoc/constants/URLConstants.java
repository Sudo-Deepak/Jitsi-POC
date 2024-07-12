package com.jitsi.meetingpoc.constants;

public class URLConstants {
    public interface MeetingURLConstants {
        String MEETING_BASE_URL = "/api/meetings";
        String CREATE = "/create";
        String SCHEDULE = "/schedule";
        String JOIN_MEETING = "/join/{meetingId}";
        String UPDATE_MEETING = "/updateMeeting";
        String INVITE = "/invite";
    }
}
