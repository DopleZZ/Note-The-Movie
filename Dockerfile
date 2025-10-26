FROM node:18 AS node_builder
WORKDIR /src/frontend


COPY frontend/package.json frontend/package-lock.json* ./
RUN npm ci


COPY frontend/ ./
COPY .env ./
RUN npm run build


FROM eclipse-temurin:17-jdk AS gradle_builder
WORKDIR /src


RUN apt-get update && apt-get install -y wget unzip && \
    wget -q https://services.gradle.org/distributions/gradle-8.4-bin.zip && \
    unzip gradle-8.4-bin.zip && \
    mv gradle-8.4 /opt/gradle && \
    ln -s /opt/gradle/bin/gradle /usr/bin/gradle && \
    rm gradle-8.4-bin.zip


COPY build/ ./build


COPY --from=node_builder /src/frontend/dist ./build/src/main/resources/static


RUN gradle -p build bootJar --no-daemon


FROM eclipse-temurin:17-jre
WORKDIR /app


COPY --from=gradle_builder /src/build/build/libs/*.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
