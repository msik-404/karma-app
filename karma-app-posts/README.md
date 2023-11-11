# karma-app-posts

Posts microservice for karma-app microservices version. karma-app-posts is grpc server with access to mongodb database.

Check out other karma-app microservices:

- [karma-app-gateway]
- [karma-app-users]

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
service methods and message structures can be found inside [karma-app-posts.proto].

In this project to help with message validation [protovalidate-java] is
used.
This project significantly simplifies validation of messages and reduces the time required to build stable system.
Additionally potential user of this microservice can see which fields are required and what
constraints need to be met to build valid message.

## Using postman

To test this microservice in postman one must not only import .proto file in postman service definition but also import
path to .proto files of protovalidate-java. Usually this path looks something like this:
some_personal_path/karma-app-posts/target/protoc-dependencies/some-long-code. Under some-long-code there should be the
following files buf/validate/priv/expression.proto and buf/validate/priv/validate.proto.

Additionally, [mongo_object_id.proto] also needs to be imported in the same manner sa above. Just import path
to [proto folder]. This is because mongo_object_id defines messages which are used in many microservices. 
Duplicating them would case code-duplication and smelly code.

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

Docker is required to run tests locally because [Testcontainers for Java] is used for integration tests.

All the code which comes into contact with data persistence is tested in integration tests under [src/test].
The rest of the code is much simpler and easier to follow and was tested manually using postman.

# Transaction requirements

Because backend of this microservice uses transactions, mongodb cannot be run in standalone server mode. It needs
either replica-set or cluster. In this case single node replica-set is used. Inside  [mongo health-check] there is a 
simple script which checks for replica-set status. If status indicates that replica-set is not initiated, initiation 
happens. If initiation is successful, 1 is returned and container is healthy, else container is unhealthy. Other 
containers wait for mongo container to become healthy.

Mongo replica-set authentication minimally requires `keyfile`.
To generate one simply run:

```
openssl rand -base64 756 > keyfile
chmod 600 keyfile
```

# Starting the microservice | deployment for testing

## Environment variables

Backend requires four environment variables to be set:

- KARMA_APP_POSTS_DB_HOST
- KARMA_APP_POSTS_DB_NAME
- KARMA_APP_POSTS_DB_USER
- KARMA_APP_POSTS_DB_PASSWORD

for details see: [application.yaml].

Additionally, [docker-compose.yaml] needs:

- KARMA_APP_POSTS_HOST

Simply create .env and place it in the root of project.

For example:

```
KARMA_APP_POSTS_DB_HOST=posts-db
KARMA_APP_POSTS_DB_NAME=posts-db
KARMA_APP_POSTS_DB_USER=dev
KARMA_APP_POSTS_DB_PASSWORD=dev
KARMA_APP_POSTS_HOST=karma-app-posts
```

## Keyfile
To start the microservice keyfile in the root of the repository is required.

See [Transaction requirement] on how to generate keyfile.

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
[Testcontainers for Java]: (https://java.testcontainers.org/)

[karma-app-gateway]: https://github.com/msik-404/karma-app/tree/main/karma-app-gateway
[karma-app-users]: https://github.com/msik-404/karma-app/tree/main/karma-app-users
[karma-app-posts.proto]: https://github.com/msik-404/karma-app/blob/main/karma-app-posts/src/main/proto/karma_app_posts.proto
[mongo_object_id.proto]: https://github.com/msik-404/karma-app/blob/main/karma-app-posts/src/main/proto/mongo_object_id.proto
[proto folder]: https://github.com/msik-404/karma-app/tree/main/karma-app-posts/src/main/proto
[gRPC-API-docs]: https://github.com/msik-404/karma-app/blob/main/karma-app-posts/gRPC_API_docs.md
[encoding class]: https://github.com/msik-404/karma-app/blob/main/karma-app-posts/src/main/java/com/msik404/karmaappposts/encoding/ExceptionEncoder.java
[encodableException]: https://github.com/msik-404/karma-app/blob/main/karma-app-posts/src/main/java/com/msik404/karmaappposts/encoding/EncodableException.java
[GrpcStatusException]: https://github.com/msik-404/karma-app/blob/main/karma-app-posts/src/main/java/com/msik404/karmaappposts/grpc/impl/exception/GrpcStatusException.java
[src/test]: https://github.com/msik-404/karma-app/tree/main/karma-app-posts/src/test
[mongo health-check]: https://github.com/msik-404/karma-app/blob/main/karma-app-posts/docker-compose.yaml#L33
[application.yaml]: https://github.com/msik-404/karma-app/blob/main/karma-app-posts/src/main/resources/application.yaml
[docker-compose.yaml]: https://github.com/msik-404/karma-app/blob/main/karma-app-posts/docker-compose.yaml
[Transaction requirement]: https://github.com/msik-404/karma-app/tree/main/karma-app-posts#transaction-requirements