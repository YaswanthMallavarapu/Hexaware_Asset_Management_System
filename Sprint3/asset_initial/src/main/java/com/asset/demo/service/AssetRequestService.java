package com.asset.demo.service;

import com.asset.demo.dto.AssetRequestReqDto;
import com.asset.demo.dto.AssetRequestResDto;
import com.asset.demo.enums.RequestStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.mapper.AssetRequestMapper;
import com.asset.demo.model.Asset;
import com.asset.demo.model.AssetRequest;
import com.asset.demo.model.Employee;
import com.asset.demo.model.User;
import com.asset.demo.repository.AssetRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AssetRequestService {
    private final AssetRequestRepository assetRequestRepository;
    private final AssetService assetService;
    private final EmployeeService employeeService;

    public void requestAsset(AssetRequestReqDto assetRequestReqdto, long assetId, String username) {
        //check for employeeId
        Employee employee=employeeService.getEmployeeByUsername(username);
        //check for assetId
        Asset asset=assetService.getAssetByGivenId(assetId);
        //map assetRequestDto to entity
        AssetRequest assetRequest= AssetRequestMapper.mapToEntity(assetRequestReqdto);
        //add employee and asset to assetRequest
        assetRequest.setEmployee(employee);
        assetRequest.setAsset(asset);
        //save assetRequest
        assetRequestRepository.save(assetRequest);
    }

    public List<AssetRequestResDto> getAllAssetRequests(int page, int size) {
        Pageable pageable=PageRequest.of(page,size);
        Page<AssetRequest> list=assetRequestRepository.findAll(pageable);
        return list
                .toList()
                .stream()
                .map(AssetRequestMapper::mapToDto)
                .toList();
    }

    public AssetRequest getAssetRequestById(long assetRequestId) {
        return assetRequestRepository.findById(assetRequestId)
                .orElseThrow(()->new ResourceNotFoundException("Invalid asset request id."));
    }

    public void updateAssetRequestStatus(AssetRequest assetRequest) {
        assetRequestRepository.save(assetRequest);
    }

    public List<AssetRequestResDto> getRequestByUser(String name, int page, int size) {

        Pageable pageable=PageRequest.of(page,size);

        Page<AssetRequest>pageAssetRequest=assetRequestRepository.getByUsername(name,pageable);
        return pageAssetRequest
                .toList()
                .stream()
                .map(AssetRequestMapper::mapToDto)
                .toList();
    }

    public List<AssetRequestResDto> getRequestByStatus(String status, int page, int size) {
        Pageable pageable=PageRequest.of(page,size);

        RequestStatus requestStatus=RequestStatus.valueOf(status);
        Page<AssetRequest>pageAssetRequest=assetRequestRepository.getByStatus(requestStatus,pageable);
        return pageAssetRequest
                .toList()
                .stream()
                .map(AssetRequestMapper::mapToDto)
                .toList();
    }

    public long getCount() {
        return assetRequestRepository.count();
    }
}
