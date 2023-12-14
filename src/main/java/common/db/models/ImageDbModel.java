package main.java.common.db.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.ImageParams;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ImageDbModel extends JobDbModel {

    // input
    private ImageParams params;
    
    // output
    private String outputFilename;
    
}
