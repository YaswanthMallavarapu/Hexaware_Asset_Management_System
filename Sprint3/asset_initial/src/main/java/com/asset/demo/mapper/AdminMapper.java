package com.asset.demo.mapper;

import com.asset.demo.dto.AdminDto;
import com.asset.demo.dto.AdminReqDto;
import com.asset.demo.dto.AdminResDto;
import com.asset.demo.model.Admin;

public class AdminMapper {
    public static Admin mapToEntity(AdminReqDto adminReqDto){
        Admin admin=new Admin();
        admin.setFirstName(adminReqDto.firstName());
        admin.setLastName(adminReqDto.lastName());
        return admin;
    }

    public static AdminDto mapToDto(Admin admin) {
        return new AdminDto(admin.getFirstName(),
                admin.getLastName(),
                admin.getUser().getUsername());
    }

    public static AdminResDto mapToDtoV2(Admin admin) {
        return new AdminResDto(
                admin.getId(),
                admin.getFirstName()+" "+admin.getLastName(),
                admin.getUser().getUsername()
        );
    }
}
