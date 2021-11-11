package br.com.jhconsultores.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    //----------------------------------------------------------------------------------------------
    // Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    val cTAG   = "Sudoku"
    var strLog = ""

    private var numDisponivel = arrayOf<Array<Int>>()

    //----------------------------------------------------------------------------------------------
    // Eventos
    //----------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //-----------
        geraJogo()
        //-----------


    }

    //----------------------------------------------------------------------------------------------
    // Funções
    //----------------------------------------------------------------------------------------------
    private fun geraJogo() {

        //--- Instancializações e inicializações

        //---------------------------
        inicializaNumDisponiveis()
        //---------------------------
        listaNumDisponiveis()
        //----------------------

        // 1- Gera os quadrados menores independentes (diagonais: Q0, Q4, Q8) ou (Q2, Q4, Q6):
        






    }

    //--- inicializaNumDisponiveis
    fun inicializaNumDisponiveis() {
        for (i in 0..8) {
            var array = arrayOf<Int>()
            for (j in 1..9) { array += j }
            numDisponivel += array
        }
    }

    //--- listaNumDisponiveis
    fun listaNumDisponiveis() {
        for (linha in 0..8) {
            strLog = "linha $linha : "
            for (coluna in 0..8) {
                strLog += "${numDisponivel[linha][coluna]}" + "${if (coluna < 8) ", " else ""}"
            }
            Log.d(cTAG, strLog)
        }
    }

}