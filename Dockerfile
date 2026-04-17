#Etapa 1: Construir la aplicación con Maven y JDK 17
FROM maven:3.8.3-openjdk-17 AS builder

WORKDIR /app

# Copiar archivo Maven en Docker para aprovechar la caché
COPY pom.xml ./

# Copiamos el código fuente de la aplicación
COPY src ./src

# Construye el aplicativo de acuerdo a la definicion de packing dentro del pom.xml
RUN mvn clean package -DskipTests




# Etapa 2: Crear una imagen de runtime mínima con JRE 17
FROM maven:3.8.3-openjdk-17 AS final

WORKDIR /app

# Copia el archivo JAR desde la etapa 'build' a la etapa 'final'
COPY --from=builder /app/target/captio-bbva-integracion-0.0.1.jar /app/captio-bbva-integracion.jar

# Puerto aplicativo
EXPOSE 8080

# Comandos para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/captio-bbva-integracion.jar", "--spring.job.enabled=false"]
CMD ["--spring.job.name=defaultJob"]
