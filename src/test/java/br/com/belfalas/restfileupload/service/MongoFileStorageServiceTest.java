package br.com.belfalas.restfileupload.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MongoFileStorageServiceTest {

    @Autowired
    MongoFileStorageService mongoFileStorageService;

    @Test
    void save() {
        mongoFileStorageService.save(null, null);
    }

    @Test
    void delete() {
        mongoFileStorageService.delete(null, null);
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