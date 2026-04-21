//package com.asset.demo.service;
//
//import com.asset.demo.enums.AuditStatus;
//import com.asset.demo.model.*;
//import com.asset.demo.repository.AssetAuditResultRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.springframework.data.domain.*;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
//class AssetAuditResultServiceTest {
//
//    @Mock
//    private AssetAuditResultRepository assetAuditResultRepository;
//
//    @InjectMocks
//    private AssetAuditResultService assetAuditResultService;
//
//    @Test
//    void saveAll_success() {
//
//        AssetAllocation allocation1 = new AssetAllocation();
//        allocation1.setId(1L);
//
//        AssetAllocation allocation2 = new AssetAllocation();
//        allocation2.setId(2L);
//
//        AssetAudit audit = new AssetAudit();
//        audit.setId(10L);
//
//        assetAuditResultService.saveAll(List.of(allocation1, allocation2), audit);
//
//        verify(assetAuditResultRepository, times(2))
//                .save(any(AssetAuditResult.class));
//    }
//
//    @Test
//    void getByAuditId_success() {
//
//        AssetAuditResult result = new AssetAuditResult();
//        result.setId(1L);
//
//        Page<AssetAuditResult> page = new PageImpl<>(List.of(result));
//
//        when(assetAuditResultRepository.getByAuditId(eq(10L), any(Pageable.class)))
//                .thenReturn(page);
//
//        Page<AssetAuditResult> response =
//                assetAuditResultService.getByAuditId(10L, PageRequest.of(0, 5));
//
//        assertEquals(1, response.getTotalElements());
//    }
//}