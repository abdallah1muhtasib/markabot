# ===== Stage 1: Build with Gradle =====
FROM gradle:8.5.0-jdk17 AS builder

WORKDIR /home/gradle/src
COPY . .

# انسخ gradlew والملف gradle-wrapper.properties لتشغيل Gradle wrapper
COPY gradlew ./
COPY gradle gradle

RUN chmod +x gradlew
RUN ./gradlew --no-daemon clean build -x test

# ===== Stage 2: Runtime (JRE only) =====
# استخدم JRE خفيفة ومستقرة
FROM eclipse-temurin:17-jre

# متغيرات اختيارية لتخصيص JVM
ENV JAVA_OPTS=""

# مجلد تشغيل التطبيق
WORKDIR /app

# انسخ الـ JAR النهائي من مرحلة البناء
# ملاحظة: ننسخ JAR الناتج من وحدة bootstrap (التي تحتوي الـmain)
COPY --from=builder /home/gradle/src/bootstrap/build/libs/*.jar /app/markabot.jar

# شغّل التطبيق
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/markabot.jar"]

