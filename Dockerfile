# ====== Stage 1: Build (Gradle) ======
FROM gradle:8.5.0-jdk17 AS builder

WORKDIR /home/gradle/src
# انسخ كل السورس (المشروع multi-module)
COPY . .

# شغّل Gradle Wrapper إن كان موجودًا، وإلا استخدم gradle من الصورة
# نلغي الاختبارات لتسريع البناء الأول
RUN set -eux; \
    if [ -f "./gradlew" ]; then \
        chmod +x ./gradlew; \
        ./gradlew --no-daemon clean build -x test; \
    else \
        gradle --no-daemon clean build -x test; \
    fi

# ====== Stage 2: Runtime (JRE خفيفة) ======
FROM eclipse-temurin:17-jre

ENV JAVA_OPTS=""
WORKDIR /app

# انسخ الـ JAR الناتج من وحدة bootstrap (التي تحتوي على main)
COPY --from=builder /home/gradle/src/bootstrap/build/libs/*.jar /app/markabot.jar

# شغّل التطبيق
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/markabot.jar"]
