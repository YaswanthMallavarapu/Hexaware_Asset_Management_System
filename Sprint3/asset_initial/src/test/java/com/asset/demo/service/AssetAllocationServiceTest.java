package com.asset.demo.service;

import com.asset.demo.dto.AssetAllocationResDto;
import com.asset.demo.enums.*;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.*;
import com.asset.demo.repository.AssetAllocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AssetAllocationServiceTest {

    @Mock private AssetAllocationRepository assetAllocationRepository;
    @Mock private UserService userService;
    @Mock private AssetService assetService;
    @Mock private AssetRequestService assetRequestService;
    @Mock private AssetCategoryService assetCategoryService;
    @Mock private ManagerService managerService;
    @Mock private EmployeeService employeeService;

    @InjectMocks
    private AssetAllocationService assetAllocationService;

    private Employee employee;
    private Manager manager;
    private Asset asset;
    private AssetCategory category;
    private AssetRequest assetRequest;

    @BeforeEach
    void setup() {

        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        manager = new Manager();
        manager.setId(2L);

        category = new AssetCategory();
        category.setRemaining(5);

        asset = new Asset();
        asset.setId(10L);
        asset.setAssetName("Laptop");
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setCategory(category);

        assetRequest = new AssetRequest();
        assetRequest.setId(100L);
        assetRequest.setAsset(asset);
        assetRequest.setEmployee(employee);
    }

    @Test
    void allocateAsset_success() {

        when(assetRequestService.getAssetRequestById(100L)).thenReturn(assetRequest);
        when(managerService.getManagerByUsername("manager")).thenReturn(manager);

        assetAllocationService.allocateAsset("manager", 100L);

        assertEquals(RequestStatus.APPROVED, assetRequest.getStatus());
        assertEquals(AssetStatus.ALLOCATED, asset.getStatus());
        assertEquals(4, category.getRemaining());

        verify(assetRequestService).updateAssetRequestStatus(assetRequest);
        verify(assetService).updateAsset(asset);
        verify(assetCategoryService).updateAssetCategory(category);
        verify(assetAllocationRepository).save(any(AssetAllocation.class));
    }

    @Test
    void allocateAsset_assetNotAvailable() {

        asset.setStatus(AssetStatus.ALLOCATED);

        when(assetRequestService.getAssetRequestById(100L)).thenReturn(assetRequest);
        when(managerService.getManagerByUsername("manager")).thenReturn(manager);

        assertThrows(ResourceNotFoundException.class,
                () -> assetAllocationService.allocateAsset("manager", 100L));
    }

    @Test
    void rejectAsset_success() {

        when(assetRequestService.getAssetRequestById(100L)).thenReturn(assetRequest);

        assetAllocationService.rejectAsset(100L);

        assertEquals(RequestStatus.REJECTED, assetRequest.getStatus());
        verify(assetRequestService).updateAssetRequestStatus(assetRequest);
    }

    @Test
    void getAssetAllocationById_success() {

        AssetAllocation allocation = new AssetAllocation();

        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.of(allocation));

        assertNotNull(assetAllocationService.getAssetAllocationById(1L));
    }

    @Test
    void getAssetAllocationById_notFound() {

        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> assetAllocationService.getAssetAllocationById(1L));
    }

    @Test
    void returnAsset_success() {

        AssetAllocation allocation = new AssetAllocation();
        allocation.setId(1L);
        allocation.setEmployee(employee);
        allocation.setStatus(AllocationStatus.ALLOCATED);

        when(employeeService.getEmployeeByUsername("emp")).thenReturn(employee);
        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.of(allocation));

        assetAllocationService.returnAsset("emp", 1L);

        assertEquals(AllocationStatus.RETURN_REQUESTED, allocation.getStatus());
        verify(assetAllocationRepository).save(allocation);
    }

    @Test
    void returnAsset_invalidUser() {

        Employee other = new Employee();

        AssetAllocation allocation = new AssetAllocation();
        allocation.setEmployee(other);
        allocation.setStatus(AllocationStatus.ALLOCATED);

        when(employeeService.getEmployeeByUsername("emp")).thenReturn(employee);
        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.of(allocation));

        assertThrows(ResourceNotFoundException.class,
                () -> assetAllocationService.returnAsset("emp", 1L));
    }

    @Test
    void returnAsset_invalidStatus() {

        AssetAllocation allocation = new AssetAllocation();
        allocation.setEmployee(employee);
        allocation.setStatus(AllocationStatus.RETURNED);

        when(employeeService.getEmployeeByUsername("emp")).thenReturn(employee);
        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.of(allocation));

        assertThrows(ResourceNotFoundException.class,
                () -> assetAllocationService.returnAsset("emp", 1L));
    }

    @Test
    void getAllAllocatedByUser_success() {

        AssetAllocation allocation = new AssetAllocation();
        allocation.setId(99L);
        allocation.setEmployee(employee);
        allocation.setManager(manager);
        allocation.setAsset(asset);
        allocation.setStatus(AllocationStatus.ALLOCATED);

        Page<AssetAllocation> page =
                new PageImpl<>(List.of(allocation));

        when(assetAllocationRepository.getByUsername(eq("emp"), any(Pageable.class)))
                .thenReturn(page);

        List<AssetAllocationResDto> result =
                assetAllocationService.getAllAllocatedByUser("emp", 0, 5);

        assertEquals(1, result.size());
    }

    @Test
    void getAllAllocatedAssets_success() {

        AssetAllocation allocation = new AssetAllocation();

        when(assetAllocationRepository.getAllAllocatedAssets())
                .thenReturn(List.of(allocation));

        assertEquals(1, assetAllocationService.getAllAllocatedAssets().size());
    }

    @Test
    void acceptReturnRequest_success() {

        AssetAllocation allocation = new AssetAllocation();
        allocation.setAsset(asset);

        when(assetAllocationRepository.findById(1L))
                .thenReturn(Optional.of(allocation));

        assetAllocationService.acceptReturnRequest(1L);

        assertEquals(AllocationStatus.RETURNED, allocation.getStatus());
        assertEquals(AssetStatus.AVAILABLE, asset.getStatus());
        assertEquals(6, category.getRemaining());
        assertNotNull(allocation.getReturnDate());

        verify(assetService).updateAsset(asset);
        verify(assetAllocationRepository).save(allocation);
    }

    @Test
    void acceptReturnRequest_notFound() {

        when(assetAllocationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> assetAllocationService.acceptReturnRequest(1L));
    }

    @Test
    void getAllByStatus_success() {

        AssetAllocation allocation = new AssetAllocation();
        allocation.setId(100L);
        allocation.setEmployee(employee);
        allocation.setManager(manager);
        allocation.setAsset(asset);
        allocation.setStatus(AllocationStatus.ALLOCATED);

        Page<AssetAllocation> page =
                new PageImpl<>(List.of(allocation));

        when(assetAllocationRepository
                .getAllByStatus(eq(AllocationStatus.ALLOCATED), any(Pageable.class)))
                .thenReturn(page);

        List<AssetAllocationResDto> result =
                assetAllocationService.getAllByStatus("ALLOCATED", 0, 5);

        assertEquals(1, result.size());
    }
}