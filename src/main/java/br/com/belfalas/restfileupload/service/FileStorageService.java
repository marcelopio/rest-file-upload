package br.com.belfalas.restfileupload.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    void save(String userId, MultipartFile file);

    void delete(String userId, String filename);

    void find(String userId, String filename);

    void findAll(String userId);

    void findAllFilesInAllUsers();

}
