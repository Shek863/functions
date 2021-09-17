
import android.content.Context
import android.util.Log
import co.opensi.kkiapay_pos.models.Country
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

import org.json.JSONObject
import java.io.File

import org.apache.commons.io.FileUtils

import org.json.CDL
import java.io.FileWriter
import java.lang.Exception


class Functions {

    fun getCountry(countryCode : String, context: Context): String{
        var countryName : String = ""

        var json: String? = null
        try {
            val mJson: InputStream = context.assets.open("data/country.json")
            val size = mJson.available()
            val buffer = ByteArray(size)
            mJson.read(buffer)
            mJson.close()
            json = String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        val type = object : TypeToken<List<Country>>() {}.type
        val data: List<Country> =  Gson().fromJson(json, type)

        for (i in data){
            if (i.code.equals(countryCode))
            {
                countryName = i.name
            }
        }

        return  countryName
    }

    fun getDate(value: String, context: Context): String {
        val lString: List<String> = value.split("-","T")
        return lString[0] + "/" + lString[1] + "/" + lString[2]
    }

    fun getHour(value: String, context: Context): String {
        val lString: List<String> = value.split("T", ":")
        return lString[1] + ":" + lString[2]
    }

    fun saveCsv(jsonArrayString: String,size: String): Boolean{

        val folder = File("/storage/emulated/0/Kkiapay/", "documents")
        if (!folder.exists()) folder.mkdirs()
        val file = File(folder, "Transactions$size.csv")

        val output: JSONObject
        try {
            output = JSONObject(jsonArrayString)
            val docs = output.getJSONArray("data")
            val csv = CDL.toString(docs)
            val writer = FileWriter(file)
            writer.append(csv)
            writer.flush()
            writer.close()

            //FileUtils.writeStringToFile(file, csv)
             Log.e("CSV","Data has been Sucessfully Writeen to $file")
             Log.e("CSV",csv)

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CSV","catch",e)
            return false
        }

    }
    
    
    
    
    /**
    * a metre sous forme de fonction plutart
    **/
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.registerDefaultNetworkCallback(@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        //take action when network connection is gained
                        runOnUiThread{
                            networkV.visibility = View.GONE
                        }
                    }

                    override fun onLost(network: Network) {
                        //take action when network connection is lost
                        runOnUiThread{
                            networkV.visibility = View.VISIBLE
                        }
                    }
                })
            }
        }
    

}
