package com.asset.demo.service;

import com.asset.demo.dto.*;
import com.asset.demo.enums.AuditStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.*;
import com.asset.demo.repository.AssetAuditRepository;
import com.asset.demo.repository.AssetAuditResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AssetAuditServiceTest {

    @Mock
    private AssetAuditRepository auditRepository;

    @Mock
    private AssetAuditResultService auditResultService;

    @Mock
    private AssetAuditResultRepository auditResultRepository;

    @Mock
    private AssetAllocationService allocationService;

    @Mock
    private ManagerService managerService;

    @InjectMocks
    private AssetAuditService service;

    @Test
    void auditAsset_success() {

        AssetAuditResult result = new AssetAuditResult();
        result.setId(1L);

        when(auditResultRepository.findById(1L))
                .thenReturn(Optional.of(result));

        AssetAuditReqDto dto = new AssetAuditReqDto(AuditStatus.VERIFIED);

        service.auditAsset(dto, 1L);

        assertEquals(AuditStatus.VERIFIED, result.getStatus());
        verify(auditResultRepository).save(result);
    }

    @Test
    void auditAsset_fail() {

        when(auditResultRepository.findById(1L))
                .thenReturn(Optional.empty());

        AssetAuditReqDto dto = new AssetAuditReqDto(AuditStatus.REJECTED);

        assertThrows(ResourceNotFoundException.class,
                () -> service.auditAsset(dto, 1L));
    }

    @Test
    void getAllAssetToBeAudited() {

        Manager manager = new Manager();
        manager.setId(5L);

        AssetAllocation allocation = new AssetAllocation();
        allocation.setId(1L);

        when(managerService.getManagerByUsername("manager"))
                .thenReturn(manager);

        when(allocationService.getAllAllocatedAssets())
                .thenReturn(List.of(allocation));

        service.getAllAssetToBeAudited("manager");

        verify(auditRepository).save(any());
        verify(auditResultService).saveAll(anyList(), any());
    }

    @Test
    void getAllAuditDates() {

        AuditDateDto dto = new AuditDateDto(1L, 2L, Instant.now());

        when(auditRepository.getAllAuditDates())
                .thenReturn(List.of(dto));

        List<AuditDateDto> list = service.getAllAuditDates();

        assertEquals(1, list.size());
    }

    @Test
    void getAllAuditResults() {

        Asset asset = new Asset();
        asset.setId(10L);
        asset.setAssetName("Laptop");

        AssetAllocation allocation = new AssetAllocation();
        allocation.setId(20L);
        allocation.setAsset(asset);

        AssetAudit audit = new AssetAudit();
        audit.setId(30L);

        AssetAuditResult result = new AssetAuditResult();
        result.setId(1L);
        result.setAssetAllocation(allocation);
        result.setAudit(audit);
        result.setStatus(AuditStatus.PENDING);

        Page<AssetAuditResult> page =
                new PageImpl<>(List.of(result));

        when(auditResultService.getByAuditId(30L, PageRequest.of(0, 5)))
                .thenReturn(page);

        List<AssetAuditResultResDto> list =
                service.getAllAuditResults(30L, 0, 5);

        assertEquals(1, list.size());
    }

    @Test
    void getAllAssetByStatus() {

        Asset asset = new Asset();
        asset.setId(10L);
        asset.setAssetName("Laptop");

        AssetAllocation allocation = new AssetAllocation();
        allocation.setId(20L);
        allocation.setAsset(asset);

        AssetAudit audit = new AssetAudit();
        audit.setId(30L);

        AssetAuditResult result = new AssetAuditResult();
        result.setId(1L);
        result.setAssetAllocation(allocation);
        result.setAudit(audit);
        result.setStatus(AuditStatus.VERIFIED);

        Page<AssetAuditResult> page =
                new PageImpl<>(List.of(result));

        when(auditResultRepository.getAllByAuditIdAndStatus(
                30L, AuditStatus.VERIFIED, PageRequest.of(0, 5)))
                .thenReturn(page);

        AssetAuditResStatusdto res =
                service.getAllAssetByStatus(0, 5, 30L, "VERIFIED");

        assertEquals(1, res.list().size());
    }

    @Test
    void getCount() {

        when(auditRepository.count()).thenReturn(5L);

        long count = service.getCount();

        assertEquals(5, count);
    }
}