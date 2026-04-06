# civil-service-assessment
Technical Assessment for Civil Service Application

## Run instructions

To run the backend Spring Boot server, navigate to the directory `/backend/hmcts-dev-test-backend` and run `./gradlew bootRun`.

To run the frontend node application navigate to the directory `/frontend/hmcts-dev-test-frontend` and run `yarn start`.

## API Reference

Base URL: `http://localhost:4000`

CORS is enabled for `http://localhost:3100`.

---

### Case object

All endpoints that return a case use this structure:

```json
{
  "id": 1,
  "title": "Smith v Jones",
  "description": "Personal injury claim",
  "status": "Open",
  "dueDate": "2026-05-06"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `id` | integer | auto-assigned | Set by the server on creation |
| `title` | string | yes | Must not be blank |
| `description` | string | no | May be blank or omitted |
| `status` | string | yes | Must not be blank |
| `dueDate` | string | yes | ISO 8601 date format: `YYYY-MM-DD` |

---

### GET /cases

Returns all cases.

**Response `200 OK`**
```json
[
  {
    "id": 1,
    "title": "Smith v Jones",
    "description": "Personal injury claim",
    "status": "Open",
    "dueDate": "2026-05-06"
  }
]
```

---

### GET /cases/{id}

Returns a single case by ID.

**Response `200 OK`**
```json
{
  "id": 1,
  "title": "Smith v Jones",
  "description": "Personal injury claim",
  "status": "Open",
  "dueDate": "2026-05-06"
}
```

**Response `404 Not Found`**
```json
{
  "error": "Case with id 1 not found"
}
```

---

### POST /cases

Creates a new case.

**Request body**
```json
{
  "title": "Smith v Jones",
  "description": "Personal injury claim",
  "status": "Open",
  "dueDate": "2026-05-06"
}
```

**Response `201 Created`**

Returns the created case object. The `Location` header is set to `/cases/{id}`.

```json
{
  "id": 1,
  "title": "Smith v Jones",
  "description": "Personal injury claim",
  "status": "Open",
  "dueDate": "2026-05-06"
}
```

**Response `400 Bad Request` — validation failure**

Returned when required fields are blank or missing. Spring Boot's default error handler intercepts this and returns RFC 9457 Problem Details format:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failure",
  "instance": "/cases"
}
```

**Response `400 Bad Request` — malformed JSON**

Returned when the request body is not valid JSON. Also intercepted by Spring Boot's default error handler:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Failed to read request",
  "instance": "/cases"
}
```

---

### PATCH /cases/{id}/status

Updates the status of an existing case.

**Request body**
```json
{
  "status": "In Progress"
}
```

**Response `200 OK`**

Returns the full updated case object.

```json
{
  "id": 1,
  "title": "Smith v Jones",
  "description": "Personal injury claim",
  "status": "In Progress",
  "dueDate": "2026-05-06"
}
```

**Response `404 Not Found`**
```json
{
  "error": "Case with id 1 not found"
}
```

---

### DELETE /cases/{id}

Deletes a case by ID.

**Response `204 No Content`**

Empty response body.

**Response `404 Not Found`**
```json
{
  "error": "Case with id 1 not found"
}
```
