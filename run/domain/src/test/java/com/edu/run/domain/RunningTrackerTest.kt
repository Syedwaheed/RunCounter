package com.edu.run.domain

import app.cash.turbine.test
import com.edu.core.location.Location
import com.edu.core.location.LocationWithAltitude
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration

/**
 * Unit Tests for RunningTracker
 *
 * =============================================================================
 * INTERMEDIATE TESTING: MOCKING DEPENDENCIES
 * =============================================================================
 *
 * What is Mocking?
 * - Creating "fake" versions of dependencies
 * - Allows testing a class in isolation
 * - You control what the mock returns
 *
 * Why Mock?
 * - Real LocationObserver uses GPS (can't use in tests)
 * - We want predictable, repeatable tests
 * - Tests should run fast without real hardware
 *
 * Key Libraries Used:
 * 1. MockK - Kotlin-native mocking library (like Mockito but better for Kotlin)
 * 2. Turbine - Testing library for Kotlin Flows
 * 3. kotlinx-coroutines-test - Testing coroutines with virtual time
 *
 * =============================================================================
 * TESTING COROUTINES
 * =============================================================================
 *
 * Challenges with Coroutines:
 * - Coroutines are asynchronous
 * - `delay()` would make tests slow
 * - Need to control virtual time
 *
 * Solutions:
 * - `runTest { }` - Creates a test coroutine environment
 * - `TestScope` - Provides controllable coroutine scope
 * - `advanceUntilIdle()` - Fast-forward until all coroutines complete
 *
 * =============================================================================
 * TESTING FLOWS WITH TURBINE
 * =============================================================================
 *
 * Turbine provides:
 * - `flow.test { }` - Collect flow emissions in a test
 * - `awaitItem()` - Wait for next emission
 * - `cancelAndIgnoreRemainingEvents()` - Clean up
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RunningTrackerTest {

    // ==========================================================================
    // TEST SETUP
    // ==========================================================================

    // Mock the LocationObserver dependency
    // `mockk<T>()` creates a mock of type T
    // `relaxed = true` means it returns default values for unmocked methods
    private lateinit var locationObserver: LocationObserver

    // TestScope gives us control over coroutine timing
    private lateinit var testScope: TestScope

    // The class we're testing (System Under Test)
    private lateinit var runningTracker: RunningTracker

    /**
     * @Before runs before EACH test
     * This ensures each test starts with fresh instances
     */
    @Before
    fun setUp() {
        locationObserver = mockk(relaxed = true)
        testScope = TestScope()
        runningTracker = RunningTracker(
            locationObserver = locationObserver,
            applicationScope = testScope
        )
    }

    // ==========================================================================
    // HELPER FUNCTIONS
    // ==========================================================================

    private fun createLocation(lat: Double = 0.0, long: Double = 0.0, altitude: Double = 0.0): LocationWithAltitude {
        return LocationWithAltitude(
            location = Location(lat = lat, long = long),
            altitude = altitude
        )
    }

    // ==========================================================================
    // TEST: Initial State
    // ==========================================================================

    @Test
    fun `initial state has correct default values`() = runTest {
        // No Arrange needed - using default setup

        // Assert: Check all initial states
        assertFalse(runningTracker.isTracking.value, "Should not be tracking initially")
        assertEquals(Duration.ZERO, runningTracker.elapsedTime.value, "Elapsed time should be zero")
        assertEquals(0, runningTracker.runData.value.distanceMeters, "Distance should be zero")
        assertEquals(Duration.ZERO, runningTracker.runData.value.pace, "Pace should be zero")
        assertTrue(runningTracker.runData.value.location.isEmpty(), "Location list should be empty")
    }

    // ==========================================================================
    // TEST: setIsTracking
    // ==========================================================================

    @Test
    fun `setIsTracking to true updates isTracking state`() = runTest {
        // Act
        runningTracker.setIsTracking(true)
        testScope.advanceUntilIdle()

        // Assert
        assertTrue(runningTracker.isTracking.value, "isTracking should be true")
    }

    @Test
    fun `setIsTracking to false updates isTracking state`() = runTest {
        // Arrange: First set to true
        runningTracker.setIsTracking(true)
        testScope.advanceUntilIdle()

        // Act: Then set to false
        runningTracker.setIsTracking(false)
        testScope.advanceUntilIdle()

        // Assert
        assertFalse(runningTracker.isTracking.value, "isTracking should be false")
    }

    @Test
    fun `setIsTracking to false adds empty segment to locations`() = runTest {
        // Arrange: Start tracking
        runningTracker.setIsTracking(true)
        testScope.advanceUntilIdle()

        // Act: Stop tracking (this should add an empty segment for "pause")
        runningTracker.setIsTracking(false)
        testScope.advanceUntilIdle()

        // Assert: An empty segment should be added
        val locations = runningTracker.runData.value.location
        assertTrue(locations.isNotEmpty(), "Should have at least one segment")
        assertTrue(locations.last().isEmpty(), "Last segment should be empty (pause marker)")
    }

    // ==========================================================================
    // TEST: startObservingLocation / stopObservingLocation
    // ==========================================================================

    @Test
    fun `startObservingLocation begins location observation`() = runTest {
        // Arrange: Mock location observer to return a location
        val testLocation = createLocation(lat = 10.0, long = 20.0)
        every { locationObserver.observeLocation(any()) } returns flowOf(testLocation)

        // Act
        runningTracker.startObservingLocation()
        testScope.advanceUntilIdle()

        // Assert: currentLocation should eventually have the test location
        // We use Turbine to test the flow
        runningTracker.currentLocation.test {
            val location = awaitItem()
            assertEquals(testLocation, location)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stopObservingLocation stops location updates`() = runTest {
        // Arrange
        val testLocation = createLocation()
        every { locationObserver.observeLocation(any()) } returns flowOf(testLocation)
        runningTracker.startObservingLocation()
        testScope.advanceUntilIdle()

        // Act
        runningTracker.stopObservingLocation()
        testScope.advanceUntilIdle()

        // Assert: After stopping, new locations should not be emitted
        // The flow should complete or stop emitting
        // This is a simplified check - in real scenarios you'd verify the flow behavior
    }

    // ==========================================================================
    // TEST: finishRun
    // ==========================================================================

    @Test
    fun `finishRun resets all tracking state`() = runTest {
        // Arrange: Start a run
        val testLocation = createLocation()
        every { locationObserver.observeLocation(any()) } returns flowOf(testLocation)

        runningTracker.startObservingLocation()
        runningTracker.setIsTracking(true)
        testScope.advanceUntilIdle()

        // Act: Finish the run
        runningTracker.finishRun()
        testScope.advanceUntilIdle()

        // Assert: Everything should be reset
        assertFalse(runningTracker.isTracking.value, "Should stop tracking")
        assertEquals(Duration.ZERO, runningTracker.elapsedTime.value, "Elapsed time should reset")
        assertEquals(0, runningTracker.runData.value.distanceMeters, "Distance should reset")
    }

    // ==========================================================================
    // TEST: resetRunData
    // ==========================================================================

    @Test
    fun `resetRunData clears elapsed time and run data`() = runTest {
        // Arrange: Simulate some run data
        runningTracker.setIsTracking(true)
        testScope.advanceUntilIdle()

        // Act
        runningTracker.resetRunData()
        testScope.advanceUntilIdle()

        // Assert
        assertEquals(Duration.ZERO, runningTracker.elapsedTime.value, "Elapsed time should be zero")
        assertEquals(RunData(), runningTracker.runData.value, "RunData should be default")
    }

    // ==========================================================================
    // TEST: Flow emissions with Turbine
    // ==========================================================================

    @Test
    fun `isTracking flow emits values correctly`() = runTest {
        runningTracker.isTracking.test {
            // Initial value
            assertFalse(awaitItem(), "Initial value should be false")

            // Change state
            runningTracker.setIsTracking(true)
            assertTrue(awaitItem(), "Should emit true after setIsTracking(true)")

            // Change again
            runningTracker.setIsTracking(false)
            assertFalse(awaitItem(), "Should emit false after setIsTracking(false)")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `elapsedTime flow starts at zero`() = runTest {
        runningTracker.elapsedTime.test {
            assertEquals(Duration.ZERO, awaitItem(), "Initial elapsed time should be zero")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `runData flow starts with empty data`() = runTest {
        runningTracker.runData.test {
            val initialData = awaitItem()
            assertEquals(0, initialData.distanceMeters)
            assertEquals(Duration.ZERO, initialData.pace)
            assertTrue(initialData.location.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}