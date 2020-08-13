package t.macbeth.earthquakes_kotlinandroid

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create a map centered at 0,0 (long, lat) and zoomed out (level 1)
        val map = ArcGISMap(Basemap.Type.STREETS_VECTOR, 0.0, 0.0, 1)

        // 'mapView' is the name of the component in the activity_main layout
        mapView.map = map

        // Create an overlay for the earthquake markers (overlay id = 0)
        mapView.graphicsOverlays.add(0, GraphicsOverlay())

        // Read earthquake data from the USGS server within a thread to avoid blocking
        // the UI Thread
        val thread = Thread(EarthquakeReader(this))
        thread.start()
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.dispose()
    }

    fun displayEarthquakes(earthquakes: Earthquakes) {
        // Loop through each earthquake and create a marker
        for (e in earthquakes.features) {
            // Create Point Object for earthquake marker
            val point = Point(e.geometry.coordinates[0], e.geometry.coordinates[1], SpatialReferences.getWgs84())

            // Determine color based on the magnitude of the earthquake
            val color = when {
                e.properties.mag < 3.0 -> Color.BLUE
                e.properties.mag < 5.0 -> Color.YELLOW
                else                   -> Color.RED
            }

            // Create the marker
            val marker = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, color, 10.0f)

            // Create a graphic using the point and marker object and add it to the overlay ID 0
            mapView.graphicsOverlays[0].graphics.add(Graphic(point, marker))
        }
    }

    fun displayToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}