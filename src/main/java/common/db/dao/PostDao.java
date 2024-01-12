package main.java.common.db.dao;

import main.java.common.db.models.PostDbModel;

public class PostDao extends BaseDao<PostDbModel> {

    public PostDao() {
        super(PostDbModel.class, "post");    
    }

}
