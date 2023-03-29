package com.example.awslambda;

import com.example.awslambda.services.AwsS3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AwsS3ServiceTest {
    @Autowired AwsS3Service awsS3Service;

    @Test
    void ListS3ObjectsSuccess() {
        awsS3Service.listObjects();
    }
}
