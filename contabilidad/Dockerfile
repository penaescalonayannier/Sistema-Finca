# Dockerfile simplificado - usa JAR pre-construido
FROM eclipse-temurin:21-jre-jammy

ENV TZ="America/Guayaquil"

# Instala dependencias para JasperReports y curl para healthcheck
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    fontconfig \
    libfreetype6 \
    fonts-dejavu \
    fonts-liberation \
    curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Copia el JAR pre-construido
COPY target/contabilidad-1.0.0.jar /app/app.jar

# Crea directorios necesarios
RUN mkdir -p /app/temp /app/jasper-output /logs && \
    chmod -R 777 /app/temp /app/jasper-output /logs

ENV JASPER_TEMP_DIR=/app/temp
ENV JASPER_OUTPUT_DIR=/app/jasper-output

EXPOSE 9908

ENTRYPOINT ["java", \
    "-Xms512m", "-Xmx2048m", \
    "-XX:+UseG1GC", \
    "-Djava.awt.headless=true", \
    "-Dserver.port=9908", \
    "-jar", "/app/app.jar"]
