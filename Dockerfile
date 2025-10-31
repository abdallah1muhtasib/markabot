# ====== Stage 1: Build (Gradle) ======
FROM gradle:8.5.0-jdk17 AS builder

WORKDIR /home/gradle/src
COPY . .

# ميموري آمن للبناء على Railway + بناء wrapper قبل البناء الفعلي
ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx1024m -Dfile.encoding=UTF-8"

RUN set -eux; \
    gradle --no-daemon wrapper --gradle-version 8.5; \
    chmod +x ./gradlew; \
    ./gradlew --no-daemon clean build -x test --stacktrace --warning-mode all

# ====== Stage 2: Runtime (JRE خفيفة) ======
FROM eclipse-temurin:17-jre

ENV JAVA_OPTS=""
WORKDIR /app

# نسخ ملف ال Shadow Jar الناتج
COPY --from=builder /home/gradle/src/build/libs/*-all.jar /app/markabot.jar

# تشغيل التطبيق
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/markabot.jar"]
