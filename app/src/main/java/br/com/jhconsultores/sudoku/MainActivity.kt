package br.com.jhconsultores.sudoku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

//import android.widget.ScrollView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    //----------------------------------------------------------------------------------------------
    //                         Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private val cTAG   = "Sudoku"
    private var strLog = ""

    private var quadMaior = arrayOf<Array<Int>>()

    private var btnGeraJogo   : Button? = null
    private var btnAdaptaJogo : Button? = null
    private var btnJogaJogo   : Button? = null

    private var strOpcaoJogo = "JogoGerado"

    private var intJogoAdaptar = 1

    private var txtDadosJogo : TextView? = null

    private var sgg = SudokuGameGenerator ()

    //----------------------------------------------------------------------------------------------
    // Eventos da MainActivity
    //----------------------------------------------------------------------------------------------
    //--- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //--------------
        // testaGenRnd ()
        //--------------

        //--- Instancializações e inicializações
        btnGeraJogo   = findViewById(R.id.btn_GerarJogo)
        btnAdaptaJogo = findViewById(R.id.btn_AdaptarJogo)
        btnJogaJogo   = findViewById(R.id.btn_JogarJogo)

        txtDadosJogo  = findViewById(R.id.txtJogos)

    }

    //--- onResume
    override fun onResume() {

        super.onResume()

        txtDadosJogo!!.text = ""

    }

    //----------------------------------------------------------------------------------------------
    // Funções para o atendimento ao tapping nos botões
    //----------------------------------------------------------------------------------------------
    //--- Evento tapping no botão de geração de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnGeraJogoClick(view : View?) {

        strOpcaoJogo = "JogoGerado"

        sgg.txtDados = ""

        strLog = "-> Tap no btnGeraJogo"
        Log.d(cTAG, strLog)
        //txtDadosJogo?.text = strLog

        //--- Gera um novo jogo
        //---------------------------
        quadMaior = sgg.geraJogo()
        //---------------------------

        txtDadosJogo?.append(sgg.txtDados)

    }

    //--- Evento tapping no botão de adaptação de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnAdaptaJogoClick(view : View?) {

        strOpcaoJogo = "JogoAdaptado"

        sgg.txtDados = ""

        strLog = "-> Tap no btnAdaptaJogo"
        Log.d(cTAG, strLog)
        txtDadosJogo?.text = strLog

        //--- Adapta jogo
        //---------------------------------------
        quadMaior = sgg.adaptaJogoAlgoritmo2()
        //---------------------------------------
        txtDadosJogo?.append(sgg.txtDados)

    }

    //--- Evento tapping no botão de adaptação de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnJogaJogoClick(view : View?) {

        strLog = "-> Tap no btnJogaJogo"
        Log.d(cTAG, strLog)
        txtDadosJogo?.text = strLog

        //--- Envia o jogo gerado para ser usado como gabarito
        val arIntNumsGab = ArrayList <Int> ()
        for (idxLin in 0..8) {
            for (idxCol in 0..8) {
                //--------------------------------------------------
                arIntNumsGab += sgg.quadMaiorRet[idxLin][idxCol]
                //--------------------------------------------------
            }
        }

        //--- Envia o jogo preparado para ser jogado
        val arIntNumsJogo = ArrayList <Int> ()
        for (idxLin in 0..8) {
            for (idxCol in 0..8) {
                //-------------------------------------------
                arIntNumsJogo += quadMaior[idxLin][idxCol]
                //-------------------------------------------
            }
        }

        //--- Prepara a Intent para chamar JogarActivity
        val intent = Intent(this, JogarActivity::class.java)
        intent.action = strOpcaoJogo
        intent.putExtra("NivelDoJogo", SudokuBackTracking.intNumBackTracking)
        intent.putIntegerArrayListExtra("GabaritoDoJogo", arIntNumsGab)
        intent.putIntegerArrayListExtra("JogoPreparado",  arIntNumsJogo)
        //----------------------
        startActivity(intent)
        //----------------------

    }

    //--- Verifica o Gerador de números aleatórios
    private fun testaGenRnd () {

        //--- Inicializa um vetor para evitar repetição de números Rnd
        val arIntNumRnd = Array(81) { 0 }

        Log.d(cTAG, "-> Inicio do teste do gerador RND ...")

        var intContaZero = 0
        do {

            //--- Gera número aleatório sem repetição
            //---------------------------------
            val numRnd = (1..81).random()      // gera de 1 a 81 inclusives
            //---------------------------------
            if (arIntNumRnd[numRnd - 1] == 0) { arIntNumRnd[numRnd - 1] = numRnd }

            intContaZero = 0
            for (idxNumRnd in 0..80) {

                if (arIntNumRnd[idxNumRnd] == 0) intContaZero++

            }

        } while (intContaZero > 0)

        Log.d(cTAG, "-> Fim do teste do gerador RND!")

    }

}

