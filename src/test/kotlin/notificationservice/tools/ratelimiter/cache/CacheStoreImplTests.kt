package notificationservice.tools.ratelimiter.cache

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.redisson.api.RRateLimiterReactive
import org.redisson.api.RateIntervalUnit
import org.redisson.api.RateType
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class CacheStoreImplTests {
    private val cacheStoreImpl = CacheStoreImpl(true)

    @org.junit.Test
    fun `When acquiring limit then redis client is called correctly`() {
        // Given
        val rateLimiterMock: RRateLimiterReactive = mockk {
            every {
                trySetRate(RateType.OVERALL, 3, 1, RateIntervalUnit.HOURS)
            } returns Mono.just(false)

            every {
                tryAcquire()
            } returns Mono.just(true)
        }
        every { cacheStoreImpl.redisClient.getRateLimiter("userid-MARKETING") } returns rateLimiterMock

        // When
        StepVerifier.create(
            cacheStoreImpl.getRateLimiterPermit("userid", "MARKETING", 3, 1, "HOURS")
        ).expectNext(true).verifyComplete()

        // Then
        verify(exactly = 1) {
            rateLimiterMock.trySetRate(RateType.OVERALL, 3, 1, RateIntervalUnit.HOURS)
        }
        verify(exactly = 2) {
            cacheStoreImpl.redisClient.getRateLimiter("userid-MARKETING")
        }
        verify (exactly = 1) {
            rateLimiterMock.tryAcquire()
        }
    }

    @org.junit.Test
    fun `When acquiring limit is false then cache store returns false`() {
        // Given
        val rateLimiterMock: RRateLimiterReactive = mockk {
            every {
                trySetRate(RateType.OVERALL, 2, 2, RateIntervalUnit.HOURS)
            } returns Mono.just(false)

            every {
                tryAcquire()
            } returns Mono.just(false)
        }
        every { cacheStoreImpl.redisClient.getRateLimiter("userid-MARKETING") } returns rateLimiterMock

        // When
        StepVerifier.create(
            cacheStoreImpl.getRateLimiterPermit("userid", "MARKETING", 2, 2, "HOURS")
        ).expectNext(false).verifyComplete()

        // Then
        verify(exactly = 1) {
            rateLimiterMock.trySetRate(RateType.OVERALL, 2, 2, RateIntervalUnit.HOURS)
        }
        verify(exactly = 2) {
            cacheStoreImpl.redisClient.getRateLimiter("userid-MARKETING")
        }
        verify (exactly = 1) {
            rateLimiterMock.tryAcquire()
        }
    }

    @org.junit.Test
    fun `When rate limiter was not initialized previously then acquire is called the same way`() {
        // Given
        val rateLimiterMock: RRateLimiterReactive = mockk {
            every {
                trySetRate(RateType.OVERALL, 3, 1, RateIntervalUnit.HOURS)
            } returns Mono.just(false)

            every {
                tryAcquire()
            } returns Mono.just(true)
        }
        every { cacheStoreImpl.redisClient.getRateLimiter("userid-NEWS") } returns rateLimiterMock

        // When
        StepVerifier.create(
            cacheStoreImpl.getRateLimiterPermit("userid", "NEWS", 3, 1, "HOURS")
        ).expectNext(true).verifyComplete()

        // Then
        verify(exactly = 1) {
            rateLimiterMock.trySetRate(RateType.OVERALL, 3, 1, RateIntervalUnit.HOURS)
        }
        verify(exactly = 2) {
            cacheStoreImpl.redisClient.getRateLimiter("userid-NEWS")
        }
        verify (exactly = 1) {
            rateLimiterMock.tryAcquire()
        }
    }
}