package com.example.awslambda.services;

import com.example.awslambda.configs.AwsCredentials;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Service
public class AwsDynamoDBService {
    private AwsCredentials awsCredentials;

    private static final String TABLE_NAME = "user_images";

    public GetItemResponse getItem(String userId) {
        DynamoDbClient dynamoDbClient = createClient(awsCredentials.getAccessKey(), awsCredentials.getSecretKey());

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("userId", AttributeValue.builder().s(userId).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build();

        return dynamoDbClient.getItem(getItemRequest);
    }

    public QueryResponse getItemFiltered(String userId, String imagesCollectionId, String imageIndex) {
        log.info("userId: {}, imagesCollectionId: {}, imageIndex: {}", userId, imagesCollectionId, imageIndex);
        DynamoDbClient dynamoDbClient = createClient(awsCredentials.getAccessKey(), awsCredentials.getSecretKey());

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":userId", AttributeValue.builder().s(userId).build());
        expressionAttributeValues.put(":imagesCollectionId", AttributeValue.builder().s(imagesCollectionId).build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("userId = :userId")
                .expressionAttributeValues(expressionAttributeValues)
                .filterExpression("contains(imagesCollection[" + imageIndex + "].imagesCollectionId, :imagesCollectionId)")
                .projectionExpression("imagesCollection[" + imageIndex + "]")
                .build();

        return dynamoDbClient.query(queryRequest);
    }

    private static DynamoDbClient createClient(String accessKey, String secretKey) {
        var basicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
                .build();
    }
}
