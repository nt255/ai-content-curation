package main.java.server.request;

import com.google.inject.Inject;

import main.java.server.models.image.PostImageRequest;
import main.java.server.service.ImageService;
import main.java.server.validator.ImageValidator;

class ImageRequestHandler extends 
BaseRequestHandler<PostImageRequest, ImageService, ImageValidator> {
    
    @Inject
    public ImageRequestHandler(ImageService imageService, 
            ImageValidator imageValidator) {
        super(imageService, imageValidator, "/images");
    }

}
