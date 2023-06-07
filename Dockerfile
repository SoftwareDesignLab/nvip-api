### Build Stage
FROM gradle:8.1.1-jdk17 AS builder

WORKDIR /home/app

ADD build.gradle .
ADD settings.gradle .

#RUN mvn dependency:go-offline
ADD docker.context.xml WebContent/META-INF/context.xml
ADD src/main src/main
ADD WebContent WebContent/

#RUN mvn package -Dmaven.test.skip=true
RUN gradle bootJar -x test --no-daemon

### Run Stage
FROM eclipse-temurin:17-jre-alpine as deploy
COPY --from=builder /home/app/build/libs/nvip_api-1.0.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]