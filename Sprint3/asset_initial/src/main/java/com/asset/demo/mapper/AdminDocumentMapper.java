package com.asset.demo.mapper;

import com.asset.demo.model.AdminDocument;

public class AdminDocumentMapper {
    public static AdminDocumentResDto mapToDto(AdminDocument adminDocument){
        return new AdminDocumentResDto(
                adminDocument.getAdmin().getFirstName()+" "+adminDocument.getAdmin().getLastName(),
                adminDocument.getAdmin().getUser().getUsername(),
                adminDocument.getAdmin().getUser().getRole(),
                adminDocument.getProfileImage()
        );
    }
}
