# Application Service

Tài liệu này mô tả phạm vi API và entity đề xuất cho `application-service`. Service này quản lý vòng đời một hồ sơ ứng tuyển, pipeline tuyển dụng, đánh giá và phỏng vấn.

## 1. Phạm vi và quy ước

- Base URL: `/api/v1`.
- `candidateId`, `authorId`, `interviewerId` lấy từ JWT/Keycloak hoặc service-to-service token; client không được tự ghi đè các giá trị này.
- API 2.1 hiện nhận `X-Candidate-Id` do gateway/security layer gắn vào request; client không được tự gửi hoặc thay đổi header này.
- `jobId`, `candidateId`, `cvId`, `departmentId` là tham chiếu cross-service và không tạo JPA relationship/FK tới database của service khác.
- API recruiter/admin cần kiểm tra quyền truy cập theo job/department.
- ID theo code hiện tại: `jobId` và `departmentId` là `UUID`; `candidateId`, `cvId` là `Long`; ID của application và các entity nội bộ là `Long`.
- Các response lỗi nên thống nhất với format của candidate-service (`code`, `message`, `timestamp`, `path`).

## 2. API đề xuất

### 2.1 Candidate applications

| Method | Endpoint | Actor | Mục đích |
|---|---|---|---|
| `POST` | `/api/v1/applications` | Candidate | Tạo hồ sơ ứng tuyển cho một job |
| `GET` | `/api/v1/applications/me` | Candidate | Liệt kê các hồ sơ ứng tuyển của candidate hiện tại |
| `GET` | `/api/v1/applications/{applicationId}` | Candidate/Recruiter | Xem chi tiết hồ sơ nếu có quyền |
| `POST` | `/api/v1/applications/{applicationId}/withdraw` | Candidate | Candidate rút hồ sơ |

Request tạo application:

```json
{
  "jobId": "018f2f95-4f7f-7c4e-b2d7-2bba5a4a2f01",
  "cvId": 15
}
```

`cvId` có thể bỏ qua nếu hệ thống tự dùng CV active của candidate. Service phải kiểm tra job đang `PUBLISHED`, CV thuộc candidate hiện tại, candidate chưa có application active cho job này và gán pipeline stage mặc định.

Response `201 Created`:

```json
{
  "id": 101,
  "jobId": "018f2f95-4f7f-7c4e-b2d7-2bba5a4a2f01",
  "candidateId": 42,
  "cvId": 15,
  "departmentId": "018f2f95-4f7f-7c4e-b2d7-2bba5a4a2f02",
  "status": "SUBMITTED",
  "pipelineStage": {
    "id": 1,
    "name": "Applied",
    "order": 1
  },
  "appliedAt": "2026-10-01T09:30:00Z"
}
```

### 2.2 Recruiter application management

| Method | Endpoint | Actor | Mục đích |
|---|---|---|---|
| `GET` | `/api/v1/applications` | Recruiter/Admin | Tìm kiếm, lọc và phân trang applications |
| `PATCH` | `/api/v1/applications/{applicationId}/stage` | Recruiter/Admin | Chuyển application sang pipeline stage khác |
| `PATCH` | `/api/v1/applications/{applicationId}/status` | Recruiter/Admin | Cập nhật trạng thái nghiệp vụ |
| `POST` | `/api/v1/applications/{applicationId}/notes` | Recruiter/Admin | Thêm ghi chú đánh giá |
| `GET` | `/api/v1/applications/{applicationId}/notes` | Recruiter/Admin | Xem lịch sử ghi chú |

Query đề xuất cho danh sách:

```text
GET /api/v1/applications?jobId={jobId}&stageId=2&status=IN_REVIEW&candidateId=42&page=0&size=20&sort=appliedAt,desc
```

Request chuyển stage:

```json
{
  "toStageId": 2,
  "notes": "Đạt vòng sàng lọc CV"
}
```

Mỗi lần chuyển stage phải tạo một `stage_transition`; không cập nhật hoặc xóa lịch sử đã ghi nhận. `PATCH status` chỉ cho phép các chuyển đổi hợp lệ, ví dụ `SUBMITTED -> IN_REVIEW -> SHORTLISTED/REJECTED`.

Request thêm evaluation note:

```json
{
  "content": "Có kinh nghiệm Spring Boot phù hợp vị trí.",
  "stageId": 2
}
```

`authorId` lấy từ JWT. `stageId` có thể bỏ qua để ghi chú chung cho application.

### 2.3 Pipeline stages

| Method | Endpoint | Actor | Mục đích |
|---|---|---|---|
| `GET` | `/api/v1/pipeline-stages` | Recruiter/Admin | Lấy danh sách stage theo `stageOrder` |
| `POST` | `/api/v1/pipeline-stages` | Admin | Tạo stage |
| `PUT` | `/api/v1/pipeline-stages/{stageId}` | Admin | Cập nhật tên, thứ tự, màu |
| `PATCH` | `/api/v1/pipeline-stages/{stageId}/active` | Admin | Bật/tắt stage, chỉ dùng nếu bổ sung `isActive` |

