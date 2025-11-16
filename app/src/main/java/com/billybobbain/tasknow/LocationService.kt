// ===== LocationService.kt =====
package com.billybobbain.tasknow

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.*

class LocationService(private val context: Context) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get current GPS location
     * Returns null if permissions not granted or location unavailable
     */
    suspend fun getCurrentLocation(): android.location.Location? {
        if (!hasLocationPermission()) return null

        return suspendCancellableCoroutine { continuation ->
            try {
                // Try GPS first
                val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (gpsLocation != null) {
                    continuation.resume(gpsLocation)
                    return@suspendCancellableCoroutine
                }

                // Fallback to network provider
                val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (networkLocation != null) {
                    continuation.resume(networkLocation)
                    return@suspendCancellableCoroutine
                }

                // No location available
                continuation.resume(null)
            } catch (_: SecurityException) {
                continuation.resume(null)
            }
        }
    }

    /**
     * Calculate distance between two points in meters using Haversine formula
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val earthRadius = 6371000.0 // Earth radius in meters

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * asin(sqrt(a))

        return (earthRadius * c).toFloat()
    }

    /**
     * Check if current location is within a location's radius
     */
    fun isWithinRadius(
        currentLat: Double,
        currentLon: Double,
        locationLat: Double,
        locationLon: Double,
        radiusMeters: Float
    ): Boolean {
        val distance = calculateDistance(currentLat, currentLon, locationLat, locationLon)
        return distance <= radiusMeters
    }

    /**
     * Get tasks within proximity of current location
     */
    suspend fun filterTasksByCurrentLocation(
        tasks: List<Task>,
        locations: List<Location>
    ): List<Task> {
        val currentLocation = getCurrentLocation() ?: return emptyList()

        val locationMap = locations.associateBy { it.id }

        return tasks.filter { task ->
            task.locationId?.let { locationId ->
                locationMap[locationId]?.let { location ->
                    isWithinRadius(
                        currentLocation.latitude,
                        currentLocation.longitude,
                        location.latitude,
                        location.longitude,
                        location.radiusMeters
                    )
                } ?: false
            } ?: false
        }
    }

    /**
     * Get tasks for a specific location
     */
    fun filterTasksByLocation(
        tasks: List<Task>,
        locationId: String
    ): List<Task> {
        return tasks.filter { it.locationId == locationId }
    }
}
