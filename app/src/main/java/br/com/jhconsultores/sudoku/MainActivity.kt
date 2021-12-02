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

    private var intJogoAdaptar = 1

    private var txtDadosJogo : TextView? = null

    private var sgg = SudokuGameGenerator ()

    //----------------------------------------------------------------------------------------------
    // Eventos da MainActivity
    //----------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //--- Instancializações e inicializações
        btnGeraJogo   = findViewById(R.id.btn_GerarJogo)
        btnAdaptaJogo = findViewById(R.id.btn_AdaptarJogo)
        btnJogaJogo   = findViewById(R.id.btn_JogarJogo)
        txtDadosJogo  = findViewById(R.id.txtJogos)

    }

    //----------------------------------------------------------------------------------------------
    // Funções para o atendimento ao tapping nos botões
    //----------------------------------------------------------------------------------------------
    //--- Evento tapping no botão de geração de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnGeraJogoClick(view : View?) {

        sgg.txtDados = ""

        strLog = "-> Tap no btnGeraJogo"
        Log.d(cTAG, strLog)
        txtDadosJogo?.text = strLog

        //--- Gera um novo jogo
        //---------------------------
        sgg.inicQuadMaiorGeracao()
        //---------------------------
        quadMaior = sgg.geraJogo()
        //---------------------------
        txtDadosJogo?.append(sgg.txtDados)

        //--- Envia o jogo gerado para ser usado como gabarito
        val arIntNumsJogo = ArrayList <Int> ()
        for (idxLin in 0..8) {
            for (idxCol in 0..8) {
                //-------------------------------------------
                arIntNumsJogo += quadMaior[idxLin][idxCol]
                //-------------------------------------------
            }
        }

        //--- Passa à atividade de Jogar
        val intent = Intent(this, JogarActivity::class.java)
        intent.action = "JogoGerado"
        intent.putIntegerArrayListExtra("GabaritoDoJogo", arIntNumsJogo)
        //----------------------
        startActivity(intent)
        //----------------------

    }

    //--- Evento tapping no botão de adaptação de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnAdaptaJogoClick(view : View?) {

        sgg.txtDados = ""

        strLog = "-> Tap no btnAdaptaJogo"
        Log.d(cTAG, strLog)
        txtDadosJogo?.text = strLog

        //--- Adapta jogo
        //---------------------------------------
        quadMaior = sgg.adaptaJogoAlgoritmo2()
        //---------------------------------------
        txtDadosJogo?.append(sgg.txtDados)
        //--- Se prepara para numa nova chamada passar ao próximo preset
        if (++sgg.intJogoAdaptar > 4) sgg.intJogoAdaptar = 1

        //--- Transfere o jogo adaptado para um vetor
        val arIntNumsJogo = ArrayList <Int> ()
        for (idxLin in 0..8) {
            for (idxCol in 0..8) {
                arIntNumsJogo += quadMaior[idxLin][idxCol]
            }
        }
        //--- Chama a atividade Jogar passando o gabarito do jogo
        val intent = Intent(this, JogarActivity::class.java)
        intent.putExtra ("intNumPreset", intJogoAdaptar)
        intent.action = "JogoAdaptado"
        intent.putIntegerArrayListExtra("GabaritoDoJogo", arIntNumsJogo)
        //----------------------
        startActivity(intent)
        //----------------------

    }

    //--- Evento tapping no botão de adaptação de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnJogaJogoClick(view : View?) {

        sgg.txtDados = ""

        strLog = "-> Tap no btnJogaJogo"
        Log.d(cTAG, strLog)
        txtDadosJogo?.text = strLog

        //----------------
        sgg.jogaJogo ()
        //----------------
        txtDadosJogo?.append(sgg.txtDados)

    }

}