Theo schema hiện tại, `PipelineStage` chưa có `isActive`; API này chỉ cần triển khai nếu muốn vô hiệu hóa stage mà không xóa dữ liệu. Không cho tắt stage đang được application sử dụng nếu chưa có stage thay thế. Chỉ một stage mặc định nên được active tại một thời điểm trong cùng pipeline.

### 2.4 Interviews

| Method | Endpoint | Actor | Mục đích |
|---|---|---|---|
| `POST` | `/api/v1/applications/{applicationId}/interviews` | Recruiter/Admin | Lên lịch phỏng vấn |
| `GET` | `/api/v1/applications/{applicationId}/interviews` | Candidate/Recruiter | Xem các buổi phỏng vấn của application |
| `GET` | `/api/v1/interviews/{interviewId}` | Candidate/Recruiter | Xem chi tiết một buổi phỏng vấn |
| `PATCH` | `/api/v1/interviews/{interviewId}` | Recruiter/Admin | Đổi lịch, meeting link hoặc trạng thái |
| `POST` | `/api/v1/interviews/{interviewId}/feedback` | Interviewer/Recruiter | Ghi nhận feedback và kết quả |
| `PUT` | `/api/v1/interviews/{interviewId}/interviewers` | Recruiter/Admin | Thay thế danh sách interviewer |

Request tạo interview:

```json
{
  "interviewTemplateId": 3,
  "interviewType": "TECHNICAL",
  "scheduledAt": "2026-10-05T08:00:00Z",
  "meetingLink": "https://meeting.example/abc",
  "durationMinutes": 60,
  "interviewerIds": ["recruiter-user-id", "engineer-user-id"]
}
```

Request feedback:

```json
{
  "feedbackText": "Nắm vững Java và REST API.",
  "score": 8,
  "result": "PASS",
  "notesFilePath": null
}
```

Feedback nên được cập nhật bởi interviewer được gán hoặc recruiter có quyền. Có thể cho phép nhiều interviewer cùng ghi feedback, nhưng mỗi request phải xác định được người ghi từ JWT.

### 2.5 Interview templates

| Method | Endpoint | Actor | Mục đích |
|---|---|---|---|
| `GET` | `/api/v1/interview-templates` | Recruiter/Admin | Liệt kê template đang active |
| `POST` | `/api/v1/interview-templates` | Admin | Tạo template |
| `PUT` | `/api/v1/interview-templates/{templateId}` | Admin | Cập nhật tên hoặc file template |
| `PATCH` | `/api/v1/interview-templates/{templateId}/active` | Admin | Bật/tắt template |

### 2.6 Email action token

Các API này phục vụ email worker/notification service, không mở cho client thông thường:

| Method | Endpoint | Actor | Mục đích |
|---|---|---|---|
| `POST` | `/internal/v1/applications/{applicationId}/action-tokens` | Internal service | Tạo token cho một action trong email |
| `POST` | `/internal/v1/application-action-tokens/{token}/consume` | Internal service | Kiểm tra và consume token một lần |

Token chỉ lưu `tokenHash`, có thời hạn, gắn với một `actionType` và không được trả lại dưới dạng plaintext sau lần tạo. Việc consume phải idempotent và không cho phép dùng lại token đã có `usedAt`.

## 3. Entity cần review trước khi code

### 3.1 Aggregate root và entity

