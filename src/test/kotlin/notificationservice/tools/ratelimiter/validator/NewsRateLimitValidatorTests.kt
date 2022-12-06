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

class NewsRateLimitValidatorTests {

    @org.junit.Test
    fun `When calling appliesFor then return true for NEWS`() {
        // Given
        val command = RateLimitRequestCommand(
            RateLimitEventOrigin.NEWS,
            "userId"
        )
        val cacheStoreMock: CacheStore = mockk()
        val newsRateLimitValidator = NewsRateLimitValidator(cacheStoreMock)

        // Then
        assert(newsRateLimitValidator.appliesFor(command))
    }

    @org.junit.Test
    fun `When calling appliesFor then return false for other value`() {
        // Given
        val command = RateLimitRequestCommand(
            RateLimitEventOrigin.MARKETING,
            "userId"
        )
        val cacheStoreMock: CacheStore = mockk()
        val newsRateLimitValidator = NewsRateLimitValidator(cacheStoreMock)

        // Then
        assertFalse(newsRateLimitValidator.appliesFor(command))
    }

    @org.junit.Test
    fun `When calling validate then call cachestore accordingly`() {
        // Given
        val command = RateLimitRequestCommand(
            RateLimitEventOrigin.NEWS,
            "userId"
        )
        val cacheStoreMock: CacheStore = mockk {
            every {
                getRateLimiterPermit(command.userId, command.type.toString(), 1, 1, "DAYS")
            } returns Mono.just(true)
        }
        val newsRateLimitValidator = NewsRateLimitValidator(cacheStoreMock)

        // When
        StepVerifier.create(newsRateLimitValidator.validate(command)).expectNext(true).verifyComplete()

        // Then
        verify(exactly = 1) {
            cacheStoreMock.getRateLimiterPermit(
                command.userId, command.type.toString(), 1, 1, "DAYS"
            )
        }
    }
}