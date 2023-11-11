# karma-app-users

Users microservice for karma-app microservices version. karma-app-users is grpc server with access to mongodb database.

Check out other karma-app microservices:

- [karma-app-gateway]
- [karma-app-posts]

# Technologies used

- Java 21
- MongoDB
- Docker
- [gRPC]
- Java spring
- [spring-boot-starter-data-mongodb]
- spring-boot-starter-test
- [spring-boot-testcontainers]
- junit-jupiter
- [grpc-java]
- [protovalidate-java]
- lombok

# gRPC, Protobuf and protovalidate

[gRPC] is a modern open source high performance Remote Procedure Call (RPC) framework that can run in any environment. 
gRPC simplifies microservices API implementation and later the usage of the API. gRPC is self-documenting, all available 
service methods and message structures can be found inside [karma-app-users.proto].

In this project to help with message validation I use: [protovalidate-java]. This project significantly simplifies 
validation of messages and reduces the time required to build stable system. Additionally potential user of this 
microservice can see which fields are required and what constraints need to be met to build valid message.

## Using postman

To test this microservice in postman one must not only import .proto file in postman service definition but also import
path to .proto files of protovalidate-java. Usually this path looks something like this:
some_personal_path/karma-app-users/target/protoc-dependencies/some-long-code. Under some-long-code there should be the
following files buf/validate/priv/expression.proto and buf/validate/priv/validate.proto.

Additionally, [mongo_object_id.proto] also needs to be imported in the same manner sa above. Just import path
to [proto folder]. This is because mongo_object_id defines messages which are used in many microservices. Duplicating 
them would case code-duplication and smelly code.

# Features

## gRPC API

Documentation for the API can be found [here][gRPC-API-docs].

## Exception encoding

When some exception which is not critical is thrown on the backend side, it is being encoded and passed with appropriate
gRPC code to the caller. Each exception has its unique identifier. With this it can be decoded on the caller side.
In this setup client side can use the same exception classes as backend.

Simple [encoding class] which simply inserts "exceptionId EXCEPTION_ID" at the begging of error message. This 
EXCEPTION_ID can be parsed with simple regex.

Each encodable exception must implement [EncodableException] and [GrpcStatusException].

# Building the project

To get target folder and build the project with maven simply run:

```
./mvnw clean package -DskipTests
```

If one would like to build the project with running the tests, one must have docker installed on their machine and run:

```
./mvnw clean package
```

# Tests

Docker is required to run tests locally because I use [Testcontainers for Java].

All the code which comes into contact with data persistence is tested in integration tests under [src/test].
The rest of the code is much simpler and easier to follow and was tested manually using postman.

# Starting the microservices

## Environment variables

Backend requires four environment variables to be set:

- KARMA_APP_USERS_DB_HOST
- KARMA_APP_USERS_DB_USER
- KARMA_APP_USERS_DB_PASSWORD
- KARMA_APP_USERS_DB_NAME

for details see: [application.yaml].

Additionally, [docker-compose.yaml] uses:

- KARMA_APP_USERS_HOST

Simply create .env and place it in the root of project.

For example:

```
KARMA_APP_USERS_DB_HOST=users-db
KARMA_APP_USERS_DB_USER=dev
KARMA_APP_USERS_DB_PASSWORD=dev
KARMA_APP_USERS_DB_NAME=users-db
KARMA_APP_USERS_HOST=karma-app-users
```

## Docker compose

To start the microservice locally, docker compose is required.

In this repository one can find [docker-compose-yaml].

To start all containers one should run in the root of the project:

```
docker compose up
```

To stop containers:

```
docker compose stop
```

To remove containers and their data:

```
docker compose down -v
```

[spring-boot-starter-data-mongodb]: https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/
[spring-boot-testcontainers]: https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1
[grpc-java]: https://github.com/grpc/grpc-java
[protovalidate-java]: https://github.com/bufbuild/protovalidate-java
[gRPC]: https://grpc.io/
[Testcontainers for Java]: https://java.testcontainers.org/

[karma-app-gateway]: https://github.com/msik-404/karma-app/tree/main/karma-app-gateway
[karma-app-posts]: https://github.com/msik-404/karma-app/tree/main/karma-app-posts
[docker-compose.yaml]: https://github.com/msik-404/karma-app/blob/main/karma-app-users/docker-compose.yaml
[application.yaml]: https://github.com/msik-404/karma-app/blob/main/karma-app-users/src/main/resources/application.yaml
[karma-app-users.proto]: https://github.com/msik-404/karma-app/blob/main/karma-app-users/src/main/proto/karma_app_users.proto
[mongo_object_id.proto]: https://github.com/msik-404/karma-app/blob/main/karma-app-users/src/main/proto/mongo_object_id.proto
[proto folder]: https://github.com/msik-404/karma-app/tree/main/karma-app-users/src/main/proto
[gRPC-API-docs]: https://github.com/msik-404/karma-app/blob/main/karma-app-users/gRPC_API_docs.md
[encoding class]: https://github.com/msik-404/karma-app/blob/main/karma-app-users/src/main/java/com/msik404/karmaappusers/encoding/ExceptionEncoder.java
[EncodableException]: https://github.com/msik-404/karma-app/blob/main/karma-app-users/src/main/java/com/msik404/karmaappusers/encoding/EncodableException.java
[GrpcStatusException]: https://github.com/msik-404/karma-app/blob/main/karma-app-users/src/main/java/com/msik404/karmaappusers/grpc/impl/exception/GrpcStatusException.java
[src/test]: https://github.com/msik-404/karma-app/tree/main/karma-app-users/src/test