package com.asset.demo.service;

import com.asset.demo.dto.AssetAuditReqDto;
import com.asset.demo.enums.AuditStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.*;
import com.asset.demo.repository.AssetAuditRepository;
import com.asset.demo.repository.AssetAuditResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AssetAuditServiceTest {

    @Mock private AssetAuditRepository assetAuditRepository;
    @Mock private AssetAuditResultService assetAuditResultService;
    @Mock private AssetAuditResultRepository assetAuditResultRepository;
    @Mock private AssetAllocationService assetAllocationService;
    @Mock private UserService userService;
    @Mock private AssetService assetService;
    @Mock private ManagerService managerService;

    @InjectMocks
    private AssetAuditService assetAuditService;

    @Test
    void auditAsset_success() {

        AssetAuditResult result = new AssetAuditResult();
        result.setId(1L);

        when(assetAuditResultRepository.findById(1L))
                .thenReturn(Optional.of(result));

        AssetAuditReqDto dto = new AssetAuditReqDto(AuditStatus.VERIFIED);

        assetAuditService.auditAsset(dto, 1L);

        assertEquals(AuditStatus.VERIFIED, result.getStatus());
        verify(assetAuditResultRepository).save(result);
    }

    @Test
    void auditAsset_notFound() {

        when(assetAuditResultRepository.findById(1L))
                .thenReturn(Optional.empty());

        AssetAuditReqDto dto = new AssetAuditReqDto(AuditStatus.REJECTED);

        assertThrows(ResourceNotFoundException.class,
                () -> assetAuditService.auditAsset(dto, 1L));
    }

    @Test
    void getAllAssetToBeAudited_success() {

        Manager manager = new Manager();
        manager.setId(5L);

        AssetAllocation allocation = new AssetAllocation();
        allocation.setId(1L);

        when(managerService.getManagerByUsername("manager"))
                .thenReturn(manager);

        when(assetAllocationService.getAllAllocatedAssets())
                .thenReturn(List.of(allocation));

        assetAuditService.getAllAssetToBeAudited("manager");

        verify(assetAuditRepository).save(any(AssetAudit.class));
        verify(assetAuditResultService)
                .saveAll(anyList(), any(AssetAudit.class));
    }

    @Test
    void getAllAuditDates_success() {

        when(assetAuditRepository.getAllAuditDates())
                .thenReturn(List.of(Instant.now()));

        List<Instant> dates = assetAuditService.getAllAuditDates();

        assertEquals(1, dates.size());
    }

    @Test
    void getAllAuditResults_success() {

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

        Page<AssetAuditResult> page = new PageImpl<>(List.of(result));

        when(assetAuditResultService.getByAuditId(eq(30L), any(Pageable.class)))
                .thenReturn(page);

        List<?> list = assetAuditService.getAllAuditResults(30L, 0, 5);

        assertEquals(1, list.size());
    }
}