package com.asset.demo.service;

import com.asset.demo.dto.AssetAllocationPageResDto;
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
    private AssetAllocationService service;

    private Employee employee;
    private Manager manager;
    private Asset asset;
    private AssetCategory category;
    private AssetRequest request;

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

        request = new AssetRequest();
        request.setId(100L);
        request.setAsset(asset);
        request.setEmployee(employee);
    }

    private AssetAllocation buildAllocation(AllocationStatus status) {
        AssetAllocation a = new AssetAllocation();
        a.setId(99L); // ✅ IMPORTANT FIX
        a.setEmployee(employee);
        a.setManager(manager);
        a.setAsset(asset);
        a.setStatus(status);
        return a;
    }

    @Test
    void allocateAsset_success() {
        when(assetRequestService.getAssetRequestById(100L)).thenReturn(request);
        when(managerService.getManagerByUsername("manager")).thenReturn(manager);

        service.allocateAsset("manager", 100L);

        assertEquals(RequestStatus.APPROVED, request.getStatus());
        assertEquals(AssetStatus.ALLOCATED, asset.getStatus());
        assertEquals(4, category.getRemaining());

        verify(assetAllocationRepository).save(any());
    }

    @Test
    void allocateAsset_notAvailable() {
        asset.setStatus(AssetStatus.ALLOCATED);

        when(assetRequestService.getAssetRequestById(100L)).thenReturn(request);
        when(managerService.getManagerByUsername("manager")).thenReturn(manager);

        assertThrows(ResourceNotFoundException.class,
                () -> service.allocateAsset("manager", 100L));
    }

    @Test
    void rejectAsset() {
        when(assetRequestService.getAssetRequestById(100L)).thenReturn(request);

        service.rejectAsset(100L);

        assertEquals(RequestStatus.REJECTED, request.getStatus());
    }

    @Test
    void getAssetAllocationById_success() {
        AssetAllocation a = buildAllocation(AllocationStatus.ALLOCATED);

        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.of(a));

        assertNotNull(service.getAssetAllocationById(1L));
    }

    @Test
    void getAssetAllocationById_fail() {
        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getAssetAllocationById(1L));
    }

    @Test
    void returnAsset_success() {
        AssetAllocation a = buildAllocation(AllocationStatus.ALLOCATED);

        when(employeeService.getEmployeeByUsername("emp")).thenReturn(employee);
        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.of(a));

        service.returnAsset("emp", 1L);

        assertEquals(AllocationStatus.RETURN_REQUESTED, a.getStatus());
    }

    @Test
    void returnAsset_invalidUser() {
        Employee other = new Employee();

        AssetAllocation a = buildAllocation(AllocationStatus.ALLOCATED);
        a.setEmployee(other);

        when(employeeService.getEmployeeByUsername("emp")).thenReturn(employee);
        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThrows(ResourceNotFoundException.class,
                () -> service.returnAsset("emp", 1L));
    }

    @Test
    void returnAsset_invalidStatus() {
        AssetAllocation a = buildAllocation(AllocationStatus.RETURNED);

        when(employeeService.getEmployeeByUsername("emp")).thenReturn(employee);
        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThrows(ResourceNotFoundException.class,
                () -> service.returnAsset("emp", 1L));
    }

    @Test
    void getAllAllocatedByUser() {
        AssetAllocation a = buildAllocation(AllocationStatus.ALLOCATED);

        Page<AssetAllocation> page = new PageImpl<>(List.of(a));

        when(assetAllocationRepository.getByUsername(eq("emp"), any()))
                .thenReturn(page);

        AssetAllocationPageResDto res = service.getAllAllocatedByUser("emp", 0, 5);

        assertEquals(1, res.list().size());
    }

    @Test
    void getAllAllocatedAssets() {
        when(assetAllocationRepository.getAllAllocatedAssets())
                .thenReturn(List.of(buildAllocation(AllocationStatus.ALLOCATED)));

        assertEquals(1, service.getAllAllocatedAssets().size());
    }

    @Test
    void acceptReturnRequest_success() {
        AssetAllocation a = buildAllocation(AllocationStatus.ALLOCATED);

        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.of(a));

        service.acceptReturnRequest(1L);

        assertEquals(AllocationStatus.RETURNED, a.getStatus());
        assertEquals(AssetStatus.AVAILABLE, asset.getStatus());
        assertEquals(6, category.getRemaining());
    }

    @Test
    void acceptReturnRequest_fail() {
        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.acceptReturnRequest(1L));
    }

    @Test
    void getAllByStatus() {
        AssetAllocation a = buildAllocation(AllocationStatus.ALLOCATED);

        Page<AssetAllocation> page = new PageImpl<>(List.of(a));

        when(assetAllocationRepository.getAllByStatus(eq(AllocationStatus.ALLOCATED), any()))
                .thenReturn(page);

        assertEquals(1, service.getAllByStatus("ALLOCATED", 0, 5).size());
    }

    @Test
    void getAllAllocations() {
        AssetAllocation a = buildAllocation(AllocationStatus.ALLOCATED);

        when(assetAllocationRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(a)));

        assertEquals(1, service.getAllAllocations(0, 5).size());
    }

    @Test
    void getCount() {
        when(assetAllocationRepository.count()).thenReturn(5L);
        assertEquals(5, service.getCount());
    }

    @Test
    void getCountByUser() {
        when(assetAllocationRepository.getCountByUser("emp")).thenReturn(3L);
        assertEquals(3, service.getCountByUser("emp"));
    }

    @Test
    void getAllByUserStatus() {
        AssetAllocation a = buildAllocation(AllocationStatus.ALLOCATED);

        Page<AssetAllocation> page = new PageImpl<>(List.of(a));

        when(assetAllocationRepository.findByUserStatus(eq("emp"),
                eq(AllocationStatus.ALLOCATED), any()))
                .thenReturn(page);

        AssetAllocationPageResDto res =
                service.getAllByUserStatus("ALLOCATED", 0, 5, "emp");

        assertEquals(1, res.list().size());
    }

    @Test
    void cancelReturnAsset_success() {
        AssetAllocation a = buildAllocation(AllocationStatus.RETURN_REQUESTED);

        when(employeeService.getEmployeeByUsername("emp")).thenReturn(employee);
        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.of(a));

        service.cancelReturnAsset("emp", 1L);

        assertEquals(AllocationStatus.ALLOCATED, a.getStatus());
    }

    @Test
    void cancelReturnAsset_invalidUser() {
        AssetAllocation a = buildAllocation(AllocationStatus.RETURN_REQUESTED);
        a.setEmployee(new Employee());

        when(employeeService.getEmployeeByUsername("emp")).thenReturn(employee);
        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThrows(ResourceNotFoundException.class,
                () -> service.cancelReturnAsset("emp", 1L));
    }

    @Test
    void cancelReturnAsset_invalidStatus() {
        AssetAllocation a = buildAllocation(AllocationStatus.ALLOCATED);

        when(employeeService.getEmployeeByUsername("emp")).thenReturn(employee);
        when(assetAllocationRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThrows(ResourceNotFoundException.class,
                () -> service.cancelReturnAsset("emp", 1L));
    }
}