package co.opensi.kkiapay_pos.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.nio.charset.StandardCharsets
import org.json.JSONObject
import org.json.CDL
import java.io.*
import java.lang.Exception

/***************************************************** Function Collection V 0.9.1 ******************************************************/
/***************************************************** ** updated at 11/10/2021 ** ****************************************************/

data class Country(
    var name : String,
    var code: String
)

const val DEFAULT_VALUE = "value"

/********************** @TextFormatter **********************/

/**
 * .[date] , @TextFormatter
 * function to format date for view
 * return "dd/mm/YYYY"
 */
@Override
fun String.date(): String {
    val lString: List<String> = this.split("-","T")
    return lString[0] + "/" + lString[1] + "/" + lString[2]
}

/**
 * .[hour], @TextFormatter
 * function to format date for view
 * and move hour to GMT+1
 * return "hh:mm"
 */
@Override
fun String.hour(): String {
    val lString: List<String> = this.split("T", ":")
    val hour = Integer.parseInt(lString[1]) + 1 // lock GMT
    return hour.toString() + ":" + lString[2]
}

/**
 * .[mountFcfa], @TextFormatter
 * return "Mount Fcfa"
 */
@Override
fun String.mountFcfa(): String {
    return this + "Fcfa"
}

/**
 * .[formattedNum], @TextFormatter
 * receive::: 229XXXXXXXX
 * return "XXXXXXXX"
 */
@Override
fun String.formattedNum(): String {
    return this.substring(3)
}

/**
 * .[formattedNum], @TextFormatter
 * receive::: 229XXXXXXXX
 * return "XXXXXXXX"
 */
fun List<Any>.getCountry(): String{
    val context: Context = this[1] as Context
    var countryName = ""

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
        if (i.code == this[0].toString())
        {
            countryName = i.name
        }
    }

    return  countryName
}


/********************** @Asserter **********************/


/**
 * :@String
 * [assertNotNull]
 * return "FullName"
 */
@Override
fun String.assertNotNull(): String {
    return if (this==""||this==" "||this.isEmpty()) DEFAULT_VALUE else this
}


/**
 * :@String
 * [assertNotNull]
 * return "FullName"
 */
@Override
fun Any.assertNotNull(): Any {
    return if (this==""||this==" "||this==null) DEFAULT_VALUE else this
}


/**
 * [AssertInteger]
 * return "FullName"
 */
@Override
fun String.assertInteger(): Boolean {
    return try {
        val long : Long = this.toLong()
        true
    }catch(e: Exception){
        false
    }

}

/**
 * [workLogger]
 * @[assertexception]
 * :) :) :)
 */
fun workLogger( work: () -> Unit , result: (state:Int,message:String) -> Unit ) = try {
    work()
} catch(e: Exception){
    Log.e("WORKLOGGER:: ","Cause:: ${e.message} ")
    result(2,"Cause:: ${e.message} ")
}finally{
    Log.e("WORKLOGGER:: ","Well done")
    result(1,"Well done")
}


/**********************  @Saver **********************/


/**
 * let save Facture.pdf on phone directory
 */
fun saveFile(nameFile: String,input:InputStream): File {

    val folder = File("/storage/emulated/0/Kkiapay/", "documents")
    if (!folder.exists()) folder.mkdirs()
    val mFile = File(folder, nameFile)

    input.use { i ->
        FileOutputStream(mFile).use { output ->
            val buffer = ByteArray(4 * 1024) // or other buffer size
            var read: Int
            while (i.read(buffer).also { read = it } != -1) {
                output.write(buffer, 0, read)
            }
            output.flush()
            Log.e("PDF","Data has been Successfully Written to $mFile")
            return mFile
        }
    }

}

fun getCountry(countryCode : String, context: Context): String{
    var countryName = ""

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
        if (i.code == countryCode)
        {
            countryName = i.name
        }
    }

    return  countryName
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
        Log.e("CSV","Data has been Successfully Written to $file")
        Log.e("CSV",csv)

        return true
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("CSV","catch",e)
        return false
    }

}

/**********************  @Manager **********************/

/**
 * @connectivityManager
 * observe connexion state
 *
 * @example
 * this.[connectivityManager] (
 * onAvailable = { _ ->
 *  runOnUiThread {
 *      viewModel.setNet(true)
 *      networkV.visibility = View.GONE
 *  }
 * },
 * onLost = { _ ->
 *  runOnUiThread {
 *       viewModel.setNet(false)
 *       networkV.visibility = View.VISIBLE
 *  }
 * })
 */
fun Context.connectivityManager (onAvailable:(network: Network)->Unit , onLost:(network: Network)->Unit) = try{
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivityManager.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            it.registerDefaultNetworkCallback(@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    //take action when network connection is gained
                    onAvailable(network)
                }

                override fun onLost(network: Network) {
                    //take action when network connection is lost
                    onLost(network)
                }
            })
        }
    }
}catch(e: Exception){}



