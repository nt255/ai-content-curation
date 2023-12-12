package main.java.processor;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.db.dao.JobDao;
import main.java.common.db.models.JobDbModel;
import main.java.common.enums.JobState;
import main.java.common.mq.ZMQModel;
import main.java.processor.models.JobRequest;
import main.java.processor.models.JobResult;

public class DbAndFileClient {
    
    private static final Logger LOG = LoggerFactory.getLogger(DbAndFileClient.class);
    
    @Inject private JobDao dao;
    
    public JobRequest hydrateJobRequest(ZMQModel payload) {
        throw new UnsupportedOperationException();
    }
    
    public void persistJobResult(JobResult result) {
        
        UUID id = result.getId();
        
        JobDbModel existing = dao.get(id).get();
        existing.setLastModifiedOn(Instant.now());
        existing.setState(JobState.COMPLETED);
        existing.setOutputText(result.getOutputText());
        
        LOG.info("persisting JobResult to db: {}", existing);
        
        // TODO: implement mongodb update
        dao.delete(id);
        dao.insert(existing);
    }

}
