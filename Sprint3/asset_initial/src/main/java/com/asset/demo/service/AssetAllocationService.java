package com.asset.demo.service;

import com.asset.demo.dto.AssetAllocationPageResDto;
import com.asset.demo.dto.AssetAllocationResDto;
import com.asset.demo.enums.AllocationStatus;
import com.asset.demo.enums.AssetStatus;
import com.asset.demo.enums.RequestStatus;
import com.asset.demo.enums.ReturnRequestStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.mapper.AssetAllocationMapper;
import com.asset.demo.model.*;
import com.asset.demo.repository.AssetAllocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;


@Service
@AllArgsConstructor
public class AssetAllocationService {
    private final AssetAllocationRepository assetAllocationRepository;
    private final UserService userService;
    private final AssetService assetService;
    private final AssetRequestService assetRequestService;
    private final AssetCategoryService assetCategoryService;
    private final ManagerService managerService;
    private final EmployeeService employeeService;

    public void allocateAsset(String username, long assetRequestId) {
        AssetRequest assetRequest=assetRequestService.getAssetRequestById(assetRequestId);
        Manager manager=managerService.getManagerByUsername(username);
        Asset asset=assetRequest.getAsset();
        // check weather the asset is available
        if(asset.getStatus()!=AssetStatus.AVAILABLE)
            throw new ResourceNotFoundException("Asset not available at the moment.");
        Employee employee=assetRequest.getEmployee();
        // make allocation true
        assetRequest.setStatus(RequestStatus.APPROVED);

        assetRequestService.updateAssetRequestStatus(assetRequest);
        asset.setStatus(AssetStatus.ALLOCATED);
        assetService.updateAsset(asset);
        AssetCategory assetCategory=asset.getCategory();
        assetCategory.setRemaining(assetCategory.getRemaining()-1);
        assetCategoryService.updateAssetCategory(assetCategory);
        //add employee and asset to assetAllocation
        AssetAllocation assetAllocation=new AssetAllocation();
        assetAllocation.setEmployee(employee);
        assetAllocation.setAsset(asset);
        assetAllocation.setManager(manager);
        //save asset allocation in DB
        assetAllocationRepository.save(assetAllocation);
    }

    public void rejectAsset(long assetRequestId) {
        AssetRequest assetRequest=assetRequestService.getAssetRequestById(assetRequestId);
       // Manager manager=managerService.getManagerByUsername(username);

        assetRequest.setStatus(RequestStatus.REJECTED);
        assetRequestService.updateAssetRequestStatus(assetRequest);

    }

    public AssetAllocation getAssetAllocationById(long assetAllocationId) {
        return assetAllocationRepository.findById(assetAllocationId)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Asset Allocation Id."));
    }

    public void returnAsset(String username,long assetAllocationId) {

        Employee loggedUser=employeeService.getEmployeeByUsername(username);
        //get assetAllocation
        AssetAllocation assetAllocation=getAssetAllocationById(assetAllocationId);
        Employee employee=assetAllocation.getEmployee();

        //verify user
        if(loggedUser!=employee){
            throw new ResourceNotFoundException("You cannot post return request for asset you don't have");
        }
        if(assetAllocation.getStatus()!=AllocationStatus.ALLOCATED){
            throw new ResourceNotFoundException("Invalid asset return request.");
        }

        assetAllocation.setStatus(AllocationStatus.RETURN_REQUESTED);
        assetAllocationRepository.save(assetAllocation);

    }

    public AssetAllocationPageResDto getAllAllocatedByUser(String name, int page, int size) {
        Pageable pageable= PageRequest.of(page,size);
        Page<AssetAllocation> pageAllocations=assetAllocationRepository.getByUsername(name,pageable);
        List<AssetAllocationResDto>list= pageAllocations
                .toList()
                .stream()
                .map(AssetAllocationMapper::mapToDto)
                .toList();
        return new AssetAllocationPageResDto(list,
                pageAllocations.getTotalElements(),
                pageAllocations.getTotalPages());
    }

    public List<AssetAllocation> getAllAllocatedAssets() {
        return assetAllocationRepository.getAllAllocatedAssets();
    }

    public void acceptReturnRequest(long assetAllocationId) {
        AssetAllocation assetAllocation=assetAllocationRepository.findById(assetAllocationId)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Asset Allocation Id."));
        assetAllocation.setStatus(AllocationStatus.RETURNED);
        Asset asset=assetAllocation.getAsset();
        asset.setStatus(AssetStatus.AVAILABLE);
        AssetCategory category=asset.getCategory();
        category.setRemaining(category.getRemaining()+1);
        assetService.updateAsset(asset);
        assetAllocation.setReturnDate(LocalDate.now());
        assetAllocationRepository.save(assetAllocation);
    }

    public List<AssetAllocationResDto> getAllByStatus(String status, int page, int size) {
        Pageable pageable=PageRequest.of(page,size);
        AllocationStatus status1=AllocationStatus.valueOf(status);
        Page<AssetAllocation>pageAllocation=assetAllocationRepository.getAllByStatus(status1,pageable);
        return pageAllocation
                .toList()
                .stream()
                .map(AssetAllocationMapper::mapToDto)
                .toList();
    }

    public long getCount() {
        return assetAllocationRepository.count();
    }

    public List<AssetAllocationResDto> getAllAllocations(int page, int size) {
        Pageable pageable=PageRequest.of(page, size);
        Page<AssetAllocation>pageAllocations=assetAllocationRepository.findAll(pageable);
        return pageAllocations
                .toList()
                .stream()
                .map(AssetAllocationMapper::mapToDto)
                .toList();
    }

    public long getCountByUser(String name) {
        return assetAllocationRepository.getCountByUser(name);
    }

    public AssetAllocationPageResDto getAllByUserStatus(String status, int page, int size, String name) {
        Pageable pageable=PageRequest.of(page,size);
        AllocationStatus allocationStatus=AllocationStatus.valueOf(status);
        Page<AssetAllocation>pageAllocations=assetAllocationRepository.findByUserStatus(name,allocationStatus,pageable);

        List<AssetAllocationResDto>list=pageAllocations
                .toList()
                .stream()
                .map(AssetAllocationMapper::mapToDto)
                .toList();
        return new AssetAllocationPageResDto(list,
                pageAllocations.getTotalElements(),
                pageAllocations.getTotalPages());
    }

    public void cancelReturnAsset(String name, long assetAllocationId) {
        Employee loggedUser=employeeService.getEmployeeByUsername(name);
        //get assetAllocation
        AssetAllocation assetAllocation=getAssetAllocationById(assetAllocationId);
        Employee employee=assetAllocation.getEmployee();

        //verify user
        if(loggedUser!=employee){
            throw new ResourceNotFoundException("You cannot post return request for asset you don't have");
        }
        if(assetAllocation.getStatus()!=AllocationStatus.RETURN_REQUESTED){
            throw new ResourceNotFoundException("Invalid asset return request.");
        }

        assetAllocation.setStatus(AllocationStatus.ALLOCATED);
        assetAllocationRepository.save(assetAllocation);
    }
}
