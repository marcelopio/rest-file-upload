package br.com.belfalas.restfileupload.dto;

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
}
