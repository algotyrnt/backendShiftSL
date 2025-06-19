package com.shiftsl.backend.Controller;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.shiftsl.backend.Service.UserService;
import com.shiftsl.backend.model.Role;
import com.shiftsl.backend.model.User;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController_GraphQL {
    
    private final UserService userService;

    @QueryMapping
    public User userById(@Argument Long userId) {
        return userService.getUserById(userId);
    }

    @QueryMapping
    public List<User> users() {
        return userService.getAllUsers();
    }

    @QueryMapping
    public List<User> userByRole(@Argument Role role){
        return userService.getUsersByRole(role);
    }

}
