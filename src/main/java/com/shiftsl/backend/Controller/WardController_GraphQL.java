package com.shiftsl.backend.Controller;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.shiftsl.backend.Service.WardService;
import com.shiftsl.backend.model.Ward;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WardController_GraphQL {

    private final WardService wardService;

    @QueryMapping
    public List<Ward> wards(){
        return wardService.getWardList();
    }

    @QueryMapping
    public Ward wardById(@Argument Long id){
        return wardService.getWardByID(id);
    }
    
}
