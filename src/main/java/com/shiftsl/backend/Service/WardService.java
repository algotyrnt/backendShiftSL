package com.shiftsl.backend.Service;

import com.shiftsl.backend.Exceptions.NotAWardAdminException;
import com.shiftsl.backend.Exceptions.WardNotFoundException;
import com.shiftsl.backend.model.Role;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.model.Ward;
import com.shiftsl.backend.repo.WardRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WardService {

    private final WardRepo wardRepo;
    private final UserService userService;

    public Ward createWard(Long wardAdminID, String wardName){
        Ward ward = new Ward();
        User wardAdmin = userService.getUserById(wardAdminID);
        if(!wardAdmin.getRole().equals(Role.WARD_ADMIN)){
            throw new NotAWardAdminException("Given user is not a ward admin");
        }
        ward.setWardAdmin(wardAdmin);

        ward.setName(wardName);
        return wardRepo.save(ward);
    }

    public Ward updateWard(Ward ward){
        return wardRepo.save(ward);
    }

    public Ward getWardByID(Long ID){
        return wardRepo.findById(ID).orElseThrow(() -> new WardNotFoundException("Ward (ID - " + ID + " not found."));
    }

    public Optional<Ward> findByName(String name){
        return wardRepo.findByName(name);
    }

    public List<Ward> getWardList(){
        return wardRepo.findAll();
    }

    public void deleteWardById(Long id){
        wardRepo.deleteById(id);
    }
}
