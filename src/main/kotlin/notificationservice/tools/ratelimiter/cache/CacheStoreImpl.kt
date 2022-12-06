package notificationservice.tools.ratelimiter.cache

import io.mockk.mockk
import org.redisson.Redisson
import org.redisson.api.RateIntervalUnit
import org.redisson.api.RateType
import org.redisson.api.RedissonReactiveClient
import org.redisson.config.Config
import org.springframework.stereotype.Component
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.utility.DockerImageName
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class CacheStoreImpl(
    isUnitTesting: Boolean = false
): CacheStore {
    final val redisClient: RedissonReactiveClient
    init {
        if (!isUnitTesting) {
            val redis: GenericContainer<*> = GenericContainer<Nothing>(
                DockerImageName.parse("redis:5.0.3-alpine")
            ).withExposedPorts(6379)
            redis.start()
            redis.waitingFor(
                HttpWaitStrategy().forPort(6379)
                    .withStartupTimeout(Duration.ofMinutes(1))
            )
            // Create a config object and set the appropriate Redis connection settings
            val config = Config()
            config.useSingleServer().address = "redis://localhost:${redis.getMappedPort(6379)}"

            // Initialize the RedissonReactiveClient with the config
            redisClient = Redisson.create(config).reactive()
        } else {
            redisClient = mockk()
        }
    }

    override fun getRateLimiterPermit(
        userId: String,
        type: String,
        rate: Long,
        rateInterval: Long,
        intervalUnit: String
    ): Mono<Boolean> {
        return redisClient.getRateLimiter("$userId-$type")
            .trySetRate(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.valueOf(intervalUnit))
            .flatMap {
                redisClient.getRateLimiter("$userId-$type").tryAcquire()
            }
    }
}