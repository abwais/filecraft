FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN sed -i 's/\r$//' gradlew
RUN chmod +x gradlew
RUN ./gradlew clean bootJar -x test --no-daemon

EXPOSE 8080

CMD ["java", "-jar", "build/libs/filecraft-backend-0.0.1-SNAPSHOT.jar"]