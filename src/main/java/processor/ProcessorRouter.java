package main.java.processor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

import main.java.common.models.JobType;
import main.java.common.models.image.ImageParams;
import main.java.common.models.text.TextParams;
import main.java.processor.impl.ImageProcessor;
import main.java.processor.impl.TextProcessor;
import main.java.processor.models.ProcessorResult;

public class ProcessorRouter {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessorRouter.class);

    private final Map<JobType, BiFunction<UUID, String, ProcessorResult>> processorMap;

    @Inject private TextProcessor textProcessor; 
    @Inject private ImageProcessor imageProcessor;
    @Inject private Gson gson;

    public ProcessorRouter() {
        processorMap = Map.of(
                JobType.TEXT, (id, params) -> processAndSaveText(id, params),
                JobType.IMAGE, (id, params) -> processAndSaveImage(id, params));
    }

    public ProcessorResult processAndSave(JobType type, UUID id, String params) {

        String errorMessage = String.format("no matching processor found for JobType: {}", type.name());
        ProcessorResult errorResponse = ProcessorResult.builder()
                .id(id)
                .isSuccessful(false)
                .errors(List.of(errorMessage))
                .build();

        return processorMap.getOrDefault(type, (ignoredOne, ignoredTwo) -> {
            LOG.error(errorMessage);
            return errorResponse;
        }).apply(id, params);
    }

    private ProcessorResult processAndSaveText(UUID id, String params) {
        List<TextParams> steps = gson.fromJson(
                params, new TypeToken<List<TextParams>>(){}.getType());
        ProcessorResult result = textProcessor.process(id, steps);
        textProcessor.save(id, result);
        return result;
    }
    
    private ProcessorResult processAndSaveImage(UUID id, String params) {
        List<ImageParams> steps = gson.fromJson(
                params, new TypeToken<List<ImageParams>>(){}.getType());
        ProcessorResult result = imageProcessor.process(id, steps);
        imageProcessor.save(id, result);
        return result;
    }

}
