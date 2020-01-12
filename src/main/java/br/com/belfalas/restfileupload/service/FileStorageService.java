package br.com.belfalas.restfileupload.service;

import br.com.belfalas.restfileupload.dto.FileDTO;
import br.com.belfalas.restfileupload.dto.ImageDTO;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageService {

    void save(String userId, MultipartFile file) throws IOException;

    void delete(String userId, String filename);

    ImageDTO find(String userId, String filename) throws IOException;

    List<FileDTO> findAllByUser(String userId);

    List<FileDTO> findAll(String filename);

}
