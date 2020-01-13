package br.com.belfalas.restfileupload.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel(description = "Response to the find methods")
public class FileDTO {
    @ApiModelProperty(notes = "The userId that uploaded the file")
    private String userId;

    @ApiModelProperty(notes = "The filename of the file that got uploaded")
    private String filename;

    @ApiModelProperty(notes = "The contentType of the file uploaded")
    private String contentType;

    @ApiModelProperty(notes = "The current status of the uploaded file")
    private String status;

    public FileDTO(String userId, String filename, String contentType, String status) {
        this.userId = userId;
        this.filename = filename;
        this.contentType = contentType;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDTO fileDTO = (FileDTO) o;
        return Objects.equals(userId, fileDTO.userId) &&
                Objects.equals(filename, fileDTO.filename) &&
                Objects.equals(contentType, fileDTO.contentType) &&
                Objects.equals(status, fileDTO.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, filename, contentType, status);
    }

    @Override
    public String toString() {
        return "FileDTO{" +
                "userId='" + userId + '\'' +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
