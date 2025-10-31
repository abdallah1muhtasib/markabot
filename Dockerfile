# ===== Stage 1: Build with Gradle =====
# استخدم صورة Gradle ثابتة مع JDK17
FROM gradle:8.5.0-jdk17 AS builder

# ضع كل السورس داخل الحاوية
WORKDIR /home/gradle/src
COPY . .

# نفّذ البناء (بدون اختبارات لتسريع أول نشر)
RUN gradle --no-daemon clean build -x test

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

