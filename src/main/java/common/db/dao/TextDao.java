package main.java.common.db.dao;

import java.time.Instant;
import java.util.UUID;

import main.java.common.db.models.TextDbModel;
import main.java.common.models.JobState;

public class TextDao extends BaseDao<TextDbModel> {

    public TextDao() {
        super(TextDbModel.class, "text");    
    }
    
    public void update(UUID id, String outputText) {
        TextDbModel existing = this.get(id).get();
        existing.setLastModifiedOn(Instant.now());
        existing.setState(JobState.COMPLETED);
        existing.setOutputText(outputText);

        this.delete(id);
        this.insert(existing);
    }

}