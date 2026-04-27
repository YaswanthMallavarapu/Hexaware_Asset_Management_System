package com.asset.demo.service;

import com.asset.demo.dto.ServiceRequestPageResDto;
import com.asset.demo.dto.ServiceRequestReqDto;
import com.asset.demo.dto.ServiceRequestResponseDto;
import com.asset.demo.enums.AllocationStatus;
import com.asset.demo.enums.AssetStatus;
import com.asset.demo.enums.ServiceStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.*;
import com.asset.demo.repository.AdminRepository;
import com.asset.demo.repository.AssetAllocationRepository;
import com.asset.demo.repository.ServiceRequestRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceRequestServiceTest {

    @InjectMocks
    private ServiceRequestService serviceRequestService;

    @Mock private ServiceRequestRepository serviceRequestRepository;
    @Mock private UserService userService;
    @Mock private AssetService assetService;
    @Mock private AssetAllocationService assetAllocationService;
    @Mock private EmployeeService employeeService;
    @Mock private ManagerService managerService;
    @Mock private AdminRepository adminRepository;
    @Mock private AssetAllocationRepository assetAllocationRepository;

    private ServiceRequest createMockServiceRequest() {
        Asset asset = new Asset();
        asset.setId(10L);
        asset.setAssetName("Laptop");
        asset.setStatus(AssetStatus.IN_SERVICE);

        Employee employee = new Employee();
        employee.setId(20L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        User user = new User();
        user.setUsername("john");
        employee.setUser(user);

        ServiceRequest sr = new ServiceRequest();
        sr.setId(1L);
        sr.setAsset(asset);
        sr.setEmployee(employee);
        sr.setDescription("Issue");
        sr.setStatus(ServiceStatus.OPEN);

        return sr;
    }

    @Test
    void requestService_success() {
        ServiceRequestReqDto dto = new ServiceRequestReqDto("Issue");

        Employee employee = new Employee();
        Asset asset = new Asset();
        AssetAllocation allocation = new AssetAllocation();

        allocation.setAsset(asset);
        allocation.setEmployee(employee);
        allocation.setStatus(AllocationStatus.ALLOCATED);

        when(employeeService.getEmployeeByUsername("user")).thenReturn(employee);
        when(assetAllocationService.getAssetAllocationById(1L)).thenReturn(allocation);
        when(assetService.updateAsset(any(Asset.class))).thenAnswer(i -> i.getArgument(0));

        serviceRequestService.requestService(dto, "user", 1L);

        verify(assetAllocationRepository).save(allocation);
        verify(serviceRequestRepository).save(any(ServiceRequest.class));
    }

    @Test
    void requestService_invalidUser() {
        ServiceRequestReqDto dto = new ServiceRequestReqDto("Issue");

        Employee employee1 = new Employee();
        Employee employee2 = new Employee();

        AssetAllocation allocation = new AssetAllocation();
        allocation.setEmployee(employee2);
        allocation.setStatus(AllocationStatus.ALLOCATED);

        when(employeeService.getEmployeeByUsername("user")).thenReturn(employee1);
        when(assetAllocationService.getAssetAllocationById(1L)).thenReturn(allocation);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> serviceRequestService.requestService(dto, "user", 1L));
    }

    @Test
    void requestService_invalidAllocationStatus() {
        ServiceRequestReqDto dto = new ServiceRequestReqDto("Issue");

        Employee employee = new Employee();
        AssetAllocation allocation = new AssetAllocation();
        allocation.setEmployee(employee);
        allocation.setStatus(AllocationStatus.RETURNED);

        when(employeeService.getEmployeeByUsername("user")).thenReturn(employee);
        when(assetAllocationService.getAssetAllocationById(1L)).thenReturn(allocation);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> serviceRequestService.requestService(dto, "user", 1L));
    }

    @Test
    void acceptRequest_success() {
        ServiceRequest sr = createMockServiceRequest();
        Manager manager = new Manager();

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(sr));
        when(managerService.getManagerByUsername("admin")).thenReturn(manager);
        when(assetService.updateAsset(any(Asset.class))).thenAnswer(i -> i.getArgument(0));

        serviceRequestService.acceptRequest("admin", 1L);

        Assertions.assertEquals(ServiceStatus.IN_PROGRESS, sr.getStatus());
        verify(serviceRequestRepository).save(sr);
    }

    @Test
    void acceptRequest_notFound() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> serviceRequestService.acceptRequest("admin", 1L));
    }

    @Test
    void rejectRequest_success() {
        ServiceRequest sr = createMockServiceRequest();
        Manager manager = new Manager();

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(sr));
        when(managerService.getManagerByUsername("admin")).thenReturn(manager);

        serviceRequestService.rejectRequest(1L, "admin");

        Assertions.assertEquals(ServiceStatus.REJECTED, sr.getStatus());
        verify(serviceRequestRepository).save(sr);
    }

    @Test
    void rejectRequest_notFound() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> serviceRequestService.rejectRequest(1L, "admin"));
    }

    @Test
    void resolveRequest_success() {
        Asset asset = new Asset();
        asset.setId(10L);
        asset.setStatus(AssetStatus.IN_SERVICE);

        Employee employee = new Employee();
        employee.setId(1L);

        ServiceRequest sr = new ServiceRequest();
        sr.setId(1L);
        sr.setAsset(asset);
        sr.setEmployee(employee);
        sr.setStatus(ServiceStatus.OPEN);

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(sr));
        when(assetService.updateAsset(any(Asset.class))).thenAnswer(i -> i.getArgument(0));

        serviceRequestService.resolvedRequest(1L);

        Assertions.assertEquals(ServiceStatus.RESOLVED, sr.getStatus());
        Assertions.assertEquals(AssetStatus.ALLOCATED, sr.getAsset().getStatus());

        verify(serviceRequestRepository).save(sr);
    }

    @Test
    void resolveRequest_notFound() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> serviceRequestService.resolvedRequest(1L));
    }

    @Test
    void getAll_success() {
        ServiceRequest sr = createMockServiceRequest();
        Page<ServiceRequest> page = new PageImpl<>(List.of(sr));

        when(serviceRequestRepository.findAll(any(Pageable.class))).thenReturn(page);

        ServiceRequestResponseDto result = serviceRequestService.getAll(0, 5);

        Assertions.assertEquals(1, result.list().size());
        Assertions.assertEquals(1, result.totalRecords());
    }

    @Test
    void getByStatus_success() {
        ServiceRequest sr = createMockServiceRequest();
        Page<ServiceRequest> page = new PageImpl<>(List.of(sr));

        when(serviceRequestRepository.getByStatus(eq(ServiceStatus.OPEN), any(Pageable.class)))
                .thenReturn(page);

        ServiceRequestResponseDto result =
                serviceRequestService.getAllByStatus(0, 5, "OPEN");

        Assertions.assertEquals(1, result.list().size());
    }

    @Test
    void getByStatus_invalidStatus() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> serviceRequestService.getAllByStatus(0, 5, "INVALID"));
    }

    @Test
    void getByUser_success() {
        ServiceRequest sr = createMockServiceRequest();
        Page<ServiceRequest> page = new PageImpl<>(List.of(sr));

        when(serviceRequestRepository.getServiceRequestByUser(eq("john"), any(Pageable.class)))
                .thenReturn(page);

        ServiceRequestResponseDto result =
                serviceRequestService.getRequestByUsername("john", 0, 5);

        Assertions.assertEquals(1, result.list().size());
    }

    @Test
    void getByUserStatus_success() {
        ServiceRequest sr = createMockServiceRequest();
        Page<ServiceRequest> page = new PageImpl<>(List.of(sr));

        when(serviceRequestRepository.getAllByUserStatus(eq(ServiceStatus.OPEN), eq("john"), any(Pageable.class)))
                .thenReturn(page);

        ServiceRequestPageResDto result =
                serviceRequestService.getByUserStatus("OPEN", 0, 5, "john");

        Assertions.assertEquals(1, result.list().size());
        Assertions.assertEquals(1, result.totalElements());
    }

    @Test
    void getByUserStatus_invalidStatus() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> serviceRequestService.getByUserStatus("INVALID", 0, 5, "john"));
    }

    @Test
    void deleteRequest_success() {
        ServiceRequest sr = createMockServiceRequest();

        AssetAllocation allocation = new AssetAllocation();
        sr.setAssetAllocation(allocation);

        Principal principal = () -> "john";

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(sr));

        serviceRequestService.deleteRequest(1L, principal);

        verify(serviceRequestRepository).deleteById(1L);
        verify(assetAllocationRepository).save(allocation);
    }

    @Test
    void deleteRequest_invalidUser() {
        ServiceRequest sr = createMockServiceRequest();
        Principal principal = () -> "wrongUser";

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(sr));

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> serviceRequestService.deleteRequest(1L, principal));
    }

    @Test
    void deleteRequest_notFound() {
        Principal principal = () -> "john";

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> serviceRequestService.deleteRequest(1L, principal));
    }

    @Test
    void getCount_success() {
        when(adminRepository.count()).thenReturn(5L);

        long count = serviceRequestService.getCount();

        Assertions.assertEquals(5L, count);
    }

    @Test
    void getCountByUser_success() {
        when(serviceRequestRepository.getCountByUser("john")).thenReturn(3L);

        long count = serviceRequestService.getCountByUser("john");

        Assertions.assertEquals(3L, count);
    }
}