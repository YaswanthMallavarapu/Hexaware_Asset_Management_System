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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserService userService;
    private final AssetService assetService;
    private final AssetAllocationService assetAllocationService;
    private final EmployeeService employeeService;
    private final ManagerService managerService;
    private final AdminRepository adminRepository;
    private final AssetAllocationRepository assetAllocationRepository;
    private final String IN_VALID_ID_MESSAGE="Invalid service request ID.";

    public void requestService(ServiceRequestReqDto serviceRequestReqDto, String username, long assetAllocationId) {
        log.atInfo().log("Service request created by username={} for allocationId={}", username, assetAllocationId);

        Employee employee = employeeService.getEmployeeByUsername(username);
        AssetAllocation assetAllocation = assetAllocationService.getAssetAllocationById(assetAllocationId);

        Asset asset = assetAllocation.getAsset();
        Employee assetEmployee = assetAllocation.getEmployee();

        if (employee != assetEmployee) {
            log.atWarn().log("Unauthorized service request attempt by username={}", username);
            throw new ResourceNotFoundException("You cannot request service for this asset");
        }

        if (assetAllocation.getStatus() != AllocationStatus.ALLOCATED) {
            throw new ResourceNotFoundException("Invalid asset for request service.");
        }

        assetAllocation.setStatus(AllocationStatus.SERVICE_REQUESTED);
        assetAllocationRepository.save(assetAllocation);

        asset.setStatus(AssetStatus.IN_SERVICE);
        assetService.updateAsset(asset);

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setEmployee(assetEmployee);
        serviceRequest.setAsset(asset);
        serviceRequest.setDescription(serviceRequestReqDto.description());
        serviceRequest.setAssetAllocation(assetAllocation);

        serviceRequestRepository.save(serviceRequest);

        log.atInfo().log("Service request saved successfully for allocationId={}", assetAllocationId);
    }

    public void acceptRequest(String username, long serviceRequestId) {
        log.atInfo().log("Service request {} accepted by manager={}", serviceRequestId, username);

        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> {
                    log.atWarn().log("Invalid service request id={}", serviceRequestId);
                    return new ResourceNotFoundException(IN_VALID_ID_MESSAGE);
                });

        Manager manager = managerService.getManagerByUsername(username);

        serviceRequest.setStatus(ServiceStatus.IN_PROGRESS);

        Asset asset = serviceRequest.getAsset();
        asset.setStatus(AssetStatus.IN_SERVICE);

        asset = assetService.updateAsset(asset);

        serviceRequest.setAsset(asset);
        serviceRequest.setManager(manager);

        serviceRequestRepository.save(serviceRequest);
    }

    public void rejectRequest(long serviceRequestId, String username) {
        log.atInfo().log("Service request {} rejected by manager={}", serviceRequestId, username);

        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(IN_VALID_ID_MESSAGE));

        Manager manager = managerService.getManagerByUsername(username);

        serviceRequest.setManager(manager);
        serviceRequest.setStatus(ServiceStatus.REJECTED);

        serviceRequestRepository.save(serviceRequest);
    }

    public void resolvedRequest(long serviceRequestId) {
        log.atInfo().log("Resolving service request id={}", serviceRequestId);

        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(IN_VALID_ID_MESSAGE));

        serviceRequest.setStatus(ServiceStatus.RESOLVED);

        Asset asset = serviceRequest.getAsset();
        asset.setStatus(AssetStatus.ALLOCATED);

        asset = assetService.updateAsset(asset);

        serviceRequest.setAsset(asset);
        serviceRequestRepository.save(serviceRequest);
    }

    public ServiceRequestResponseDto getRequestByUsername(String name, int page, int size) {
        log.atInfo().log("Fetching service requests for user={} page={} size={}", name, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceRequest> pageServiceRequest = serviceRequestRepository.getServiceRequestByUser(name, pageable);

        List<ServiceRequestResDto> list = pageServiceRequest.toList()
                .stream()
                .map(ServiceRequestMapper::mapToDto)
                .toList();

        return new ServiceRequestResponseDto(list,
                pageServiceRequest.getTotalPages(),
                pageServiceRequest.getTotalElements());
    }

    public long getCount() {
        log.atInfo().log("Fetching admin count (used incorrectly in service request service)");
        return adminRepository.count();
    }

    public ServiceRequestResponseDto getAll(int page, int size) {
        log.atInfo().log("Fetching all service requests page={} size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceRequest> pageServices = serviceRequestRepository.findAll(pageable);

        List<ServiceRequestResDto> list = pageServices
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
        log.atInfo().log("Fetching service requests by status={} page={} size={}", status, page, size);

        ServiceStatus serviceStatus = ServiceStatus.valueOf(status);
        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceRequest> list = serviceRequestRepository.getByStatus(serviceStatus, pageable);

        List<ServiceRequestResDto> requests = list.toList()
                .stream()
                .map(ServiceRequestMapper::mapToDto)
                .toList();

        return new ServiceRequestResponseDto(requests,
                list.getTotalPages(),
                list.getTotalElements());
    }

    public long getCountByUser(String name) {
        log.atInfo().log("Fetching service request count for user={}", name);
        return serviceRequestRepository.getCountByUser(name);
    }

    public void deleteRequest(long serviceRequestId, Principal principal) {
        log.atInfo().log("Deleting service request id={} by user={}", serviceRequestId, principal.getName());

        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(IN_VALID_ID_MESSAGE));

        Employee employee = serviceRequest.getEmployee();

        if (!employee.getUser().getUsername().equals(principal.getName())) {
            log.atWarn().log("Unauthorized delete attempt by user={}", principal.getName());
            throw new ResourceNotFoundException("You cannot delete this request");
        }

        AssetAllocation assetAllocation = serviceRequest.getAssetAllocation();
        assetAllocation.setStatus(AllocationStatus.ALLOCATED);

        assetAllocationRepository.save(assetAllocation);
        serviceRequestRepository.deleteById(serviceRequestId);

        log.atInfo().log("Service request deleted successfully id={}", serviceRequestId);
    }

    public ServiceRequestPageResDto getByUserStatus(String status, int page, int size, String name) {
        log.atInfo().log("Fetching service requests by user={} status={}", name, status);

        ServiceStatus serviceStatus = ServiceStatus.valueOf(status);
        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceRequest> pageServices =
                serviceRequestRepository.getAllByUserStatus(serviceStatus, name, pageable);

        List<ServiceRequestResDto> list = pageServices
                .toList()
                .stream()
                .map(ServiceRequestMapper::mapToDto)
                .toList();

        return new ServiceRequestPageResDto(list,
                pageServices.getTotalElements(),
                pageServices.getTotalPages());
    }
}