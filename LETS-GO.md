# Loyalty Platform Documentation

## English Version

### 1. What is a Loyalty System and How Does It Work?

A loyalty system is a customer retention strategy that rewards customers for their continued patronage. It typically involves awarding points, discounts, or special privileges to customers who repeatedly purchase products or services from a company.

**How it works:**
- Customers earn points through purchases or other engagement activities
- Points accumulate in their loyalty account
- Customers can redeem points for rewards, discounts, or exclusive benefits
- The system tracks customer behavior and tailors offers to encourage repeat business

### 2. Analysis of This Loyalty System

This loyalty platform is a comprehensive microservices solution built with Java, Spring Boot, and PostgreSQL. It implements a realistic loyalty and voucher management system with the following key features:

**Architecture:**
- **Microservices Architecture**: Three main services (User, Voucher, Loyalty) with event-driven communication
- **Event-Driven Communication**: Services communicate asynchronously via Apache Kafka
- **Database Per Service**: Each service maintains its own database for data isolation
- **Shared Common Module**: Contains reusable components across all services

**Key Components:**
- **User Service**: Manages user registration and profiles
- **Voucher Service**: Handles voucher lifecycle (creation, redemption, expiration)
- **Loyalty Service**: Manages loyalty points (earning, redemption, tracking)
- **Common Module**: Shared entities, events, configurations, and utilities

**Advanced Features:**
- Outbox Pattern: Ensures reliable event publishing with transactional consistency
- Saga Pattern: Orchestrates distributed transactions across services
- Idempotency: Prevents duplicate processing of events
- Circuit Breaker: Prevents cascading failures using Resilience4j
- Optimistic Locking: Prevents concurrent modification conflicts

### 3. Structure and Usage of the Common Module

The common module serves as a shared library that provides reusable components across all microservices. Here's how it's structured and used:

**Structure:**
- **Entities**: Shared data models used across services
- **Events**: Common event definitions for inter-service communication
- **Configuration**: Shared configuration classes and properties
- **Repositories**: Common data access patterns
- **Services**: Reusable business logic components
- **Utilities**: Helper functions and utility classes
- **Security**: Shared authentication and authorization components
- **Exception Handling**: Common exception types and handlers

**How to Use:**
1. The common module is included as a dependency in each service's build file (build.gradle or pom.xml)
2. Services can import and use shared components directly
3. Changes to the common module affect all services that depend on it
4. The common module uses the `java-library` plugin to expose its components to other modules

**Benefits:**
- Reduces code duplication across services
- Ensures consistency in data models and event definitions
- Simplifies maintenance of shared functionality
- Promotes code reuse and standardization

### 4. Step-by-Step Setup Guide for Ubuntu WSL

**Prerequisites:**
- Ubuntu WSL with at least 4GB RAM available
- Java 17 installed
- Docker and Docker Compose installed
- Git installed

**Steps:**

1. **Update System Packages**
   ```bash
   sudo apt update && sudo apt upgrade -y
   ```

2. **Install Java 17**
   ```bash
   sudo apt install openjdk-17-jdk -y
   java -version
   ```

3. **Install Git**
   ```bash
   sudo apt install git -y
   ```

4. **Install Docker**
   ```bash
   sudo apt install docker.io -y
   sudo systemctl start docker
   sudo systemctl enable docker
   sudo usermod -aG docker $USER
   ```

5. **Install Docker Compose**
   ```bash
   sudo apt install docker-compose -y
   ```

6. **Clone the Repository**
   ```bash
   git clone https://github.com/your-repo/loyalty-platform-spring.git
   cd loyalty-platform-spring
   ```

7. **Build the Services**
   Using Gradle:
   ```bash
   ./gradlew clean build
   ```
   
   Or using Maven:
   ```bash
   mvn clean install
   ```

8. **Start the Infrastructure and Services**
   ```bash
   docker-compose up --build
   ```

9. **Access the Services**
   - User Service: http://localhost:8081
   - Voucher Service: http://localhost:8082
   - Loyalty Service: http://localhost:8083
   - Prometheus: http://localhost:9090
   - Grafana: http://localhost:3000 (admin/admin)

10. **Verify Communication**
    - Check that all services are running in Docker containers
    - Verify that services can connect to their respective databases
    - Confirm that Kafka is facilitating communication between services
    - Test API endpoints to ensure cross-service communication works

---

## نسخه فارسی

### ۱. سیستم وفاداری چیست و چگونه کار می‌کند؟

سیستم وفاداری یک استراتژی حفظ مشتری است که مشتریان را برای تعهد مداوم خود پاداش می‌دهد. این سیستم معمولاً شامل اعطا کردن امتیازات، تخفیف‌ها یا امتیازات ویژه به مشتریانی است که مرتباً محصولات یا خدمات یک شرکت را خریداری می‌کنند.

**نحوه عملکرد:**
- مشتریان از طریق خرید یا سایر فعالیت‌های تعاملی امتیاز کسب می‌کنند
- امتیازات در حساب وفاداری آن‌ها انباشته می‌شوند
- مشتریان می‌توانند امتیازات خود را برای جوایز، تخفیف‌ها یا مزایای منحصر به فرد استفاده کنند
- سیستم رفتار مشتری را ردیابی می‌کند و پیشنهادهایی را برای تشویق کسب و کار مجدد ارائه می‌دهد

### ۲. تحلیل این سیستم وفاداری

این پلتفرم وفاداری یک راه‌حل جامع میکروسرویس است که با جاوا، اسپرینگ بوت و پست‌گرس کیو ال ساخته شده است. این سیستم یک سیستم مدیریت وفاداری و کوپن واقعی را با ویژگی‌های کلیدی زیر پیاده‌سازی می‌کند:

