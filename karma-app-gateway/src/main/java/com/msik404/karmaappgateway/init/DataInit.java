package com.msik404.karmaappgateway.init;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import com.msik404.grpc.mongo.id.ProtoObjectId;
import com.msik404.karmaappgateway.grpc.client.GrpcDispatcherService;
import com.msik404.karmaappgateway.grpc.client.GrpcService;
import com.msik404.karmaappgateway.grpc.client.mapper.RoleMapper;
import com.msik404.karmaappgateway.grpc.client.mapper.VisibilityMapper;
import com.msik404.karmaappgateway.post.dto.Visibility;
import com.msik404.karmaappgateway.user.Role;
import com.msik404.karmaappgateway.user.exception.UserNotFoundException;
import com.msik404.karmaappposts.grpc.ChangePostVisibilityRequest;
import com.msik404.karmaappposts.grpc.CreatePostRequest;
import com.msik404.karmaappposts.grpc.PostVisibility;
import com.msik404.karmaappposts.grpc.PostsGrpc;
import com.msik404.karmaappusers.grpc.CreateUserRequest;
import com.msik404.karmaappusers.grpc.UsersGrpc;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final PostsGrpc.PostsFutureStub postsStub;
    private final UsersGrpc.UsersFutureStub usersStub;
    private final GrpcDispatcherService grpcDispatcher;
    private final GrpcService grpcService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(DataInit.class);

    private static final int USER_AMOUNT = 25;
    private static final int MAX_POSTS_PER_USER = 3;
    private static final int MIN_POSTS_PER_USER = 1;
    private static final Visibility[] VISIBILITY_OPTIONS = Visibility.class.getEnumConstants();
    private static final String ADMIN_USERNAME = "ADMIN";
    private static final String MOD_USERNAME = "MOD";

    @NonNull
    private static String getUsername(long id) {
        return String.format("username_%d", id);
    }

    @NonNull
    private static String getEmail(@NonNull String username) {
        return String.format("%s@mail.com", username);
    }

    @NonNull
    private CreateUserRequest getUserForInserting(@NonNull String username, @NonNull Role role) {

        return CreateUserRequest.newBuilder()
                .setUsername(username)
                .setEmail(getEmail(username))
                .setPassword(bCryptPasswordEncoder.encode(username))
                .setRole(RoleMapper.map(role))
                .build();
    }

    @NonNull
    private CreatePostRequest getPostForInserting(long postNum, @NonNull ProtoObjectId userId) {

        var headline = String.format("Example headline: %d of user: %s", postNum, userId.getHexString());
        var text = String.format("Example text: %d of user: %s", postNum, userId.getHexString());

        return CreatePostRequest.newBuilder()
                .setUserId(userId)
                .setHeadline(headline)
                .setText(text)
                .build();
    }

    @Value("${initialize.data}")
    private String shouldInitData;


    // TODO: Implement grpc methods for batch user and posts creations.
    @Override
    public void run(String... args) {

        if (!shouldInitData.equalsIgnoreCase("true")) {
            return;
        }

        try {
            grpcService.findUserDetails(getEmail(ADMIN_USERNAME));
            logger.info("Data initialization has been already performed.");
        } catch (UserNotFoundException ex) {
            try {
                grpcDispatcher.createUser(getUserForInserting(ADMIN_USERNAME, Role.ADMIN));
                grpcDispatcher.createUser(getUserForInserting(MOD_USERNAME, Role.MOD));

                // Async create user accounts and collect usernames.
                List<ListenableFuture<ProtoObjectId>> usersCreateFutures = new ArrayList<>(USER_AMOUNT);
                for (int userId = 0; userId < USER_AMOUNT; userId++) {
                    usersCreateFutures.add(usersStub.createUser(getUserForInserting(getUsername(userId), Role.USER)));
                }
                // Wait until finish and collect userIds.
                List<ProtoObjectId> userIds = new ArrayList<>(USER_AMOUNT);
                for (ListenableFuture<ProtoObjectId> future : usersCreateFutures) {
                    userIds.add(future.get());
                }

                // Async create random amount of posts per each user.
                List<ListenableFuture<ProtoObjectId>> postsCreateFutures = new ArrayList<>();
                var random = new Random();
                for (ProtoObjectId userId : userIds) {
                    var randPostsAmount = random.nextInt(MAX_POSTS_PER_USER - MIN_POSTS_PER_USER) + MIN_POSTS_PER_USER;
                    for (int i = 0; i < randPostsAmount; i++) {
                        postsCreateFutures.add(postsStub.createPost(getPostForInserting(i, userId)));
                    }
                }
                // Wait until finish and collect postIds.
                List<ProtoObjectId> postIds = new ArrayList<>();
                for (ListenableFuture<ProtoObjectId> future : postsCreateFutures) {
                    postIds.add(future.get());
                }

                // Async change visibility of each post to random value.
                List<ListenableFuture<Empty>> changeVisibilityFutures = new ArrayList<>(postIds.size());
                for (ProtoObjectId postId : postIds) {
                    // Get new random visibility.
                    var randVis = VisibilityMapper.map(
                            VISIBILITY_OPTIONS[random.nextInt(VISIBILITY_OPTIONS.length)]
                    );
                    changeVisibilityFutures.add(postsStub.changePostVisibility(
                            ChangePostVisibilityRequest.newBuilder()
                                    .setPostId(postId)
                                    .setVisibility(randVis)
                                    .build()
                    ));
                }
                // Wait until finish.
                for (ListenableFuture<Empty> future : changeVisibilityFutures) {
                    future.get();
                }

                logger.info("Data initialization is done.");
            } catch (ExecutionException | InterruptedException grpcEx) {
                logger.error(String.format("Failed to insert dummy data. Error message: %s", grpcEx.getMessage()));
            }
        }
    }
}