package com.shiftsl.backend.Controller;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.shiftsl.backend.Service.ShiftService;
import com.shiftsl.backend.model.Shift;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ShiftController_GraphQL {

    private final ShiftService shiftService;

    @QueryMapping
    public List<Shift> shifts(){
        return shiftService.getAllShifts();
    }

    @QueryMapping
    public Shift shiftByID(@Argument Long id){
        return shiftService.getShiftByID(id);
    }
    
    @QueryMapping
    public List<Shift> shiftsByDoctorsID(@Argument Long id){
        return shiftService.getShiftsForDoctor(id);
    }

    @QueryMapping
    public List<Shift> shiftsAvailable(){
        return shiftService.getAvailableShifts();
    }

}
