package br.com.belfalas.restfileupload.service;

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
        InputStream resourceAsStream = MongoFileStorageServiceTest.class.getResourceAsStream("/testes.png");
        DBObject metadata = new BasicDBObjectBuilder()
                .add("userId", "foo")
                .get();


        gridFsTemplate.store(resourceAsStream, "teste", "image/png", metadata);

        mongoFileStorageService.delete("foo", "teste");

        GridFSFile deletedFile = gridFsTemplate.findOne(Query.query(Criteria.where("metadata.userId").is("foo")));

        Assertions.assertThat(deletedFile).isNull();
    }

    @Test
    void find() {
        mongoFileStorageService.find(null, null);
    }

    @Test
    void findAll() {
        mongoFileStorageService.findAll(null);
    }

    @Test
    void findAllFilesInAllUsers() {
        mongoFileStorageService.findAllFilesInAllUsers();
    }
}