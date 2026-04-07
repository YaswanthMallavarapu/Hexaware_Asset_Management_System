package com.asset.demo.service;

import com.asset.demo.dto.AssetRequestReqDto;
import com.asset.demo.dto.AssetRequestResDto;
import com.asset.demo.enums.RequestStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.Asset;
import com.asset.demo.model.AssetRequest;
import com.asset.demo.model.Employee;
import com.asset.demo.model.User;
import com.asset.demo.repository.AssetRequestRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssetRequestServiceTest {

    @InjectMocks
    private AssetRequestService assetRequestService;

    @Mock
    private AssetRequestRepository assetRequestRepository;

    @Mock
    private AssetService assetService;

    @Mock
    private EmployeeService employeeService;

    @Test
    public void requestAssetTest(){

        AssetRequestReqDto dto = new AssetRequestReqDto("Need for project");

        User user = new User();
        user.setUsername("john123");

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setUser(user);

        Asset asset = new Asset();
        asset.setId(10L);
        asset.setAssetName("Laptop");

        when(employeeService.getEmployeeByUsername("john123"))
                .thenReturn(employee);

        when(assetService.getAssetByGivenId(10L))
                .thenReturn(asset);

        assetRequestService.requestAsset(dto,10L,"john123");

        ArgumentCaptor<AssetRequest> captor =
                ArgumentCaptor.forClass(AssetRequest.class);

        verify(assetRequestRepository).save(captor.capture());

        AssetRequest savedRequest = captor.getValue();

        Assertions.assertEquals(employee,savedRequest.getEmployee());
        Assertions.assertEquals(asset,savedRequest.getAsset());
        Assertions.assertEquals("Need for project",
                savedRequest.getRemarks());
    }

    @Test
    public void getAllAssetRequestsTest(){

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        Asset asset = new Asset();
        asset.setId(10L);
        asset.setAssetName("Laptop");

        AssetRequest request = new AssetRequest();
        request.setId(100L);
        request.setEmployee(employee);
        request.setAsset(asset);
        request.setRequestDate(Instant.now());
        request.setStatus(RequestStatus.PENDING);

        List<AssetRequest> list = List.of(request);

        Page<AssetRequest> pageRequest =
                new PageImpl<>(list);

        Pageable pageable = PageRequest.of(0,1);

        when(assetRequestRepository.findAll(pageable))
                .thenReturn(pageRequest);

        List<AssetRequestResDto> result =
                assetRequestService.getAllAssetRequests(0,1);

        Assertions.assertEquals(1,result.size());
        Assertions.assertEquals(1L,result.get(0).employeeId());
        Assertions.assertEquals(10L,result.get(0).assetId());
    }

    @Test
    public void getAssetRequestByIdTestWhenExist(){

        AssetRequest request = new AssetRequest();
        request.setId(1L);

        when(assetRequestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        Assertions.assertEquals(request,
                assetRequestService.getAssetRequestById(1L));
    }

    @Test
    public void getAssetRequestByIdTestWhenNotExist(){

        when(assetRequestRepository.findById(50L))
                .thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(
                ResourceNotFoundException.class,
                ()->assetRequestService.getAssetRequestById(50L)
        );

        Assertions.assertEquals("Invalid asset request id.",
                e.getMessage());
    }

    @Test
    public void updateAssetRequestStatusTest(){

        AssetRequest request = new AssetRequest();
        request.setId(1L);

        assetRequestService.updateAssetRequestStatus(request);

        verify(assetRequestRepository,times(1))
                .save(request);
    }

    @Test
    public void getRequestByUserTest(){

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        Asset asset = new Asset();
        asset.setId(10L);
        asset.setAssetName("Laptop");

        AssetRequest request = new AssetRequest();
        request.setId(100L);
        request.setEmployee(employee);
        request.setAsset(asset);
        request.setRequestDate(Instant.now());
        request.setStatus(RequestStatus.PENDING);

        List<AssetRequest> list = List.of(request);

        Page<AssetRequest> pageRequest =
                new PageImpl<>(list);

        Pageable pageable = PageRequest.of(0,1);

        when(assetRequestRepository.getByUsername("john123",pageable))
                .thenReturn(pageRequest);

        List<AssetRequestResDto> result =
                assetRequestService.getRequestByUser("john123",0,1);

        Assertions.assertEquals(1,result.size());
        Assertions.assertEquals(1L,result.get(0).employeeId());
    }

    @Test
    public void getRequestByStatusTest(){

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        Asset asset = new Asset();
        asset.setId(10L);
        asset.setAssetName("Laptop");

        AssetRequest request = new AssetRequest();
        request.setId(100L);
        request.setEmployee(employee);
        request.setAsset(asset);
        request.setRequestDate(Instant.now());
        request.setStatus(RequestStatus.PENDING);

        List<AssetRequest> list = List.of(request);

        Page<AssetRequest> pageRequest =
                new PageImpl<>(list);

        Pageable pageable = PageRequest.of(0,1);

        when(assetRequestRepository
                .getByStatus(RequestStatus.PENDING,pageable))
                .thenReturn(pageRequest);

        List<AssetRequestResDto> result =
                assetRequestService.getRequestByStatus("PENDING",0,1);

        Assertions.assertEquals(1,result.size());
        Assertions.assertEquals(RequestStatus.PENDING,
                result.get(0).status());
    }

}