| Entity / bảng | Vai trò | Field chính | Quan hệ / ràng buộc đề xuất |
|---|---|---|---|
| `Application` / `applications` | Aggregate root, đại diện một lần candidate ứng tuyển vào job | `id: Long`, `jobId: UUID`, `candidateId: Long`, `cvId: Long`, `departmentId: UUID`, `transferredFrom: Long?`, `pipelineStageId: Long`, `status`, `appliedAt` | N-1 tới `PipelineStage`; `transferredFrom` tự tham chiếu; các ID job/candidate/CV/department là cross-service reference; unique application active theo `(jobId, candidateId)` |
| `PipelineStage` / `pipeline_stages` | Cấu hình các bước trong pipeline | `id: Long`, `stageName`, `stageOrder`, `color`, `isDefault` | `stageName` không rỗng; `stageOrder` không trùng trong pipeline; không xóa stage đang được sử dụng; chỉ bổ sung `isActive` nếu cần tắt stage mềm |
| `StageTransition` / `stage_transitions` | Lịch sử chuyển stage | `id: Long`, `applicationId`, `fromStageId`, `toStageId`, `notes`, `movedAt` | N-1 tới `Application` và `PipelineStage`; append-only; `fromStageId` có thể null khi tạo application |
| `EvaluationNote` / `evaluation_notes` | Ghi chú đánh giá của recruiter/interviewer | `id: Long`, `applicationId`, `content`, `stageId`, `authorId` | N-1 tới `Application`; `authorId` là Keycloak reference; không xóa cứng nếu cần audit |
| `Interview` / `interviews` | Một buổi phỏng vấn của application | `id: Long`, `applicationId`, `interviewTemplateId`, `interviewType`, `scheduledAt`, `meetingLink`, `status`, `durationMinutes`, `feedback` | N-1 tới `Application`; N-1 tới `InterviewTemplate`; danh sách interviewer qua junction table; trạng thái đề xuất `SCHEDULED`, `COMPLETED`, `CANCELLED`, `NO_SHOW` |
| `InterviewTemplate` / `interview_templates` | Template/file dùng cho phỏng vấn | `id: Long`, `templateName`, `filePath`, `isActive` | Không xóa cứng template đã được interview sử dụng; chỉ tạo interview với template active |
| `InterviewInterviewer` / `interview_interviewers` | Bảng liên kết interview và user | `id: Long`, `interviewId`, `userId` | Unique `(interviewId, userId)`; `userId` là Keycloak reference |
| `ActionableEmailToken` / `actionable_email_tokens` | Token dùng một lần cho hành động từ email | `id: Long`, `applicationId`, `tokenHash`, `actionType`, `expiresAt`, `usedAt` | N-1 tới `Application`; index trên `tokenHash`; token hết hạn hoặc đã dùng thì không hợp lệ |

### 3.2 Value object và enum

| Thành phần | Nội dung đề xuất |
|---|---|
| `InterviewFeedback` | `feedbackText`, `score`, `result`, `interviewerName`, `interviewerEmail`, `notesFilePath`; có thể lưu embedded trong `interviews` theo schema hiện tại |
| `ApplicationStatus` | `SUBMITTED`, `IN_REVIEW`, `SHORTLISTED`, `INTERVIEWING`, `OFFERED`, `HIRED`, `REJECTED`, `WITHDRAWN` |
| `InterviewStatus` | `SCHEDULED`, `COMPLETED`, `CANCELLED`, `NO_SHOW` |
| `InterviewResult` | `PENDING`, `PASS`, `FAIL` |
| `ActionType` | Các action được phép trong email, ví dụ `CONFIRM_INTERVIEW`, `CANCEL_INTERVIEW`, `SUBMIT_FEEDBACK` |

### 3.3 Audit fields dùng chung

Các entity persistence nên cân nhắc kế thừa hoặc embed một `AuditMetadata` gồm:

```text
createdAt: OffsetDateTime
updatedAt: OffsetDateTime
createdBy: String/UUID?
updatedBy: String/UUID?
isDeleted: boolean
```

`isDeleted` chỉ áp dụng khi nghiệp vụ cần soft delete; lịch sử `StageTransition` và token đã consume không nên bị xóa để mất dấu audit.

## 4. Quy tắc nghiệp vụ cần chốt

1. Một candidate có được ứng tuyển lại sau khi `REJECTED` hoặc `WITHDRAWN` không? Nếu có, cần dùng version/re-application thay vì unique tuyệt đối `(jobId, candidateId)`.
2. `departmentId` lấy snapshot từ job tại thời điểm apply hay luôn đọc lại từ `job-service`?
3. `cvId` bắt buộc hay tự động lấy active CV; có cho phép apply khi CV đang `PENDING` parse không?
4. `ApplicationStatus` có độc lập với `PipelineStage` hay mỗi stage ánh xạ cố định sang một status?
5. Pipeline là dùng chung toàn hệ thống hay mỗi department/job có pipeline riêng? Schema hiện tại đang thể hiện pipeline dùng chung.
6. `score` của feedback dùng thang điểm nào và có bắt buộc khi `result` là `PASS`/`FAIL` không?
7. `transferredFrom` là chuyển giữa các job hay giữa các department; ai được phép chuyển và có cần lưu lý do chuyển không?

## 5. Ranh giới với service khác

- `candidate-service`: xác minh candidate, CV, CV active và quyền sở hữu CV; không copy toàn bộ `CandidateEntity` vào application-service.
- `job-service`: xác minh job đang mở, lấy `departmentId` và kiểm tra recruiter có quyền với job; không tạo `JobEntity` trong application-service.
- Keycloak/identity: cung cấp `candidateId`, `authorId`, `interviewerId` theo subject/claim đã thống nhất.
- Notification/email service: nhận event tạo application, đổi stage, lên lịch phỏng vấn và tạo action token; application-service là nơi sở hữu trạng thái nghiệp vụ.

Các điểm ở mục 4 cần được chốt trước khi tạo migration và entity JPA.
