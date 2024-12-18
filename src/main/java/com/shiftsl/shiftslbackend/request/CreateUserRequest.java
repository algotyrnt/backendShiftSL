package com.shiftsl.shiftslbackend.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String email;
    private String password;
    private String name;
    private String role;
}
