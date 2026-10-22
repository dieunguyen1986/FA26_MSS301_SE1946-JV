# ATS configuration repository

Đây là configuration repository bên ngoài module `config-server`.

- Local/training: Config Server đọc thư mục này bằng `CONFIG_SERVER_PROFILE=native`.
- Production: có thể tách nguyên thư mục này thành một Git repository private và dùng `CONFIG_SERVER_PROFILE=git`.
- Chỉ lưu cấu hình không nhạy cảm và các `${ENV_VAR}` placeholder. Không commit password, token hoặc client secret thật.

Khi chạy Config Server trong IntelliJ, đặt Working directory là root project hoặc đặt biến `CONFIG_SERVER_NATIVE_SEARCH_LOCATIONS` tới đường dẫn tuyệt đối của thư mục này.
