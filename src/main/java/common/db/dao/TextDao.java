package main.java.common.db.dao;

import main.java.common.db.models.TextDbModel;

public class TextDao extends BaseDao<TextDbModel> {

    public TextDao() {
        super(TextDbModel.class, "text");    
    }

}