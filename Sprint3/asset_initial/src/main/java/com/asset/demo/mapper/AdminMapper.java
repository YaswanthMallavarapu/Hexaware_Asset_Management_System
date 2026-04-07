package com.asset.demo.mapper;

import com.asset.demo.dto.AdminReqDto;
import com.asset.demo.model.Admin;

public class AdminMapper {
    public static Admin mapToEntity(AdminReqDto adminReqDto){
        Admin admin=new Admin();
        admin.setFirstName(adminReqDto.firstName());
        admin.setLastName(adminReqDto.lastName());
        return admin;
    }
}
