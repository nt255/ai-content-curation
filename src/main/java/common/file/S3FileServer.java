package main.java.common.file;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class S3FileServer implements FileServer {

    private static final Logger LOG = 
            LoggerFactory.getLogger(S3FileServer.class);
    
    private static final String NO_SUCH_KEY = "NoSuchKey";

    private final String bucketName;
    private final S3Client s3;

    @Inject
    public S3FileServer(Properties properties) {
        this.bucketName = properties.getProperty("s3.bucketname");
        this.s3 = S3Client.builder()
                .credentialsProvider(
                        EnvironmentVariableCredentialsProvider.create())
                .region(Region.of(properties.getProperty("s3.region")))
                .build();
    }

    @Override
    public void uploadFile(String name, String sourcePath) {
        String objectKey = name;
        String objectPath = sourcePath;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3.putObject(putRequest, 
                    RequestBody.fromFile(new File(objectPath)));
            LOG.info("Successfully placed {} into bucket {}", 
                    objectKey, bucketName);

        } catch (S3Exception e) {
            LOG.error(e.awsErrorDetails().errorMessage());
        }
    }

    @Override
    public File downloadFile(String name, String targetPath) 
            throws FileNotFoundException {
        
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(name)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = 
                    s3.getObjectAsBytes(getRequest);
            byte[] data = objectBytes.asByteArray();

            // Write the data to a local file.
            File file = new File(targetPath + name);
            FileOutputStream os = new FileOutputStream(file);
            os.write(data);
            LOG.info("Successfully obtained bytes from an S3 object");
            os.close();

            return file;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (S3Exception e) {
            LOG.error(e.awsErrorDetails().errorMessage());
            if (NO_SUCH_KEY.equals(e.awsErrorDetails().errorCode()))
                throw new FileNotFoundException();    
        }

        return null;
    }

    @Override
    public void deleteFile(String name) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(name)
                    .build();

            s3.deleteObject(deleteRequest);

        } catch (S3Exception e) {
            LOG.error(e.awsErrorDetails().errorMessage());
        }

        LOG.info("Deleted");
    }

}
