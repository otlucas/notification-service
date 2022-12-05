package notificationservice.repository.cache

import org.redisson.Redisson
import org.redisson.api.RateIntervalUnit
import org.redisson.api.RateType
import org.redisson.api.RedissonReactiveClient
import org.redisson.config.Config
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CacheStoreImpl: CacheStore {
    private final val redisClient: RedissonReactiveClient
    init {
        // Create a config object and set the appropriate Redis connection settings
        val config = Config()
        config.useSingleServer().address = "redis://redis:6379"

        // Initialize the RedissonReactiveClient with the config
       redisClient = Redisson.create(config).reactive()
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