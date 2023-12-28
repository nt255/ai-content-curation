package main.java.server.validator;

import java.util.List;

import io.javalin.http.Context;
import io.javalin.validation.BodyValidator;
import main.java.common.models.image.ImageParams;
import main.java.common.models.image.ImageParamsType;
import main.java.server.models.image.PostImageRequest;

public class ImageValidator extends BaseValidator<PostImageRequest> {

    @Override
    public BodyValidator<PostImageRequest> validate(Context ctx) {

        return ctx.bodyValidator(PostImageRequest.class)
                .check(this::paramsNotEmpty, 
                        getValidationError(ErrorCode.NO_PARAMS))
                .check(this::upscalingExistingImage, 
                        getValidationError(ErrorCode.UPSCALE_IMAGE_MISSING));

    }

    private boolean paramsNotEmpty(PostImageRequest body) {
        if (body.getParams().isEmpty())
            return false;
        return true;
    }


    private boolean upscalingExistingImage(PostImageRequest body) {
        List<ImageParams> params = body.getParams();
        if (params.size() != 0)
            if (ImageParamsType.UPSCALE.equals(params.get(0).getType()))
                if (body.getBaseImageId() == null)
                    return false;

        return true;
    }

}
