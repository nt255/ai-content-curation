package main.java.processor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;

import com.google.inject.Inject;

import main.java.common.db.dao.ImageDao;
import main.java.common.db.dao.TextDao;
import main.java.common.db.models.ImageDbModel;
import main.java.common.db.models.TextDbModel;
import main.java.common.enums.JobState;
import main.java.common.enums.JobType;
import main.java.common.file.FileServer;
import main.java.common.mq.ZMQModel;
import main.java.processor.models.ProcessorResponse;
import zmq.util.function.Optional;

public class DbAndFileClient {

    @Inject private TextDao textDao;
    @Inject private ImageDao imageDao;
    @Inject private FileServer fileServer;

    public Map<String, String> hydrateJobRequest(ZMQModel payload) {
        throw new UnsupportedOperationException();
    }

    public void persistJobResult(JobType jobType, ProcessorResponse result) {

        UUID id = result.getId();
        
        if (JobType.TEXT.equals(jobType)) {
            TextDbModel existing = textDao.get(id).get();
            existing.setLastModifiedOn(Instant.now());
            existing.setState(JobState.COMPLETED);
            existing.setOutputText(result.getOutputString());
            
            textDao.delete(id);
            textDao.insert(existing);
        }
        else if (JobType.IMAGE.equals(jobType)) {
            ImageDbModel existing = imageDao.get(id).get();
            existing.setLastModifiedOn(Instant.now());
            existing.setState(JobState.COMPLETED);
            
            Optional.ofNullable(result.getOutputString()).ifPresent(path -> {
                UUID imageId = UUID.randomUUID();
                String ext = FilenameUtils.getExtension(path);
                String newFilename = imageId + "." + ext;
                fileServer.uploadFile(newFilename, path);
                existing.setOutputFilename(newFilename);
            });
            
            imageDao.delete(id);
            imageDao.insert(existing);
        }
    }

}
