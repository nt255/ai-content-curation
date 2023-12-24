package main.java.common.models.image;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class LoraSettings {
    
    private String name;
    private Double modelStrength;
    private Double clipStrength;

}
