package com.kynsoft.share.core.infrastructure.redis;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${REDIS_ADDRESS:localhost}")
    private String redisAddress;

    @Value("${REDIS_PORT:6379}")
    private Integer redisPort;

    @Value("${REDIS_USERNAME:}")
    private String redisUsername;

    @Value("${REDIS_PASSWORD:}")
    private String redisPassword;

    @Value("${REDIS_SSL:false}")
    private boolean redisSsl;

    @Value("${REDIS_SSL_VERIFY_PEER:true}")
    private boolean redisSslVerifyPeer;

    /**
     * Builds the Redisson single server address, selecting the TLS scheme when SSL is enabled.
     */
    static String buildRedissonAddress(String address, int port, boolean useSsl) {
        String scheme = useSsl ? "rediss://" : "redis://";
        return scheme + address + ":" + port;
    }

    /**
     * Builds the Lettuce client configuration, enabling TLS and optionally relaxing peer
     * verification. Peer verification is only relaxed when SSL is enabled.
     */
    static LettuceClientConfiguration buildLettuceClientConfiguration(boolean useSsl, boolean verifyPeer) {
        if (!useSsl) {
            return LettuceClientConfiguration.builder().build();
        }
        LettuceClientConfiguration.LettuceSslClientConfigurationBuilder sslBuilder =
                LettuceClientConfiguration.builder().useSsl();
        if (!verifyPeer) {
            sslBuilder.disablePeerVerification();
        }
        return sslBuilder.build();
    }

    /**
     * Builds the Redisson single server configuration, applying credentials only when provided and
     * relaxing peer verification only when SSL is enabled and verification is explicitly turned off.
     *
     * <p>Relaxing peer verification disables both the certificate chain validation and the hostname
     * check, matching the semantics of the Lettuce and Jedis clients so that the
     * {@code REDIS_SSL_VERIFY_PEER} flag means the same thing across all three.
     */
    static Config buildRedissonConfig(String address, int port, String username, String password,
                                      boolean useSsl, boolean verifyPeer) {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(buildRedissonAddress(address, port, useSsl))
                .setUsername(username.isEmpty() ? null : username)
                .setPassword(password.isEmpty() ? null : password);

        if (useSsl && !verifyPeer) {
            // Skip the hostname check and accept any certificate chain, mirroring Lettuce's
            // disablePeerVerification() and Jedis' SslVerifyMode.INSECURE.
            serverConfig.setSslEnableEndpointIdentification(false);
            serverConfig.setSslTrustManagerFactory(InsecureTrustManagerFactory.INSTANCE);
            logger.warn("Redisson TLS certificate verification is DISABLED for Redis at {}:{}. "
                    + "Certificate chain and hostname are not validated. "
                    + "Do not use REDIS_SSL_VERIFY_PEER=false in production.", address, port);
        }

        return config;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisAddress, redisPort);

        if (!redisUsername.isEmpty()) {
            redisConfig.setUsername(redisUsername);
        }
        if (!redisPassword.isEmpty()) {
            redisConfig.setPassword(redisPassword);
        }
     //   redisConfig.setUsername("redis");
//        redisConfig.setPassword("IdoHj0o1oe");
        LettuceClientConfiguration clientConfig = buildLettuceClientConfiguration(redisSsl, redisSslVerifyPeer);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig, clientConfig);
        factory.afterPropertiesSet();

        // Connection check
        try (RedisConnection connection = factory.getConnection()) {
            String pingResponse = connection.ping();
            logger.info("Redis connection established with address: {}:{}, ssl: {} and username: {}. Ping response: {}",
                    redisAddress, redisPort, redisSsl, redisUsername.isEmpty() ? "none" : redisUsername, pingResponse);
        } catch (Exception e) {
            logger.error("Failed to connect to Redis at {}:{} with ssl: {}", redisAddress, redisPort, redisSsl, e);
        }

        return factory;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = buildRedissonConfig(redisAddress, redisPort, redisUsername, redisPassword,
                redisSsl, redisSslVerifyPeer);

        RedissonClient client = Redisson.create(config);
        logger.info("Redisson client configured for Redis at {}:{} with ssl: {}", redisAddress, redisPort, redisSsl);
        return client;
    }
}
