package notificationservice.tools.ratelimiter.validator

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import notificationservice.tools.ratelimiter.RateLimitEventOrigin
import notificationservice.tools.ratelimiter.RateLimitRequestCommand
import notificationservice.tools.ratelimiter.cache.CacheStore
import org.junit.jupiter.api.Assertions.assertFalse
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class MarketingRateLimitValidatorTests {

    @org.junit.Test
    fun `When calling appliesFor then return true for MARKETING`() {
        // Given
        val command = RateLimitRequestCommand(
            RateLimitEventOrigin.MARKETING,
            "userId"
        )
        val cacheStoreMock: CacheStore = mockk()
        val marketingRateLimitValidator = MarketingRateLimitValidator(cacheStoreMock)

        // Then
        assert(marketingRateLimitValidator.appliesFor(command))
    }

    @org.junit.Test
    fun `When calling appliesFor then return false for other value`() {
        // Given
        val command = RateLimitRequestCommand(
            RateLimitEventOrigin.NEWS,
            "userId"
        )
        val cacheStoreMock: CacheStore = mockk()
        val marketingRateLimitValidator = MarketingRateLimitValidator(cacheStoreMock)

        // Then
        assertFalse(marketingRateLimitValidator.appliesFor(command))
    }

    @org.junit.Test
    fun `When calling validate then call cachestore accordingly`() {
        // Given
        val command = RateLimitRequestCommand(
            RateLimitEventOrigin.MARKETING,
            "userId"
        )
        val cacheStoreMock: CacheStore = mockk {
            every {
                getRateLimiterPermit(command.userId, command.type.toString(), 3, 1, "HOURS")
            } returns Mono.just(true)
        }
        val marketingRateLimitValidator = MarketingRateLimitValidator(cacheStoreMock)

        // When
        StepVerifier.create(marketingRateLimitValidator.validate(command)).expectNext(true).verifyComplete()

        // Then
        verify(exactly = 1) {
            cacheStoreMock.getRateLimiterPermit(
                command.userId,
                command.type.toString(),
                3,
                1,
                "HOURS"
            )
        }
    }
}