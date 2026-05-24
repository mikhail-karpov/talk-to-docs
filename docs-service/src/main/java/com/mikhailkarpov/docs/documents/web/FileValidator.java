package com.mikhailkarpov.docs.documents.web;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator implements ConstraintValidator<FileConstraint, MultipartFile> {

    private Set<String> allowedContentTypes = new HashSet<>();

    @Override
    public void initialize(FileConstraint annotation) {
        if (annotation.allowedTypes() != null) {
          this.allowedContentTypes = new HashSet<>(Arrays.asList(annotation.allowedTypes()));
        }
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

      return file.getContentType() != null && allowedContentTypes.contains(file.getContentType());
    }
}
