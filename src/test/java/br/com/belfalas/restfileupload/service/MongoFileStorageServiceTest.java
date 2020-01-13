package br.com.belfalas.restfileupload.service;

import br.com.belfalas.restfileupload.dto.FileDTO;
import br.com.belfalas.restfileupload.dto.ImageDTO;
import br.com.belfalas.restfileupload.exception.FailedToReadFileException;
import br.com.belfalas.restfileupload.exception.FileNotFoundInStorageException;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Spliterator;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MongoFileStorageServiceTest {

    @Mock
    private GridFsTemplate gridFsTemplate;

    private MongoFileStorageService fileStorageService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
        fileStorageService = new MongoFileStorageService(gridFsTemplate);
    }

    @Test
    void save() throws IOException, FailedToReadFileException {
        InputStream inputStream = mock(InputStream.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        when(multipartFileMock.getContentType()).thenReturn("image/png");
        when(multipartFileMock.getOriginalFilename()).thenReturn("filename");
        when(multipartFileMock.getInputStream()).thenReturn(inputStream);

        fileStorageService.save("test", multipartFileMock);

        verify(gridFsTemplate).store(eq(inputStream),
                        eq("filename"),
                        eq("image/png"),
                        any(DBObject.class));
    }

    @Test
    void saveThrowsException() throws IOException {
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        when(multipartFileMock.getContentType()).thenReturn("image/png");
        when(multipartFileMock.getOriginalFilename()).thenReturn("filename");
        when(multipartFileMock.getInputStream()).thenThrow(IOException.class);

        Assertions.assertThrows(FailedToReadFileException.class, () -> fileStorageService.save("test", multipartFileMock));
    }

    @Test
    void delete() {
        fileStorageService.delete("test", "filename");

        verify(gridFsTemplate).delete(Query.query(Criteria
                .where("metadata.userId").is("test")
                .and("filename").is("filename")));
    }

    @Test
    void find() throws IOException, FileNotFoundInStorageException, FailedToReadFileException {
        InputStream resourceAsStream = MongoFileStorageServiceTest.class.getResourceAsStream("/testes.png");

        GridFSFile gridFSFileMock = mock(GridFSFile.class);
        Document documentMock = mock(Document.class);
        GridFsResource gridFsResourceMock = mock(GridFsResource.class);
        when(gridFsTemplate.findOne(any())).thenReturn(gridFSFileMock);
        when(gridFsTemplate.getResource(gridFSFileMock)).thenReturn(gridFsResourceMock);

        when(gridFSFileMock.getMetadata()).thenReturn(documentMock);
        when(documentMock.getString("contentType")).thenReturn("image/png");
        when(gridFsResourceMock.getInputStream()).thenReturn(resourceAsStream);


        ImageDTO imageDTO = fileStorageService.find("test", "filename");

        verify(gridFsTemplate).findOne(any());
        verify(gridFsTemplate).getResource(gridFSFileMock);

        byte[] imageBytes = IOUtils.toByteArray(MongoFileStorageServiceTest.class.getResourceAsStream("/testes.png"));

        Assertions.assertEquals("image/png", imageDTO.getContentType());
        Assertions.assertArrayEquals(imageBytes, imageDTO.getImage());
    }

    @Test
    void findNotFound(){
        when(gridFsTemplate.findOne(any())).thenReturn(null);

        Assertions.assertThrows(FileNotFoundInStorageException.class, () -> fileStorageService.find("test", "filename"));
    }

    @Test
    void findFailedToRead() throws IOException {
        InputStream resourceAsStream = MongoFileStorageServiceTest.class.getResourceAsStream("/testes.png");

        GridFSFile gridFSFileMock = mock(GridFSFile.class);
        Document documentMock = mock(Document.class);
        GridFsResource gridFsResourceMock = mock(GridFsResource.class);
        when(gridFsTemplate.findOne(any())).thenReturn(gridFSFileMock);
        when(gridFsTemplate.getResource(gridFSFileMock)).thenReturn(gridFsResourceMock);

        when(gridFSFileMock.getMetadata()).thenReturn(documentMock);
        when(documentMock.getString("contentType")).thenReturn("image/png");
        when(gridFsResourceMock.getInputStream()).thenThrow(IOException.class);


        Assertions.assertThrows(FailedToReadFileException.class, () -> fileStorageService.find("test", "filename"));
    }

    @Test
    void findAllByUser() {
        GridFSFindIterable gridFSFindIterableMock = mock(GridFSFindIterable.class);
        GridFSFile gridFSFileMock = mock(GridFSFile.class);
        Document documentMock = mock(Document.class);

        when(documentMock.getString("contentType")).thenReturn("image/png");
        when(gridFSFileMock.getFilename()).thenReturn("filename");
        when(gridFSFileMock.getMetadata()).thenReturn(documentMock);
        when(gridFSFindIterableMock.spliterator()).thenReturn(singletonList(gridFSFileMock).spliterator());
        when(gridFsTemplate.find(any())).thenReturn(gridFSFindIterableMock);
        List<FileDTO> allByUser = fileStorageService.findAllByUser("test");

        verify(gridFsTemplate).find(any());

        Assertions.assertEquals(1, allByUser.size());
        FileDTO fileDTO = allByUser.get(0);

        Assertions.assertEquals("filename", fileDTO.getFilename());
        Assertions.assertEquals("image/png", fileDTO.getContentType());
        Assertions.assertEquals("test", fileDTO.getUserId());
        Assertions.assertEquals("Concluído", fileDTO.getStatus());
    }

    @Test
    void findAll() {
        GridFSFindIterable gridFSFindIterableMock = mock(GridFSFindIterable.class);
        GridFSFile gridFSFileMock = mock(GridFSFile.class);
        Document documentMock = mock(Document.class);

        when(documentMock.getString("contentType")).thenReturn("image/png");
        when(documentMock.getString("userId")).thenReturn("test");
        when(gridFSFileMock.getFilename()).thenReturn("filename");
        when(gridFSFileMock.getMetadata()).thenReturn(documentMock);
        when(gridFSFindIterableMock.spliterator()).thenReturn(singletonList(gridFSFileMock).spliterator());
        when(gridFsTemplate.find(any())).thenReturn(gridFSFindIterableMock);

        List<FileDTO> allByUser = fileStorageService.findAll("");

        verify(gridFsTemplate).find(any());

        Assertions.assertEquals(1, allByUser.size());
        FileDTO fileDTO = allByUser.get(0);

        Assertions.assertEquals("filename", fileDTO.getFilename());
        Assertions.assertEquals("image/png", fileDTO.getContentType());
        Assertions.assertEquals("test", fileDTO.getUserId());
        Assertions.assertEquals("Concluído", fileDTO.getStatus());
    }
}