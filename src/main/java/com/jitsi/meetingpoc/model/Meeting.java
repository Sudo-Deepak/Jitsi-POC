package com.jitsi.meetingpoc.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int meetingId;
    private String description;
    private String meetingUrl;
    private LocalDateTime time;
    private boolean IsMeetingOn;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<JitsiUser> jitsiUsers;
    private boolean isLocked;
}
