package com.jitsi.meetingpoc.util;

import java.util.Random;

public class Utils {
    static final String CHAR_POOL = "abcdefghijklmnopqrstuvwxyz";
    static final int SEGMENT_LENGTH = 4;
    static final int NUM_SEGMENTS = 3;
    static final String SEPARATOR = "-";

    public static String generateMeetingID() {
        StringBuilder meetingID = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < NUM_SEGMENTS; i++) {
            if (i > 0) {
                meetingID.append(SEPARATOR);
            }
            for (int j = 0; j < SEGMENT_LENGTH; j++) {
                int index = random.nextInt(CHAR_POOL.length());
                meetingID.append(CHAR_POOL.charAt(index));
            }
        }
        return meetingID.toString();
    }
}
