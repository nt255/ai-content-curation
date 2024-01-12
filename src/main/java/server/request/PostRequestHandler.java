package main.java.server.request;

import com.google.inject.Inject;

import main.java.server.models.post.CreatePostRequest;
import main.java.server.service.PostService;
import main.java.server.validator.PostValidator;

public class PostRequestHandler extends 
BaseRequestHandler<CreatePostRequest, PostService, PostValidator> {
    
    @Inject
    public PostRequestHandler(PostService postService, 
            PostValidator postValidator) {
        super(postService, postValidator, "/posts");
    }

}
