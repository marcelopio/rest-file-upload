package br.com.belfalas.restfileupload.dto;

import java.util.Arrays;
import java.util.Objects;

public class ImageDTO {

    private String contentType;
    private byte[] image;

    public ImageDTO() {
    }

    public ImageDTO(String contentType, byte[] image) {
        this.contentType = contentType;
        this.image = image;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageDTO imageDTO = (ImageDTO) o;
        return Objects.equals(contentType, imageDTO.contentType) &&
                Arrays.equals(image, imageDTO.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(contentType);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "ImageDTO{" +
                "contentType='" + contentType + '\'' +
                '}';
    }
}
