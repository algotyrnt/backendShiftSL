package com.shiftsl.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

}
