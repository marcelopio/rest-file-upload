package br.com.belfalas.restfileupload.service;

import br.com.belfalas.restfileupload.dto.FileDTO;
import br.com.belfalas.restfileupload.dto.ImageDTO;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class MongoFileStorageServiceIntegrationTest {

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
        InputStream resourceAsStream = MongoFileStorageServiceIntegrationTest.class.getResourceAsStream("/testes.png");

        byte[] imageBytes = IOUtils.toByteArray(resourceAsStream);

        MultipartFile multipartFile = new MockMultipartFile("test", "test.png", "image/png", imageBytes);

        mongoFileStorageService.save("test", multipartFile);

        GridFSFile savedFile = gridFsTemplate.findOne(Query.query(Criteria.where("metadata.userId").is("test")));
        Assertions.assertThat(savedFile).isNotNull();
    }

    @Test
    void delete() {
        addImageToMongo("foo", "test");

        mongoFileStorageService.delete("foo", "test");

        GridFSFile deletedFile = gridFsTemplate.findOne(Query.query(Criteria.where("metadata.userId").is("foo")));

        Assertions.assertThat(deletedFile).isNull();
    }

    @Test
    void deleteCheckDeleteJustOne() {
        addImageToMongo("foo", "test1");
        addImageToMongo("foo", "test2");

        mongoFileStorageService.delete("foo", "test1");

        GridFSFile notDeleted = gridFsTemplate.findOne(Query.query(Criteria.where("metadata.userId").is("foo")));

        Assertions.assertThat(notDeleted).isNotNull();
        Assertions.assertThat(notDeleted.getFilename()).isEqualTo("test2");
    }

    @Test
    void find() throws IOException {
        addImageToMongo("foo", "test");

        ImageDTO result = mongoFileStorageService.find("foo", "test");

        byte[] originalByteArray = IOUtils.toByteArray(MongoFileStorageServiceIntegrationTest.class.getResourceAsStream("/testes.png"));

        Assertions.assertThat(result.getImage()).hasSameSizeAs(originalByteArray);
        Assertions.assertThat(result.getImage()).containsExactly(originalByteArray);
    }

    @Test
    void findAllByUser() {
        addImageToMongo("foo", "test1");
        addImageToMongo("foo", "test2");
        addImageToMongo("foo", "test3");

        List<FileDTO> allFiles = mongoFileStorageService.findAllByUser("foo");

        List<String> fileNames = allFiles.stream().map(FileDTO::getFilename).collect(Collectors.toList());

        Assertions.assertThat(fileNames).containsAll(Arrays.asList("test1", "test2", "test3"));
    }

    @Test
    void findAllByUserCheckNotGettingFromAnotherUser(){
        addImageToMongo("foo", "test1");
        addImageToMongo("bar", "test2");

        List<FileDTO> allFiles = mongoFileStorageService.findAllByUser("foo");

        List<String> fileNames = allFiles.stream().map(FileDTO::getFilename).collect(Collectors.toList());

        Assertions.assertThat(fileNames).containsAll(Arrays.asList("test1"));
    }


    @Test
    void findAll() {
        addImageToMongo("foo", "test1");
        addImageToMongo("bar", "test2");

        List<FileDTO> list = mongoFileStorageService.findAll("");

        List<String> fileNames = list.stream().map(FileDTO::getFilename).collect(Collectors.toList());
        Assertions.assertThat(fileNames).containsAll(Arrays.asList("test1", "test2"));
    }

    @Test
    void findAllWithFilename(){
        addImageToMongo("foo", "test1");
        addImageToMongo("bar", "test2");

        List<FileDTO> list = mongoFileStorageService.findAll("1");

        List<String> fileNames = list.stream().map(FileDTO::getFilename).collect(Collectors.toList());
        Assertions.assertThat(fileNames).containsAll(Arrays.asList("test1"));
    }

    private void addImageToMongo(String userId, String filename) {
        InputStream resourceAsStream = MongoFileStorageServiceIntegrationTest.class.getResourceAsStream("/testes.png");
        DBObject metadata = new BasicDBObjectBuilder()
                .add("userId", userId)
                .get();

        gridFsTemplate.store(resourceAsStream, filename, "image/png", metadata);
    }
}