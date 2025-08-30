package com.codecomet.linkedInProject.connectionService.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionAccepted {

    private Long senderId;
    private Long receiverId;
    private String receiverName;
}
