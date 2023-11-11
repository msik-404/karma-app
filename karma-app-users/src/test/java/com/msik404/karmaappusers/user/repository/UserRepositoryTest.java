package com.msik404.karmaappusers.user.repository;

import java.util.List;
import java.util.Optional;

import com.msik404.karmaappusers.MongoConfiguration;
import com.msik404.karmaappusers.TestingDataGenerator;
import com.msik404.karmaappusers.user.UserDocument;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {
        MongoConfiguration.class,
        UserRepository.class,
        CustomUserRepository.class,
        CustomUserRepositoryImpl.class
})
@EnableMongoRepositories
@Testcontainers
class UserRepositoryTest {

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

    private final MongoOperations ops;
    private final UserRepository repository;

    @Autowired
    UserRepositoryTest(MongoTemplate template, UserRepository repository) {

        this.ops = template;
        this.repository = repository;
    }

    @BeforeEach
    void setUp() {
        repository.saveAll(TestingDataGenerator.TEST_USER_DOCS);
    }

    @AfterEach
    void tearDown() {
        ops.dropCollection(UserDocument.class);
    }

    @Test
    void findUsernames_allIdsAreValid_allUsernamesAreFound() {

        // given
        List<ObjectId> ids = TestingDataGenerator.TEST_USER_DOCS.stream().map(UserDocument::getId).toList();
        List<Optional<String>> groundTruth = TestingDataGenerator.TEST_USER_DOCS.stream()
                .map(doc -> Optional.of(doc.getUsername())).toList();

        // when
        List<Optional<String>> usernames = repository.findUsernames(ids);

        // then
        assertEquals(groundTruth.size(), usernames.size());
        for (int i = 0; i < groundTruth.size(); i++) {
            assertEquals(groundTruth.get(i), usernames.get(i));
        }
    }

    @Test
    void findUsernames_OneIdIsInvalid_AllValidUsernamesAreFoundAndNullIsInProperPlaceInResult() {

        // given
        List<ObjectId> ids = new java.util.ArrayList<>(TestingDataGenerator.TEST_USER_DOCS.stream()
                .map(UserDocument::getId).toList());

        List<Optional<String>> groundTruth = new java.util.ArrayList<>(TestingDataGenerator.TEST_USER_DOCS.stream()
                .map(doc -> Optional.of(doc.getUsername())).toList());

        ids.add(3, ObjectId.get());
        groundTruth.add(3, Optional.empty());

        // when
        List<Optional<String>> usernames = repository.findUsernames(ids);

        // then
        assertEquals(groundTruth.size(), usernames.size());
        for (int i = 0; i < groundTruth.size(); i++) {
            assertEquals(groundTruth.get(i), usernames.get(i));
        }
    }

}
