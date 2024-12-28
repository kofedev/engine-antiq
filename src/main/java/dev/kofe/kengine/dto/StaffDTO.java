package dev.kofe.kengine.dto;

import lombok.Data;

@Data
public class StaffDTO {

    private Long staffId;
    private String firstName;
    private String lastName;
    private UserDTO user;
    private Boolean isReceiverMails;

}
