package com.asset.demo.mapper;

import com.asset.demo.dto.ServiceRequestResDto;
import com.asset.demo.model.ServiceRequest;

public class ServiceRequestMapper {
    public static ServiceRequestResDto mapToDto(ServiceRequest serviceRequest){
        return new ServiceRequestResDto(
                serviceRequest.getId(),
                serviceRequest.getAsset().getId(),
                serviceRequest.getAsset().getAssetName(),
                serviceRequest.getDescription(),
                serviceRequest.getEmployee().getId(),
                serviceRequest.getEmployee().getFirstName()+" "+serviceRequest.getEmployee().getLastName(),
                serviceRequest.getManager().getId(),
                serviceRequest.getRequestDate(),
                serviceRequest.getStatus()
        );

    }
}
