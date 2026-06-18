---
name: file-upload
description: "Load when implementing multipart file upload, download, or file storage. Covers multipart parsing, validation, disk persistence, and URL generation."
---

## When to use me
- Adding file upload endpoints (images, audio, attachments)
- Creating a file storage service (like `ImageService`)
- Reading/writing files to disk from multipart requests
- Configuring file storage paths

## Not intended for
- Database operations → use `data-access`
- General route setup → use `api-routing`

---

## File Upload Flow

```
HTTP Multipart Request
  ↓
call.receiveMultipart()         — Ktor multipart stream
  ↓
getAllParts()                   — collect all parts (utils/MultipartExtensions.kt)
  ↓
.filter { contentType startsWith "audio"|"image" }  — content type validation
  ↓
PartData.FileItem               — the actual file data (streamProvider, content-type, filename)
  ↓
File storage service            — save to disk, return accessible URL
```

## Multipart Parsing

### Collect parts
```kotlin
val multipartData = call.receiveMultipart()
val allParts = multipartData.getAllParts()
```

### Filter by content type
```kotlin
val audioFiles = allParts
    .filterIsInstance<PartData.FileItem>()
    .filter { it.contentType.toString().startsWith("audio") }

// Single file lookup
val singleFile = allParts
    .filterIsInstance<PartData.FileItem>()
    .find { it.contentType.toString().startsWith("image") }
    ?: run {
        call.respond(HttpStatusCode.UnsupportedMediaType)
        return@post
    }
```

### `getAllParts()` extension
Located in `utils/MultipartExtensions.kt`:
```kotlin
suspend fun MultiPartData.getAllParts(): List<PartData> = mutableListOf<PartData>().apply {
    forEachPart { add(it) }
}.toList()
```

## File Storage Service Pattern

Use an `object` (like `ImageService`) or a Koin `single` for file operations:

```kotlin
object MyFileService {
    private const val EXTENSION = "mp3"
    private val storeFolder = config.getString("myFolder")
    private val baseUri = config.getString("baseUri")

    fun insertFile(id: String, file: PartData.FileItem): String {
        val fileName = "$id.$EXTENSION"
        val filePath = File("$storeFolder/$id", fileName).normalize()
        val inputStream = file.streamProvider.invoke()

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        filePath.writeBytes(inputStream.readBytes())
        inputStream.close()

        return "$baseUri/files/$id/".normalizeUrl()
    }
}
```

### Key API
- `PartData.FileItem` — `.streamProvider.invoke()` gives `InputStream`
- `PartData.FileItem` — `.contentType` gives MIME type
- `PartData.FileItem` — `.originalFileName` gives original filename
- Use `java.io.File(...).normalize()` to prevent path traversal
- Use `File(...).normalize().path.replace("\\", "/")` for URL-safe paths

### Serving files via Ktor
```kotlin
get("/file/{id}") {
    val id = call.parameters["id"]
    id?.let {
        call.respondFile(MyFileService.getFile(id))
    }
}
```

## Config (`app.conf`)

Always add storage paths to `app.conf`:
```hocon
# Folders are relative to the working directory by default
imageFolder = "images/"
soundFolder = "sounds/"
```

Access via:
```kotlin
val folder = config.getString("imageFolder")
val baseUri = config.getString("baseUri")
```

## Response Pattern for Uploads

After persisting a file, respond with the generated URL:
```kotlin
val url = service.insertFile(userId, file)
call.respond(HttpStatusCode.OK, url)
```

## Blockers (MUST NOT)
- Saving files without content-type validation (can lead to arbitrary file upload)
- Using user-provided filenames directly (path traversal risk)
- Not calling `streamProvider.invoke()` within a `use {}` block (resource leak)
- Skipping folder creation (`mkdirs()`) before writing
- Returning raw file paths — always generate a URL via `normalizeUrl()`

## References
- `services/ImageService.kt` — reference file upload implementation
- `routing/UserRouting.kt` — multipart usage in routes (images endpoint)
- `routing/SoundRouting.kt` — multipart usage for audio upload
- `utils/MultipartExtensions.kt` — `getAllParts()` helper
- `utils/StringUtils.kt` — `normalizeUrl()`
