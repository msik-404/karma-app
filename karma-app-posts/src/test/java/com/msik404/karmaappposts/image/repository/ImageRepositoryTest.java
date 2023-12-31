package com.msik404.karmaappposts.image.repository;

import java.util.Optional;

import com.msik404.karmaappposts.MongoConfiguration;
import com.msik404.karmaappposts.TestingDataGenerator;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        MongoConfiguration.class,
        ImageRepository.class,
        CustomImageRepository.class,
        CustomImageRepositoryImpl.class
})
@EnableMongoRepositories
@Testcontainers
class ImageRepositoryTest {

    public static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    static {
        MONGO_DB_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "test");
    }

    private final ImageRepository repository;

    private final TestingDataGenerator dataGenerator;

    @Autowired
    ImageRepositoryTest(ImageRepository repository) {

        this.repository = repository;

        dataGenerator = new TestingDataGenerator();
    }

    @BeforeEach
    void setUp() {
        repository.insert(dataGenerator.getTestImageDocs());
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void findImageDataById_RequestedDocExistsAndHasImage_OnlyImageDataIsFetched() {

        // given
        int idx = 0;
        var groundTruth = dataGenerator.getTestImageDocs().get(idx);

        // when
        Optional<Binary> optionalImageData = repository.findImageDataById(groundTruth.getPostId());

        // then
        assertTrue(optionalImageData.isPresent());
        assertArrayEquals(groundTruth.getImageData().getData(), optionalImageData.get().getData());
    }

    @Test
    void findImageDataById_RequestedDocDoesNotExist_OptionalEmpty() {

        // given
        ObjectId nonExistingId = ObjectId.get();

        // when
        Optional<Binary> optionalImageData = repository.findImageDataById(nonExistingId);

        // then
        assertTrue(optionalImageData.isEmpty());
    }

}
