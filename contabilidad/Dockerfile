# Etapa de construcción: utiliza Maven para compilar la aplicación
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Establece el directorio de trabajo
WORKDIR /build

# Copia el archivo de configuración de Maven
COPY rc-settings.xml settings.xml

# Configura variables de entorno para Maven y token de paquete
# Configura el token para el acceso a repositorios privados en settings.xml
ARG PACKAGE_TOKEN
RUN sed -i "s|GIT_TOKEN|$PACKAGE_TOKEN|g" settings.xml

# Copia los archivos de proyecto
COPY . /build

# Compila las dependencias con reintentos para mayor resiliencia de red
RUN mvn dependency:go-offline -s settings.xml \
    -Daether.connector.basic.retryPolicy.retries=3 \
    -Daether.connector.requestTimeout=60000 && \
    mvn clean package -DskipTests

# Etapa final: imagen con Java 21 y las dependencias necesarias para JasperReports
FROM eclipse-temurin:21-jre-jammy

# Establece la zona horaria
ENV TZ="America/Guayaquil"

# Instala fuentes y bibliotecas necesarias para JasperReports
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    fontconfig \
    libfreetype6 \
    fonts-dejavu \
    fonts-liberation \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Copia el JAR generado desde la etapa de construcción
COPY --from=builder /build/target/report-1.0.0.jar /app/report-1.0.0.jar

## Copia los recursos necesarios para JasperReports
#COPY --from=builder /build/src/main/resources/templates /app/resources/templates
#COPY --from=builder /build/receta.jrxml /app/receta.jrxml
#COPY --from=builder /build/receta.jasper /app/receta.jasper

# Crea directorios necesarios para la aplicación
RUN mkdir -p /app/temp /app/jasper-output /logs

# Expone el puerto que utiliza la aplicación
EXPOSE 9909

# Permisos para directorios de trabajo
RUN chmod -R 777 /app/temp /app/jasper-output /logs

# Variables de entorno para configurar Jasper Reports
ENV JASPER_TEMP_DIR=/app/temp
ENV JASPER_OUTPUT_DIR=/app/jasper-output
ENV JAVA_TOOL_OPTIONS="-Dnet.sf.jasperreports.compiler.class=net.sf.jasperreports.engine.design.JRJdtCompiler -Dnet.sf.jasperreports.compiler.temp.dir=/app/temp -Dnet.sf.jasperreports.compiler.useThreadContextClassLoader=true -Djava.awt.headless=true"

# Comando de inicio de la aplicación con configuración optimizada para máximo rendimiento
ENTRYPOINT ["java", \
    "-Xms512m", "-Xmx2048m", \
    "-XX:+UseG1GC", \
    "-XX:+ParallelRefProcEnabled", \
    "-XX:G1SummaryPeriodMs=5000", \
    "-XX:+PrintGC", \
    "-XX:+PrintGCDetails", \
    "-XX:+PrintGCTimeStamps", \
    "-Xloggc:/logs/gc.log", \
    "-XX:+UnlockDiagnosticVMOptions", \
    "-XX:+PrintCompilation", \
    "-jar", "/app/report-1.0.0.jar"]