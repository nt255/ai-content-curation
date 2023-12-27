package main.java.processor.image;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import org.apache.commons.io.FilenameUtils;

import com.google.inject.Inject;

import main.java.common.db.dao.ImageDao;
import main.java.common.db.models.ImageDbModel;
import main.java.common.file.FileServer;
import main.java.common.models.JobState;
import main.java.common.models.image.ImageParams;
import main.java.common.models.image.ImageParamsType;
import main.java.processor.MultistepProcessor;
import main.java.processor.comfy.ComfyFileManager;
import main.java.processor.models.ProcessorResult;
import zmq.util.function.Optional;

public class ImageProcessor implements MultistepProcessor<ImageParams> {

    private final Map<ImageParamsType, 
    BiFunction<String, ImageParams, String>> singleStepMap;

    private final ImageDao imageDao;
    private final FileServer fileServer;
    private final ComfyFileManager comfyFileManager;


    @Inject
    public ImageProcessor(ImageDao imageDao, FileServer fileServer, 
            ComfyFileManager comfyFileManager, CreateStep createStep) {

        this.imageDao = imageDao;
        this.fileServer = fileServer;
        this.comfyFileManager = comfyFileManager;

        singleStepMap = Map.of(ImageParamsType.CREATE, createStep::execute);
    }


    @Override
    public ProcessorResult process(UUID id, List<ImageParams> steps) {

        String localImagePath = "";

        for (ImageParams step : steps) {
            ImageParamsType type = step.getType();
            localImagePath = singleStepMap.getOrDefault(type, (ig1, ig2) -> {
                String errorMessage = String.format(
                        "no step found for ImageParamsType: {}", type.name());
                throw new IllegalStateException(errorMessage);
            }).apply(localImagePath, step);
        }

        ProcessorResult result = ProcessorResult.builder()
                .id(id)
                .outputString(localImagePath)
                .build();
        
        return result;
    }

    @Override
    public void save(UUID id, ProcessorResult result) {

        ImageDbModel existing = imageDao.get(id).get();
        existing.setLastModifiedOn(Instant.now());
        existing.setState(JobState.COMPLETED);

        Optional.ofNullable(result.getOutputString()).ifPresent(path -> {
            UUID imageId = UUID.randomUUID();
            String ext = FilenameUtils.getExtension(path);
            String newFilename = imageId + "." + ext;

            fileServer.uploadFile(newFilename, path);
            existing.setOutputFilename(newFilename);

            comfyFileManager.clearDirectory();
        });

        imageDao.delete(id);
        imageDao.insert(existing);
    }

}
