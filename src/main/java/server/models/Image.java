package main.java.server.models;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import main.java.common.enums.ImageType;

@Getter
@SuperBuilder
public class Image extends Job {
    
    private ImageType type;
    
    private Integer height;
    private Integer width;
    private String checkpoint;
    private String workflow;
    
    private String outputFilename;

}
