package main.java.server.validator;

enum ErrorCode {

    // ----- general
    NO_PARAMS("no parameters are provided"),
    MULTIPLE_STEPS_NOT_SUPPORTED("multiple steps are not supported"),
    

    // ----- image
    UPSCALE_IMAGE_MISSING("upscale must work on an existing image"),
    CREATE_IS_NOT_FIRST("create image must appear first");


    final String detail;

    private ErrorCode(String detail) {
        this.detail = detail;
    }

}
