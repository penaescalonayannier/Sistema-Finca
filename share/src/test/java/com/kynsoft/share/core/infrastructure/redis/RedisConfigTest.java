package com.kynsoft.share.core.infrastructure.redis;

import io.lettuce.core.SslVerifyMode;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedisConfigTest {

    @Test
    @DisplayName("Redisson address uses the redis:// scheme when SSL is disabled")
    void redissonAddressUsesPlainSchemeWhenSslDisabled() {
        String address = RedisConfig.buildRedissonAddress("localhost", 6379, false);

        assertEquals("redis://localhost:6379", address);
    }

    @Test
    @DisplayName("Redisson address uses the rediss:// scheme when SSL is enabled")
    void redissonAddressUsesSecureSchemeWhenSslEnabled() {
        String address = RedisConfig.buildRedissonAddress("localhost", 6379, true);

        assertEquals("rediss://localhost:6379", address);
    }

    @Test
    @DisplayName("Redisson address carries the configured host and port")
    void redissonAddressCarriesHostAndPort() {
        assertEquals("rediss://redis.preprod.internal:6380",
                RedisConfig.buildRedissonAddress("redis.preprod.internal", 6380, true));
        assertEquals("redis://10.0.0.5:16379",
                RedisConfig.buildRedissonAddress("10.0.0.5", 16379, false));
    }

    @Test
    @DisplayName("Lettuce client configuration has TLS disabled when SSL is disabled")
    void lettuceClientConfigurationWithoutSsl() {
        LettuceClientConfiguration configuration = RedisConfig.buildLettuceClientConfiguration(false, true);

        assertFalse(configuration.isUseSsl());
    }

    @Test
    @DisplayName("Peer verification stays enabled when SSL is enabled and verification is requested")
    void lettuceClientConfigurationWithSslAndPeerVerification() {
        LettuceClientConfiguration configuration = RedisConfig.buildLettuceClientConfiguration(true, true);

        assertTrue(configuration.isUseSsl());
        assertEquals(SslVerifyMode.FULL, configuration.getVerifyMode());
    }

    @Test
    @DisplayName("Peer verification is disabled when SSL is enabled and verification is turned off")
    void lettuceClientConfigurationWithSslAndWithoutPeerVerification() {
        LettuceClientConfiguration configuration = RedisConfig.buildLettuceClientConfiguration(true, false);

        assertTrue(configuration.isUseSsl());
        assertEquals(SslVerifyMode.NONE, configuration.getVerifyMode());
    }

    @Test
    @DisplayName("Peer verification flag is ignored while SSL is disabled")
    void peerVerificationFlagIgnoredWhenSslDisabled() {
        LettuceClientConfiguration configuration = RedisConfig.buildLettuceClientConfiguration(false, false);

        assertFalse(configuration.isUseSsl());
    }

    @Test
    @DisplayName("Redisson keeps strict TLS verification when SSL is disabled")
    void redissonConfigWithoutSslKeepsStrictVerification() {
        SingleServerConfig serverConfig = RedisConfig
                .buildRedissonConfig("localhost", 6379, "", "", false, true)
                .useSingleServer();

        assertTrue(serverConfig.isSslEnableEndpointIdentification());
        assertNull(serverConfig.getSslTrustManagerFactory());
    }

    @Test
    @DisplayName("Redisson keeps strict TLS verification when SSL is enabled and peer verification is requested")
    void redissonConfigWithSslAndPeerVerificationKeepsStrictVerification() {
        SingleServerConfig serverConfig = RedisConfig
                .buildRedissonConfig("localhost", 6379, "", "", true, true)
                .useSingleServer();

        assertTrue(serverConfig.isSslEnableEndpointIdentification());
        assertNull(serverConfig.getSslTrustManagerFactory());
    }

    @Test
    @DisplayName("Redisson relaxes chain and hostname verification when SSL is enabled and peer verification is off")
    void redissonConfigWithSslAndWithoutPeerVerificationRelaxesVerification() {
        SingleServerConfig serverConfig = RedisConfig
                .buildRedissonConfig("localhost", 6379, "", "", true, false)
                .useSingleServer();

        assertFalse(serverConfig.isSslEnableEndpointIdentification());
        assertSame(InsecureTrustManagerFactory.INSTANCE, serverConfig.getSslTrustManagerFactory());
    }

    @Test
    @DisplayName("Redisson ignores the peer verification flag while SSL is disabled")
    void redissonConfigIgnoresPeerVerificationFlagWhenSslDisabled() {
        SingleServerConfig serverConfig = RedisConfig
                .buildRedissonConfig("localhost", 6379, "", "", false, false)
                .useSingleServer();

        assertTrue(serverConfig.isSslEnableEndpointIdentification());
        assertNull(serverConfig.getSslTrustManagerFactory());
    }

    @Test
    @DisplayName("Redisson configuration carries the address scheme, host and port")
    void redissonConfigCarriesAddress() {
        assertEquals("rediss://redis.preprod.internal:6380", RedisConfig
                .buildRedissonConfig("redis.preprod.internal", 6380, "", "", true, true)
                .useSingleServer()
                .getAddress());
        assertEquals("redis://10.0.0.5:16379", RedisConfig
                .buildRedissonConfig("10.0.0.5", 16379, "", "", false, true)
                .useSingleServer()
                .getAddress());
    }

    @Test
    @DisplayName("Redisson credentials are applied when username and password are provided")
    void redissonConfigAppliesCredentialsWhenProvided() {
        SingleServerConfig serverConfig = RedisConfig
                .buildRedissonConfig("localhost", 6379, "redis", "secret", false, true)
                .useSingleServer();

        assertEquals("redis", serverConfig.getUsername());
        assertEquals("secret", serverConfig.getPassword());
    }

    @Test
    @DisplayName("Redisson credentials stay unset when username and password are empty")
    void redissonConfigOmitsCredentialsWhenEmpty() {
        SingleServerConfig serverConfig = RedisConfig
                .buildRedissonConfig("localhost", 6379, "", "", false, true)
                .useSingleServer();

        assertNull(serverConfig.getUsername());
        assertNull(serverConfig.getPassword());
    }

    @Test
    @DisplayName("Each Redisson configuration build produces an independent instance")
    void redissonConfigBuildsIndependentInstances() {
        Config insecure = RedisConfig.buildRedissonConfig("localhost", 6379, "", "", true, false);
        Config secure = RedisConfig.buildRedissonConfig("localhost", 6379, "", "", true, true);

        assertNotSame(insecure, secure);
        assertFalse(insecure.useSingleServer().isSslEnableEndpointIdentification());
        assertTrue(secure.useSingleServer().isSslEnableEndpointIdentification());
    }
}
