# Cấu hình tập trung

Config Server:

- Khi học/chạy local, Config Server đọc cấu hình không chứa secret trong thư mục ngoài JAR `config-repo/` với profile `native`.
- Khi deploy production, đặt `CONFIG_SERVER_PROFILE=git` để đọc từ một Git repository cấu hình riêng.
- Mật khẩu DB, mật khẩu Config Server, Git token và Google Client ID được truyền qua Environment Variables. Không đặt giá trị thật vào các file YAML hoặc Git.
- Actuator chỉ mở `health,info`; endpoint `env` đã được tắt vì có thể làm lộ secret.
- Profile `dev` dùng `ddl-auto: update`; profile `prod` dùng `ddl-auto: validate` để ứng dụng không tự ý thay đổi schema production.
- Bốn service nghiệp vụ nhận identity đã được API Gateway xác thực qua `X-User-ID`, `X-User-Email`, `X-User-Name`, `X-User-Roles` và dựng lại `SecurityContext` stateless. Role hợp lệ là `RECRUITER`, `CANDIATE` và `ADMIN`.

## Chạy local trong IntelliJ

1. Mở `.env.example`, lấy tên biến và tạo Environment Variables trong Run Configuration của từng ứng dụng. Có thể dùng cùng một bộ biến cho cả project.
2. Đặt Working directory của `ConfigServerApplication` là root project để đường dẫn mặc định `file:./config-repo` hoạt động. Hoặc cấu hình `CONFIG_SERVER_NATIVE_SEARCH_LOCATIONS` bằng đường dẫn tuyệt đối.
3. Chạy `ConfigServerApplication` trước, sau đó chạy `EurekaServerApplication`, các service và cuối cùng là `ApiGatewayApplication`.
4. Giữ `CONFIG_SERVER_PROFILE=native`, `SPRING_PROFILES_ACTIVE=dev` khi học local.

Config Server: `http://localhost:8888/actuator/health`  
Eureka: `http://localhost:8761`  
Gateway: `http://localhost:8080`

## Chạy toàn bộ bằng Docker

Từ thư mục gốc project:

```bash
docker compose up -d --build
docker compose ps
```

Các địa chỉ dùng khi học local:

- API Gateway: `http://localhost:8080`
- Keycloak: `http://localhost:8180` — admin console `admin/admin`
- Keycloak demo users: `admin/admin123`, `recruiter/recruiter123`, `candidate/candidate123`
- Eureka: `http://localhost:8761`
- RabbitMQ: `http://localhost:15672` — `ats/ats`
- MinIO: `http://localhost:9001` — `minio/minio123`
- MailHog: `http://localhost:8025`
- PostgreSQL: `localhost:5432` — `ats/ats`
- Redis: `localhost:6379`
- Kafka: `localhost:9092`

Dừng hệ thống bằng `docker compose down`. Database được tạo tự động ở lần chạy đầu tiên khi volume PostgreSQL còn trống.

Nếu cổng `8180` đang được sử dụng, chạy `KEYCLOAK_PORT=8181 docker compose up -d` và mở Keycloak tại `http://localhost:8181`.

## Production checklist

- Tạo Git repository cấu hình riêng, không đóng gói cùng Config Server, và đặt `CONFIG_REPO_URI`, branch, username/token trong secret manager hoặc Environment Variables.
- Đặt `CONFIG_SERVER_PROFILE=git` và `SPRING_PROFILES_ACTIVE=prod`.
- Đổi `CONFIG_SERVER_PASSWORD`, dùng DB user riêng cho từng service, và chỉ cho phép network nội bộ truy cập Config Server.
- Chạy migration/schema management riêng trước khi deploy vì profile `prod` dùng `ddl-auto: validate`.
