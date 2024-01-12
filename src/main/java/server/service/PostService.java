package main.java.server.service;

import com.google.inject.Inject;

import main.java.common.db.dao.PostDao;
import main.java.common.db.models.PostDbModel;
import main.java.server.mappers.Mapper;
import main.java.server.models.post.CreatePostRequest;
import main.java.server.models.post.GetPostResponse;

public class PostService extends 
BaseService<GetPostResponse, CreatePostRequest, PostDbModel> {

    @Inject
    public PostService(PostDao dao, 
            Mapper<GetPostResponse, CreatePostRequest, PostDbModel> mapper) {
        super(dao, mapper);
    }

}
