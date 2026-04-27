package com.asset.demo.service;

import com.asset.demo.dto.AssetRequestPageResDto;
import com.asset.demo.dto.AssetRequestReqDto;
import com.asset.demo.enums.RequestStatus;
import com.asset.demo.exceptions.DuplicateRequestException;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.*;
import com.asset.demo.repository.AssetRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AssetRequestServiceTest {

    @Mock private AssetRequestRepository assetRequestRepository;
    @Mock private AssetService assetService;
    @Mock private EmployeeService employeeService;

    @InjectMocks
    private AssetRequestService assetRequestService;

    // =========================
    // Helper (VERY IMPORTANT)
    // =========================
    private AssetRequest createMockRequest() {
        AssetRequest request = new AssetRequest();
        request.setId(1L);

        Employee emp = new Employee();
        emp.setId(1L);
        emp.setFirstName("John");
        emp.setLastName("Doe");

        Asset asset = new Asset();
        asset.setId(10L);
        asset.setAssetName("Laptop");

        request.setEmployee(emp);
        request.setAsset(asset);
        request.setStatus(RequestStatus.PENDING);

        return request;
    }

    // =========================
    // requestAsset()
    // =========================

    @Test
    void requestAsset_success() {

        Employee emp = new Employee();
        emp.setId(1L);

        Asset asset = new Asset();
        asset.setId(10L);

        when(employeeService.getEmployeeByUsername("user")).thenReturn(emp);
        when(assetRequestRepository.findAllByUser("user")).thenReturn(List.of());
        when(assetService.getAssetByGivenId(10L)).thenReturn(asset);

        assetRequestService.requestAsset(new AssetRequestReqDto("Need"), 10L, "user");

        verify(assetRequestRepository).save(any(AssetRequest.class));
    }

    @Test
    void requestAsset_duplicate() {

        Employee emp = new Employee();
        emp.setId(1L);

        Asset asset = new Asset();
        asset.setId(10L);

        AssetRequest existing = new AssetRequest();
        existing.setAsset(asset);
        existing.setStatus(RequestStatus.PENDING);

        when(employeeService.getEmployeeByUsername("user")).thenReturn(emp);
        when(assetRequestRepository.findAllByUser("user"))
                .thenReturn(List.of(existing));

        assertThrows(DuplicateRequestException.class,
                () -> assetRequestService.requestAsset(
                        new AssetRequestReqDto("dup"), 10L, "user"));
    }

    // =========================
    // getAllAssetRequests()
    // =========================

    @Test
    void getAllAssetRequests_success() {

        Page<AssetRequest> page =
                new PageImpl<>(List.of(createMockRequest()));

        when(assetRequestRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        assertEquals(1,
                assetRequestService.getAllAssetRequests(0, 5).size());
    }

    // =========================
    // getAssetRequestById()
    // =========================

    @Test
    void getById_success() {

        AssetRequest req = createMockRequest();

        when(assetRequestRepository.findById(1L))
                .thenReturn(Optional.of(req));

        assertEquals(1L,
                assetRequestService.getAssetRequestById(1L).getId());
    }

    @Test
    void getById_notFound() {

        when(assetRequestRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> assetRequestService.getAssetRequestById(1L));
    }

    // =========================
    // updateAssetRequestStatus()
    // =========================

    @Test
    void updateStatus_success() {

        AssetRequest req = createMockRequest();

        assetRequestService.updateAssetRequestStatus(req);

        verify(assetRequestRepository).save(req);
    }

    // =========================
    // getRequestByUser()
    // =========================

    @Test
    void getRequestByUser_success() {

        Page<AssetRequest> page =
                new PageImpl<>(List.of(createMockRequest()));

        when(assetRequestRepository.getByUsername(eq("user"), any(Pageable.class)))
                .thenReturn(page);

        AssetRequestPageResDto result =
                assetRequestService.getRequestByUser("user", 0, 5);

        assertEquals(1, result.list().size());
    }

    // =========================
    // getRequestByStatus()
    // =========================

    @Test
    void getRequestByStatus_success() {

        Page<AssetRequest> page =
                new PageImpl<>(List.of(createMockRequest()));

        when(assetRequestRepository.getByStatus(
                eq(RequestStatus.PENDING), any(Pageable.class)))
                .thenReturn(page);

        assertEquals(1,
                assetRequestService.getRequestByStatus("PENDING", 0, 5).size());
    }

    // =========================
    // count()
    // =========================

    @Test
    void getCount_success() {
        when(assetRequestRepository.count()).thenReturn(3L);
        assertEquals(3, assetRequestService.getCount());
    }

    @Test
    void getCountByUser_success() {
        when(assetRequestRepository.getCountByUser("user")).thenReturn(2L);
        assertEquals(2, assetRequestService.getCountByUser("user"));
    }

    // =========================
    // deleteAssetRequest()
    // =========================

    @Test
    void delete_success() {

        AssetRequest req = createMockRequest();

        when(assetRequestRepository.findById(1L))
                .thenReturn(Optional.of(req));

        assetRequestService.deleteAssetRequest(1L);

        verify(assetRequestRepository).deleteById(1L);
    }

    @Test
    void delete_notFound() {

        when(assetRequestRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> assetRequestService.deleteAssetRequest(1L));
    }

    @Test
    void delete_notPending() {

        AssetRequest req = createMockRequest();
        req.setStatus(RequestStatus.APPROVED);

        when(assetRequestRepository.findById(1L))
                .thenReturn(Optional.of(req));

        assertThrows(ResourceNotFoundException.class,
                () -> assetRequestService.deleteAssetRequest(1L));
    }

    // =========================
    // getRequestByUserStatus()
    // =========================

    @Test
    void getRequestByUserStatus_success() {

        Page<AssetRequest> page =
                new PageImpl<>(List.of(createMockRequest()));

        when(assetRequestRepository.getByUserStatus(
                eq(RequestStatus.PENDING), eq("user"), any(Pageable.class)))
                .thenReturn(page);

        AssetRequestPageResDto result =
                assetRequestService.getRequestByUserStatus("PENDING", 0, 5, "user");

        assertEquals(1, result.list().size());
    }
}