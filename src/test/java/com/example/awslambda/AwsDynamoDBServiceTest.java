package com.example.awslambda;

import com.example.awslambda.services.AwsDynamoDBService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AwsDynamoDBServiceTest {
    @Autowired AwsDynamoDBService awsDynamoDBService;
    final String testUserId = "ec8e3d53-5b88-438e-8187-4d4aacdebb04";

    @Test
    void getItemSuccess() {
        var getItemResponse = awsDynamoDBService.getItem(testUserId);
        getItemResponse.item().values().forEach(System.out::println);
    }

    @Test
    void getItemFilterSuccess() {
        String imageIndex = "0";
        final String testImagesCollectionId0 = "5ec5d9a2-3104-4472-89ad-fc45bf4ade51";
        final String testImagesCollectionId1 = "f0f48770-79e1-4735-a01e-4de5f154926d";
        var getItemResponse = awsDynamoDBService.getItemFiltered(testUserId, testImagesCollectionId0, imageIndex);
        System.out.println(getItemResponse);
    }
}
