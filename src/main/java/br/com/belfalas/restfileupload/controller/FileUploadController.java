package br.com.belfalas.restfileupload.controller;

import br.com.belfalas.restfileupload.dto.FileDTO;
import br.com.belfalas.restfileupload.dto.ImageDTO;
import br.com.belfalas.restfileupload.exception.FailedToReadFileException;
import br.com.belfalas.restfileupload.exception.FileNotFoundInStorageException;
import br.com.belfalas.restfileupload.service.FileStorageService;
import br.com.belfalas.restfileupload.validation.ValidUploadImageFile;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/fileUpload/v1")
@Validated
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @Autowired
    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @ApiOperation("List the files stored for the userId")
    @GetMapping("/{userId}")
    public List<FileDTO> listFiles(
            @ApiParam(value = "The userId to look the files for", required = true)
            @PathVariable
                    String userId
    ) {
        return fileStorageService.findAllByUser(userId);
    }

    @ApiOperation("List the all files stored, optionally querying by filename")
    @GetMapping("/listAll")
    public List<FileDTO> listAllFiles(
            @ApiParam(value = "The filename to filter")
            @RequestParam(required = false)
                    String filename
    ) {
        return fileStorageService.findAll(filename);
    }

    @ApiOperation("Get the file of the user by filename")
    @GetMapping("/{userId}/{filename}")
    public ResponseEntity<byte[]> getFile(
            @ApiParam(value = "The userId to look the files for", required = true)
            @PathVariable
                    String userId,
            @ApiParam(value = "The filename of the file to be downloaded", required = true)
            @PathVariable
                    String filename
    ) throws FailedToReadFileException, FileNotFoundInStorageException {
        ImageDTO result = fileStorageService.find(userId, filename);

        MediaType mediaType = MediaType.parseMediaType(result.getContentType());
        byte[] image = result.getImage();

        return ResponseEntity.ok().contentType(mediaType).contentLength(image.length).body(image);
    }

    @ApiOperation("Upload the file for the user")
    @PostMapping("/{userId}")
    public ResponseEntity<String> uploadFile(
            @ApiParam(value = "The userId to upload the file to", required = true)
            @PathVariable
                    String userId,
            @ApiParam(value = "The file to be uploaded", required = true)
            @RequestParam("file")
            @ValidUploadImageFile
                    MultipartFile file
    ) throws FailedToReadFileException {
        fileStorageService.save(userId, file);

        return ResponseEntity.ok(String.format("File %s uploaded successfully", file.getOriginalFilename()));
    }

    @ApiOperation("Delete the file of the user")
    @DeleteMapping("/{userId}/{filename}")
    public void deleteFile(
            @ApiParam(value = "The userId to delete the file from", required = true)
            @PathVariable
                    String userId,
            @ApiParam(value = "The file to be deleted", required = true)
            @PathVariable String filename
    ) {
        fileStorageService.delete(userId, filename);
    }

    @ExceptionHandler(FileNotFoundInStorageException.class)
    public ResponseEntity<?> handleFileNotFoundException(FileNotFoundInStorageException e){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(FailedToReadFileException.class)
    public ResponseEntity<?> handleFailedToReadFileException(FailedToReadFileException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
