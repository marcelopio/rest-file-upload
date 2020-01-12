package br.com.belfalas.restfileupload.controller;

import br.com.belfalas.restfileupload.dto.FileDTO;
import br.com.belfalas.restfileupload.dto.ImageDTO;
import br.com.belfalas.restfileupload.service.FileStorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileUploadControllerTest {

    @Mock
    private FileStorageService fileStorageService;

    private FileUploadController fileUploadController;

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
        fileUploadController = new FileUploadController(fileStorageService);
    }

    @Test
    void listFiles() {
        FileDTO fileDTO = new FileDTO("test", "filename", "image/png", "Concluído");

        when(fileStorageService.findAllByUser("test")).thenReturn(singletonList(fileDTO));

        List<FileDTO> fileDTOS = fileUploadController.listFiles("test");

        verify(fileStorageService).findAllByUser("test");

        assertEquals(1, fileDTOS.size());

        FileDTO dto = fileDTOS.get(0);
        assertEquals("test", dto.getUserId());
        assertEquals("filename", dto.getFilename());
        assertEquals("image/png", dto.getContentType());
        assertEquals("Concluído", dto.getStatus());
    }

    @Test
    void listAllFiles() {
        FileDTO fileDTO = new FileDTO("test", "filename", "image/png", "Concluído");

        when(fileStorageService.findAll("")).thenReturn(singletonList(fileDTO));

        List<FileDTO> fileDTOS = fileUploadController.listAllFiles("");

        verify(fileStorageService).findAll("");

        assertEquals(1, fileDTOS.size());

        FileDTO dto = fileDTOS.get(0);
        assertEquals("test", dto.getUserId());
        assertEquals("filename", dto.getFilename());
        assertEquals("image/png", dto.getContentType());
        assertEquals("Concluído", dto.getStatus());
    }

    @Test
    void getFile() throws IOException {
        ImageDTO imageDTO = new ImageDTO("image/png", new byte[0]);

        when(fileStorageService.find("test", "filename")).thenReturn(imageDTO);

        ResponseEntity<byte[]> file = fileUploadController.getFile("test", "filename");

        verify(fileStorageService).find("test", "filename");

        Assertions.assertEquals(0, file.getBody().length);
    }

    @Test
    void uploadFile() throws IOException {
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        when(multipartFileMock.getOriginalFilename()).thenReturn("filename");

        ResponseEntity<String> responseEntity = fileUploadController.uploadFile("test", multipartFileMock);

        verify(fileStorageService).save("test", multipartFileMock);

        assertEquals("File filename uploaded successfully", responseEntity.getBody());
    }

    @Test
    void deleteFile() {
        ResponseEntity<String> responseEntity = fileUploadController.deleteFile("test", "filename");

        verify(fileStorageService).delete("test", "filename");

        assertEquals("File filename deleted successfully", responseEntity.getBody());
    }
}