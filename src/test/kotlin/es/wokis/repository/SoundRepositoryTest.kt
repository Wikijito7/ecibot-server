package es.wokis.repository

import es.wokis.data.bo.sound.SoundBO
import es.wokis.data.bo.sound.SoundUserBO
import es.wokis.data.bo.user.UserBO
import es.wokis.data.datasource.local.sound.SoundsLocalDataSource
import es.wokis.data.exception.SoundNotFoundException
import es.wokis.data.repository.sound.SoundRepositoryImpl
import es.wokis.services.SoundFileService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SoundRepositoryTest {
    private val dataSource = mockk<SoundsLocalDataSource>()
    private val repository = SoundRepositoryImpl(dataSource)

    private val baseSound = SoundBO(
        displayId = "sound123",
        title = "Test Sound",
        soundUrl = "https://api.example.com/sound/sound123/file",
        createdBy = "owner123",
        createdOn = 1000L,
        status = "pending"
    )

    private val alice = UserBO(
        id = "user1",
        username = "Alice",
        email = "alice@test.com",
        password = "hash"
    )

    @BeforeEach
    fun setup() {
        mockkObject(SoundFileService)
        every { SoundFileService.deleteSoundFiles(any()) } returns true
    }

    @AfterEach
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `Given no existing votes When voteSound up Then adds user to thumbsUp`() = runTest {
        coEvery { dataSource.getSoundByDisplayId("sound123") } returns baseSound
        coEvery { dataSource.updateSound(any()) } returns true

        val result = repository.voteSound(alice, "sound123", "up")

        assertTrue(result)
        coVerify {
            dataSource.updateSound(match { it.thumbsUp.any { u -> u.id == "user1" } })
        }
    }

    @Test
    fun `Given user already voted up When voteSound down Then moves from thumbsUp to thumbsDown`() = runTest {
        val soundWithUpvote = baseSound.copy(
            thumbsUp = listOf(SoundUserBO(id = "user1", displayName = "Alice"))
        )
        coEvery { dataSource.getSoundByDisplayId("sound123") } returns soundWithUpvote
        coEvery { dataSource.updateSound(any()) } returns true

        val result = repository.voteSound(alice, "sound123", "down")

        assertTrue(result)
        coVerify {
            dataSource.updateSound(match { sound ->
                sound.thumbsUp.none { it.id == "user1" } &&
                    sound.thumbsDown.any { it.id == "user1" }
            })
        }
    }

    @Test
    fun `Given same vote twice When voteSound Then toggles off`() = runTest {
        val soundWithUpvote = baseSound.copy(
            thumbsUp = listOf(SoundUserBO(id = "user1", displayName = "Alice"))
        )
        coEvery { dataSource.getSoundByDisplayId("sound123") } returns soundWithUpvote
        coEvery { dataSource.updateSound(any()) } returns true

        repository.voteSound(alice, "sound123", "up")

        coVerify {
            dataSource.updateSound(match { sound ->
                sound.thumbsUp.none { it.id == "user1" } &&
                    sound.thumbsDown.none { it.id == "user1" }
            })
        }
    }

    @Test
    fun `Given non-existent sound When voteSound Then throws SoundNotFoundException`() = runTest {
        coEvery { dataSource.getSoundByDisplayId("nonexistent") } returns null

        assertThrows<SoundNotFoundException> {
            repository.voteSound(alice, "nonexistent", "up")
        }
    }

    @Test
    fun `Given user id is null When voteSound Then returns false`() = runTest {
        val userWithNullId = alice.copy(id = null)
        coEvery { dataSource.getSoundByDisplayId("sound123") } returns baseSound

        val result = repository.voteSound(userWithNullId, "sound123", "up")

        assertFalse(result)
    }

    @Test
    fun `Given existing sound When updateSound Then updates title only`() = runTest {
        coEvery { dataSource.getSoundByDisplayId("sound123") } returns baseSound
        coEvery { dataSource.updateSound(any()) } returns true

        val result = repository.updateSound("sound123", "New Title", null, alice)

        assertTrue(result)
        coVerify {
            dataSource.updateSound(match { sound ->
                sound.title == "New Title" && sound.description == baseSound.description
            })
        }
    }

    @Test
    fun `Given existing sound When removeSound Then deletes sound`() = runTest {
        coEvery { dataSource.getSoundByDisplayId("sound123") } returns baseSound
        coEvery { dataSource.deleteSound("sound123") } returns true

        val result = repository.removeSound("sound123", alice)

        assertTrue(result)
        coVerify { dataSource.deleteSound("sound123") }
    }

    @Test
    fun `Given non-existent sound When removeSound Then throws SoundNotFoundException`() = runTest {
        coEvery { dataSource.getSoundByDisplayId("nonexistent") } returns null

        assertThrows<SoundNotFoundException> {
            repository.removeSound("nonexistent", alice)
        }
    }
}
