package com.asset.demo.service;

import com.asset.demo.dto.ServiceRequestPageResDto;
import com.asset.demo.dto.ServiceRequestReqDto;
import com.asset.demo.dto.ServiceRequestResDto;
import com.asset.demo.dto.ServiceRequestResponseDto;
import com.asset.demo.enums.AllocationStatus;
import com.asset.demo.enums.AssetStatus;
import com.asset.demo.enums.ServiceStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.mapper.ServiceRequestMapper;
import com.asset.demo.model.*;
import com.asset.demo.repository.AdminRepository;
import com.asset.demo.repository.AssetAllocationRepository;
import com.asset.demo.repository.ServiceRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.ServerRequest;

import java.security.Principal;
import java.util.List;

@Service
@AllArgsConstructor
public class ServiceRequestService {
    private final ServiceRequestRepository serviceRequestRepository;

    private final UserService userService;
    private final AssetService assetService;
    private final AssetAllocationService assetAllocationService;
    private final EmployeeService employeeService;
    private final ManagerService managerService;
    private final AdminRepository adminRepository;
    private final AssetAllocationRepository assetAllocationRepository;

    public void requestService(ServiceRequestReqDto serviceRequestReqDto, String username,long assetAllocationId) {
        //check for employee
        Employee employee=employeeService.getEmployeeByUsername(username);

        AssetAllocation assetAllocation=assetAllocationService.getAssetAllocationById(assetAllocationId);
        Asset asset=assetAllocation.getAsset();
        Employee assetEmployee=assetAllocation.getEmployee();

        if(employee!=assetEmployee){
            throw new ResourceNotFoundException("You cannot request service for this asset");
        }
        if(assetAllocation.getStatus()!= AllocationStatus.ALLOCATED){
            throw new ResourceNotFoundException("Invalid asset for request service.");
        }

        assetAllocation.setStatus(AllocationStatus.SERVICE_REQUESTED);
        assetAllocationRepository.save(assetAllocation);

        asset.setStatus(AssetStatus.IN_SERVICE);
        assetService.updateAsset(asset);
        //add details to service request
        ServiceRequest serviceRequest=new ServiceRequest();
        serviceRequest.setEmployee(assetEmployee);
        serviceRequest.setAsset(asset);
        serviceRequest.setDescription(serviceRequestReqDto.description());
        serviceRequest.setAssetAllocation(assetAllocation);
        //save assetRequest in DB
        serviceRequestRepository.save(serviceRequest);
    }

    public void acceptRequest(String username,long serviceRequestId) {
        //get service request
        ServiceRequest serviceRequest=serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Service Request Id."));
        Manager manager=managerService.getManagerByUsername(username);
        serviceRequest.setStatus(ServiceStatus.IN_PROGRESS);
        Asset asset=serviceRequest.getAsset();
        //update asset status
        asset.setStatus(AssetStatus.IN_SERVICE);
        asset=assetService.updateAsset(asset);
        serviceRequest.setAsset(asset);
        serviceRequest.setManager(manager);
        //save updated request
        serviceRequestRepository.save(serviceRequest);
    }

    public void rejectRequest(long serviceRequestId,String username) {
        //get service request
        ServiceRequest serviceRequest=serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Service Request Id."));
        Manager manager=managerService.getManagerByUsername(username);
        serviceRequest.setManager(manager);
        serviceRequest.setStatus(ServiceStatus.REJECTED);
        //save updated request
        serviceRequestRepository.save(serviceRequest);
    }

    public void resolvedRequest(long serviceRequestId) {
        //get service request
        ServiceRequest serviceRequest=serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Service Request Id."));
        serviceRequest.setStatus(ServiceStatus.RESOLVED);
        Asset asset=serviceRequest.getAsset();
        //update asset status
        asset.setStatus(AssetStatus.ALLOCATED);
        asset=assetService.updateAsset(asset);
        serviceRequest.setAsset(asset);
        //save updated request
        serviceRequestRepository.save(serviceRequest);
    }

    public ServiceRequestResponseDto getRequestByUsername(String name, int page, int size) {
        Pageable pageable= PageRequest.of(page,size);
        Page<ServiceRequest> pageServiceRequest=serviceRequestRepository.getServiceRequestByUser(name,pageable);
        List<ServiceRequestResDto>list= pageServiceRequest.toList()
                .stream()
                .map(ServiceRequestMapper::mapToDto)
                .toList();
        return new ServiceRequestResponseDto(list,
                pageServiceRequest.getTotalPages(),
                pageServiceRequest.getTotalPages());
    }

    public long getCount() {
        return adminRepository.count();
    }

    public ServiceRequestResponseDto getAll(int page,int size) {
        Pageable pageable=PageRequest.of(page,size);
        Page<ServiceRequest>pageServices=serviceRequestRepository.findAll(pageable);
        List<ServiceRequestResDto>list=pageServices
                .toList()
                .stream()
                .map(ServiceRequestMapper::mapToDto)
                .toList();
        return new ServiceRequestResponseDto(
                list,
                pageServices.getTotalPages(),
                pageServices.getTotalElements()
        );
    }

    public ServiceRequestResponseDto getAllByStatus(int page, int size, String status) {

        ServiceStatus serviceStatus=ServiceStatus.valueOf(status);
        Pageable pageable=PageRequest.of(page,size);
        Page<ServiceRequest>list=serviceRequestRepository.getByStatus(serviceStatus,pageable);
        List<ServiceRequestResDto>requests=list.toList()
                .stream()
                .map(ServiceRequestMapper::mapToDto)
                .toList();
        return new ServiceRequestResponseDto(requests,list.getTotalPages(), list.getTotalElements());
    }

    public long getCountByUser(String name) {
        return serviceRequestRepository.getCountByUser(name);
    }

    public void deleteRequest(long serviceRequestId, Principal principal) {

        ServiceRequest serviceRequest=serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(()->new ResourceNotFoundException("Invalid service request ID."));
        Employee employee=serviceRequest.getEmployee();
        if(!employee.getUser().getUsername().equals(principal.getName())){
            throw new ResourceNotFoundException("You cannot delete this request");
        }
        AssetAllocation assetAllocation=serviceRequest.getAssetAllocation();
        assetAllocation.setStatus(AllocationStatus.ALLOCATED);
        assetAllocationRepository.save(assetAllocation);
        serviceRequestRepository.deleteById(serviceRequestId);
    }

    public ServiceRequestPageResDto getByUserStatus(String status, int page, int size, String name) {
        ServiceStatus serviceStatus=ServiceStatus.valueOf(status);
        Pageable pageable=PageRequest.of(page,size);
        Page<ServiceRequest>pageServices=serviceRequestRepository.getAllByUserStatus(serviceStatus,name,pageable);
        List<ServiceRequestResDto>list=pageServices
                .toList()
                .stream()
                .map(ServiceRequestMapper::mapToDto)
                .toList();
        return new ServiceRequestPageResDto(list,
                pageServices.getTotalElements(),
                pageServices.getTotalPages());
    }
}
