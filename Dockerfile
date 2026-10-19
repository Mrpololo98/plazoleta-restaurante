# ------------------------------------------------------------------
# Microservicio de Restaurantes - Plazoleta de Comidas
# Imagen multietapa: compila con Maven y ejecuta en un JRE ligero
# ------------------------------------------------------------------

# Etapa 1: construccion del JAR
# Se copia primero el pom.xml para aprovechar la cache de capas y
# no volver a descargar dependencias en cada cambio de codigo.
FROM maven:3.9.16-eclipse-temurin-17 AS compilacion
WORKDIR /codigo
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# Etapa 2: imagen final de ejecucion
# Solo contiene el JRE y el JAR, sin fuentes ni herramientas de build.
FROM eclipse-temurin:17-jre-jammy
WORKDIR /aplicacion
COPY --from=compilacion /codigo/target/restaurante-*.jar ./ms-restaurantes.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar ms-restaurantes.jar"]
