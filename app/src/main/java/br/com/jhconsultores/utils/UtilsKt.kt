package br.com.jhconsultores.utils

class UtilsKt {

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