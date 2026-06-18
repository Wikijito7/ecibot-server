---
name: social-reaction
description: "Load when implementing voting, reactions, or any user-content interaction (upvote, downvote, emoji reactions). Covers toggle logic, data modeling, and API design."
---

## When to use me
- Implementing upvote/downvote or star ratings
- Adding emoji/unicode reactions to content
- Any feature where users interact with content items
- Designing vote/reaction data models

## Not intended for
- File uploads → use `file-upload`
- General CRUD → use `data-access` / `api-routing`

---

## Voting Patterns

### Single Vote Endpoint (recommended)
Use one endpoint with a request body to specify vote direction:

```kotlin
post("/{id}/vote") {
    val callUser = call.user
    val contentId = call.parameters["id"]
    val voteRequest = call.receive<VoteRequestDTO>()  // { "type": "up"|"down" }
    
    callUser?.let { user ->
        contentId?.let {
            repository.vote(user, it, voteRequest.type)
        }
    }
}
```

```kotlin
data class VoteRequestDTO(
    @SerialName("type")
    val type: String  // "up" or "down"
)
```

### Split Endpoints (alternative)
Separate endpoints for each direction — simpler routing but more routes:

```kotlin
post("/{id}/upvote") { /* ... */ }
post("/{id}/downvote") { /* ... */ }
```

## Toggle Logic

### User-ID-based tracking (recommended)
Store lists of user IDs who voted:

```kotlin
// In repository/vote logic:
fun toggleVote(user: UserBO, contentId: String, direction: String) {
    val content = getContent(contentId)
    
    // Remove user from opposite list first
    val oppositeVotes = if (direction == "up") 
        content.thumbsDown.filterNot { it == user.id } 
    else 
        content.thumbsUp.filterNot { it == user.id }
    
    // Toggle: remove if already in list, add if not
    val targetVotes = if (direction == "up") 
        content.thumbsUp.toMutableList().apply {
            if (contains(user.id)) remove(user.id) else add(user.id!!)
        }
    else 
        content.thumbsDown.toMutableList().apply {
            if (contains(user.id)) remove(user.id) else add(user.id!!)
        }
    
    // Update in DB
    updateVotes(contentId, targetVotes, oppositeVotes)
}
```

### Embedded User Data (avoid when possible)
Storing full user objects in vote lists makes the DB document grow and can become stale. Prefer storing just user IDs.

```kotlin
// DO this — lightweight, references only:
val thumbsUp: List<String>     // user IDs
val thumbsDown: List<String>   // user IDs

// AVOID this — heavy, stale-prone:
val thumbsUp: List<UserDBO>    // full user documents
```

## Data Model for Votes

```kotlin
// DTO (API response)
data class SoundDTO(
    val thumbsUp: List<String>,    // user IDs
    val thumbsDown: List<String>,  // user IDs
    // ... other fields
)

// BO (business logic)
data class SoundBO(
    val thumbsUp: List<String>,
    val thumbsDown: List<String>,
    // ... other fields
)

// DBO (MongoDB)
data class SoundDBO(
    val thumbsUp: List<String>,
    val thumbsDown: List<String>,
    // ... other fields
)
```

## Emoji/Unicode Reactions Pattern

For reactions like 👍, ❤️, 😂, use a flat list of (unicode, userId) pairs:

```kotlin
data class ReactionDBO(
    val unicode: String,    // emoji character
    val addedBy: String      // user ID
)
```

This allows:
- Multiple reactions per user (one per emoji)
- Counting per emoji type
- Easy toggle (add/remove by matching both fields)

## API Response Examples

```json
{
    "id": 1,
    "title": "cat sound",
    "soundUrl": "...",
    "createdBy": "user123",
    "thumbsUp": ["user1", "user2"],
    "thumbsDown": ["user3"],
    "createdOn": 1700000000000,
    "reactions": [
        { "unicode": "😂", "userId": "user1" },
        { "unicode": "👍", "userId": "user2" }
    ]
}
```

## Blockers (MUST NOT)
- Allowing duplicate votes from the same user (always check before inserting)
- Storing full user documents in vote lists (use IDs)
- Missing toggle — clicking again should undo the vote
- Not clearing opposite vote when switching direction (up→down should remove up)
- Using separate DB collections for votes when a simple list in the content document works

## References
- `data/dbo/SoundDBO.kt` — vote + reaction model
- `data/bo/sound/SoundBO.kt` — business model
- `data/dto/sound/SoundDTO.kt` — API contract
- `routing/SoundRouting.kt` — vote endpoints
