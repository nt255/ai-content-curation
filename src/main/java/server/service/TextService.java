package main.java.server.service;

import com.google.inject.Inject;

import main.java.common.db.dao.BaseDao;
import main.java.common.db.models.TextDbModel;
import main.java.common.mq.ZMQProducer;
import main.java.server.mappers.JobMapper;
import main.java.server.models.text.GetTextResponse;
import main.java.server.models.text.PostTextRequest;

public class TextService extends JobService<GetTextResponse, PostTextRequest, TextDbModel> {

    @Inject
    public TextService(
            BaseDao<TextDbModel> dao, 
            JobMapper<GetTextResponse, PostTextRequest, TextDbModel> mapper, 
            ZMQProducer producer) {
        
        super(dao, mapper, producer);
    }

}
