package br.com.belfalas.restfileupload.service;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class MongoFileStorageService implements FileStorageService {

    private final GridFsTemplate gridFsTemplate;

    public MongoFileStorageService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    public void save(String userId, MultipartFile file) throws IOException {
        DBObject metadata = new BasicDBObjectBuilder()
                .add("userId", userId)
                .get();

        gridFsTemplate.store(file.getInputStream(), file.getName(), file.getContentType(), metadata);
    }

    @Override
    public void delete(String userId, String filename) {
        gridFsTemplate.delete(Query.query(Criteria
                .where("metadata.userId").is(userId)
                .and("filename").is(filename)));
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
