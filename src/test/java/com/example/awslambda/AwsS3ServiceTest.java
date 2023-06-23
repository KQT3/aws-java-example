package com.example.awslambda;

import com.example.awslambda.services.AwsS3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;

@SpringBootTest
@Profile("test")
class AwsS3ServiceTest {
    @Autowired
    AwsS3Service awsS3Service;
    @Autowired
    ResourceLoader resourceLoader;

    @Test
    void ListS3ObjectsSuccess() {
        awsS3Service.listObjects();
    }

    @Test
    void DownloadS3ObjectSuccess() {
        String bucketName = "";
        String key = "";
        String localDirectoryPath = "";
        awsS3Service.downloadS3Object(bucketName, key, localDirectoryPath);
    }

    @Test
    void DownloadS3FolderSuccess() {
        long start = System.currentTimeMillis();
        final String localDirPath = "src/test/java/resources/downloads/";

        String s3BucketName = "";
        String s3FolderKey = "";
        String localDirParentName = "";

        awsS3Service.downloadS3Folder(s3BucketName, s3FolderKey, localDirPath + localDirParentName);
        long end = System.currentTimeMillis();
        System.out.println("\nFinished in " + (end - start) / 1000 + " Seconds");
    }

}
