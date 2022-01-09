package br.com.jhconsultores.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import br.com.jhconsultores.sudoku.ui.MainActivity
import br.com.jhconsultores.sudoku.ui.MainActivity.Companion.cTAG
import java.io.BufferedOutputStream
import java.io.File
import java.nio.IntBuffer
import kotlin.concurrent.thread

class UtilsKt {

    /*
    //--- requestAllFilesAccessPermission para Android >= A11
    fun requestAllFilesAccessPermission(context: Context) {

        //--- Android < A11 (R -> A11 API30)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
                                                         Environment.isExternalStorageManager()) {

            val strToast = "We can access all files on external storage now"
            mToast (context, strToast)
            Log.d(cTAG, "-> $strToast")

        }

        //--- Android >= A11
        else {

            val builder = AlertDialog.Builder(context)

                .setTitle("Tip")
                .setMessage("We need permission to access all files on external storage")
                .setPositiveButton("OK") { _, _ ->

                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivityForResult(intent, ALL_FILES_ACCESS_PERMISSION)

                }
                .setNegativeButton("Cancel", null)

            builder.show()
        }

    }
    */

    /*
    // Scoped storage demo
    fun writeFile(context : Context, fileName: String) {

        //--- SO < A10
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

            //strToast = "You must use device running Android 10 or higher"
            //Toast.makeText(this, strToast, Toast.LENGTH_SHORT).show()

            //return

        } else {

            thread {

                try {
                    /*
                    //----------------------------------------------------------------------------------
                    // Busca e recepção do arquivo
                    //----------------------------------------------------------------------------------
                    //--- Conexão http
                    val url = URL(fileUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 8000
                    connection.readTimeout = 8000

                    //--- Recepção do arquivo em stream
                    val inputStream = connection.inputStream
                    val bis = BufferedInputStream(inputStream)
                     */

                    //----------------------------------------------------------------------------------
                    // Prepara o arquivo
                    //----------------------------------------------------------------------------------
                    val file  = File(fileName)
                    val bytes = file.readBytes()

                    //----------------------------------------------------------------------------------
                    // Salvamento do arquivo
                    //----------------------------------------------------------------------------------
                    //--- Prepara para salvar o arquivo recebido
                    val values = ContentValues()
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH,
                                                                    Environment.DIRECTORY_DOWNLOADS)
                    var contentResolver = context.getContentResolver()
                    val uri =
                        contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

                    //--- Salva o arquivo stream
                    if (uri != null) {

                        val outputStream = contentResolver.openOutputStream(uri)
                        if (outputStream != null) {

                            val bos = BufferedOutputStream(outputStream)

                            val buffer = ByteArray(1024)               //file.length().toInt())
                            while (bytes >= 0) {

                                bos.write(buffer, 0, bytes)
                                bos.flush()
                                bytes = file.readBytes()                         //bis.read(buffer)

                            }
                            bos.close()

                            runOnUiThread {
                                strToast = "$fileName is in Download directory now."
                                Toast.makeText(this, strToast, Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                    bis.close()

                } catch (e: Exception) {
                     e.printStackTrace()
                }
            }
        }
    }
    */

    //--- mToast
    fun mToast(context : Context, msgErro : String) {

        Toast.makeText(context, msgErro, Toast.LENGTH_LONG).show()

    }

    //--- copiaBmpByBuffer
    fun copiaBmpByBuffer(bmpSrc: Bitmap?, bmpDest: Bitmap?) {

        val buffBase = IntBuffer.allocate(bmpSrc!!.width * bmpSrc.height)
        //--------------------------------------
        bmpSrc.copyPixelsToBuffer(buffBase)
        //--------------------------------------
        buffBase.rewind()
        //----------------------------------------
        bmpDest!!.copyPixelsFromBuffer(buffBase)
        //----------------------------------------

    }

    //--- copiaArArInt
    fun copiaArArInt(arArIntPreset: Array<Array<Int>>) : Array<Array<Int>> {

        /* https://stackoverflow.com/questions/45199704/kotlin-2d-array-initialization
            // A 6x5 array of Int, all set to 0.
            var m = Array(6) {Array(5) {0} }
         */

        //------------------------------------------------------
        val arArIntTmp = Array(9) { Array(9) { 0 } }
        //------------------------------------------------------

        for (intLin in 0..8) {
            for (intCol in 0..8) { arArIntTmp[intLin][intCol] = arArIntPreset[intLin][intCol] }
        }
        return arArIntTmp
    }

    //--- quantZeros
    fun quantZeros(arArIntJogo : Array <Array <Int>>) : Int{

        var intQtiZeros = 0
        for (idxLin in 0..8) {
            for (idxCol in 0..8) {
                if (arArIntJogo[idxLin][idxCol] == 0) intQtiZeros++
            }
        }
        //Log.d(cTAG, "-> Quantidade de Zeros: $intQtiZeros")

        return intQtiZeros

    }

}