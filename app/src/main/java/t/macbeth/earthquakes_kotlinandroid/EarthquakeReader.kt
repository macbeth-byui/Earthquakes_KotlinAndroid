package t.macbeth.earthquakes_kotlinandroid

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParseException
import java.io.IOException
import java.net.URL

// Classes representing the data from the USGS server
data class EarthquakeProperties(val mag: Float, val place: String)
data class EarthquakeGeometry(val coordinates: List<Double>)
data class EarthquakeFeature(val properties: EarthquakeProperties, val geometry: EarthquakeGeometry)
data class Earthquakes(val features: List<EarthquakeFeature>)

class EarthquakeReader(private val activity: MainActivity): Runnable {

    // The activity property is provided so that functions can be called to update the UI

    override fun run() {
        try {
            // Read earthquake JSON data from the server
            val jsonText = URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson").readText()

            // Convert JSON data to Earthquakes object
            val gson = Gson()
            val earthquakes = gson.fromJson(jsonText, Earthquakes::class.java)

            // Add markers for each of the earthquakes on the map.  This does not need runOnUiThread
            // because the ArcGIS library is already doing this for us.
            activity.displayEarthquakes(earthquakes)

        } catch (e: IOException) {
            handleError(e)
        } catch (e: JsonParseException) {
            handleError(e)
        }
    }

    private fun handleError(e: Exception) {
        // Log the error to LogCat and display message to user
        // The runOnUiThread function is needed to ensure toast is done on the UI thread.
        Log.e("EarthquakeReader", e.toString())
        activity.runOnUiThread(Runnable { activity.displayToast("Error Reading USGS Server") })
    }


}