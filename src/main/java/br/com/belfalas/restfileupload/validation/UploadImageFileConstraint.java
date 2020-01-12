package br.com.belfalas.restfileupload.validation;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class UploadImageFileConstraint implements ConstraintValidator<ValidUploadImageFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        switch (Objects.requireNonNull(value.getContentType())){
            case MediaType.IMAGE_PNG_VALUE:
            case MediaType.IMAGE_JPEG_VALUE:
            case MediaType.IMAGE_GIF_VALUE:
            case "image/bmp":
                return true;
            default:
                return false;
        }
    }

}
