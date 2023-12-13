package main.java.common.db.dao;

import main.java.common.db.models.ImageDbModel;

public class ImageDao extends BaseDao<ImageDbModel> {

    public ImageDao() {
        super(ImageDbModel.class, "image");    
    }

}
