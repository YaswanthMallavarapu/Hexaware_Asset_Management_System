package com.asset.demo.service;

import com.asset.demo.dto.ManagerDocumentResDto;
import com.asset.demo.exceptions.InvalidFileFormatException;
import com.asset.demo.model.Manager;
import com.asset.demo.model.ManagerDocument;
import com.asset.demo.repository.ManagerDocumentRepository;
import com.asset.demo.repository.ManagerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagerDocumentServiceTest {

    @InjectMocks
    private ManagerDocumentService managerDocumentService;

    @Mock
    private ManagerDocumentRepository managerDocumentRepository;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private MultipartFile file;

    @Test
    public void upload_success_jpg() throws IOException {

        Manager manager = new Manager();
        manager.setId(1L);
        manager.setFirstName("John");
        manager.setLastName("Doe");

        when(managerRepository.getManagerByUsername("john")).thenReturn(manager);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getBytes()).thenReturn("data".getBytes());

        ManagerDocument saved = new ManagerDocument();
        saved.setId(10L);
        saved.setManager(manager);
        saved.setProfileImage("test.jpg");

        when(managerDocumentRepository.save(any())).thenReturn(saved);

        ManagerDocumentResDto res = managerDocumentService.upload("john", file);

        Assertions.assertEquals(10L, res.id());
        Assertions.assertEquals("test.jpg", res.profileImage());
    }

    @Test
    public void upload_success_uppercase_png() throws IOException {

        Manager manager = new Manager();
        manager.setId(2L);

        when(managerRepository.getManagerByUsername("abc")).thenReturn(manager);
        when(file.getOriginalFilename()).thenReturn("img.PNG");
        when(file.getBytes()).thenReturn("data".getBytes());

        ManagerDocument saved = new ManagerDocument();
        saved.setId(20L);
        saved.setManager(manager);
        saved.setProfileImage("img.PNG");

        when(managerDocumentRepository.save(any())).thenReturn(saved);

        ManagerDocumentResDto res = managerDocumentService.upload("abc", file);

        Assertions.assertEquals("img.PNG", res.profileImage());
    }

    @Test
    public void upload_invalid_extension_txt() {

        Manager manager = new Manager();
        manager.setId(1L);

        when(managerRepository.getManagerByUsername("john")).thenReturn(manager);
        when(file.getOriginalFilename()).thenReturn("file.txt");

        Exception e = Assertions.assertThrows(
                InvalidFileFormatException.class,
                () -> managerDocumentService.upload("john", file)
        );

        Assertions.assertEquals("File Extension not supported.", e.getMessage());
    }

    @Test
    public void upload_invalid_extension_pdf() {

        Manager manager = new Manager();
        manager.setId(1L);

        when(managerRepository.getManagerByUsername("john")).thenReturn(manager);
        when(file.getOriginalFilename()).thenReturn("file.pdf");

        Assertions.assertThrows(
                InvalidFileFormatException.class,
                () -> managerDocumentService.upload("john", file)
        );
    }

    @Test
    public void upload_null_filename_branch() {

        Manager manager = new Manager();
        manager.setId(1L);

        when(managerRepository.getManagerByUsername("john")).thenReturn(manager);
        when(file.getOriginalFilename()).thenReturn(null);

        Assertions.assertThrows(
                NullPointerException.class,
                () -> managerDocumentService.upload("john", file)
        );
    }

    @Test
    public void getProfile_success() {

        Manager manager = new Manager();
        manager.setId(5L);

        when(managerRepository.getManagerByUsername("john")).thenReturn(manager);
        when(managerDocumentRepository.getByManagerId(5L)).thenReturn("profile.jpg");

        String result = managerDocumentService.getProfile("john");

        Assertions.assertEquals("profile.jpg", result);
    }

    @Test
    public void getProfile_null_result() {

        Manager manager = new Manager();
        manager.setId(6L);

        when(managerRepository.getManagerByUsername("john")).thenReturn(manager);
        when(managerDocumentRepository.getByManagerId(6L)).thenReturn(null);

        String result = managerDocumentService.getProfile("john");

        Assertions.assertNull(result);
    }
}