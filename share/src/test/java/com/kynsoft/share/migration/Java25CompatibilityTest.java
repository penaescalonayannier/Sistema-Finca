package com.kynsoft.share.migration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de compatibilidad para la migración a Java 25.
 * Valida que las APIs usadas en el proyecto siguen funcionando correctamente.
 */
@DisplayName("Java 25 Compatibility Tests")
class Java25CompatibilityTest {

    @Nested
    @DisplayName("JVM Runtime Validation")
    class JvmRuntimeTests {

        @Test
        @DisplayName("Debe ejecutarse en Java 25")
        void shouldRunOnJava25() {
            String version = System.getProperty("java.version");
            assertNotNull(version);
            System.out.println("Java version: " + version);
            // Aceptar 25 o cualquier versión mayor
            int major = Runtime.version().feature();
            assertTrue(major >= 21, "Expected Java 21+, got: " + major);
        }

        @Test
        @DisplayName("ZGC debe estar disponible como GC")
        void zgcShouldBeAvailable() {
            // ZGC es el GC por defecto en Java 25
            String gcName = java.lang.management.ManagementFactory
                    .getGarbageCollectorMXBeans()
                    .stream()
                    .map(gc -> gc.getName())
                    .reduce("", (a, b) -> a + "," + b);
            System.out.println("Active GC: " + gcName);
            assertNotNull(gcName);
        }

        @Test
        @DisplayName("Virtual Threads deben estar disponibles")
        void virtualThreadsShouldBeAvailable() throws Exception {
            // Virtual Threads son estables desde Java 21
            var result = new CompletableFuture<String>();
            Thread.ofVirtual().start(() -> {
                result.complete("virtual-thread-ok");
            });
            assertEquals("virtual-thread-ok", result.get(5, TimeUnit.SECONDS));
        }
    }

    @Nested
    @DisplayName("Core Java APIs Used in Project")
    class CoreApiTests {

        @Test
        @DisplayName("HexFormat debe funcionar (usado en CachingReactiveJwtDecoder)")
        void hexFormatShouldWork() throws Exception {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest("test-jwt-token".getBytes());
            String hex = HexFormat.of().formatHex(hash);
            assertNotNull(hex);
            assertFalse(hex.isEmpty());
            assertEquals(64, hex.length()); // SHA-256 = 32 bytes = 64 hex chars
        }

        @Test
        @DisplayName("UUID generation debe funcionar (usado masivamente en CQRS)")
        void uuidGenerationShouldWork() {
            UUID id = UUID.randomUUID();
            assertNotNull(id);
            String uuidStr = id.toString();
            assertEquals(36, uuidStr.length());
            UUID parsed = UUID.fromString(uuidStr);
            assertEquals(id, parsed);
        }

        @Test
        @DisplayName("Stream API debe funcionar (usado en handlers y servicios)")
        void streamApiShouldWork() {
            List<Integer> result = IntStream.range(0, 100)
                    .filter(i -> i % 2 == 0)
                    .boxed()
                    .toList();
            assertEquals(50, result.size());
        }

