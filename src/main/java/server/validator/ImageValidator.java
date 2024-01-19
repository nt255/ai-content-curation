package main.java.server.validator;

import java.util.ArrayList;
import java.util.List;

import main.java.common.models.image.ImageParams;
import main.java.common.models.image.ImageParamsType;
import main.java.server.models.image.CreateImageRequest;

public class ImageValidator extends BaseValidator<CreateImageRequest> {
    
    public ImageValidator() {
        super(CreateImageRequest.class);
    }

    @Override
    List<ErrorCode> validate(CreateImageRequest body) {
        List<ErrorCode> errorCodes = new ArrayList<>();
        
        List<ImageParams> params = body.getParams();
        if (params.isEmpty())
            errorCodes.add(ErrorCode.NO_PARAMS);
        
        for (int i = 0; i != params.size(); ++i) {
            ImageParams step = params.get(i);
            ImageParamsType type = step.getType();
            
            if (ImageParamsType.UPSCALE.equals(type)) {
                if (i == 0 && body.getBaseImageId() == null) 
                	errorCodes.add(ErrorCode.UPSCALE_IMAGE_MISSING);
            }
            
            else if (ImageParamsType.CREATE.equals(type)) {
                if (i > 0)
                    errorCodes.add(ErrorCode.CREATE_IS_NOT_FIRST);
            }
        }
        return errorCodes;
    }
}
