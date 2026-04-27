package com.asset.demo.service;

import com.asset.demo.dto.AssetAllocationPageResDto;
import com.asset.demo.dto.AssetAllocationResDto;
import com.asset.demo.enums.*;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.mapper.AssetAllocationMapper;
import com.asset.demo.model.*;
import com.asset.demo.repository.AssetAllocationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AssetAllocationService {

    private final AssetAllocationRepository assetAllocationRepository;
    private final UserService userService;
    private final AssetService assetService;
    private final AssetRequestService assetRequestService;
    private final AssetCategoryService assetCategoryService;
    private final ManagerService managerService;
    private final EmployeeService employeeService;

    public void allocateAsset(String username, long assetRequestId) {

        log.atInfo().log("Manager {} allocating asset for request {}", username, assetRequestId);

        AssetRequest assetRequest = assetRequestService.getAssetRequestById(assetRequestId);
        Manager manager = managerService.getManagerByUsername(username);
        Asset asset = assetRequest.getAsset();

        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            log.atInfo().log("Asset not available for allocation. Asset ID: {}", asset.getId());
            throw new ResourceNotFoundException("Asset not available at the moment.");
        }

        Employee employee = assetRequest.getEmployee();

        assetRequest.setStatus(RequestStatus.APPROVED);
        assetRequestService.updateAssetRequestStatus(assetRequest);

        asset.setStatus(AssetStatus.ALLOCATED);
        assetService.updateAsset(asset);

        AssetCategory category = asset.getCategory();
        category.setRemaining(category.getRemaining() - 1);
        assetCategoryService.updateAssetCategory(category);

        AssetAllocation allocation = new AssetAllocation();
        allocation.setEmployee(employee);
        allocation.setAsset(asset);
        allocation.setManager(manager);

        assetAllocationRepository.save(allocation);

        log.atInfo().log("Asset {} allocated to employee {}", asset.getId(), employee.getId());
    }

    public void rejectAsset(long assetRequestId) {

        log.atInfo().log("Rejecting asset request {}", assetRequestId);

        AssetRequest assetRequest = assetRequestService.getAssetRequestById(assetRequestId);
        assetRequest.setStatus(RequestStatus.REJECTED);
        assetRequestService.updateAssetRequestStatus(assetRequest);
    }

    public AssetAllocation getAssetAllocationById(long id) {

        log.atInfo().log("Fetching asset allocation by ID {}", id);

        return assetAllocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Asset Allocation Id."));
    }

    public void returnAsset(String username, long assetAllocationId) {

        log.atInfo().log("User {} requesting return for allocation {}", username, assetAllocationId);

        Employee loggedUser = employeeService.getEmployeeByUsername(username);
        AssetAllocation allocation = getAssetAllocationById(assetAllocationId);
        Employee employee = allocation.getEmployee();

        if (loggedUser != employee) {
            log.atInfo().log("Unauthorized return attempt by {}", username);
            throw new ResourceNotFoundException("You cannot post return request for asset you don't have");
        }

        if (allocation.getStatus() != AllocationStatus.ALLOCATED) {
            throw new ResourceNotFoundException("Invalid asset return request.");
        }

        allocation.setStatus(AllocationStatus.RETURN_REQUESTED);
        assetAllocationRepository.save(allocation);
    }

    public AssetAllocationPageResDto getAllAllocatedByUser(String name, int page, int size) {

        log.atInfo().log("Fetching allocations for user {} page={} size={}", name, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<AssetAllocation> pageData = assetAllocationRepository.getByUsername(name, pageable);

        List<AssetAllocationResDto> list = pageData.getContent()
                .stream()
                .map(AssetAllocationMapper::mapToDto)
                .toList();

        return new AssetAllocationPageResDto(list,
                pageData.getTotalElements(),
                pageData.getTotalPages());
    }

    public List<AssetAllocation> getAllAllocatedAssets() {

        log.atInfo().log("Fetching all allocated assets");

        return assetAllocationRepository.getAllAllocatedAssets();
    }

    public void acceptReturnRequest(long id) {

        log.atInfo().log("Accepting return request for allocation {}", id);

        AssetAllocation allocation = assetAllocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Asset Allocation Id."));

        allocation.setStatus(AllocationStatus.RETURNED);

        Asset asset = allocation.getAsset();
        asset.setStatus(AssetStatus.AVAILABLE);

        AssetCategory category = asset.getCategory();
        category.setRemaining(category.getRemaining() + 1);

        assetService.updateAsset(asset);

        allocation.setReturnDate(LocalDate.now());
        assetAllocationRepository.save(allocation);

        log.atInfo().log("Asset {} returned successfully", asset.getId());
    }

    public List<AssetAllocationResDto> getAllByStatus(String status, int page, int size) {

        log.atInfo().log("Fetching allocations by status {} page={} size={}", status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        AllocationStatus s = AllocationStatus.valueOf(status);

        Page<AssetAllocation> pageData = assetAllocationRepository.getAllByStatus(s, pageable);

        return pageData.getContent()
                .stream()
                .map(AssetAllocationMapper::mapToDto)
                .toList();
    }

    public long getCount() {

        long count = assetAllocationRepository.count();
        log.atInfo().log("Total allocations count {}", count);

        return count;
    }

    public List<AssetAllocationResDto> getAllAllocations(int page, int size) {

        log.atInfo().log("Fetching all allocations page={} size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<AssetAllocation> pageData = assetAllocationRepository.findAll(pageable);

        return pageData.getContent()
                .stream()
                .map(AssetAllocationMapper::mapToDto)
                .toList();
    }

    public long getCountByUser(String name) {

        long count = assetAllocationRepository.getCountByUser(name);
        log.atInfo().log("Allocation count for user {} is {}", name, count);

        return count;
    }

    public AssetAllocationPageResDto getAllByUserStatus(String status, int page, int size, String name) {

        log.atInfo().log("Fetching allocations for user {} with status {}", name, status);

        Pageable pageable = PageRequest.of(page, size);
        AllocationStatus s = AllocationStatus.valueOf(status);

        Page<AssetAllocation> pageData =
                assetAllocationRepository.findByUserStatus(name, s, pageable);

        List<AssetAllocationResDto> list = pageData.getContent()
                .stream()
                .map(AssetAllocationMapper::mapToDto)
                .toList();

        return new AssetAllocationPageResDto(list,
                pageData.getTotalElements(),
                pageData.getTotalPages());
    }

    public void cancelReturnAsset(String name, long id) {

        log.atInfo().log("User {} cancelling return request for allocation {}", name, id);

        Employee loggedUser = employeeService.getEmployeeByUsername(name);
        AssetAllocation allocation = getAssetAllocationById(id);
        Employee employee = allocation.getEmployee();

        if (loggedUser != employee) {
            throw new ResourceNotFoundException("You cannot post return request for asset you don't have");
        }

        if (allocation.getStatus() != AllocationStatus.RETURN_REQUESTED) {
            throw new ResourceNotFoundException("Invalid asset return request.");
        }

        allocation.setStatus(AllocationStatus.ALLOCATED);
        assetAllocationRepository.save(allocation);
    }
}