package com.asset.demo.mapper;

import com.asset.demo.dto.EmployeeDocumentResDto;
import com.asset.demo.model.EmployeeDocument;

public class EmployeeDocumentMapper {

    public static EmployeeDocumentResDto mapToDto(EmployeeDocument doc) {
        return new EmployeeDocumentResDto(
                doc.getEmployee().getFirstName() + " " + doc.getEmployee().getLastName(),
                doc.getEmployee().getUser().getUsername(),
                doc.getEmployee().getUser().getRole(),
                doc.getProfileImage()
        );
    }
}
