package main.java.server.models;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.ImageParams;

@Getter
@SuperBuilder
public class Image extends Job {
    
    private ImageParams params;
    
    // output
    private String outputFilename;

}
