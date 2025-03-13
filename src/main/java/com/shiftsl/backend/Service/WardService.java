package com.shiftsl.backend.Service;

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

    public Ward createWard(Ward ward){
        return wardRepo.save(ward);
    }

    public Ward updateWard(Ward ward){
        return wardRepo.save(ward);
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