**معماری:**
- **معماری میکروسرویس: سه سرویس اصلی (کاربر، کوپن، وفاداری) با ارتباط مبتنی بر رویداد**
- **ارتباط مبتنی بر رویداد: سرویس‌ها به صورت ناهمزمان از طریق آپاچی کافکا ارتباط برقرار می‌کنند**
- **پایگاه داده برای هر سرویس: هر سرویس یک پایگاه داده مجزا برای جداسازی داده‌ها دارد**
- **ماژول مشترک: شامل اجزای قابل استفاده مجدد در تمام سرویس‌ها**

**اجزای کلیدی:**
- **سرویس کاربر: مدیریت ثبت‌نام کاربران و پروفایل‌ها**
- **سرویس کوپن: مدیریت چرخه کوپن (ایجاد، استفاده، منقضی شدن)**
- **سرویس وفاداری: مدیریت امتیازات وفاداری (کسب، استفاده، ردیابی)**
- **ماژول مشترک: موجودیت‌های مشترک، رویدادها، پیکربندی‌ها و ابزارهای مشترک**

**ویژگی‌های پیشرفته:**
- الگوی Outbox: اطمینان از انتشار قابل اعتماد رویدادها با ثبات تراکنشی
- الگوی Saga: هماهنگ‌سازی تراکنش‌های توزیع‌شده بین سرویس‌ها
- Idempotency: جلوگیری از پردازش تکراری رویدادها
- Circuit Breaker: جلوگیری از شکست‌های زنجیره‌ای با استفاده از Resilience4j
- قفل‌گذاری انتخابی: جلوگیری از تضاد در تغییرات همزمان

### ۳. ساختار و استفاده از ماژول مشترک

ماژول مشترک به عنوان یک کتابخانه مشترک عمل می‌کند که اجزای قابل استفاده مجدد را در تمام میکروسرویس‌ها فراهم می‌کند. در اینجا نحوه ساختار یافته و استفاده شدن آن آورده شده است:

**ساختار:**
- **موجودیت‌ها: مدل‌های داده مشترک استفاده شده در سرویس‌ها**
- **رویدادها: تعاریف رویداد مشترک برای ارتباط بین سرویس‌ها**
- **پیکربندی: کلاس‌ها و خصوصیات پیکربندی مشترک**
- **مخازن: الگوهای دسترسی به داده مشترک**
- **سرویس‌ها: اجزای منطق تجاری قابل استفاده مجدد**
- **ابزارها: توابع کمکی و کلاس‌های ابزار**
- **امنیت: اجزای احراز هویت و مجوزدهی مشترک**
- **مدیریت خطا: انواع خطا و متولیان خطای مشترک**

**نحوه استفاده:**
1. ماژول مشترک به عنوان یک وابستگی در فایل ساخت هر سرویس (build.gradle یا pom.xml) گنجانده می‌شود
2. سرویس‌ها می‌توانند مستقیماً اجزای مشترک را وارد کرده و از آن‌ها استفاده کنند
3. تغییرات در ماژول مشترک بر روی تمام سرویس‌هایی که به آن وابسته هستند تأثیر می‌گذارد
4. ماژول مشترک از پلاگین `java-library` برای نمایش اجزای خود به سایر ماژول‌ها استفاده می‌کند

**مزایا:**
- کاهش تکرار کد در سرویس‌ها
- اطمینان از یکپارچگی در مدل‌های داده و تعاریف رویداد
- ساده‌سازی نگهداری عملکردهای مشترک
- ترویج استفاده مجدد از کد و استانداردسازی

### ۴. راهنمای گام به گام راه‌اندازی برای اوبونتو WSL

**پیش‌نیازها:**
- اوبونتو WSL با حداقل ۴ گیگابایت RAM در دسترس
- جاوا ۱۷ نصب شده
- داکر و داکر کامپوز نصب شده
- گیت نصب شده

**مراحل:**

۱. **به‌روزرسانی بسته‌های سیستم**
   ```bash
   sudo apt update && sudo apt upgrade -y
   ```

۲. **نصب جاوا ۱۷**
   ```bash
   sudo apt install openjdk-17-jdk -y
   java -version
   ```

۳. **نصب گیت**
   ```bash
   sudo apt install git -y
   ```

۴. **نصب داکر**
   ```bash
   sudo apt install docker.io -y
   sudo systemctl start docker
   sudo systemctl enable docker
   sudo usermod -aG docker $USER
   ```

۵. **نصب داکر کامپوز**
   ```bash
   sudo apt install docker-compose -y
   ```

۶. **کلون کردن مخزن**
   ```bash
   git clone https://github.com/your-repo/loyalty-platform-spring.git
   cd loyalty-platform-spring
   ```

۷. **ساخت سرویس‌ها**
   با استفاده از Gradle:
   ```bash
   ./gradlew clean build
   ```
   
   یا با استفاده از Maven:
   ```bash
   mvn clean install
   ```

۸. **شروع زیرساخت و سرویس‌ها**
   ```bash
   docker-compose up --build
   ```

۹. **دسترسی به سرویس‌ها**
   - سرویس کاربر: http://localhost:8081
   - سرویس کوپن: http://localhost:8082
   - سرویس وفاداری: http://localhost:8083
   - Prometheus: http://localhost:9090
   - Grafana: http://localhost:3000 (admin/admin)

۱۰. **تأیید ارتباط**
    - بررسی اینکه تمام سرویس‌ها در کانتینرهای داکر در حال اجرا هستند
    - تأیید اینکه سرویس‌ها می‌توانند به پایگاه‌های داده مربوطه خود متصل شوند
    - تأیید اینکه کافکا ارتباط بین سرویس‌ها را تسهیل می‌کند
    - تست نقاط پایانی API برای اطمینان از کارکرد ارتباط بین سرویسی