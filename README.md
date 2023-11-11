# karma-app

karma-app is reddit like application which allows users to publish and rate posts. Posts are displayed based on their
score (karma score). There are different user roles. Roles allow users to change post visibility states and see posts
with certain visibilities. Currently, there are three visibility states: active, hidden, deleted. Admin users can move
every post's visibility state to every other state. Admin users can see posts with ever visibility. Mod users can move
every post's visibility state from active to hidden. Mod users can see active and hidden posts. Users who are the
creators of a given post can move it's state from active to hidden, from hidden to active and from active or hidden to
deleted. Creator users can see their owned posts with visibility state of active and hidden. Every user, even not
logged-in can see active posts.

karma-app is build using microservice architecture.

Microservices:
- [karma-app-gateway]
- [karma-app-posts]
- [karma-app-users]

Be sure to check out their documentations.

There is also [monolith][karma-app-monolith] version of this app which uses PostgreSQL 
instead of MongoDB.

# REST API documentation
Documentation can be found [here][rest-api-docs].

# Deployment

## Environment variables

Deployment requires following environment variables to be set.

- KARMA_APP_GATEWAY_REDIS_HOSTNAME
- KARMA_APP_GATEWAY_SECRET
- KARMA_APP_USERS_DB_HOST
- KARMA_APP_USERS_DB_USER
- KARMA_APP_USERS_DB_PASSWORD
- KARMA_APP_USERS_DB_NAME
- KARMA_APP_USERS_HOST
- KARMA_APP_POSTS_DB_HOST
- KARMA_APP_POSTS_DB_NAME
- KARMA_APP_POSTS_DB_USER
- KARMA_APP_POSTS_DB_PASSWORD
- KARMA_APP_POSTS_HOST

Simply create .env and place it in the root of project.

For example:

```
KARMA_APP_GATEWAY_REDIS_HOSTNAME=karma-app-redis
KARMA_APP_GATEWAY_SECRET=BARDZO-POTĘŻNY-SEKRET-JAKI-DŁUGI

KARMA_APP_USERS_DB_HOST=users-db
KARMA_APP_USERS_DB_USER=dev
KARMA_APP_USERS_DB_PASSWORD=dev
KARMA_APP_USERS_DB_NAME=users-db
KARMA_APP_USERS_HOST=karma-app-users

KARMA_APP_POSTS_DB_HOST=posts-db
KARMA_APP_POSTS_DB_NAME=posts-db
KARMA_APP_POSTS_DB_USER=dev
KARMA_APP_POSTS_DB_PASSWORD=dev
KARMA_APP_POSTS_HOST=karma-app-posts
```

### Note

KARMA_APP_GATEWAY_SECRET should have at least 32 bytes.

## Keyfile
To start the microservice keyfile in the root of the repository is required.

See [Transaction requirement] on how to generate keyfile.

## Docker compose

To start the app locally, docker compose is required.

In this repository one can
find [docker-compose-yaml].

To start the app one should run in the root of the repository:

```
docker compose up
```

To stop all containers:

```
docker compose stop
```

To remove containers and their data:

```
docker compose down -v
```

## Deployment for testing
There is also testing variant of the deployment which exposes every container port for connection. It also uses 
mongo-express to easily inspect database contents. Under [/test-deployment]  folder one can find 
[docker-compose.yaml][test-docker-compose.yaml]. Note to use this one needs to copy .env file Under [/test-deployment].

# Further development

- Unfortunately this app does not have frontend yet. Maybe in the future front will be developed. Because of the lack
  of front, CORS is not configured.
- Update post text or headline functionality.
- Search posts by headline functionality.
- Add comment section under posts feature.
- Maybe some sort of subreddits feature.

[karma-app-gateway]: https://github.com/msik-404/karma-app/tree/main/karma-app-gateway
[karma-app-posts]: https://github.com/msik-404/karma-app/tree/main/karma-app-posts
[karma-app-users]: https://github.com/msik-404/karma-app/tree/main/karma-app-users
[karma-app-monolith]: https://github.com/msik-404/karma-app-monolith
[docker-compose-yaml]: https://github.com/msik-404/karma-app/blob/main/docker-compose.yaml
[rest-api-docs]: https://github.com/msik-404/karma-app/tree/main/karma-app-gateway#rest-api
[Transaction requirement]: https://github.com/msik-404/karma-app-posts#transaction-requirements
[/test-deployment]: 
[test-docker-compose.yaml]: 