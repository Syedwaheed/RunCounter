package com.edu.run.domain

import com.edu.core.location.Location
import com.edu.core.location.LocationTimeStamp
import com.edu.core.location.LocationWithAltitude
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class LocationDataCalculatorTest {

    // ==========================================================================
    // HELPER FUNCTIONS
    // ==========================================================================
    // Creating test data can be verbose, so we use helper functions
    // This is a common pattern in testing

    /**
     * Creates a LocationTimeStamp with given coordinates, altitude, and duration
     */
    private fun createLocationTimeStamp(
        lat: Double,
        long: Double,
        altitude: Double = 0.0,
        durationSeconds: Int = 0
    ): LocationTimeStamp {
        return LocationTimeStamp(
            location = LocationWithAltitude(
                location = Location(lat = lat, long = long),
                altitude = altitude
            ),
            durationTimeStamp = durationSeconds.seconds
        )
    }

    // ==========================================================================
    // TEST: getTotalDistanceMeters
    // ==========================================================================

    @Test
    fun `getTotalDistanceMeters with empty list returns zero`() {
        // Arrange: Create empty input
        val locations: List<List<LocationTimeStamp>> = emptyList()

        // Act: Call the function
        val result = LocationDataCalculator.getTotalDistanceMeters(locations)

        // Log
        println("=== getTotalDistanceMeters with empty list ===")
        println("Input: empty list")
        println("Expected: 0")
        println("Actual: $result")
        println()

        // Assert: Verify result
        assertEquals(expected = 0, actual = result)
    }

    @Test
    fun `getTotalDistanceMeters with single point returns zero`() {
        // Arrange: Single point (can't calculate distance with one point)
        val locations = listOf(
            listOf(createLocationTimeStamp(lat = 0.0, long = 0.0))
        )

        // Act
        val result = LocationDataCalculator.getTotalDistanceMeters(locations)

        // Log
        println("=== getTotalDistanceMeters with single point ===")
        println("Input: 1 point at (0.0, 0.0)")
        println("Expected: 0")
        println("Actual: $result")
        println()

        // Assert
        assertEquals(expected = 0, actual = result)
    }

    @Test
    fun `getTotalDistanceMeters with two points calculates distance correctly`() {
        // Arrange: Two points approximately 111km apart (1 degree latitude)
        // At the equator, 1 degree of latitude ≈ 111,000 meters
        val locations = listOf(
            listOf(
                createLocationTimeStamp(lat = 0.0, long = 0.0),
                createLocationTimeStamp(lat = 1.0, long = 0.0)
            )
        )

        // Act
        val result = LocationDataCalculator.getTotalDistanceMeters(locations)

        // Log
        val expectedApprox = 111_000
        val tolerance = 1000
        println("=== getTotalDistanceMeters with two points ===")
        println("Input: Point1(0.0, 0.0) -> Point2(1.0, 0.0)")
        println("Expected: ~$expectedApprox meters (±$tolerance)")
        println("Actual: $result meters")
        println("Difference from expected: ${result - expectedApprox} meters")
        println()

        // Assert: Should be approximately 111,000 meters
        assert(result in (expectedApprox - tolerance)..(expectedApprox + tolerance)) {
            "Expected ~$expectedApprox meters, but got $result"
        }
    }

    @Test
    fun `getTotalDistanceMeters with multiple segments sums all distances`() {
        // Arrange: Two separate segments (simulates pause/resume)
        val segment1 = listOf(
            createLocationTimeStamp(lat = 0.0, long = 0.0),
            createLocationTimeStamp(lat = 0.01, long = 0.0) // ~1.1km
        )
        val segment2 = listOf(
            createLocationTimeStamp(lat = 1.0, long = 0.0),
            createLocationTimeStamp(lat = 1.01, long = 0.0) // ~1.1km
        )
        val locations = listOf(segment1, segment2)

        // Act
        val result = LocationDataCalculator.getTotalDistanceMeters(locations)

        // Log
        println("=== getTotalDistanceMeters with multiple segments ===")
        println("Input: Segment1[(0.0,0.0)->(0.01,0.0)] + Segment2[(1.0,0.0)->(1.01,0.0)]")
        println("Expected: > 2000 meters (each segment ~1.1km)")
        println("Actual: $result meters")
        println()

        // Assert: Should sum both segments
        assert(result > 2000) { "Expected combined distance > 2000m, got $result" }
    }

    // ==========================================================================
    // TEST: getMaxSpeedKmh
    // ==========================================================================

    @Test
    fun `getMaxSpeedKmh with empty list returns zero`() {
        // Arrange
        val locations: List<List<LocationTimeStamp>> = listOf(emptyList())

        // Act
        val result = LocationDataCalculator.getMaxSpeedKmh(locations)

        // Log
        println("=== getMaxSpeedKmh with empty list ===")
        println("Input: empty list")
        println("Expected: 0.0 km/h")
        println("Actual: $result km/h")
        println()

        // Assert
        assertEquals(expected = 0.0, actual = result)
    }

    @Test
    fun `getMaxSpeedKmh calculates speed correctly`() {
        // Arrange: Move 10km in 1 hour = 10 km/h
        // 0.09 degrees ≈ 10km at equator
        val locations = listOf(
            listOf(
                createLocationTimeStamp(lat = 0.0, long = 0.0, durationSeconds = 0),
                createLocationTimeStamp(lat = 0.09, long = 0.0, durationSeconds = 3600) // 1 hour later
            )
        )

        // Act
        val result = LocationDataCalculator.getMaxSpeedKmh(locations)

        // Log
        val expectedSpeed = 10.0
        val tolerance = 1.0
        println("=== getMaxSpeedKmh calculates speed correctly ===")
        println("Input: (0.0,0.0) -> (0.09,0.0) in 3600 seconds (1 hour)")
        println("Distance: ~10km, Time: 1 hour")
        println("Expected: ~$expectedSpeed km/h (±$tolerance)")
        println("Actual: $result km/h")
        println("Difference: ${result - expectedSpeed} km/h")
        println()

        // Assert: Should be approximately 10 km/h
        assert(result in (expectedSpeed - tolerance)..(expectedSpeed + tolerance)) {
            "Expected ~10 km/h, but got $result"
        }
    }

    @Test
    fun `getMaxSpeedKmh with zero time difference returns zero`() {
        // Arrange: Same timestamp (division by zero protection)
        val locations = listOf(
            listOf(
                createLocationTimeStamp(lat = 0.0, long = 0.0, durationSeconds = 100),
                createLocationTimeStamp(lat = 1.0, long = 0.0, durationSeconds = 100) // Same time!
            )
        )

        // Act
        val result = LocationDataCalculator.getMaxSpeedKmh(locations)

        // Log
        println("=== getMaxSpeedKmh with zero time difference ===")
        println("Input: (0.0,0.0)@100s -> (1.0,0.0)@100s (same timestamp!)")
        println("Expected: 0.0 km/h (division by zero protection)")
        println("Actual: $result km/h")
        println()

        // Assert: Should handle gracefully (return 0, not crash)
        assertEquals(expected = 0.0, actual = result)
    }

    @Test
    fun `getMaxSpeedKmh returns maximum speed from multiple segments`() {
        // Arrange: Two segments with different speeds
        val slowSegment = listOf(
            createLocationTimeStamp(lat = 0.0, long = 0.0, durationSeconds = 0),
            createLocationTimeStamp(lat = 0.009, long = 0.0, durationSeconds = 3600) // ~1 km/h
        )
        val fastSegment = listOf(
            createLocationTimeStamp(lat = 0.0, long = 0.0, durationSeconds = 0),
            createLocationTimeStamp(lat = 0.18, long = 0.0, durationSeconds = 3600) // ~20 km/h
        )
        val locations = listOf(slowSegment, fastSegment)

        // Act
        val result = LocationDataCalculator.getMaxSpeedKmh(locations)

        // Log
        println("=== getMaxSpeedKmh returns maximum speed from multiple segments ===")
        println("Input: SlowSegment(~1 km/h) + FastSegment(~20 km/h)")
        println("Expected: > 15 km/h (should pick the faster segment)")
        println("Actual: $result km/h")
        println()

        // Assert: Should return the faster speed
        assert(result > 15.0) { "Expected max speed > 15 km/h, got $result" }
    }

    // ==========================================================================
    // TEST: getTotalElevationMeters
    // ==========================================================================

    @Test
    fun `getTotalElevationMeters with empty list returns zero`() {
        // Arrange
        val locations: List<List<LocationTimeStamp>> = emptyList()

        // Act
        val result = LocationDataCalculator.getTotalElevationMeters(locations)

        // Log
        println("=== getTotalElevationMeters with empty list ===")
        println("Input: empty list")
        println("Expected: 0 meters")
        println("Actual: $result meters")
        println()

        // Assert
        assertEquals(expected = 0, actual = result)
    }

    @Test
    fun `getTotalElevationMeters counts only elevation gains`() {
        // Arrange: Go up 100m, then down 50m, then up 30m
        // Total GAIN should be 130m (100 + 30), ignoring the descent
        val locations = listOf(
            listOf(
                createLocationTimeStamp(lat = 0.0, long = 0.0, altitude = 0.0),
                createLocationTimeStamp(lat = 0.0, long = 0.0, altitude = 100.0),  // +100m
                createLocationTimeStamp(lat = 0.0, long = 0.0, altitude = 50.0),   // -50m (ignored)
                createLocationTimeStamp(lat = 0.0, long = 0.0, altitude = 80.0)    // +30m
            )
        )

        // Act
        val result = LocationDataCalculator.getTotalElevationMeters(locations)

        // Log
        println("=== getTotalElevationMeters counts only elevation gains ===")
        println("Input: 0m -> 100m (+100) -> 50m (-50 ignored) -> 80m (+30)")
        println("Expected: 130 meters (100 + 30, descents ignored)")
        println("Actual: $result meters")
        println()

        // Assert: Only gains counted: 100 + 30 = 130
        assertEquals(expected = 130, actual = result)
    }

    @Test
    fun `getTotalElevationMeters with only descents returns zero`() {
        // Arrange: Only going downhill
        val locations = listOf(
            listOf(
                createLocationTimeStamp(lat = 0.0, long = 0.0, altitude = 100.0),
                createLocationTimeStamp(lat = 0.0, long = 0.0, altitude = 50.0),
                createLocationTimeStamp(lat = 0.0, long = 0.0, altitude = 0.0)
            )
        )

        // Act
        val result = LocationDataCalculator.getTotalElevationMeters(locations)

        // Log
        println("=== getTotalElevationMeters with only descents ===")
        println("Input: 100m -> 50m -> 0m (only going downhill)")
        println("Expected: 0 meters (no elevation gain)")
        println("Actual: $result meters")
        println()

        // Assert: No elevation gain
        assertEquals(expected = 0, actual = result)
    }

    @Test
    fun `getTotalElevationMeters sums gains across multiple segments`() {
        // Arrange: Two segments, each with some elevation gain
        val segment1 = listOf(
            createLocationTimeStamp(lat = 0.0, long = 0.0, altitude = 0.0),
            createLocationTimeStamp(lat = 0.0, long = 0.0, altitude = 50.0)  // +50m
        )
        val segment2 = listOf(
            createLocationTimeStamp(lat = 0.0, long = 0.0, altitude = 100.0),
            createLocationTimeStamp(lat = 0.0, long = 0.0, altitude = 150.0) // +50m
        )
        val locations = listOf(segment1, segment2)

        // Act
        val result = LocationDataCalculator.getTotalElevationMeters(locations)

        // Log
        println("=== getTotalElevationMeters sums gains across multiple segments ===")
        println("Input: Segment1(0m->50m, +50) + Segment2(100m->150m, +50)")
        println("Expected: 100 meters (50 + 50)")
        println("Actual: $result meters")
        println()

        // Assert: Total gain = 50 + 50 = 100
        assertEquals(expected = 100, actual = result)
    }
}