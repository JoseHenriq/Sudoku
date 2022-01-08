package br.com.jhconsultores.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import br.com.jhconsultores.sudoku.ui.MainActivity.Companion.cTAG
import java.nio.IntBuffer

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