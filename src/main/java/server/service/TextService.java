package main.java.server.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.db.dao.TextDao;
import main.java.common.db.models.TextDbModel;
import main.java.common.models.text.TextParams;
import main.java.common.mq.ZMQProducer;
import main.java.server.mappers.JobMapper;
import main.java.server.models.text.GetTextResponse;
import main.java.server.models.text.PostTextRequest;
import main.java.server.text.TextGenerator;

public class TextService extends JobService<GetTextResponse, PostTextRequest, TextDbModel> {
    
    private static final Logger LOG = LoggerFactory.getLogger(TextService.class);
    
    private final TextDao dao;    
    private final TextGenerator textGenerator;

    @Inject
    public TextService(TextDao dao, 
            JobMapper<GetTextResponse, PostTextRequest, TextDbModel> mapper, 
            ZMQProducer producer, TextGenerator textGenerator) {
        super(dao, mapper, producer);
        this.dao = dao;
        this.textGenerator = textGenerator;
    }
    
    @Override
    public UUID create(PostTextRequest model) {
        UUID generatedId = super.create(model);
        if (model.isServiceCall()) {
            LOG.info("using service call instead of processor.");
            generateAndSaveText(generatedId, model.getParams().get(0));
        }  
        else
            submitJob(generatedId, model);
        return generatedId;
    }
    
    private void generateAndSaveText(UUID id, TextParams params) {
        CompletableFuture.runAsync(() -> {
            String outputText = 
                    textGenerator.generate(params).getOutputText();
            dao.update(id, outputText);
        });
    }

}
