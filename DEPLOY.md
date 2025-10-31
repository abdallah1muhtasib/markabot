# نشر Marka Bot على VPS/Docker

هذا المستند يشرح كيفية تشغيل البوت في بيئة إنتاج باستخدام حاوية Docker أو على خادم VPS تقليدي. يتضمن ذلك إعداد القاعدة، النظام الخدمي، والتحقق من الصحة.

## باستخدام Docker و Docker Compose

يأتي المشروع مع ملف `Dockerfile` و `docker-compose.yml` لتسهيل التشغيل. يتيح لك هذا تشغيل البوت وقاعدة البيانات PostgreSQL جنباً إلى جنب.

### الخطوات

1. عدّل ملف `.env` في جذر المشروع لملاءمة بيئتك الإنتاجية.
2. شغل الأمر التالي لبناء الصور وتشغيل الخدمات:

```bash
docker-compose up -d --build
```

3. سيتم تشغيل ثلاث خدمات: قاعدة البيانات `db`, خادم `adminer` لمراقبة قاعدة البيانات (اختياري), وحاوية `bot`. يمكن مراقبة السجلات عبر:

```bash
docker-compose logs -f bot
```

### محتوى docker-compose

```
version: '3.8'
services:
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: markabot
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - db-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD", "pg_isready", "-U", "${DB_USERNAME}"]
      interval: 10s
      retries: 5

  adminer:
    image: adminer
    depends_on:
      - db
    ports:
      - 8080:8080

  bot:
    build: .
    environment:
      BOT_TOKEN: ${BOT_TOKEN}
      BOT_VERSION: ${BOT_VERSION}
      DB_URL: jdbc:postgresql://db:5432/markabot
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
    depends_on:
      - db
    restart: unless-stopped

volumes:
  db-data:
```

## باستخدام نظام خدمة على VPS

إذا كنت تفضّل نشر البوت مباشرة على خادم VPS (Linux)، يمكن تنفيذ الخطوات التالية:

1. ثبت Java 17 وPostgreSQL على الخادم.
2. انسخ ملفات المشروع إلى الخادم وقم ببنائه عبر `./gradlew build`.
3. أنشئ ملف خدمة systemd في `/etc/systemd/system/markabot.service`:

```
[Unit]
Description=Marka Bot Discord
After=network.target

[Service]
Type=simple
User=markabot
WorkingDirectory=/opt/markabot
ExecStart=/usr/bin/env BOT_TOKEN=... BOT_VERSION=... DB_URL=... DB_USERNAME=... DB_PASSWORD=... java -jar bootstrap/build/libs/bootstrap-0.1.0-SNAPSHOT.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

4. شغّل الخدمة:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now markabot.service
```

5. تأكد من حالة البوت:

```bash
sudo systemctl status markabot.service
```

## التحقق من الصحة (Healthcheck)

يمكن استخدام Healthcheck بسيط قائم على سجل البوت أو إضافة نقطة نهاية HTTP صغيرة في المستقبل لإعلام خدمات مراقبة مثل UptimeRobot بأن البوت يعمل. حاليا، يمكنك مراقبة السجلات والتأكد من عدم وجود استثناءات متكررة.
