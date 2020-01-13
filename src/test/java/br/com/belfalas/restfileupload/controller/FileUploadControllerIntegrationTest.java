package br.com.belfalas.restfileupload.controller;

import br.com.belfalas.restfileupload.dto.FileDTO;
import br.com.belfalas.restfileupload.dto.ImageDTO;
import br.com.belfalas.restfileupload.exception.FailedToReadFileException;
import br.com.belfalas.restfileupload.exception.FileNotFoundInStorageException;
import br.com.belfalas.restfileupload.service.FileStorageService;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileUploadControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private FileStorageService fileStorageService;

    @LocalServerPort
    private int port;

    @Test
    void listFiles() {
        FileDTO fileDTO = new FileDTO("test", "filename", "image/png", "Concluído");
        given(fileStorageService.findAllByUser("test")).willReturn(Collections.singletonList(fileDTO));

        ParameterizedTypeReference<List<FileDTO>> parameterizedTypeReference = new ParameterizedTypeReference<List<FileDTO>>() {
        };

        ResponseEntity<List<FileDTO>> listResponseEntity = testRestTemplate.exchange("/fileUpload/v1/test", HttpMethod.GET, null, parameterizedTypeReference);

        assertThat(listResponseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(listResponseEntity.getBody()).hasSize(1);
        assertThat(listResponseEntity.getBody()).containsExactly(fileDTO);
    }

    @Test
    void listAllFiles() {
        FileDTO fileDTO = new FileDTO("test", "filename", "image/png", "Concluído");
        given(fileStorageService.findAll(null)).willReturn(Collections.singletonList(fileDTO));

        ParameterizedTypeReference<List<FileDTO>> parameterizedTypeReference = new ParameterizedTypeReference<List<FileDTO>>() {
        };

        ResponseEntity<List<FileDTO>> listResponseEntity = testRestTemplate.exchange("/fileUpload/v1/listAll", HttpMethod.GET, null, parameterizedTypeReference);

        assertThat(listResponseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(listResponseEntity.getBody()).hasSize(1);
        assertThat(listResponseEntity.getBody()).containsExactly(fileDTO);
    }

    @Test
    void listAllFilesFiltering() {
        FileDTO fileDTO = new FileDTO("test", "filename", "image/png", "Concluído");
        given(fileStorageService.findAll("test")).willReturn(Collections.singletonList(fileDTO));

        ParameterizedTypeReference<List<FileDTO>> parameterizedTypeReference = new ParameterizedTypeReference<List<FileDTO>>() {
        };

        ResponseEntity<List<FileDTO>> listResponseEntity = testRestTemplate.exchange("/fileUpload/v1/listAll?filename=test", HttpMethod.GET, null, parameterizedTypeReference);

        assertThat(listResponseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(listResponseEntity.getBody()).hasSize(1);
        assertThat(listResponseEntity.getBody()).containsExactly(fileDTO);
    }

    @Test
    void getFile() throws FileNotFoundInStorageException, FailedToReadFileException, IOException {
        InputStream resourceAsStream = FileUploadControllerIntegrationTest.class.getResourceAsStream("/testes.png");

        byte[] imageBytes = IOUtils.toByteArray(resourceAsStream);
        ImageDTO imageDTO = new ImageDTO("image/png", imageBytes);
        given(fileStorageService.find("test", "filename")).willReturn(imageDTO);

        ResponseEntity<byte[]> responseEntity = testRestTemplate.getForEntity("/fileUpload/v1/test/filename", byte[].class);

        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).hasSameSizeAs(imageBytes);
    }

    @Test
    void getFileNotFound() throws FileNotFoundInStorageException, FailedToReadFileException, IOException {

        given(fileStorageService.find("test", "filename")).willThrow(FileNotFoundInStorageException.class);

        ResponseEntity<byte[]> responseEntity = testRestTemplate.getForEntity("/fileUpload/v1/test/filename", byte[].class);

        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getFileFailedToRead() throws FileNotFoundInStorageException, FailedToReadFileException, IOException {

        given(fileStorageService.find("test", "filename")).willThrow(FailedToReadFileException.class);

        ResponseEntity<byte[]> responseEntity = testRestTemplate.getForEntity("/fileUpload/v1/test/filename", byte[].class);

        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void uploadFile() throws FailedToReadFileException {
        ClassPathResource resource = new ClassPathResource("/testes.png", getClass());

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("file", resource);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/fileUpload/v1/test", map, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("File testes.png uploaded successfully");

        then(fileStorageService).should().save(eq("test"), any(MultipartFile.class));
    }

    @Test
    void uploadFileGt5Mb() {
        ClassPathResource resource = new ClassPathResource("/teste10mb.jpg", getClass());

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("file", resource);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/fileUpload/v1/test", map, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void uploadFileNotImg() {
        ClassPathResource resource = new ClassPathResource("/teste.txt", getClass());

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("file", resource);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/fileUpload/v1/test", map, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void uploadFileThrowsFailedToReadFileException() throws FailedToReadFileException {
        BDDMockito.doThrow(FailedToReadFileException.class).when(fileStorageService).save(eq("test"), any(MultipartFile.class));
        ClassPathResource resource = new ClassPathResource("/testes.png", getClass());

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("file", resource);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/fileUpload/v1/test", map, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void deleteFile() {
        testRestTemplate.delete("/fileUpload/v1/test/filename");

        then(fileStorageService).should().delete("test", "filename");
    }

}