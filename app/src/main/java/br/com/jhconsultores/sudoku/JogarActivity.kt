package br.com.jhconsultores.sudoku

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class JogarActivity  : AppCompatActivity()  {

    //----------------------------------------------------------------------------------------------
    //                         Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private val cTAG     = "Sudoku"
    private var strLog   = ""

    //----------------------------------------------------------------------------------------------
    // Eventos da MainActivity
    //----------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jogar)

        //--- Recupera os dados recebidos via intent
        var arIntNumsGab = ArrayList <Int> ()
        arIntNumsGab     = intent.getIntegerArrayListExtra("GabaritoDoJogo") as ArrayList<Int>
        //--- Armazena o gabarito em um array<array<int>>






    }

}