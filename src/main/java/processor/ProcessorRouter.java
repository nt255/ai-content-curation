package main.java.processor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

import main.java.common.models.JobType;
import main.java.common.models.image.ImageParams;
import main.java.common.models.text.TextParams;
import main.java.processor.image.ImageProcessor;
import main.java.processor.models.ProcessorResult;
import main.java.processor.text.TextProcessor;

class ProcessorRouter {

    private final Map<JobType, BiFunction<UUID, String, ProcessorResult>> processorMap;

    @Inject private TextProcessor textProcessor; 
    @Inject private ImageProcessor imageProcessor;
    @Inject private Gson gson;

    public ProcessorRouter() {
        processorMap = Map.of(
                JobType.TEXT, (id, params) -> processAndSaveText(id, params),
                JobType.IMAGE, (id, params) -> processAndSaveImage(id, params));
    }

    ProcessorResult processAndSave(JobType type, UUID id, String params) {
        return processorMap.getOrDefault(type, (ignoredOne, ignoredTwo) -> {
            String errorMessage = String.format(
                    "no processor found for JobType: {}", type.name());
            throw new IllegalStateException(errorMessage);
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
