package br.com.belfalas.restfileupload.service;

import br.com.belfalas.restfileupload.dto.FileDTO;
import br.com.belfalas.restfileupload.dto.ImageDTO;
import br.com.belfalas.restfileupload.exception.FailedToReadFileException;
import br.com.belfalas.restfileupload.exception.FileNotFoundInStorageException;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MongoFileStorageService implements FileStorageService {

    private final GridFsTemplate gridFsTemplate;

    public MongoFileStorageService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    public void save(String userId, MultipartFile file) throws FailedToReadFileException {
        DBObject metadata = new BasicDBObjectBuilder()
                .add("userId", userId)
                .add("contentType", Objects.requireNonNull(file.getContentType()))
                .get();

        try {
            gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metadata);
        } catch (IOException e) {
            throw new FailedToReadFileException("Failed to read file from sent by client", e);
        }
    }

    @Override
    public void delete(String userId, String filename) {
        gridFsTemplate.delete(Query.query(Criteria
                .where("metadata.userId").is(userId)
                .and("filename").is(filename)));
    }

    @Override
    public ImageDTO find(String userId, String filename) throws FailedToReadFileException, FileNotFoundInStorageException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria
                .where("metadata.userId").is(userId)
                .and("filename").is(filename)));

        if (gridFSFile == null){
            throw new FileNotFoundInStorageException(String.format("File %s not found", filename));
        }

        GridFsResource resource = gridFsTemplate.getResource(gridFSFile);

        try {
            byte[] byteArray = IOUtils.toByteArray(resource.getInputStream());
            return new ImageDTO(gridFSFile.getMetadata().getString("contentType"), byteArray);
        } catch (IOException e) {
            throw new FailedToReadFileException(String.format("File %s was not read", filename), e);
        }
    }

    @Override
    public List<FileDTO> findAllByUser(String userId) {
        GridFSFindIterable gridFSFiles = gridFsTemplate.find(Query.query(Criteria
                .where("metadata.userId").is(userId)));

        return StreamSupport.stream(gridFSFiles.spliterator(), false)
                .map(file -> new FileDTO(
                        userId,
                        file.getFilename(),
                        file.getMetadata().getString("contentType"),
                        "Concluído"))
                .collect(Collectors.toList());
    }

    @Override
    public List<FileDTO> findAll(String filename) {
        String regex = String.format(".*%s.*", Optional.ofNullable(filename).orElse(""));
        GridFSFindIterable gridFSFiles = gridFsTemplate.find(
                Query.query(Criteria.where("filename").regex(regex, "i")));

        return StreamSupport.stream(gridFSFiles.spliterator(), false)
                .map(file -> new FileDTO(
                        file.getMetadata().getString("userId"),
                        file.getFilename(),
                        file.getMetadata().getString("contentType"),
                        "Concluído"))
                .collect(Collectors.toList());
    }

}
