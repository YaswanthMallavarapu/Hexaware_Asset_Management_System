package com.asset.demo.mapper;

import com.asset.demo.dto.ManagerDto;
import com.asset.demo.dto.ManagerReqDto;
import com.asset.demo.dto.ManagerResDto;
import com.asset.demo.model.Employee;
import com.asset.demo.model.Manager;

public class ManagerMapper {
    public static Manager mapToEntity(ManagerReqDto dto){
        Manager manager=new Manager();
        manager.setFirstName(dto.firstName());
        manager.setLastName(dto.lastName());
        return manager;

    }

    public static ManagerResDto mapToDto(Manager manager) {
        return new ManagerResDto(
                manager.getId(),
                manager.getFirstName()+" "+manager.getLastName(),
                manager.getUser().getId(),
                manager.getUser().getAccountStatus()
        );
    }

    public static ManagerDto mapToDtoV2(Manager manager) {
        return new ManagerDto(
                manager.getId(),
                manager.getFirstName()+" "+manager.getLastName(),
                manager.getUser().getUsername()
        );
    }
}
