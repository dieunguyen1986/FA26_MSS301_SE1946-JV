## Candidate Service API

Base URL:

```text
/api/v1
```

Các API dành cho candidate sử dụng `userId` lấy từ JWT/Keycloak `sub`. Client không được tự truyền hoặc thay đổi `userId` trong request body.

## 1. Candidate profile

### 1.1 Tạo hoặc cập nhật hồ sơ candidate

| Method | Endpoint |
|---|---|
| `PUT` | `/api/v1/candidates/me` |

Request:

```json
{
  "fullName": "Nguyen Van A",
  "source": "LINKEDIN",
  "utmSource": "linkedin",
  "utmMedium": "social",
  "utmCampaign": "java-hiring"
}
```

Response `200 OK`:

```json
{
  "id": 1,
  "fullName": "Nguyen Van A",
  "status": "ACTIVE",
  "source": "LINKEDIN",
  "utmSource": "linkedin",
  "utmMedium": "social",
  "utmCampaign": "java-hiring",
  "duplicate": false,
  "activeCvId": null,
  "parsedCvData": null
}
```

### 1.2 Lấy hồ sơ candidate hiện tại

| Method | Endpoint |
|---|---|
| `GET` | `/api/v1/candidates/me` |

Response `200 OK`: trả về thông tin candidate hiện tại, CV đang active và dữ liệu CV đã được chuẩn hóa nếu có.

### 1.3 Cập nhật một phần hồ sơ candidate

| Method | Endpoint |
|---|---|
| `PATCH` | `/api/v1/candidates/me` |

Request:

```json
{
  "fullName": "Nguyen Van B",
  "source": "WEBSITE"
}
```

Response `200 OK`: trả về hồ sơ candidate sau khi cập nhật.

### 1.4 Lấy candidate theo ID

| Method | Endpoint |
|---|---|
| `GET` | `/api/v1/candidates/{candidateId}` |

API này dành cho recruiter/admin hoặc các service nội bộ có quyền phù hợp.

Response `200 OK`: trả về thông tin candidate theo `candidateId`.

## 2. CV management

### 2.1 Upload CV mới

| Method | Endpoint |
|---|---|
| `POST` | `/api/v1/candidates/me/cvs` |

Content type:

```text
multipart/form-data
```

Form data:

```text
file: candidate-cv.pdf
```

Response `202 Accepted`:

```json
{
  "cvId": 15,
  "fileType": "application/pdf",
  "sizeBytes": 245760,
  "parseStatus": "PENDING",
  "uploadedAt": "2026-09-25T10:30:00Z",
  "message": "CV uploaded and queued for parsing"
}
```

Khi upload thành công, `parseStatus` được đặt là `PENDING`. Candidate service có thể phát event hoặc gọi `cv-parser-service` để bắt đầu xử lý.

### 2.2 Lấy danh sách CV của candidate

| Method | Endpoint |
|---|---|
| `GET` | `/api/v1/candidates/me/cvs` |

Response `200 OK`:

```json
[
  {
    "cvId": 15,
    "fileType": "application/pdf",
    "sizeBytes": 245760,
    "parseStatus": "PARSED",
    "uploadedAt": "2026-09-25T10:30:00Z",
    "active": true
  }
]
```

### 2.3 Lấy chi tiết một CV

| Method | Endpoint |
|---|---|
| `GET` | `/api/v1/candidates/me/cvs/{cvId}` |

Response `200 OK`: trả về metadata CV, trạng thái parse và dữ liệu đã parse nếu trạng thái là `PARSED`.

### 2.4 Chọn CV hiện tại

| Method | Endpoint |
|---|---|
| `PUT` | `/api/v1/candidates/me/cvs/{cvId}/active` |

Response `200 OK`:

```json
{
  "candidateId": 1,
  "activeCvId": 15,
  "message": "CV selected as active"
}
```

API cập nhật `CandidateEntity.cvFileId` và chỉ cho phép chọn CV thuộc về candidate hiện tại.

### 2.5 Xóa hoặc vô hiệu hóa CV

| Method | Endpoint |
|---|---|
| `DELETE` | `/api/v1/candidates/me/cvs/{cvId}` |

Response `204 No Content`.

Nếu hệ thống sử dụng soft delete, CV nên được đánh dấu vô hiệu hóa thay vì xóa file vật lý ngay lập tức. Không cho phép xóa CV đang active nếu candidate chưa chọn CV thay thế.

## 3. CV parsing

### 3.1 Kiểm tra trạng thái parse

| Method | Endpoint |
|---|---|
| `GET` | `/api/v1/candidates/me/cvs/{cvId}/parse-status` |

Response `200 OK`:

```json
{
  "cvId": 15,
  "parseStatus": "PENDING",
  "message": "CV is waiting to be parsed"
}
```

Các giá trị hợp lệ của `parseStatus`:

| Giá trị | Ý nghĩa |
|---|---|
| `PENDING` | CV đã upload và đang chờ hoặc đang được parse |
| `PARSED` | Parse thành công |
| `FAILED` | Parse thất bại |

### 3.2 Yêu cầu parse hoặc parse lại CV

| Method | Endpoint |
|---|---|
| `POST` | `/api/v1/candidates/me/cvs/{cvId}/parse` |

