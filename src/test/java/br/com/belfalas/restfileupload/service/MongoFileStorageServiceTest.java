package br.com.belfalas.restfileupload.service;

import br.com.belfalas.restfileupload.dto.FileDTO;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.compress.utils.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MongoFileStorageServiceTest {

    @Autowired
    MongoFileStorageService mongoFileStorageService;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @AfterEach
    void teardown(){
        GridFSFindIterable gridFSFiles = gridFsTemplate.find(new Query());

        for (GridFSFile file : gridFSFiles) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(file.getId())));
        }
    }

    @Test
    void save() throws IOException {
        InputStream resourceAsStream = MongoFileStorageServiceTest.class.getResourceAsStream("/testes.png");

        byte[] imageBytes = IOUtils.toByteArray(resourceAsStream);

        MultipartFile multipartFile = new MockMultipartFile("test", imageBytes);
        mongoFileStorageService.save("teste", multipartFile);

        GridFSFile savedFile = gridFsTemplate.findOne(Query.query(Criteria.where("metadata.userId").is("teste")));
        Assertions.assertThat(savedFile).isNotNull();
    }

    @Test
    void delete() {
        addImageToMongo("foo", "teste");

        mongoFileStorageService.delete("foo", "teste");

        GridFSFile deletedFile = gridFsTemplate.findOne(Query.query(Criteria.where("metadata.userId").is("foo")));

        Assertions.assertThat(deletedFile).isNull();
    }

    @Test
    void find() throws IOException {
        addImageToMongo("foo", "teste");

        byte[] bytes = mongoFileStorageService.find("foo", "teste");

        byte[] originalByteArray = IOUtils.toByteArray(MongoFileStorageServiceTest.class.getResourceAsStream("/testes.png"));

        Assertions.assertThat(bytes).hasSameSizeAs(originalByteArray);
        Assertions.assertThat(bytes).containsExactly(originalByteArray);
    }

    @Test
    void findAll() {
        addImageToMongo("foo", "teste1");
        addImageToMongo("foo", "teste2");
        addImageToMongo("foo", "teste3");

        List<FileDTO> allFiles = mongoFileStorageService.findAllByUser("foo");

        List<String> fileNames = allFiles.stream().map(FileDTO::getFilename).collect(Collectors.toList());

        Assertions.assertThat(fileNames).containsAll(Arrays.asList("teste1", "teste2", "teste3"));
    }


    @Test
    void findAllFilesInAllUsers() {
        addImageToMongo("foo", "teste1");
        addImageToMongo("bar", "teste2");

        List<FileDTO> list = mongoFileStorageService.findAll("");

        List<String> fileNames = list.stream().map(FileDTO::getFilename).collect(Collectors.toList());
        Assertions.assertThat(fileNames).containsAll(Arrays.asList("teste1", "teste2"));
    }

    private void addImageToMongo(String userId, String filename) {
        InputStream resourceAsStream = MongoFileStorageServiceTest.class.getResourceAsStream("/testes.png");
        DBObject metadata = new BasicDBObjectBuilder()
                .add("userId", userId)
                .get();

        gridFsTemplate.store(resourceAsStream, filename, "image/png", metadata);
    }
}