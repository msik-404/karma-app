package com.msik404.karmaappusers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.msik404.karmaappusers.grpc.impl.UsersGrpcImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class KarmaAppUsersApplication {

    @Value("${KarmaAppPosts.grpc.default.threadAmount}")
    private int threadPoolSize;

    @Value("${KarmaAppPosts.grpc.default.port}")
    private int defaultGrpcPort;

    private final UsersGrpcImpl grpcImpl;

    private static final Logger logger = LoggerFactory.getLogger(KarmaAppUsersApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(KarmaAppUsersApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return (args) -> {
            logger.info("Users gRPC server is running here.");

            ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(threadPoolSize);

            Server server = ServerBuilder
                    .forPort(defaultGrpcPort)
                    .executor(threadPoolExecutor)
                    .addService(grpcImpl)
                    .build();

            server.start();
            server.awaitTermination();
        };
    }

}
