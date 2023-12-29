package main.java.server.validator;

enum ErrorCode {
    
    NO_PARAMS("no parameters are provided"),
    UPSCALE_IMAGE_MISSING("upscale must work on an existing image"),
    CREATE_NOT_FIRST("create must appear first");

    final String detail;

    private ErrorCode(String detail) {
        this.detail = detail;
    }
    
}
