package br.com.jhconsultores.sudoku

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

//import android.widget.ScrollView
import android.widget.TextView

import br.com.jhconsultores.sudoku.SudokuBackTracking.solveSudoku
import br.com.jhconsultores.sudoku.SudokuGameGenerator

class MainActivity : AppCompatActivity() {

    //----------------------------------------------------------------------------------------------
    //                         Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private val cTAG     = "Sudoku"
    private var strLog   = ""

    private var quadMaior = arrayOf<Array<Int>>()

    private var btnGeraJogo   : Button? = null
    private var btnAdaptaJogo : Button? = null
    private var btnJogaJogo   : Button? = null

    private var intJogoAdaptar = 1

    private var txtDadosJogo : TextView?   = null

    var sgg = SudokuGameGenerator (txtDadosJogo)

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

        strLog = "-> Tap no btnGeraJogo"
        Log.d(cTAG, strLog)
        txtDadosJogo?.text = strLog

        //---------------------------
        sgg.inicQuadMaiorGeracao()
        //---------------------------

        //--- Gera um novo jogo
        //---------------------------
        quadMaior = sgg.geraJogo()
        //---------------------------

        //-----------------
        listaQuadMaior()
        //-----------------

    }

    //--- Evento tapping no botão de adaptação de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnAdaptaJogoClick(view : View?) {

        strLog = "-> Tap no btnAdaptaJogo"
        Log.d(cTAG, strLog)
        txtDadosJogo?.text = strLog

        //--- Adapta jogo
        //---------------------------------------
        quadMaior = sgg.adaptaJogoAlgoritmo2()
        //---------------------------------------

        //-----------------
        listaQuadMaior()
        //-----------------

        if (++sgg.intJogoAdaptar > 4) sgg.intJogoAdaptar = 1

    }

    //--- Evento tapping no botão de adaptação de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnJogaJogoClick(view : View?) {

        strLog = "-> Tap no btnJogaJogo"
        Log.d(cTAG, strLog)
        txtDadosJogo?.text = strLog

        //----------------
        sgg.jogaJogo ()
        //----------------

    }

    /*
    //----------------------------------------------------------------------------------------------
    // Funções para jogar o jogo
    //----------------------------------------------------------------------------------------------
    private fun jogaJogo () {

        strLog = "\n-> Jogar Sodoku"
        Log.d(cTAG, strLog)
        txtDadosJogo?.text = "${txtDadosJogo?.text}$strLog"

    }
    */

    /*
    //----------------------------------------------------------------------------------------------
    // Funções gerais
    //----------------------------------------------------------------------------------------------
    //--- Calcula as linhas do QM para um Qm
    private fun calcLinsQM (quadMenor : Int) : Array <Int> {

        // INT(EXT.TEXTO(F17;2;1)/3)*3
        val linInicQM = (quadMenor / 3) * 3

        return (arrayOf(linInicQM, linInicQM + 1, linInicQM + 2))

    }

    //--- Calcula as colunas do QM para um Qm
    private fun calcColsQM(quadMenor: Int): Array<Int> {

        // EXT.TEXTO(F17;2;1)*3-INT(EXT.TEXTO(F17;2;1)/3)*9
        val colInicQM = quadMenor * 3 - (quadMenor / 3) * 9

        return (arrayOf(colInicQM, colInicQM + 1, colInicQM + 2))

    }

    //--- verifValidade de um número do Qm para inserção no QM
    private fun verifValidade(quadMenor : Int, linhaQm : Int, colunaQm : Int,
                                                                      numero : Int) : Boolean {
        var flagNumeroOk = true

        //--- Calcula as linhas desse quadrado menor no QM
        //--------------------------------------
        val linhasQM = calcLinsQM (quadMenor)
        //--------------------------------------
        //--- Calcula as colunas desse quadrado menor no QM
        //------------------------------------
        val colsQM = calcColsQM (quadMenor)
        //------------------------------------

        //--- Converte a linha do numero gerado do Qm para a do QM
        val linQM = linhasQM[linhaQm]
        //--- Converte a coluna do numero gerado do Qm para a do QM
        val colQM = colsQM[colunaQm]

        //--- Verifica se número existe na LINHA do QM
        var numeroQM : Int
        for (idxColQM in 0..8) {

            if (!colsQM.contains(idxColQM)) {

                numeroQM = quadMaior[linQM][idxColQM]
                if (numero == numeroQM) {

                    flagNumeroOk = false
                    break

                }
            }
        }

        //--- Se existe, retorna para gerar um novo numero
        if (!flagNumeroOk) return false

        //--- Se não existe na linha, verifica se número existe na COLUNA do QM
        else {

            for (idxLinQM in 0..8) {

                if (!linhasQM.contains(idxLinQM)) {

                    numeroQM = quadMaior[idxLinQM][colQM]
                    if (numero == numeroQM) {

                        flagNumeroOk = false
                        break

                    }
                }
            }
        }

        return flagNumeroOk

    }
    //--- insere Qm no QM
    private fun insereQmEmQM (quadMenor : Int, array : Array <Int>) {

        // Converte os quadrados menores no quadMaior
        var valCel : Int

        //--- Calcula as linhas desse quadrado
        //------------------------------------------
        val linhasQuadQM = calcLinsQM (quadMenor)
        //------------------------------------------
        //--- Calcula as colunas desse quadrado
        //----------------------------------------
        val colsQuadQM = calcColsQM (quadMenor)
        //----------------------------------------

        for (linMenor in 0..2) {

            for (colMenor in 0..2) {

                valCel = array[linMenor * 3 + colMenor]

                quadMaior[linhasQuadQM[linMenor]][colsQuadQM[colMenor]] = valCel

            }
        }
    }
     */

    //--- listaQuadMaior
    @SuppressLint("SetTextI18n")
    private fun listaQuadMaior() {

        var strDados: String
        for (linha in 0..8) {

            strLog   = "linha $linha : "
            strDados = ""
            for (coluna in 0..8) {

                val strTmp = "${quadMaior[linha][coluna]}" + if (coluna < 8) ", " else ""
                strDados += strTmp
                strLog   += strTmp

            }
            Log.d(cTAG, strLog)

            val strDadosTmp = "\n" + strDados
            txtDadosJogo?.text = "${txtDadosJogo?.text}$strDadosTmp"

        }
    }

}

