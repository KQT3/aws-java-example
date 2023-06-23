package com.example.awslambda.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.example.awslambda.configs.AwsCredentials;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AwsS3Service {
    AwsCredentials awsCredentials;
    private static final String S3_BUCKET = "qt3test";

    public void listObjects() {
        var credentials = new BasicAWSCredentials(awsCredentials.getAccessKey(), awsCredentials.getSecretKey());

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_NORTH_1)
                .build();

        ObjectListing objectListing = s3client.listObjects(S3_BUCKET);
        for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
            System.out.println(os.getKey());
        }
    }

    public void downloadS3Object(String bucketName, String key, String localDirectoryPath) {
        var credentials = new BasicAWSCredentials(awsCredentials.getAccessKey(), awsCredentials.getSecretKey());
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();

        try {
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, key));
            InputStream objectContent = s3Object.getObjectContent();

            String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);
            String localFileName = decodedKey.substring(decodedKey.lastIndexOf('/') + 1);
            File localFile = new File(localDirectoryPath + localFileName);

            Path localPath = localFile.toPath();
            Files.copy(objectContent, localPath, StandardCopyOption.REPLACE_EXISTING);
            objectContent.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadS3Folder(String bucketName, String folderKey, String localDirectoryPath) {
        var credentials = new BasicAWSCredentials(awsCredentials.getAccessKey(), awsCredentials.getSecretKey());
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();

        try {
            ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withPrefix(folderKey);

            ListObjectsV2Result listObjectsResult;
            do {
                listObjectsResult = s3Client.listObjectsV2(listObjectsRequest);

                for (S3ObjectSummary objectSummary : listObjectsResult.getObjectSummaries()) {
                    String objectKey = objectSummary.getKey();
                    if (!objectKey.endsWith("/")) {
                        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, objectKey));

                        String relativeFilePath = objectKey.substring(folderKey.length());
                        String localFilePath = localDirectoryPath + File.separator + relativeFilePath;

                        File localFile = new File(localFilePath);
                        boolean mkdirs = localFile.getParentFile().mkdirs();
                        if (mkdirs) System.out.println("Directory created: " + localFile.getParentFile().getName());

                        try (InputStream objectContent = object.getObjectContent()) {
                            Files.copy(objectContent, localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }

                    } else {
                        String relativeFolderPath = objectKey.substring(folderKey.length());
                        String localFolderPath = localDirectoryPath + File.separator + relativeFolderPath;

                        File localFolder = new File(localFolderPath);
                        boolean mkdirs = localFolder.mkdirs();
                        if (mkdirs) System.out.println("Directory created: " + localFolder.getParentFile().getName());
                    }
                }

                List<String> commonPrefixes = listObjectsResult.getCommonPrefixes();
                for (String commonPrefix : commonPrefixes) {
                    downloadS3Folder(bucketName, commonPrefix, localDirectoryPath);
                }

                listObjectsRequest.setContinuationToken(listObjectsResult.getNextContinuationToken());
            } while (listObjectsResult.isTruncated());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
