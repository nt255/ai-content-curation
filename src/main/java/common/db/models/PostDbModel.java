package main.java.common.db.models;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PostDbModel extends BaseDbModel {

    // for now, we'll model posts as a pairing between 
    // an existing generation of text and image
    private UUID textJobId;
    private UUID imageJobId;
    
}
