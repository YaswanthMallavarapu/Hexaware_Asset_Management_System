package com.asset.demo.service;

import com.asset.demo.dto.ManagerDocumentResDto;
import com.asset.demo.exceptions.InvalidFileFormatException;
import com.asset.demo.mapper.ManagerDocumentMapper;
import com.asset.demo.model.Manager;
import com.asset.demo.model.ManagerDocument;
import com.asset.demo.repository.ManagerDocumentRepository;
import com.asset.demo.repository.ManagerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ManagerDocumentService {
    private final ManagerDocumentRepository managerDocumentRepository;
    private final ManagerRepository managerRepository;
    private final static String UPLOAD_PATH="C:/Users/harip/Desktop/training/Hexaware_Asset_Management_System/Sprint5/UI/public/uploads/";

    public ManagerDocumentResDto upload(String name, MultipartFile file) throws IOException {
        Manager manager=managerRepository.getManagerByUsername(name);

        File directory=new File(UPLOAD_PATH);
        String fileName= file.getOriginalFilename();
        String extension= Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        if(!extension.equalsIgnoreCase("PNG") &&
                !extension.equalsIgnoreCase("JPG") &&
                !extension.equalsIgnoreCase("JPEG"))
            throw new InvalidFileFormatException("File Extension not supported.");
        Path path= Paths.get(UPLOAD_PATH+fileName);
        Files.write(path,file.getBytes());

        ManagerDocument managerDocument=new ManagerDocument();
        managerDocument.setManager(manager);
        managerDocument.setProfileImage(fileName);



    managerDocument=managerDocumentRepository.save(managerDocument);
    return ManagerDocumentMapper.mapToDto(managerDocument);

    }

    public String getProfile(String name) {
        Manager manager=managerRepository.getManagerByUsername(name);
        return managerDocumentRepository.getByManagerId(manager.getId());
    }
}
