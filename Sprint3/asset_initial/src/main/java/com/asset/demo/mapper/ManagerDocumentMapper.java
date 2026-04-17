package com.asset.demo.mapper;

import com.asset.demo.dto.ManagerDocumentResDto;
import com.asset.demo.model.Manager;
import com.asset.demo.model.ManagerDocument;

public class ManagerDocumentMapper {

    public static ManagerDocumentResDto mapToDto(ManagerDocument managerDocument){
        return new ManagerDocumentResDto(
                managerDocument.getId(),
                managerDocument.getProfileImage(),
                managerDocument.getManager().getId(),
                managerDocument.getManager().getFirstName()+" "+managerDocument.getManager().getLastName()

        );
    }
}
