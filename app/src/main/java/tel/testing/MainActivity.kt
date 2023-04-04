package tel.testing

import android.content.Context
import android.content.res.AssetManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import tel.testing.databinding.ActivityMainBinding
import java.io.*
import java.net.InetAddress
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var myNet: Thread? = null
    private var dataInputStream: DataInputStream? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.output!!.append("\n")
        binding.hostname!!.hint = "192.168.232.2"

        binding.zoomScale.setText("16")
        binding.btnConnect.visibility = View.GONE

        binding.btnZoomIn.setOnClickListener {
            val zoomScale = binding.zoomScale.text.toString()
            if(zoomScale != "" && zoomScale.toInt() > 0 ){
                val request = getZoomJson("ZOOM_IN", zoomScale.toInt())
                sendRemoteRequest(request)
            } else {
                Toast.makeText(this, "Please enter a valid zoom scale value", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnZoomOut.setOnClickListener {
            val zoomScale = binding.zoomScale.text.toString()
            if(zoomScale != "" && zoomScale.toInt() > 0){
                val request = getZoomJson("ZOOM_OUT", zoomScale.toInt())
                sendRemoteRequest(request)
            } else {
                Toast.makeText(this, "Please enter a valid zoom scale value", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnPanUp.setOnClickListener {
            val request = getPanJson("PAN_UP")
            sendRemoteRequest(request)
        }

        binding.btnPanDown.setOnClickListener {
            val request = getPanJson("PAN_DOWN")
            sendRemoteRequest(request)
        }

        binding.btnPanRight.setOnClickListener {
            val request = getPanJson("PAN_RIGHT")
            sendRemoteRequest(request)
        }

        binding.btnPanLeft.setOnClickListener {
            val request = getPanJson("PAN_LEFT")
            sendRemoteRequest(request)
        }

        binding.btnMoveToLocation.setOnClickListener {
            val location = binding.txtEnterLocation.text.toString()
            if(location.trim().isNotEmpty()){
                val request = getMoveToLocationJson("MOVE_TO_LOCATION", location)
                sendRemoteRequest(request)
            } else {
                Toast.makeText(this, "Please enter a valid location", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSetDestination.setOnClickListener {
            val location = binding.txtEnterLocation.text.toString()
            if(location.trim().isNotEmpty()){
                val request = getMoveToLocationJson("SET_DESTINATION", location)
                sendRemoteRequest(request)
            } else {
                Toast.makeText(this, "Please enter a valid location", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnCurrentLocation.setOnClickListener {
            val request = getPanJson("CURRENT_LOCATION")
            sendRemoteRequest(request)
        }

        binding.btnDrawPolygon.setOnClickListener {
            //val request = getPolygonJson("DRAW_POLYGON")
            val request = getPolygonFromJson()
            for(i in 1.. 3){
                val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS'Z'", Locale.getDefault()).format(
                    Date())
                Log.i("polygon_data_send", currentDate  )
                val stuff: doNetwork = doNetwork(request)
                myNet = Thread(stuff)
                myNet!!.start()
            }

        }
    }


    internal object Config {
        val POLYGON_COORDINATES: ArrayList<PayloadModel?> = object : ArrayList<PayloadModel?>() {
            init {
                add(PayloadModel(75.82782415994964, 11.256118130260049))
                add(PayloadModel(75.82938126312945, 11.255705334670054))
                add(PayloadModel(75.82957753424168, 11.254258197104463))
                add(PayloadModel(75.82806528945574, 11.253656909157769))
                add(PayloadModel(75.82691675909678, 11.254651029978945))
                add(PayloadModel(75.82782415994964, 11.256118130260049))
            }
        }

        val INNER_POLYGON_COORDINATES: ArrayList<PayloadModel?> = object : ArrayList<PayloadModel?>() {
            init {
                add(PayloadModel(75.82792222822493, 11.255833342267238))
                add(PayloadModel(75.82896029712097, 11.255468696404535))
                add(PayloadModel(75.8292014264502, 11.25447046772004))
                add(PayloadModel(75.82810206505582, 11.254057669767704))
                add(PayloadModel(75.8272928670398, 11.254755256738818))
                add(PayloadModel(75.82792222822493, 11.255833342267238))
            }
        }

        fun getTime(): String{
            val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(
                Date()
            )
            return currentDate
        }

    }

    private fun getZoomJson(type: String, zoomValue: Int): String{
        var request = ""
        try {
            val uniqueID = UUID.randomUUID().toString()
            val headerObject = JSONObject()
            headerObject.put("deviceID", "10")
            headerObject.put("tid", uniqueID)
            headerObject.put("feature_id", type)
            headerObject.put("version", "1.0")
            headerObject.put("timestamp", "")
            val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(
                Date()
            )
            val payloadObject = JSONObject()
            payloadObject.put("request_time", currentDate)
            payloadObject.put("feature_data", zoomValue)
            val jsonResponse = JSONObject()
            jsonResponse.put("header", headerObject)
            jsonResponse.put("payload", payloadObject)
            request = jsonResponse.toString().trim { it <= ' ' }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return request
    }

    private fun getPanJson(type: String): String{
        var request = ""
        try {
            val uniqueID = UUID.randomUUID().toString()
            val headerObject = JSONObject()
            headerObject.put("deviceID", "10")
            headerObject.put("tid", uniqueID)
            headerObject.put("feature_id", type)
            headerObject.put("version", "1.0")
            headerObject.put("timestamp", "")
            val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(
                Date()
            )
            val payloadObject = JSONObject()
            payloadObject.put("request_time", currentDate)
            payloadObject.put("feature_data", "")
            val jsonResponse = JSONObject()
            jsonResponse.put("header", headerObject)
            jsonResponse.put("payload", payloadObject)
            request = jsonResponse.toString().trim { it <= ' ' }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return request
    }

    private fun getMoveToLocationJson(type: String, location: String): String{
        var request = ""
        try {
            val uniqueID = UUID.randomUUID().toString()
            val headerObject = JSONObject()
            headerObject.put("deviceID", "10")
            headerObject.put("tid", uniqueID)
            headerObject.put("feature_id", type)
            headerObject.put("version", "1.0")
            headerObject.put("timestamp", "")
            val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(
                Date()
            )
            val payloadObject = JSONObject()
            payloadObject.put("request_time", currentDate)
            payloadObject.put("feature_data", location)
            val jsonResponse = JSONObject()
            jsonResponse.put("header", headerObject)
            jsonResponse.put("payload", payloadObject)
            request = jsonResponse.toString().trim { it <= ' ' }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return request
    }

    private fun getPolygonJson(type: String): String{
        var request = ""

        val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS'Z'", Locale.getDefault()).format(
            Date()
        )
        Log.i("polygon_data_send", currentDate  )

        //val jsArray =  JSONArray(Gson().toJson(Config.POLYGON_COORDINATES))
        val gson = Gson()
        val listString = gson.toJson(
            Config.POLYGON_COORDINATES,
            object : TypeToken<ArrayList<PayloadModel?>?>() {}.type
        )
        val jsonArray = JSONArray(listString)

        val listString1 = gson.toJson(
            Config.INNER_POLYGON_COORDINATES,
            object : TypeToken<ArrayList<PayloadModel?>?>() {}.type
        )
        val jsonArray1 = JSONArray(listString1)

        try {
            val uniqueID = UUID.randomUUID().toString()
            val headerObject = JSONObject()
            headerObject.put("deviceID", "10")
            headerObject.put("tid", uniqueID)
            headerObject.put("feature_id", type)
            headerObject.put("version", "1.0")
            headerObject.put("timestamp", "")
            val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(
                Date()
            )
            val payloadObject = JSONObject()
            payloadObject.put("request_time", currentDate)
            payloadObject.put("feature_data", "")

            payloadObject.put("feature_data1", jsonArray)
            payloadObject.put("feature_data2", jsonArray1)
            val jsonResponse = JSONObject()
            jsonResponse.put("header", headerObject)
            jsonResponse.put("payload", payloadObject)
            request = jsonResponse.toString().trim { it <= ' ' }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return request
    }

    fun getPolygonFromJson(): String{
        var res = loadJSONFromAssets(applicationContext, "file.json")
        return res.toString()
    }


    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            binding.output!!.append(msg.data.getString("msg"))
        }
    }

    fun mkmsg(str: String?) {
        //handler junk, because thread can't update screen!
        val msg = Message()
        val b = Bundle()
        b.putString("msg", str)
        msg.data = b
        handler.sendMessage(msg)
    }

    private fun sendRemoteRequest(request: String) {
        if (binding.hostname!!.text.isEmpty()) {
            Toast.makeText(this, "Please enter server ip", Toast.LENGTH_SHORT).show()
        } else if (binding.port!!.text.isEmpty()) {
            Toast.makeText(this, "Please port number", Toast.LENGTH_SHORT).show()
        } else {
            val stuff: doNetwork = doNetwork(request)
            myNet = Thread(stuff)
            myNet!!.start()
        }
    }

    internal inner class doNetwork(val request: String) : Runnable {
        var out: PrintWriter? = null
        var ins: BufferedReader? = null
        override fun run() {
            val p = binding.port!!.text.toString().toInt()
            val h = binding.hostname!!.text.toString()
            mkmsg("host is $h\n")
            mkmsg(" Port is $p\n")
            try {
                val serverAddr = InetAddress.getByName(h)
                mkmsg("Attempt Connecting...$h\n")
                val socket = Socket(serverAddr, p)
                val message = "Hello from Client android emulator"

                //made connection, setup the read (in) and write (out)
                out =
                    PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
                ins = BufferedReader(InputStreamReader(socket.getInputStream()))
                dataInputStream = DataInputStream(socket!!.getInputStream())

                //now send a message to the server and then read back the response.
                try {
                    //write a message to the server
                    mkmsg("Attempting to send message ...\n")
                    out!!.println(request)
                    mkmsg("Message sent...\n")

                    //read back a message from the server.
                    mkmsg("Attempting to receive a message ...\n")
                    val str = ins!!.readLine()
                    mkmsg("received a message:\n$str\n")
                    mkmsg("We are done, closing connection\n")
                } catch (e: Exception) {
                    mkmsg("Error happened sending/receiving\n")
                } finally {
                    ins!!.close()
                    out!!.close()
                    socket.close()
                }
            } catch (e: Exception) {
                mkmsg("Unable to connect...\n")
            }
        }
    }

    fun loadJSONFromAssets(context: Context, jsonFileName: String?): JsonObject? {
        val manager: AssetManager = context.getAssets()
        val `is`: InputStream = manager.open(jsonFileName!!)
        val size: Int = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()
        val json = String(buffer)
        return Gson().fromJson(json, JsonObject::class.java)
    }
}