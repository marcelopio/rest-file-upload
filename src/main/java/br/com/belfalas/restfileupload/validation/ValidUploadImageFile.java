package br.com.belfalas.restfileupload.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UploadImageFileConstraint.class)
@Documented
public @interface ValidUploadImageFile {
    String message() default "Image must be png, jpeg, bmp or gif";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
