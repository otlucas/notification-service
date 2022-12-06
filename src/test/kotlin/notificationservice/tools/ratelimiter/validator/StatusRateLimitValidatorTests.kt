package notificationservice.tools.ratelimiter.validator

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import notificationservice.tools.ratelimiter.RateLimitEventOrigin
import notificationservice.tools.ratelimiter.RateLimitRequestCommand
import notificationservice.tools.ratelimiter.cache.CacheStore
import org.junit.jupiter.api.Assertions
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class StatusRateLimitValidatorTests {

    @org.junit.Test
    fun `When calling appliesFor then return true for STATUS`() {
        // Given
        val command = RateLimitRequestCommand(
            RateLimitEventOrigin.STATUS,
            "userId"
        )
        val cacheStoreMock: CacheStore = mockk()
        val statusRateLimitValidator = StatusRateLimitValidator(cacheStoreMock)

        // Then
        assert(statusRateLimitValidator.appliesFor(command))
    }

    @org.junit.Test
    fun `When calling appliesFor then return false for other value`() {
        // Given
        val command = RateLimitRequestCommand(
            RateLimitEventOrigin.NEWS,
            "userId"
        )
        val cacheStoreMock: CacheStore = mockk()
        val statusRateLimitValidator = StatusRateLimitValidator(cacheStoreMock)

        // Then
        Assertions.assertFalse(statusRateLimitValidator.appliesFor(command))
    }

    @org.junit.Test
    fun `When calling validate then call cachestore accordingly`() {
        // Given
        val command = RateLimitRequestCommand(
            RateLimitEventOrigin.STATUS,
            "userId"
        )
        val cacheStoreMock: CacheStore = mockk {
            every {
                getRateLimiterPermit(command.userId, command.type.toString(), 2, 1, "MINUTES")
            } returns Mono.just(true)
        }
        val statusRateLimitValidator = StatusRateLimitValidator(cacheStoreMock)

        // When
        StepVerifier.create(statusRateLimitValidator.validate(command)).expectNext(true).verifyComplete()

        // Then
        verify(exactly = 1) {
            cacheStoreMock.getRateLimiterPermit(
                command.userId, command.type.toString(), 2, 1, "MINUTES"
            )
        }
    }
}