package br.com.jhconsultores.sudoku

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log

import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import androidx.core.view.isVisible

@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {

    //----------------------------------------------------------------------------------------------
    //                         Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private val cTAG   = "Sudoku"
    private var strLog = ""

    private var quadMaior = arrayOf<Array<Int>>()

    private lateinit var ivSudokuBoardMain : ImageView
    private var bmpMyImage : Bitmap? = null
    private var intCellwidth  = 0
    private var intCellheight = 0

    private lateinit var btnGeraJogo   : Button
    private lateinit var btnAdaptaJogo : Button
    private lateinit var btnJogaJogo   : Button

    private lateinit var groupRBnivel   : RadioGroup
    private lateinit var rbFacil        : RadioButton
    private lateinit var rbMedio        : RadioButton
    private lateinit var rbDificil      : RadioButton
    private lateinit var rbMuitoDificil : RadioButton

    private lateinit var groupRBadapta : RadioGroup
    private lateinit var rbPreset      : RadioButton
    private lateinit var rbEdicao      : RadioButton

    private lateinit var edtViewSubNivel: EditText
    private var flagAdaptaPreset = true

    private var strOpcaoJogo   = "JogoGerado"
    private var strNivelJogo   = "Fácil"
    private var nivelJogo      = 0
    private var subNivelJogo   = 0
    private var nivelTotalJogo = 0

    private val FACIL         = 20
    private val MEDIO         = 30
    private val DIFICIL       = 40
    private val MUITO_DIFICIL = 50

    private var quadMaiorAdapta = Array(9) { Array(9) { 0 } }
    private var arArIntNums     = Array(9) { Array(9) { 0 } }

    private lateinit var txtDadosJogo : TextView

    private var sgg = SudokuGameGenerator ()

    // Núm     0   21               31        41          51       61     81
    // clues  81   60               50        40          30       20      0
    //        |----|----------------|---------|-----------|--------|-------|
    //        |xxxx|  MUITO DIFÍCIL | DIFÍCIL |   MÉDIO   |  FÁCIL |xxxxxxx|
    //        |----|----------------|---------|-----------|--------|-------|
    // SETUP      60                50        40          30       20

    //----------------------------------------------------------------------------------------------
    // Eventos da MainActivity
    //----------------------------------------------------------------------------------------------
    //--- onCreate
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //--- Instancializações e inicializações
        btnAdaptaJogo = findViewById(R.id.btn_AdaptarJogo)
        btnJogaJogo   = findViewById(R.id.btn_JogarJogo)
        btnGeraJogo   = findViewById(R.id.btn_GerarJogo)

        groupRBnivel  = findViewById(R.id.radioGrpNivel)
        rbFacil       = findViewById(R.id.nivelFacil)
        rbMedio       = findViewById(R.id.nivelMédio)
        rbDificil     = findViewById(R.id.nivelDifícil)
        rbMuitoDificil= findViewById(R.id.nivelMuitoDifícil)
        //-----------------------------
        prepRBniveis(true)
        //-----------------------------
        edtViewSubNivel= findViewById(R.id.edtViewSubNivel)

        groupRBadapta = findViewById(R.id.radioGrpAdapta)
        rbPreset      = findViewById(R.id.preset)
        rbEdicao      = findViewById(R.id.edicao)
        groupRBadapta.visibility = INVISIBLE

        //--- Objetos gráficos
        ivSudokuBoardMain = findViewById(R.id.ivSudokuBoardMain)
        bmpMyImage = BitmapFactory.decodeResource(resources, R.drawable.sudoku_board3)
            .copy(Bitmap.Config.ARGB_8888, true)
        intCellwidth  = bmpMyImage!!.width  / 9
        intCellheight = bmpMyImage!!.height / 9

        //https://www.tutorialkart.com/kotlin-android/android-edittext-on-text-change/
        edtViewSubNivel.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                // tvSample.setText("Text in EditText : $s")

                if (s.isNotEmpty())
                    //---------------------------
                    verMudancaNivel(nivelJogo)
                    //---------------------------

            }

        })

        txtDadosJogo  = findViewById(R.id.txtJogos)

        //------------------------------------------------------------------------------------------
        // Listener para o evento onTouch do ImageView
        //------------------------------------------------------------------------------------------
        ivSudokuBoardMain.setOnTouchListener { _, event -> //--- Coordenadas tocadas

            val x = event.x.toInt()
            val y = event.y.toInt()
            Log.d(cTAG, "touched x: $x")
            Log.d(cTAG, "touched y: $y")

            //--- OffSets das coordenadas na Janela (???)
            val viewCoords = IntArray(2)

            ivSudokuBoardMain.getLocationOnScreen(viewCoords)
            Log.d(cTAG, "viewCoord x: " + viewCoords[0])
            Log.d(cTAG, "viewCoord y: " + viewCoords[1])

            //--- Coordenadas reais (???)
            val imageX = x - viewCoords[0] // viewCoords[0] is the X coordinate
            val imageY = y - viewCoords[1] // viewCoords[1] is the y coordinate
            Log.d(cTAG, "Real x: $imageX")
            Log.d(cTAG, "Real y: $imageY")

            //--- Coordenadas da célula tocada
            val intCol   = x / intCellwidth
            val intLinha = y / intCellheight
            //-----------------------------------------------------
            val intNum = arArIntNums[intLinha][intCol]
            //-----------------------------------------------------
            strLog = "-> Celula tocada: linha = " + intLinha + ", coluna = " + intCol +
                                                                            ", numero = " + intNum
            Log.d(cTAG, strLog)

            //--- Se a célula tocada contiver um número, "pinta" todas as células que contiverem
            //    o mesmo número.
            /*
            if (intNum > 0) {
                flagJoga = false    // Não quer jogar; só quer analisar ...
                intLinJogar = 0
                intColJogar = 0
                //-------------------------
                mostraNumsIguais(intNum)
                //-------------------------
            }
            //--- Se não contiver um número, quer jogar
            else {
                flagJoga = true     // Vamos ao jogo!
                intLinJogar = intLinha
                intColJogar = intCol
                //------------------------------------------
                mostraCelAJogar(intLinJogar, intColJogar)
                //------------------------------------------
            }
            */

            false

        }

    }

    //----------------------------------------------------------------------------------------------
    // Funções para o atendimento ao tapping nos botões (declarados no xml)
    //----------------------------------------------------------------------------------------------
    //--- Evento tapping no botão de geração de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnGeraJogoClick(view : View?) {

        strLog = "-> Tap no btnGeraJogo"
        Log.d(cTAG, strLog)

        strOpcaoJogo = "JogoGerado"

        sgg.txtDados = ""

        //--------------------------
        prepRBniveis(true)
        //--------------------------
        edtViewSubNivel.isEnabled = true

        groupRBadapta.visibility = INVISIBLE

        txtDadosJogo.text = ""
        sgg.txtDados      = ""

        //--- Gera um novo jogo
        nivelJogo = when {

            rbFacil.isChecked   -> {
                strNivelJogo = "Fácil"
                FACIL
            }

            rbMedio.isChecked   -> {
                strNivelJogo = "Médio"
                MEDIO
            }
            rbDificil.isChecked -> {
                strNivelJogo = "Difícil"
                DIFICIL
            }

            rbMuitoDificil.isChecked -> {
                strNivelJogo = "Muito Difícil"
                MUITO_DIFICIL
            }

            else -> {
                strNivelJogo = "Muito fácil"
                0
            }

        }
        strLog = "   - Nível: $strNivelJogo ($nivelJogo) Subnível: ${edtViewSubNivel.text}"
        Log.d(cTAG, strLog)

        if (edtViewSubNivel.text.toString().isNotEmpty()) {

            subNivelJogo   = edtViewSubNivel.text.toString().toInt()
            nivelTotalJogo = nivelJogo + subNivelJogo

            //-----------------------------------------
            quadMaior = sgg.geraJogo(nivelTotalJogo)
            //-----------------------------------------

            // txtDadosJogo.append(sgg.txtDados)

            //-------------------------------
            preencheSudokuBoard(quadMaior)
            //-------------------------------

            // **** O array preparado (quadMaior) será enviado pelo listener do botão JogaJogo ****

        }
        else Toast.makeText(this, "Não é possível gerar o jogo sem subnivel!",
                                                                         Toast.LENGTH_SHORT).show()
    }

    //--- Evento tapping no botão de adaptação de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnAdaptaJogoClick(view : View?) {

        strLog = "-> Tap no btnAdaptaJogo"
        Log.d(cTAG, strLog)

        strOpcaoJogo      = "JogoAdaptado"
        txtDadosJogo.text = ""
        sgg.txtDados      = ""

        groupRBadapta.visibility = VISIBLE

        //--- Prepara o preset para se conseguir o gabarito do jogo
        if (++sgg.intJogoAdaptar > 4) sgg.intJogoAdaptar = 1
        txtDadosJogo.text = String.format("%s%d","Preset #", sgg.intJogoAdaptar)

        //-------------------------------------------
        inicQuadMaiorAdaptacao(sgg.intJogoAdaptar)
        //-------------------------------------------

        sgg.quadMaiorRet = copiaArArInt(quadMaiorAdapta)

        sgg.flagJogoGeradoOk   = true
        sgg.flagJogoAdaptadoOk = true

        //---------------------------------------
        quadMaior = sgg.adaptaJogoAlgoritmo2()
        //---------------------------------------

        //--- Apresenta o nível e o subnível do preset
        nivelJogo    = (sgg.intQtiZeros / 10) * 10
        subNivelJogo = sgg.intQtiZeros % 10
        val rbNivelJogo : RadioButton = when (nivelJogo) {

            20 -> {
                strNivelJogo = "Fácil"
                rbFacil
            }
            30 -> {
                strNivelJogo = "Médio"
                rbMedio
            }
            40 -> {
                strNivelJogo = "Difícil"
                rbDificil
            }
            50 -> {
                strNivelJogo = "Muito Difícil"
                rbMuitoDificil
            }
            else -> {
                strNivelJogo = "Fácil"
                rbFacil
            }

        }
        rbNivelJogo.isChecked = true

        edtViewSubNivel.setText(subNivelJogo.toString())
        //---------------------------
        prepRBniveis(false)
        //---------------------------
        edtViewSubNivel.isEnabled = false

        //-------------------------------
        preencheSudokuBoard(quadMaior)
        //-------------------------------

        // **** O array preparado (quadMaior) será enviado pelo listener do botão JogaJogo ****

    }

    //--- Evento tapping no botão para jogar o jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnJogaJogoClick(view : View?) {

        strLog = "-> Tap no btnJogaJogo"
        Log.d(cTAG, strLog)

        //-------------------------------
        //prepRBniveis(false)
        //-------------------------------

        //--- Se não tiver jogo válido, informa ao usuário
        if ((groupRBadapta.isVisible && rbEdicao.isChecked) || (!sgg.flagJogoGeradoOk && !sgg.flagJogoAdaptadoOk)) {

            val strToast = "Não há jogo válido!"
            //-----------------------------------------------------------------
            Toast.makeText(this, strToast, Toast.LENGTH_LONG).show()
            //-----------------------------------------------------------------

            Log.d(cTAG, "-> $strToast")

        }
        else {

            txtDadosJogo.text = if (strOpcaoJogo == "JogoAdaptado")
                                       String.format("%s%d","Preset #", sgg.intJogoAdaptar) else ""
            sgg.txtDados = ""

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
    // Funções para o atendimento de tapping nos radioButtons de escolha de níveis de jogo
    //----------------------------------------------------------------------------------------------
    fun rbJogoFacil(view : View?) {

        strLog  = "-> onClick rbJogoFacil"
        Log.d(cTAG, strLog)

        //-----------------------
        verMudancaNivel(FACIL)
        //-----------------------

    }

    fun rbJogoMedio(view : View?) {

        strLog  = "-> onClick rbJogoMedio"
        Log.d(cTAG, strLog)

        //-----------------------
        verMudancaNivel(MEDIO)
        //-----------------------

    }

    fun rbJogoDificil(view : View?) {

        strLog  = "-> onClick rbJogoDificil"
        Log.d(cTAG, strLog)

        //-------------------------
        verMudancaNivel(DIFICIL)
        //-------------------------

    }

    fun rbJogoMuitoDificil(view : View?) {

        strLog  = "-> onClick rbJogoMuitoDificil"
        Log.d(cTAG, strLog)

        //-------------------------------
        verMudancaNivel(MUITO_DIFICIL)
        //-------------------------------

    }

    //----------------------------------------------------------------------------------------------
    // Funções para o atendimento de tapping nos radioButtons de escolha da adaptação de jogo
    //----------------------------------------------------------------------------------------------
    fun RBpresetClick(view : View?) {

        strLog  = "-> onClick rbPreset"
        Log.d(cTAG, strLog)

        if (!flagAdaptaPreset) {

            //-------------------------
            btnAdaptaJogoClick(view)
            //-------------------------
            flagAdaptaPreset = true

        }

    }

    fun RBedicaoClick(view : View?) {

        strLog  = "-> onClick rbEdicao"
        Log.d(cTAG, strLog)

        flagAdaptaPreset = false

        //------------
        editaJogo()
        //------------

    }

    //----------------------------------------------------------------------------------------------
    // Funções
    //----------------------------------------------------------------------------------------------

    //--- editaJogo
    private fun editaJogo() {

        txtDadosJogo.text = ""

        val arArIntJogo = Array(9) { Array(9) { 0 } }
        //---------------------------------
        preencheSudokuBoard(arArIntJogo)
        //---------------------------------



    }

    //--- preencheSudokuBoard
    private fun preencheSudokuBoard(arArIntJogo : Array<Array<Int>>) {

        //--- Objetos gráficos
        // Paint
        val pincelAzul = Paint()
        val intTamTxt  = 25
        val scale      = resources.displayMetrics.density
        // ImageView
        //val ivSudokuBoardMain = findViewById<View>(R.id.ivSudokuBoardMain) as ImageView
        // Canvas
        val canvasMyImage = Canvas(bmpMyImage!!)

        //--- Escreve nas células
        intCellwidth  = bmpMyImage!!.width  / 9
        intCellheight = bmpMyImage!!.height / 9

        for (intLinha in 0..8) {

            for (intCol in 0..8) {

                //-------------------------------------------
                val intNum = arArIntJogo[intLinha][intCol]
                //-------------------------------------------
                if (intNum > 0) {

                    val strTexto = intNum.toString()

                    pincelAzul.textSize = intTamTxt * scale

                    // Declarada em JogarActivity
                    // escreveCelula(intLinha, intCol, strTexto, pincel!!) // canvasMyImage!!

                    //--- Coordenada Y (linhas)
                    //--------------------------------------------------------------
                    val yCoord = intCellheight * 3 / 4 + intLinha * intCellheight
                    //--------------------------------------------------------------

                    //--- Coordenada X (colunas)
                    //------------------------------------------------------
                    val xCoord = intCellwidth / 3 + intCol * intCellwidth
                    //------------------------------------------------------

                    //------------------------------------------------------------------------------
                    canvasMyImage.drawText(strTexto, xCoord.toFloat(), yCoord.toFloat(),
                                                                                        pincelAzul)
                    //------------------------------------------------------------------------------

                }
            }
        }
        ivSudokuBoardMain.setImageBitmap(bmpMyImage)

    }

    //--- inicQuadMaiorAdaptacao
    private fun inicQuadMaiorAdaptacao(jogoAdaptar : Int) {

        var array : Array <Int>
        quadMaiorAdapta = arrayOf()

        //--- Simula os dados iniciais propostos
        when (jogoAdaptar) {

            // Nível: fácil  Subnível: 0 (nívelTotal: 20)
            1 -> run {

                for (linha in 0..8) {

                    // array = arrayOf<Int>()
                    array = when (linha) {

                        0 -> arrayOf(6, 0, 8, 7, 4, 0, 1, 0, 2)
                        1 -> arrayOf(3, 2, 0, 6, 8, 1, 0, 7, 5)
                        2 -> arrayOf(7, 5, 1, 9, 0, 3, 4, 0, 8)
                        3 -> arrayOf(9, 6, 3, 5, 0, 8, 0, 2, 0)
                        4 -> arrayOf(0, 4, 2, 3, 7, 0, 8, 9, 1)
                        5 -> arrayOf(1, 8, 0, 0, 9, 4, 6, 5, 3)
                        6 -> arrayOf(2, 1, 6, 8, 3, 9, 0, 0, 7)
                        7 -> arrayOf(0, 7, 5, 1, 6, 0, 3, 8, 9)
                        else -> arrayOf(8, 3, 0, 0, 5, 7, 2, 1, 6)

                    }
                    quadMaiorAdapta += array
                }
            }

            // Nível: médio  Subnível: 2 (nívelTotal: 32)
            2 -> run {

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

            // Nível: difícil  Subnível: 3 (nívelTotal: 43)
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

            // Nível: muito difícil  Subnível: 9 (nívelTotal: 59)
            // 4
            else -> run {

                for (linha in 0..8) {

                    // array = arrayOf<Int>()
                    array = when (linha) {

                        0 -> arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
                        1 -> arrayOf(0, 0, 0, 0, 0, 0, 0, 8, 9)
                        2 -> arrayOf(0, 0, 0, 0, 0, 8, 1, 6, 2)
                        3 -> arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
                        4 -> arrayOf(0, 0, 0, 0, 3, 0, 6, 0, 0)
                        5 -> arrayOf(0, 6, 0, 8, 0, 0, 0, 1, 0)
                        6 -> arrayOf(0, 0, 0, 0, 6, 1, 0, 3, 0)
                        7 -> arrayOf(0, 0, 1, 4, 8, 5, 0, 0, 0)
                        else -> arrayOf(8, 0, 0, 0, 7, 3, 0, 5, 0)

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

        //------------------------------------------------------
        val arArIntTmp = Array(9) { Array(9) { 0 } }
        //------------------------------------------------------

        for (intLin in 0..8) {
            for (intCol in 0..8) { arArIntTmp[intLin][intCol] = arArIntPreset[intLin][intCol] }
        }

        return arArIntTmp

    }

    //--- Prepara rbNivel
    private fun prepRBniveis(habOuDesab : Boolean){

        val intIdxChild = groupRBnivel.childCount - 1
        if (intIdxChild > 0) {

            for (idxRB in 0..intIdxChild) { groupRBnivel.getChildAt(idxRB).isEnabled = habOuDesab }

        }
    }

    //--- verMudancaNivel
    private fun verMudancaNivel(intNivel : Int) {

        if (strOpcaoJogo == "JogoGerado") {

            if (edtViewSubNivel.text.toString().isNotEmpty()) {

                val intNivelTotal = intNivel + edtViewSubNivel.text.toString().toInt()
                if (intNivelTotal != nivelJogo) {

                    sgg.flagJogoGeradoOk = false
                    sgg.flagJogoAdaptadoOk = false

                    val arArIntJogo = Array(9) { Array(9) { 0 } }
                    //---------------------------------
                    preencheSudokuBoard(arArIntJogo)
                    //---------------------------------

                    nivelJogo = intNivelTotal

                }
            } else Toast.makeText(
                this, "Não é possível gerar o jogo sem subnivel!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}


