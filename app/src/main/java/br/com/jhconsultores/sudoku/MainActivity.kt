package br.com.jhconsultores.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.util.Log

import android.view.View
import android.widget.*

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

    private lateinit var groupRBnivel   : RadioGroup
    private lateinit var rbFacil        : RadioButton
    private lateinit var rbMedio        : RadioButton
    private lateinit var rbDificil      : RadioButton
    private lateinit var rbMuitoDificil : RadioButton

    private lateinit var edtViewSubNivel: EditText

    private var strOpcaoJogo = "JogoGerado"
    private var strNivelJogo = "Fácil"

    private var quadMaiorAdapta = Array(9) { Array(9) { 0 } }

    private var txtDadosJogo : TextView? = null

    private var sgg = SudokuGameGenerator ()

    // Núm     0   21               31        41          51       61       81
    // clues  81   60               50        40          30       20
    //        |xxxx|  MUITO DIFÍCIL  |DIFÍCIL  |   MÉDIO   |  FÁCIL |xxxxxxx|
    // SETUP      60                50        40          30       20

    val arIntLimNiveis = arrayOf (20, 30, 40, 50)

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

        groupRBnivel  = findViewById(R.id.radioGrpNivel)
        rbFacil       = findViewById<RadioButton>(R.id.nivelFacil)
        rbMedio       = findViewById<RadioButton>(R.id.nivelMédio)
        rbDificil     = findViewById<RadioButton>(R.id.nivelDifícil)
        rbMuitoDificil= findViewById<RadioButton>(R.id.nivelMuitoDifícil)

        edtViewSubNivel= findViewById<EditText>(R.id.edtViewSubNivel)

        txtDadosJogo  = findViewById(R.id.txtJogos)

        //hideSoftKeyboard(currentFocus ?: View(this))

        //closeKeyBoard()

    }

    //--- onResume
    override fun onResume() {

        super.onResume()

        txtDadosJogo!!.text = ""
        sgg.txtDados        = ""

        //--------------------------
        prepRBniveis(true)
        //--------------------------
        edtViewSubNivel.isEnabled = true

    }

    //----------------------------------------------------------------------------------------------
    // Funções para o atendimento ao tapping nos botões
    //----------------------------------------------------------------------------------------------
    //--- Evento tapping no botão de geração de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnGeraJogoClick(view : View?) {

        strOpcaoJogo = "JogoGerado"

        sgg.txtDados = ""

        //--------------------------
        prepRBniveis(true)
        //--------------------------
        edtViewSubNivel.isEnabled = true

        strLog = "-> Tap no btnGeraJogo"
        Log.d(cTAG, strLog)

        //txtDadosJogo?.text = strLog
        txtDadosJogo?.text = ""
        sgg.txtDados = ""

        //--- Gera um novo jogo
        var nivelJogo = when {

            rbFacil.isChecked   -> {
                strNivelJogo = "Fácil"
                20
            }

            rbMedio.isChecked   -> {
                strNivelJogo = "Médio"
                30
            }
            rbDificil.isChecked -> {
                strNivelJogo = "Difícil"
                40
            }

            rbMuitoDificil.isChecked -> {
                strNivelJogo = "Muito Difícil"
                50
            }

            else -> {
                strNivelJogo = "Muito fácil"
                0
            }

        }
        strLog = "   - Nível: $strNivelJogo ($nivelJogo) Subnível: ${edtViewSubNivel.text}"
        Log.d(cTAG, strLog)

        nivelJogo += edtViewSubNivel.text.toString().toInt()

        //------------------------------------
        quadMaior = sgg.geraJogo(nivelJogo)
        //------------------------------------

        txtDadosJogo?.append(sgg.txtDados)

        // **** Os arrays preparados serão enviados pelo listener do botão JogaJogo ****

    }

    //--- Evento tapping no botão de adaptação de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnAdaptaJogoClick(view : View?) {

        strOpcaoJogo = "JogoAdaptado"
        sgg.txtDados = ""

        //---------------------------
        prepRBniveis(false)
        //---------------------------
        edtViewSubNivel.isEnabled = false

        strLog = "-> Tap no btnAdaptaJogo"
        Log.d(cTAG, strLog)

        txtDadosJogo?.text = ""
        sgg.txtDados = ""

        //--- Prepara o preset para se conseguir o gabarito do jogo
        if (++sgg.intJogoAdaptar > 4) sgg.intJogoAdaptar = 1
        //-------------------------------------------
        inicQuadMaiorAdaptacao(sgg.intJogoAdaptar)
        //-------------------------------------------

        sgg.quadMaiorRet = copiaArArInt(quadMaiorAdapta)

        //--- Adapta jogo--------------------
        //-----------------------------------------
        quadMaior = sgg.adaptaJogoAlgoritmo2()
        //-----------------------------------------
        txtDadosJogo?.append(sgg.txtDados)

        val nivelJogo    = sgg.intQtiZeros / 10
        val subNivelJogo = sgg.intQtiZeros % 10
        var rbNivelJogo : RadioButton = when (nivelJogo) {

            2 -> rbFacil
            3 -> rbMedio
            4 -> rbDificil
            5 -> rbMuitoDificil
            else -> rbFacil

        }
        rbNivelJogo.isChecked = true

        edtViewSubNivel.setText(subNivelJogo.toString())

        // **** Os arrays preparados serão enviados pelo listener do botão JogaJogo ****

    }

    //--- Evento tapping no botão para jogar o jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnJogaJogoClick(view : View?) {

        strLog = "-> Tap no btnJogaJogo"
        Log.d(cTAG, strLog)

        prepRBniveis(false)

        txtDadosJogo?.text = ""
        sgg.txtDados       = ""

        //--- Se não tiver jogo válido, informa ao usuário
        if (!sgg.flagJogoGeradoOk && !sgg.flagJogoAdaptadoOk) {

            val strToast = "Não há jogo válido!"
            //-----------------------------------------------------------------
            Toast.makeText(this, strToast, Toast.LENGTH_LONG).show()
            //-----------------------------------------------------------------

            Log.d(cTAG, "-> $strToast")

        }
        else {

            //--- Envia o jogo gerado para ser usado como gabarito
            val arIntNumsGab = ArrayList<Int>()
            for (idxLin in 0..8) {
                for (idxCol in 0..8) {
                    //-------------------------------------------------
                    arIntNumsGab += sgg.quadMaiorRet[idxLin][idxCol]
                    //-------------------------------------------------
                }
            }

            //--- Envia o jogo preparado para ser jogado
            val arIntNumsJogo = ArrayList<Int>()
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
            intent.putExtra("strNivelJogo", strNivelJogo)
            intent.putExtra("strSubNivelJogo", edtViewSubNivel.text.toString())
            intent.putIntegerArrayListExtra("GabaritoDoJogo", arIntNumsGab)
            intent.putIntegerArrayListExtra("JogoPreparado", arIntNumsJogo)
            //----------------------
            startActivity(intent)
            //----------------------

        }
    }

    //----------------------------------------------------------------------------------------------
    // Funções para o atendimento de tapping nos radio buttons
    //----------------------------------------------------------------------------------------------
    fun rbJogoFacil(view : View?) {

        strLog  = "-> onClick rbJogoFacil"
        Log.d(cTAG, strLog)

    }

    fun rbJogoMedio(view : View?) {

        strLog  = "-> onClick rbJogoMedio"
        Log.d(cTAG, strLog)

    }

    fun rbJogoDificil(view : View?) {

        strLog  = "-> onClick rbJogoDificil"
        Log.d(cTAG, strLog)

    }

    fun rbJogoMuitoDificil(view : View?) {

        strLog  = "-> onClick rbJogoMuitoDificil"
        Log.d(cTAG, strLog)

    }

    //----------------------------------------------------------------------------------------------
    // Funções
    //----------------------------------------------------------------------------------------------
    //--- inicQuadMaiorAdaptacao
    private fun inicQuadMaiorAdaptacao(jogoAdaptar : Int) {

        var array : Array <Int>
        quadMaiorAdapta = arrayOf<Array<Int>>()

        //--- Simula os dados iniciais propostos
        when (jogoAdaptar) {

            1 -> run {

                for (linha in 0..8) {

                    // array = arrayOf<Int>()
                    array = when (linha) {

                        0 -> arrayOf(0, 0, 4, 6, 0, 5, 8, 0, 0)
                        1 -> arrayOf(6, 5, 0, 0, 8, 0, 0, 0, 0)
                        2 -> arrayOf(0, 0, 8, 0, 4, 7, 6, 0, 5)
                        3 -> arrayOf(2, 8, 0, 3, 5, 6, 0, 0, 0)
                        4 -> arrayOf(7, 4, 0, 0, 0, 8, 2, 5, 6)
                        5 -> arrayOf(5, 6, 0, 4, 7, 2, 9, 0, 8)
                        6 -> arrayOf(8, 2, 5, 7, 0, 4, 3, 6, 0)
                        7 -> arrayOf(4, 3, 6, 5, 2, 0, 0, 8, 0)
                        else -> arrayOf(0, 0, 0, 8, 6, 3, 5, 4, 2)

                    }
                    quadMaiorAdapta += array

                }
            }

            2 -> run {

                for (linha in 0..8) {

                    // array = arrayOf<Int>()
                    array = when (linha) {

                        0 -> arrayOf(0, 6, 0, 7, 0, 8, 1, 9, 2)
                        1 -> arrayOf(1, 0, 5, 2, 0, 0, 0, 0, 7)
                        2 -> arrayOf(0, 2, 0, 0, 0, 6, 0, 0, 0)
                        3 -> arrayOf(0, 5, 0, 9, 3, 0, 0, 4, 0)
                        4 -> arrayOf(0, 0, 6, 5, 0, 2, 7, 8, 0)
                        5 -> arrayOf(9, 7, 0, 0, 0, 0, 3, 2, 5)
                        6 -> arrayOf(0, 0, 7, 4, 0, 0, 8, 0, 6)
                        7 -> arrayOf(8, 9, 4, 0, 7, 0, 0, 0, 0)
                        else -> arrayOf(0, 1, 0, 3, 0, 0, 0, 7, 4)

                    }
                    quadMaiorAdapta += array
                }
            }

            3 -> run {

                for (linha in 0..8) {

                    // array = arrayOf<Int>()
                    array = when (linha) {

                        0 -> arrayOf(0, 0, 3, 5, 0, 0, 4, 9, 0)
                        1 -> arrayOf(7, 6, 0, 0, 0, 0, 5, 0, 1)
                        2 -> arrayOf(0, 5, 4, 0, 7, 3, 6, 0, 8)
                        3 -> arrayOf(0, 1, 0, 0, 0, 0, 3, 0, 0)
                        4 -> arrayOf(0, 0, 7, 2, 6, 1, 0, 0, 0)
                        5 -> arrayOf(2, 0, 6, 0, 9, 0, 0, 1, 4)
                        6 -> arrayOf(6, 3, 2, 8, 5, 0, 0, 0, 0)
                        7 -> arrayOf(4, 0, 0, 0, 0, 2, 8, 0, 6)
                        else -> arrayOf(8, 0, 5, 0, 0, 7, 2, 0, 0)

                    }
                    quadMaiorAdapta += array
                }
            }

            // 4
            else -> run {

                for (linha in 0..8) {

                    // array = arrayOf<Int>()
                    array = when (linha) {

                        0 -> arrayOf(9, 0, 0, 8, 4, 1, 3, 0, 0)
                        1 -> arrayOf(0, 0, 1, 9, 0, 0, 4, 2, 0)
                        2 -> arrayOf(0, 0, 0, 2, 0, 0, 0, 1, 0)
                        3 -> arrayOf(8, 7, 0, 1, 0, 0, 5, 4, 0)
                        4 -> arrayOf(1, 5, 0, 3, 6, 0, 0, 0, 2)
                        5 -> arrayOf(2, 0, 0, 0, 0, 0, 7, 6, 0)
                        6 -> arrayOf(7, 2, 0, 0, 0, 5, 1, 9, 0)
                        7 -> arrayOf(6, 3, 0, 0, 0, 0, 2, 0, 7)
                        else -> arrayOf(0, 1, 5, 7, 0, 2, 0, 0, 8)

                    }
                    quadMaiorAdapta += array
                }
            }
        }
    }

    //--- copiaArArInt
    private fun copiaArArInt(arArIntPreset: Array<Array<Int>>): Array<Array<Int>> {

        /* https://stackoverflow.com/questions/45199704/kotlin-2d-array-initialization
            // A 6x5 array of Int, all set to 0.
            var m = Array(6) {Array(5) {0} }
         */

        //-------------------------------------------------------
        val arArIntTmp = Array(9) { Array(9) { 0 } }
        //-------------------------------------------------------

        for (intLin in 0..8) {
            for (intCol in 0..8) {
                arArIntTmp[intLin][intCol] = arArIntPreset[intLin][intCol]
                //arArIntCopia[intLin][intCol] = arArIntPreset[intLin][intCol]
            }
        }

        return arArIntTmp

    }

    //--- Prepara rbNivel
    private fun prepRBniveis(habilita : Boolean){

        val intIdxChild = groupRBnivel.childCount - 1
        if (intIdxChild > 0) {

            for (idxRB in 0..intIdxChild) {

                groupRBnivel.getChildAt(idxRB).isEnabled = habilita

            }

        }
    }

    //--- Esconde o keyboard
    /*
    private fun hideSoftKeyboard(view : View) {

        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)

    }
     */

    /*
    private fun Context.hideSoftKeyboard(view: View) {

        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

    }
    */

    /*
    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    */

    /*
    //--- Verifica o Gerador de números aleatórios
    private fun testaGenRnd () {

        //--- Inicializa um vetor para evitar repetição de números Rnd
        val arIntNumRnd = Array(81) { 0 }

        Log.d(cTAG, "-> Início do teste do gerador RND ...")

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
    */

}
