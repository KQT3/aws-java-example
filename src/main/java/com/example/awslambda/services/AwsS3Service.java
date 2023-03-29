package com.example.awslambda.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.awslambda.configs.AwsCredentials;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
}