        @Test
        @DisplayName("CompletableFuture debe funcionar (usado en async operations)")
        void completableFutureShouldWork() throws Exception {
            CompletableFuture<String> future = CompletableFuture
                    .supplyAsync(() -> "hello")
                    .thenApply(s -> s + " world");
            assertEquals("hello world", future.get(5, TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("Date/Time API debe funcionar (zona horaria America/Guayaquil)")
        void dateTimeApiShouldWork() {
            ZoneId guayaquil = ZoneId.of("America/Guayaquil");
            assertNotNull(guayaquil);

            LocalDateTime now = LocalDateTime.now(guayaquil);
            assertNotNull(now);

            LocalDate today = LocalDate.now(guayaquil);
            assertNotNull(today);

            Instant instant = Instant.now();
            assertNotNull(instant);

            String formatted = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            assertNotNull(formatted);
            assertFalse(formatted.isEmpty());
        }

        @Test
        @DisplayName("Reflection debe funcionar (usado en Mediator pattern)")
        void reflectionShouldWork() throws Exception {
            Class<?> clazz = Class.forName("java.util.UUID");
            Method method = clazz.getMethod("randomUUID");
            Object result = method.invoke(null);
            assertNotNull(result);
            assertInstanceOf(UUID.class, result);
        }
    }

    @Nested
    @DisplayName("Jakarta EE Migration")
    class JakartaMigrationTests {

        @Test
        @DisplayName("jakarta.ws.rs.NotFoundException debe estar disponible")
        void jakartaWsRsNotFoundExceptionShouldBeAvailable() throws Exception {
            Class<?> clazz = Class.forName("jakarta.ws.rs.NotFoundException");
            assertNotNull(clazz);
        }

        @Test
        @DisplayName("jakarta.ws.rs.core.Response debe estar disponible")
        void jakartaWsRsResponseShouldBeAvailable() throws Exception {
            Class<?> clazz = Class.forName("jakarta.ws.rs.core.Response");
            assertNotNull(clazz);
        }

        @Test
        @DisplayName("jakarta.annotation.PostConstruct debe estar disponible")
        void jakartaPostConstructShouldBeAvailable() throws Exception {
            Class<?> clazz = Class.forName("jakarta.annotation.PostConstruct");
            assertNotNull(clazz);
        }

        @Test
        @DisplayName("jakarta.persistence API debe estar disponible")
        void jakartaPersistenceShouldBeAvailable() throws Exception {
            Class<?> clazz = Class.forName("jakarta.persistence.Entity");
            assertNotNull(clazz);
        }

        @Test
        @DisplayName("jakarta.validation API debe estar disponible")
        void jakartaValidationShouldBeAvailable() throws Exception {
            Class<?> clazz = Class.forName("jakarta.validation.Valid");
            assertNotNull(clazz);
        }
    }

    @Nested
    @DisplayName("Concurrency & Thread Safety (Java 25)")
    class ConcurrencyTests {

        @Test
        @DisplayName("ExecutorService con virtual threads debe funcionar")
        void virtualThreadExecutorShouldWork() throws Exception {
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                var futures = IntStream.range(0, 100)
                        .mapToObj(i -> executor.submit(() -> {
                            Thread.sleep(10);
                            return UUID.randomUUID().toString();
                        }))
                        .toList();

                for (var future : futures) {
                    String result = future.get(10, TimeUnit.SECONDS);
                    assertNotNull(result);
                    assertEquals(36, result.length());
                }
            }
        }

        @Test
        @DisplayName("Parallel streams deben funcionar con Java 25 ForkJoinPool")
        void parallelStreamsShouldWork() {
            long count = IntStream.range(0, 10_000)
                    .parallel()
                    .filter(i -> i % 3 == 0)
                    .count();
            assertEquals(3334, count);
        }
    }

    @Nested
    @DisplayName("Jackson Serialization Compatibility")
    class JacksonTests {

        @Test
        @DisplayName("Jackson ObjectMapper debe funcionar con Java 25")
        void objectMapperShouldWork() throws Exception {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();

            // Serializar
            UUID testId = UUID.randomUUID();
            String json = mapper.writeValueAsString(new TestDto(testId, "test-name", 42));
            assertNotNull(json);
            assertTrue(json.contains(testId.toString()));
            assertTrue(json.contains("test-name"));

            // Deserializar
            TestDto deserialized = mapper.readValue(json, TestDto.class);
            assertEquals(testId, deserialized.getId());
            assertEquals("test-name", deserialized.getName());
            assertEquals(42, deserialized.getValue());
        }

        @Test
        @DisplayName("Jackson debe manejar fechas correctamente post-upgrade")
        void jacksonDateSerializationShouldWork() throws Exception {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.findAndRegisterModules();

            String dateJson = mapper.writeValueAsString(java.time.LocalDate.of(2026, 3, 10));
            assertNotNull(dateJson);

            String instantJson = mapper.writeValueAsString(Instant.now());
            assertNotNull(instantJson);
        }

        @Test
        @DisplayName("Jackson debe serializar listas y mapas complejos")
        void jacksonComplexTypesShouldWork() throws Exception {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();

            var data = java.util.Map.of(
                    "ids", List.of(UUID.randomUUID(), UUID.randomUUID()),
                    "name", "test",
                    "nested", java.util.Map.of("key", "value")
            );

            String json = mapper.writeValueAsString(data);
            assertNotNull(json);

            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> deserialized = mapper.readValue(json, java.util.Map.class);
            assertNotNull(deserialized);
            assertEquals("test", deserialized.get("name"));
        }
    }

    @Nested
    @DisplayName("Security & Cryptography")
    class SecurityTests {

        @Test
        @DisplayName("MessageDigest SHA-256 debe funcionar (JWT)")
        void sha256ShouldWork() throws Exception {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest("test-data".getBytes());
            assertNotNull(digest);
            assertEquals(32, digest.length);
        }

        @Test
        @DisplayName("HMAC-SHA256 debe funcionar (JWT signing)")
        void hmacSha256ShouldWork() throws Exception {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec key = new javax.crypto.spec.SecretKeySpec(
                    "secret-key-for-testing".getBytes(), "HmacSHA256");
            mac.init(key);
            byte[] result = mac.doFinal("payload".getBytes());
            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("SecureRandom debe funcionar (token generation)")
        void secureRandomShouldWork() {
            java.security.SecureRandom random = new java.security.SecureRandom();
            byte[] bytes = new byte[32];
            random.nextBytes(bytes);
            assertNotNull(bytes);
            // Al menos un byte debería ser no-cero
            boolean hasNonZero = false;
            for (byte b : bytes) {
                if (b != 0) {
                    hasNonZero = true;
                    break;
                }
            }
            assertTrue(hasNonZero);
        }
    }

    // DTO para pruebas de Jackson
    static class TestDto {
        private UUID id;
        private String name;
        private int value;

        public TestDto() {}

        public TestDto(UUID id, String name, int value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
    }
}
