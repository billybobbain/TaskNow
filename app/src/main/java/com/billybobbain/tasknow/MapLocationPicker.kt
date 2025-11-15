// ===== MapLocationPicker.kt =====
package com.billybobbain.tasknow

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.Toast
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapLocationPickerDialog(
    existingLocation: Location?,
    viewModel: TaskViewModel,
    onSave: (Location) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(existingLocation?.name ?: "") }
    var selectedPoint by remember {
        mutableStateOf<GeoPoint?>(
            if (existingLocation != null) {
                GeoPoint(existingLocation.latitude, existingLocation.longitude)
            } else null
        )
    }
    var radius by remember { mutableStateOf(existingLocation?.radiusMeters ?: 500f) }
    var isLoadingCurrentLocation by remember { mutableStateOf(false) }
    var mapView by remember { mutableStateOf<MapView?>(null) }

    // Initialize OSMDroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxSize(0.95f)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Text(
                    text = if (existingLocation != null) "Edit Location" else "Add Location",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Location Name") },
                    placeholder = { Text("e.g., Home, Office, Gym") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Map
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AndroidView(
                            factory = { ctx ->
                                MapView(ctx).apply {
                                    mapView = this
                                    setTileSource(TileSourceFactory.MAPNIK)
                                    setMultiTouchControls(true)
                                    controller.setZoom(15.0)

                                    // Set initial position
                                    val initialPoint = selectedPoint ?: GeoPoint(40.7128, -74.0060) // Default to NYC
                                    controller.setCenter(initialPoint)

                                    // Add map events overlay to handle taps
                                    val mapEventsReceiver = object : MapEventsReceiver {
                                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                            return false
                                        }

                                        override fun longPressHelper(p: GeoPoint?): Boolean {
                                            p?.let {
                                                selectedPoint = it
                                            }
                                            return true
                                        }
                                    }
                                    val eventsOverlay = MapEventsOverlay(mapEventsReceiver)
                                    overlays.add(eventsOverlay)

                                    // Add marker if location exists
                                    if (selectedPoint != null) {
                                        val marker = Marker(this)
                                        marker.position = selectedPoint
                                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        marker.title = "Selected Location"
                                        overlays.add(marker)
                                    }
                                }
                            },
                            update = { map ->
                                // Update marker when selectedPoint changes
                                map.overlays.clear()

                                // Re-add map events overlay
                                val mapEventsReceiver = object : MapEventsReceiver {
                                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                        return false
                                    }

                                    override fun longPressHelper(p: GeoPoint?): Boolean {
                                        p?.let {
                                            selectedPoint = it
                                            map.controller.animateTo(it)
                                        }
                                        return true
                                    }
                                }
                                val eventsOverlay = MapEventsOverlay(mapEventsReceiver)
                                map.overlays.add(eventsOverlay)

                                selectedPoint?.let { point ->
                                    // Add radius circle first (so it's behind the marker)
                                    val circle = org.osmdroid.views.overlay.Polygon(map)
                                    circle.points = Polygon.pointsAsCircle(point, radius.toDouble())
                                    circle.fillPaint.color = 0x220000FF
                                    circle.outlinePaint.color = 0xFF0000FF.toInt()
                                    circle.outlinePaint.strokeWidth = 2f
                                    map.overlays.add(circle)

                                    // Add marker
                                    val marker = Marker(map)
                                    marker.position = point
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    marker.title = "Selected Location"
                                    map.overlays.add(marker)
                                }
                                map.invalidate()
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Tap instruction overlay
                        if (selectedPoint == null) {
                            Card(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Text(
                                    text = "Long-press on the map to select a location",
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Current Location button
                Button(
                    onClick = {
                        val locationService = LocationService(context)
                        if (!locationService.hasLocationPermission()) {
                            Toast.makeText(
                                context,
                                "Location permission not granted. Please grant permission in app settings.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        isLoadingCurrentLocation = true
                        scope.launch {
                            val currentLoc = locationService.getCurrentLocation()
                            if (currentLoc != null) {
                                val newPoint = GeoPoint(currentLoc.latitude, currentLoc.longitude)
                                selectedPoint = newPoint
                                // Center map on the new location
                                mapView?.controller?.animateTo(newPoint)
                                mapView?.controller?.setZoom(16.0)
                                Toast.makeText(
                                    context,
                                    "Location set to current position",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Unable to get current location. Make sure GPS is enabled.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            isLoadingCurrentLocation = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoadingCurrentLocation
                ) {
                    if (isLoadingCurrentLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("📍 Use Current Location")
                }

                // Radius slider
                Column {
                    Text(
                        text = "Proximity Radius: ${radius.toInt()} meters",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = radius,
                        onValueChange = { radius = it },
                        valueRange = 50f..2000f,
                        steps = 38, // 50-step increments
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Selected coordinates display
                if (selectedPoint != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Lat: %.6f, Lon: %.6f".format(
                                    selectedPoint!!.latitude,
                                    selectedPoint!!.longitude
                                ),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank() && selectedPoint != null) {
                                onSave(
                                    Location(
                                        id = existingLocation?.id ?: java.util.UUID.randomUUID().toString(),
                                        name = name,
                                        latitude = selectedPoint!!.latitude,
                                        longitude = selectedPoint!!.longitude,
                                        radiusMeters = radius
                                    )
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank() && selectedPoint != null
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

// Helper object for drawing circles
object Polygon {
    fun pointsAsCircle(center: GeoPoint, radiusInMeters: Double): List<GeoPoint> {
        val points = mutableListOf<GeoPoint>()
        val earthRadius = 6371000.0 // meters

        for (i in 0..360 step 10) {
            val angle = Math.toRadians(i.toDouble())
            val dx = radiusInMeters * Math.cos(angle)
            val dy = radiusInMeters * Math.sin(angle)

            val deltaLat = dy / earthRadius
            val deltaLon = dx / (earthRadius * Math.cos(Math.toRadians(center.latitude)))

            val lat = center.latitude + Math.toDegrees(deltaLat)
            val lon = center.longitude + Math.toDegrees(deltaLon)

            points.add(GeoPoint(lat, lon))
        }

        return points
    }
}
