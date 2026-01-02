# Blog API Specification

## Base URL
```
http://localhost:8080
```

## Authentication
The API uses JWT Bearer tokens. Include the token in the `Authorization` header:
```
Authorization: Bearer <your-jwt-token>
```

---

## Endpoints

### Authentication

#### POST /api/auth/register
Register a new user.

**Request Body:**
```json
{
  "username": "string (3-50 chars, required)",
  "email": "string (valid email, required)",
  "password": "string (6-100 chars, required)",
  "displayName": "string (optional, max 100 chars)"
}
```

**Response (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "displayName": "John Doe",
    "avatarUrl": null,
    "role": "USER"
  }
}
```

---

#### POST /api/auth/login
Authenticate and get JWT token.

**Request Body:**
```json
{
  "usernameOrEmail": "string (required)",
  "password": "string (required)"
}
```

**Response (200):** Same as register response.

**Response (401):**
```json
{
  "timestamp": "2026-01-01T12:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username/email or password",
  "path": "/api/auth/login"
}
```

---

#### GET /api/auth/me
Get current authenticated user info.

**Headers:** `Authorization: Bearer <token>` (required)

**Response (200):**
```json
{
  "user": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "displayName": "John Doe",
    "avatarUrl": null,
    "role": "USER"
  }
}
```

---

### Posts

#### GET /api/posts
List published posts with pagination and filters.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | int | 0 | Page number (0-indexed) |
| size | int | 10 | Page size (max 50) |
| tag | string | - | Filter by tag slug |
| category | string | - | Filter by category slug |
| q | string | - | Search query |

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "title": "My First Post",
      "excerpt": "A short summary...",
      "slug": "my-first-post-1234567890",
      "author": "Admin",
      "tags": ["tutorial", "welcome"],
      "categoryName": "General",
      "categorySlug": "general",
      "createdAt": "2026-01-01T10:00:00Z",
      "updatedAt": "2026-01-01T10:00:00Z",
      "coverImageUrl": "https://ik.imagekit.io/...",
      "readTime": 3
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false
}
```

---

#### GET /api/posts/{id}
Get a single post by ID.

**Response (200):**
```json
{
  "id": 1,
  "title": "My First Post",
  "content": "<p>Full HTML content...</p>",
  "excerpt": "A short summary...",
  "slug": "my-first-post-1234567890",
  "author": "Admin",
  "tags": [
    { "id": 1, "name": "tutorial", "slug": "tutorial" }
  ],
  "category": {
    "id": 1,
    "name": "General",
    "slug": "general"
  },
  "createdAt": "2026-01-01T10:00:00Z",
  "updatedAt": "2026-01-01T10:00:00Z",
  "coverImageUrl": "https://ik.imagekit.io/...",
  "readTime": 3,
  "published": true
}
```

---

#### GET /api/posts/slug/{slug}
Get a single post by slug.

**Response:** Same as GET /api/posts/{id}

---

#### GET /api/posts/recent
Get recently updated posts.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| limit | int | 5 | Number of posts (max 20) |

**Response (200):** Array of PostSummary objects.

---

#### POST /api/posts
Create a new post. **Admin only.**

**Headers:** `Authorization: Bearer <token>` (required, admin role)

**Request Body:**
```json
{
  "title": "string (required, max 200 chars)",
  "content": "string (HTML content)",
  "excerpt": "string (optional, max 500 chars)",
  "author": "string (optional, max 100 chars)",
  "tags": ["string"],
  "categoryName": "string (optional)",
  "coverImageUrl": "string (optional)",
  "published": true
}
```

**Response (201):** Full Post object with Location header.

---

#### PUT /api/posts/{id}
Update an existing post. **Admin only.**

**Headers:** `Authorization: Bearer <token>` (required, admin role)

**Request Body:** Same as POST, all fields optional.

**Response (200):** Updated Post object.

---

#### DELETE /api/posts/{id}
Delete a post. **Admin only.**

**Headers:** `Authorization: Bearer <token>` (required, admin role)

**Response (204):** No content.

---

### Tags

#### GET /api/tags
Get all tags with post counts.

**Response (200):**
```json
[
  { "id": 1, "name": "tutorial", "slug": "tutorial", "postCount": 5 }
]
```

---

#### GET /api/tags/trending
Get trending tags (by post count).

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| limit | int | 10 | Number of tags |

**Response (200):** Array of Tag objects.

---

### Categories

#### GET /api/categories
Get all categories with post counts.

**Response (200):**
```json
[
  { "id": 1, "name": "General", "slug": "general", "description": "...", "postCount": 10 }
]
```

---

### Images

#### GET /api/imagekit/auth
Get ImageKit authentication parameters for client-side upload.

**Response (200):**
```json
{
  "signature": "string",
  "token": "string",
  "expire": 1234567890
}
```

---

#### POST /api/images/upload
Upload an image via backend proxy. **Authenticated users only.**

**Headers:** `Authorization: Bearer <token>` (required)

**Request:** `multipart/form-data`
- `file`: Image file
- `postId`: (optional) Associated post ID

**Response (200):**
```json
{
  "url": "https://ik.imagekit.io/.../image.jpg",
  "fileId": "abc123",
  "name": "image.jpg",
  "size": 12345,
  "fileType": "image/jpeg"
}
```

---

## Error Responses

All errors follow this format:
```json
{
  "timestamp": "2026-01-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Human-readable error message",
  "path": "/api/posts",
  "details": ["field1: error message", "field2: error message"]
}
```

### HTTP Status Codes
| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 204 | No Content (delete success) |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (not logged in) |
| 403 | Forbidden (not admin) |
| 404 | Not Found |
| 409 | Conflict (duplicate entry) |
| 500 | Internal Server Error |

---

## Example curl Commands

### Register and Login
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@example.com","password":"admin123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"admin123"}'
```

### Posts (public)
```bash
# List posts
curl "http://localhost:8080/api/posts?page=0&size=10"

# Search posts
curl "http://localhost:8080/api/posts?q=tutorial"

# Filter by tag
curl "http://localhost:8080/api/posts?tag=tutorial"

# Get single post
curl http://localhost:8080/api/posts/1
```

### Posts (admin)
```bash
# Create post (replace TOKEN with actual JWT)
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "title": "My Post",
    "content": "<p>Hello World</p>",
    "excerpt": "A greeting",
    "tags": ["hello", "world"],
    "categoryName": "General",
    "published": true
  }'

# Update post
curl -X PUT http://localhost:8080/api/posts/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"title": "Updated Title"}'

# Delete post
curl -X DELETE http://localhost:8080/api/posts/1 \
  -H "Authorization: Bearer TOKEN"
```

---

## Default Admin User

On first startup, a default admin user is created:
- **Username:** admin
- **Email:** admin@example.com
- **Password:** admin123
- **Role:** ADMIN

⚠️ **Change this password in production!**

