package main.java.server.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.db.dao.BaseDao;
import main.java.common.db.models.ImageDbModel;
import main.java.common.file.FileServer;
import main.java.common.mq.ZMQProducer;
import main.java.server.mappers.JobMapper;
import main.java.server.models.image.GetImageResponse;
import main.java.server.models.image.CreateImageRequest;
import zmq.util.function.Optional;

public class ImageService extends JobService<GetImageResponse, CreateImageRequest, ImageDbModel> {

    private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);

    private final FileServer fileServer;

    @Inject
    public ImageService(
            BaseDao<ImageDbModel> dao, 
            JobMapper<GetImageResponse, CreateImageRequest, ImageDbModel> mapper, 
            ZMQProducer producer, FileServer fileServer) {

        super(dao, mapper, producer);
        this.fileServer = fileServer;
    }

    @Override
    public UUID create(CreateImageRequest model) {
        UUID generatedId = super.create(model);
        submitJob(generatedId, model);
        return generatedId;
    }

    @Override
    public void delete(UUID id) {
        get(id).ifPresentOrElse(
                job -> {
                    LOG.info("deleting job with id: {}", id);
                    Optional.ofNullable(
                            job.getOutputFilename()).ifPresent(fn -> {
                                LOG.info("deleting file: {}", fn);
                                fileServer.deleteFile(fn);
                            });
                    super.delete(id);
                }, 
                () -> LOG.warn("unable to find job with id: {}", id));
    }
}