Response `202 Accepted`:

```json
{
  "cvId": 15,
  "parseStatus": "PENDING",
  "message": "CV parsing has been queued"
}
```

API này hợp lệ khi CV có trạng thái `FAILED`, hoặc khi candidate chủ động yêu cầu parse lại. Không nên tạo nhiều job parse trùng cho cùng một CV đang ở trạng thái `PENDING`.

### 3.3 Nhận kết quả parse từ cv-parser-service

| Method | Endpoint |
|---|---|
| `POST` | `/internal/v1/cvs/{cvId}/parse-result` |

API nội bộ, chỉ cho phép `cv-parser-service` gọi bằng service token hoặc cơ chế xác thực giữa các service.

Request khi parse thành công:

```json
{
  "status": "PARSED",
  "parsedData": {
    "fullName": "Nguyen Van A",
    "email": "a@example.com",
    "phone": "0900000000",
    "summary": "Java developer",
    "skills": ["Java", "Spring Boot"],
    "education": [
      {
        "school": "FPT University",
        "degree": "Software Engineering",
        "fieldOfStudy": "Computer Science",
        "startDate": "2021-09",
        "endDate": "2025-06"
      }
    ],
    "experience": [
      {
        "company": "ABC Technology",
        "title": "Java Developer",
        "startDate": "2024-01",
        "endDate": null,
        "description": "Developed REST APIs with Spring Boot"
      }
    ]
  }
}
```

Request khi parse thất bại:

```json
{
  "status": "FAILED",
  "parsedData": null,
  "errorMessage": "Unsupported or corrupted CV file"
}
```

Khi nhận callback:

1. `PARSED`: cập nhật `CvEntity.parsedData`, cập nhật `CandidateEntity.parsedCvData` và chuyển `parseStatus` thành `PARSED`.
2. `FAILED`: chuyển `parseStatus` thành `FAILED` và ghi nhận nguyên nhân lỗi.
3. Callback phải được xử lý idempotent để request gửi lại không tạo dữ liệu trùng.

## 4. Candidate skills

`skillId` là ID tham chiếu sang `job-service`, không tạo quan hệ JPA trực tiếp giữa hai database.

### 4.1 Lấy danh sách kỹ năng

| Method | Endpoint |
|---|---|
| `GET` | `/api/v1/candidates/me/skills` |

Response `200 OK`:

```json
[
  {
    "skillId": 3,
    "level": "SENIOR",
    "yearsExp": 4
  }
]
```

### 4.2 Thay thế toàn bộ danh sách kỹ năng

| Method | Endpoint |
|---|---|
| `PUT` | `/api/v1/candidates/me/skills` |

Request:

```json
[
  {
    "skillId": 3,
    "level": "SENIOR",
    "yearsExp": 4
  },
  {
    "skillId": 8,
    "level": "INTERMEDIATE",
    "yearsExp": 2
  }
]
```

Response `200 OK`: trả về danh sách kỹ năng sau khi thay thế.

### 4.3 Thêm một kỹ năng

| Method | Endpoint |
|---|---|
| `POST` | `/api/v1/candidates/me/skills` |

Request:

```json
{
  "skillId": 3,
  "level": "SENIOR",
  "yearsExp": 4
}
```

Response `201 Created`: trả về kỹ năng vừa thêm.

Không cho phép thêm trùng `skillId` cho cùng một candidate vì database có unique constraint trên cặp `candidate_id` và `skill_id`.

### 4.4 Xóa một kỹ năng

| Method | Endpoint |
|---|---|
| `DELETE` | `/api/v1/candidates/me/skills/{skillId}` |

Response `204 No Content`.

## 5. Quy ước response và lỗi

Các response lỗi nên sử dụng format thống nhất:

```json
{
  "code": "CV_NOT_FOUND",
  "message": "CV does not belong to the current candidate",
  "timestamp": "2026-09-25T10:30:00Z",
  "path": "/api/v1/candidates/me/cvs/15"
}
```

Một số HTTP status đề xuất:

| Status | Trường hợp |
|---|---|
| `200 OK` | Lấy hoặc cập nhật dữ liệu thành công |
| `201 Created` | Tạo candidate hoặc thêm skill thành công |
| `202 Accepted` | Upload hoặc yêu cầu parse bất đồng bộ |
| `204 No Content` | Xóa thành công |
| `400 Bad Request` | Request không hợp lệ hoặc file không được hỗ trợ |
| `401 Unauthorized` | Chưa xác thực |
| `403 Forbidden` | Không có quyền truy cập |
| `404 Not Found` | Không tìm thấy candidate hoặc CV |
| `409 Conflict` | Dữ liệu bị trùng hoặc trạng thái hiện tại không cho phép thao tác |

## 6. Đề xuất bổ sung cho entity

Để API parse có thể trả về thông tin đầy đủ, nên cân nhắc bổ sung các field sau vào `CvEntity`:

```java
private String parseError;
private OffsetDateTime parsedAt;
```

`parseError` dùng cho trạng thái `FAILED`, còn `parsedAt` ghi nhận thời điểm parse thành công. Client không được tự gửi hoặc chỉnh sửa `parsedData`; dữ liệu này chỉ được cập nhật từ callback của `cv-parser-service`.
