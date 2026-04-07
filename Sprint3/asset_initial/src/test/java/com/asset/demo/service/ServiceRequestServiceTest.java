package com.asset.demo.service;

import com.asset.demo.dto.ServiceRequestReqDto;
import com.asset.demo.enums.*;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.*;
import com.asset.demo.repository.ServiceRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ServiceRequestServiceTest {

    @Mock private ServiceRequestRepository serviceRequestRepository;
    @Mock private UserService userService;
    @Mock private AssetService assetService;
    @Mock private AssetAllocationService assetAllocationService;
    @Mock private EmployeeService employeeService;
    @Mock private ManagerService managerService;

    @InjectMocks
    private ServiceRequestService serviceRequestService;

    @Test
    void requestService_success() {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        Asset asset = new Asset();
        asset.setId(10L);
        asset.setAssetName("Laptop");
        asset.setStatus(AssetStatus.ALLOCATED);

        AssetAllocation allocation = new AssetAllocation();
        allocation.setId(100L);
        allocation.setEmployee(employee);
        allocation.setAsset(asset);
        allocation.setStatus(AllocationStatus.ALLOCATED);

        when(employeeService.getEmployeeByUsername("emp")).thenReturn(employee);
        when(assetAllocationService.getAssetAllocationById(100L)).thenReturn(allocation);
        when(assetService.updateAsset(asset)).thenReturn(asset);

        ServiceRequestReqDto dto = new ServiceRequestReqDto("screen issue");

        serviceRequestService.requestService(dto, "emp", 100L);

        assertEquals(AssetStatus.IN_SERVICE, asset.getStatus());
        verify(serviceRequestRepository).save(any(ServiceRequest.class));
    }

    @Test
    void requestService_invalidEmployee() {

        Employee employee = new Employee();
        employee.setId(1L);

        Employee other = new Employee();
        other.setId(2L);

        Asset asset = new Asset();
        asset.setStatus(AssetStatus.ALLOCATED);

        AssetAllocation allocation = new AssetAllocation();
        allocation.setEmployee(other);
        allocation.setAsset(asset);
        allocation.setStatus(AllocationStatus.ALLOCATED);

        when(employeeService.getEmployeeByUsername("emp")).thenReturn(employee);
        when(assetAllocationService.getAssetAllocationById(100L)).thenReturn(allocation);

        assertThrows(ResourceNotFoundException.class,
                () -> serviceRequestService.requestService(new ServiceRequestReqDto("issue"), "emp", 100L));
    }

    @Test
    void requestService_invalidStatus() {

        Employee employee = new Employee();

        Asset asset = new Asset();
        asset.setStatus(AssetStatus.ALLOCATED);

        AssetAllocation allocation = new AssetAllocation();
        allocation.setEmployee(employee);
        allocation.setAsset(asset);
        allocation.setStatus(AllocationStatus.RETURNED);

        when(employeeService.getEmployeeByUsername("emp")).thenReturn(employee);
        when(assetAllocationService.getAssetAllocationById(100L)).thenReturn(allocation);

        assertThrows(ResourceNotFoundException.class,
                () -> serviceRequestService.requestService(new ServiceRequestReqDto("issue"), "emp", 100L));
    }

    @Test
    void acceptRequest_success() {

        Manager manager = new Manager();
        manager.setId(5L);

        Asset asset = new Asset();
        asset.setStatus(AssetStatus.ALLOCATED);

        ServiceRequest request = new ServiceRequest();
        request.setId(1L);
        request.setAsset(asset);

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(managerService.getManagerByUsername("manager")).thenReturn(manager);
        when(assetService.updateAsset(asset)).thenReturn(asset);

        serviceRequestService.acceptRequest("manager", 1L);

        assertEquals(ServiceStatus.IN_PROGRESS, request.getStatus());
        assertEquals(AssetStatus.IN_SERVICE, asset.getStatus());
        assertEquals(manager, request.getManager());
        verify(serviceRequestRepository).save(request);
    }

    @Test
    void acceptRequest_notFound() {

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceRequestService.acceptRequest("manager", 1L));
    }

    @Test
    void rejectRequest_success() {

        Manager manager = new Manager();
        manager.setId(3L);

        ServiceRequest request = new ServiceRequest();
        request.setId(1L);

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(managerService.getManagerByUsername("manager")).thenReturn(manager);

        serviceRequestService.rejectRequest(1L, "manager");

        assertEquals(ServiceStatus.REJECTED, request.getStatus());
        assertEquals(manager, request.getManager());
        verify(serviceRequestRepository).save(request);
    }

    @Test
    void rejectRequest_notFound() {

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceRequestService.rejectRequest(1L, "manager"));
    }

    @Test
    void resolvedRequest_success() {

        Asset asset = new Asset();
        asset.setStatus(AssetStatus.IN_SERVICE);

        ServiceRequest request = new ServiceRequest();
        request.setId(1L);
        request.setAsset(asset);

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(assetService.updateAsset(asset)).thenReturn(asset);

        serviceRequestService.resolvedRequest(1L);

        assertEquals(ServiceStatus.RESOLVED, request.getStatus());
        assertEquals(AssetStatus.ALLOCATED, asset.getStatus());
        verify(serviceRequestRepository).save(request);
    }

    @Test
    void resolvedRequest_notFound() {

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceRequestService.resolvedRequest(1L));
    }

    @Test
    void getRequestByUsername_success() {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        Manager manager = new Manager();
        manager.setId(2L);

        Asset asset = new Asset();
        asset.setId(10L);
        asset.setAssetName("Laptop");

        ServiceRequest request = new ServiceRequest();
        request.setId(1L);
        request.setAsset(asset);
        request.setEmployee(employee);
        request.setManager(manager);
        request.setStatus(ServiceStatus.OPEN);

        Page<ServiceRequest> page = new PageImpl<>(List.of(request));

        when(serviceRequestRepository.getServiceRequestByUser(eq("emp"), any(Pageable.class)))
                .thenReturn(page);

        List<?> result = serviceRequestService.getRequestByUsername("emp", 0, 5);

        assertEquals(1, result.size());
    }
}