package br.com.belfalas.restfileupload.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MongoFileStorageService implements FileStorageService {

    @Override
    public void save(String userId, MultipartFile file) {
        throw new UnsupportedOperationException("Salvar ainda não implementado");
    }

    @Override
    public void delete(String userId, String filename) {
        throw new UnsupportedOperationException("Deletar ainda não implementado");
    }

    @Override
    public void find(String userId, String filename) {
        throw new UnsupportedOperationException("Find ainda não implementado");
    }

    @Override
    public void findAll(String userId) {
        throw new UnsupportedOperationException("FindAll ainda não implementado");
    }

    @Override
    public void findAllFilesInAllUsers() {
        throw new UnsupportedOperationException("FindAllFilesInAllUsers ainda não implementado");
    }
}
