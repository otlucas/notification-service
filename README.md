# Rate Limiter for Notification Service

A rate limiter service built into a NotificationService made with:
- Kotlin
- Spring
- Reactor
- Redisson
- Cucumber for testing

For solving the exercise I started with this [basic implementation](https://redis.com/redis-best-practices/basic-rate-limiting/) but soon discovered that Redisson already has a solution implemented so then I focused on how to organize my own Implementation for the different notification types. The same way I would do in an actual job.

The solution is located on the folder "tools.ratelimiter".

## Features

- Uses [Redisson](https://redisson.org/) for rate limiting. [Rate limiters with Redisson](https://redisson.org/glossary/rate-limiter.html)
- Allows the possibility of adding new rate limiting policies to users easily by adding validators.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Kotlin
- Gradle
- Docker

### Installing

Clone the repository and build the project using Gradle:

```
git clone https://github.com/otlucas/notification-service.git
gradle build
```

Start the service by running the `App` class from your IDE or with the following command, after starting up docker environment:

```
gradle run
```

The service will start on port 8080 by default.

## API

The service exposes a REST API to send notifications to users . The base URL is: `http://localhost:8080/notifications`.

Here are some examples of how to use the API:

### Send a notification

To send a notification, send a `POST` request to `/notifications` with a JSON body that specifies the userId, type of the notification and message, for example:

```
{
    "userId": "user_id_1",
    "notificationType": "MARKETING",
    "message": "MOCK_MESSAGE"
}
```

This will validate against the rate limiter. The response will include the status and message:
- In case of success:
```
{
    "status": "OK",
    "message": "Notification was sent."
}
```
- In case of failure:
```
{
    "status": "ERROR",
    "message": "Rate limiter policies for MARKETING for user user_id_1 exceeded"
}
```

Extra notes:

- With more time I would do the following tasks:
  - Refactor solution to be able to add rate limiters more easily.
  - Validators could possibly have only one function, deleting appliesFor() method.
  - I would add integration tests that assert successful notifications after waiting n time.
  - I would add stress tests if I had a production redis.

On the integration tests with waiting, this is currently not possible given that the library I used uses:
```
System.currentTimeMillis()
```
Here [link to library code](https://github.com/redisson/redisson/blob/dc0c75d494e6d0338944df10c9db071c70f47c79/redisson/src/main/java/org/redisson/RedissonRateLimiter.java#:~:text=long%20s%20%3D%20System.currentTimeMillis()%3B)

This function is not easily mocked.