package com.shiftsl.backend.Controller;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.shiftsl.backend.Service.LeaveService;
import com.shiftsl.backend.model.Leave;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LeaveController_GraphQL {

    private final LeaveService leaveService;

    @QueryMapping
    public List<Leave> leaves(){
        return leaveService.getLeaves();
    }

    @QueryMapping
    public Leave leaveByID(@Argument Long id){
        return leaveService.getLeave(id);
    }

    @QueryMapping
    public List<Leave>leavesByDoctorsID(@Argument Long id){
        return leaveService.getLeaveByDoctor(id);
    }
    
}
