package main.java.server.service;

import com.google.inject.Inject;

import main.java.common.db.dao.BaseDao;
import main.java.common.db.models.TextDbModel;
import main.java.common.mq.ZMQProducer;
import main.java.server.mappers.JobMapper;
import main.java.server.models.Text;

public class TextService extends JobService<Text, TextDbModel> {

    @Inject
    public TextService(BaseDao<TextDbModel> dao, JobMapper<Text, TextDbModel> mapper, 
            ZMQProducer producer) {
        super(dao, mapper, producer);
    }

}
