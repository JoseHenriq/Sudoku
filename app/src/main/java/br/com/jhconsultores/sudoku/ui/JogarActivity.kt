package br.com.jhconsultores.sudoku.ui

import android.os.Bundle

import android.graphics.*
import android.widget.*

import android.content.Context
import android.os.Build

import android.util.Log
import android.util.TypedValue

import android.view.View

import android.annotation.SuppressLint
import android.os.SystemClock
import android.view.ViewGroup

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import br.com.jhconsultores.sudoku.R
import br.com.jhconsultores.utils.*

import java.lang.Exception
import java.nio.IntBuffer

class JogarActivity : AppCompatActivity() {   //Activity() {

    //--------------------------------------------------------------------------
    //                    Instancializações e inicializações
    //--------------------------------------------------------------------------
    private var cTAG     = "Sudoku"
    private var strLog   = ""
    private var strToast = ""

    private var intImageResource = 0
    private var bmpInic: Bitmap? = null // Board vazio Lido a partir do resource/drawable
    private var bmpJogo: Bitmap? = null // Preset ou jogo gerado ANTES dos novos números

    private var bmpMyImage: Bitmap? = null             // Preset ou jogo gerado e novos números

    private var bmpNumDisp    : Bitmap? = null             // Números disponíveis
    private var bmpSudokuBoard: Bitmap? = null             // Jogo

    private var canvasMyImage    : Canvas? = null
    private var canvasNumDisp    : Canvas? = null
    private var canvasSudokuBoard: Canvas? = null

    private var iViewSudokuBoard: ImageView? = null
    private var iViewNumsDisps  : ImageView? = null

    private var tvNivel   : TextView? = null
    private var tvSubNivel: TextView? = null
    private var tvErros   : TextView? = null
    private var tvClues   : TextView? = null
    private var intContaErro = 0

    private lateinit var toolBar: androidx.appcompat.widget.Toolbar

    private var intTamTxt = 25 // 50 // 200 //
    private var scale     = 0f

    private var pincelVerde = Paint()
    private var pincelBranco = Paint()
    private var pincelPreto = Paint()
    private var pincelAzul = Paint()
    private var pincelLaranja = Paint()
    private var pincelPurple200 = Paint()

    //--- Medidas
    //- Células do board
    private var intCellwidth = 0
    private var intCellheight = 0

    //- Margens do board
    private var intmargTopDp = 0
    private var intMargleftdp = 0
    private var intMargtoppx = 0
    private var intMargleftpx = 0

    //- Imagem Sudoku board
    private var intImgwidth = 0
    private var intImgheight = 0

    //--- Controle de jogadas
    private var intColJogar = 0
    private var intLinJogar = 0
    private var flagJoga = false

    private var arIntNumsDisp = Array(9) { 9 }     // intArrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9)
    private var arArIntGab = Array(9) { Array(9) { 0 } }

    var arArIntNums = Array(9) { Array(9) { 0 } }

    private var arArIntCopia = Array(9) { Array(9) { 0 } }

    private var arIntNumsGab = ArrayList<Int>()   // Gabarito
    private var arIntNumsJogo = ArrayList<Int>()   // Jogo

    private var action = "JogoGerado"
    private var strNivelJogo = "Fácil"
    private var strSubNivelJogo = "0"

    private lateinit var crono: Chronometer
    private var strCronoInic = ""
    private var timeStopped: Long = 0L

    private var strInicia = ""
    private var strPause = ""
    private var strReInicia = ""

    private val utils = Utils()

    //--------------------------------------------------------------------------
    //                                Eventos
    //--------------------------------------------------------------------------
    //--- onCreate MainActivity
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_jogar)

            //--- Instancia objetos locais para os objetos XML
            tvNivel    = findViewById(R.id.tv_Nivel)
            tvSubNivel = findViewById(R.id.tv_Subnivel)
            tvErros    = findViewById(R.id.tv_Erros)
            tvClues    = findViewById(R.id.tv_Clues)

            val btnReset  = findViewById<View>(R.id.btnReset) as Button
            val btnInicia = findViewById<View>(R.id.btnInicia) as Button
            val btnSalvar = findViewById<View>(R.id.btnSalvar) as Button
            btnInicia.isEnabled = true

            //--- Ativa o actionBar
            toolBar = findViewById(R.id.toolbar2)
            setSupportActionBar(toolBar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            //--------------------------------------------------------------------------------------
            // Objetos gráficos
            //--------------------------------------------------------------------------------------

            //------------------------
            inicializaObjGraficos()
            //------------------------

            //--------------------------------------------------------------------------------------
            // Inicializa dados para deixar o jogo pronto
            //--------------------------------------------------------------------------------------
            //--- Ação à ser executada
            action          = intent.action.toString()

            //--- Recupera os dados recebidos via intent
            strNivelJogo    = intent.getStringExtra("strNivelJogo") as String
            strSubNivelJogo = intent.getStringExtra("strSubNivelJogo") as String

            strCronoInic    = intent.getStringExtra("strCronoConta") as String
            intContaErro    = (intent.getStringExtra("strErro") as String).toInt()

            // Armazena o gabarito em um array<int>
            arIntNumsGab  = intent.getIntegerArrayListExtra("GabaritoDoJogo") as ArrayList<Int>
            arIntNumsJogo = intent.getIntegerArrayListExtra("JogoPreparado") as  ArrayList<Int>

            // Gabarito e/ou jogo inválidos
            if (arIntNumsGab.size != 81 || arIntNumsJogo.size != 81) {

                Log.d(cTAG, "-> Erro: array(s) com menos numeros que o necessário (81)")

            }
            // Gabarito e jogo válido
            else {

                // Armazena o gabarito em um Array<Array<Int>> para processamento local
                for (intLinha in 0..8) {
                    for (intCol in 0..8) {

                        arArIntNums[intLinha][intCol] = arIntNumsJogo[intLinha * 9 + intCol] // Jogo
                        arArIntGab[intLinha][intCol] =
                            arIntNumsGab[intLinha * 9 + intCol] // Gabarito

                    }
                }

                //-----------------------------------------
                arArIntCopia = copiaArArInt(arArIntNums)
                //-----------------------------------------

                arIntNumsDisp = Array(9) { 9 }     // intArrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9)

                //-------------
                iniciaJogo()
                //-------------

            } // Fim de gabarito recebido via intent ok

            //--- Cronômetro
            strCronoInic = resources.getString(R.string.crono_inic)
            //---------------------------------
            crono = Chronometer(this)
            //---------------------------------
            preparaCrono(crono)
            //--------------------

            //------------------------------------------------------------------
            // Listeners para o evento onTouch dos ImageViews
            //------------------------------------------------------------------
            // SudokuBoard
            iViewSudokuBoard!!.setOnTouchListener { _, event -> //--- Coordenadas tocadas

                try {
                    val x = event.x.toInt()
                    val y = event.y.toInt()
                    //Log.d(cTAG, "touched x: $x")
                    //Log.d(cTAG, "touched y: $y")

                    //--- OffSets das coordenadas na Janela (???)
                    val viewCoords = IntArray(2)
                    iViewSudokuBoard!!.getLocationOnScreen(viewCoords)
                    //Log.d(cTAG, "viewCoord x: " + viewCoords[0])
                    //Log.d(cTAG, "viewCoord y: " + viewCoords[1])

                    //--- Coordenadas reais (???)
                    //val imageX = x - viewCoords[0] // viewCoords[0] is the X coordinate
                    //val imageY = y - viewCoords[1] // viewCoords[1] is the y coordinate
                    //Log.d(cTAG, "Real x: $imageX")
                    //Log.d(cTAG, "Real y: $imageY")

                    //--- Coordenadas da célula tocada
                    val intCol = x / intCellwidth
                    val intLinha = y / intCellheight
                    //-------------------------------------------
                    val intNum = arArIntNums[intLinha][intCol]
                    //-------------------------------------------
                    //strLog = "-> Celula tocada: linha = " + intLinha + ", coluna = " + intCol +
                    //        ", numero = " + intNum
                    //Log.d(cTAG, strLog)

                    //--- Se a célula tocada contiver um número, "pinta" todas as células que contiverem
                    //    o mesmo número.
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
                }
                catch (exc : Exception) {

                    strLog = "Erro: ${exc.message}"
                    Log.d(cTAG, strLog)

                    Toast.makeText(this, strLog, Toast.LENGTH_SHORT).show()

                }

                false

            }

            // Números Disponíveis para se colocar em jogo
            iViewNumsDisps!!.setOnTouchListener { _, event -> //--- Só transfere o número para o board se estiver jogando

                try {

                    if (flagJoga) {

                        //--- Coordenada X do numsDisps tocada
                        val x = event.x.toInt()

                        //--- Coordenada X e valor da célula tocada
                        val intCol = x / intCellwidth
                        val intNum = intCol + 1
                        val intQtidd = arIntNumsDisp[intNum - 1]

                        //strLog = "-> Celula tocada em NumDisp: coluna = " + intCol +
                        //        ", qtidd = " + intQtidd
                        //Log.d(cTAG, strLog)

                        //--- Verifica se ainda tem desse número para jogar e se esse número é válido.
                        var flagNumValido: Boolean
                        if (intQtidd > 0) {

                            //--- Verifica se num válido
                            // Determina a que Qm o número pertence
                            //----------------------------------------------------------
                            val intQuadMenor = determinaQm(intLinJogar, intColJogar)
                            //----------------------------------------------------------

                            //Log.d(
                            //    cTAG, "-> linhaJogar= " + intLinJogar + " colJogar= " +
                            //            intColJogar + " Qm = " + intQuadMenor )

                            // Verifica se esse número ainda não existe no seu Qm e nem no seu QM
                            //--------------------------------------------------------------------------
                            flagNumValido =
                                verifValidade(intQuadMenor, intLinJogar, intColJogar, intNum)
                            //--------------------------------------------------------------------------
                            if (!flagNumValido) {

                                //strLog = "-> Número NÃO válido (linha, coluna ou quadro); NÃO será incluído" +
                                //        " no Sudoku board."
                                //Log.d(cTAG, strLog)

                                strToast = "Número NÃO Ok (linha, coluna ou quadro)"
                                //-----------------------------------------------------------------
                                Toast.makeText(this, strToast, Toast.LENGTH_SHORT).show()
                                //-----------------------------------------------------------------

                            }
                            // Verifica se esse número, nessa célula, é o mesmo do gabarito
                            else {

                                //--- Número diferente do num do gabarito
                                if (intNum != arArIntGab[intLinJogar][intColJogar]) {

                                    flagNumValido = false

                                    //strLog = "-> Número NÃO válido (gab); NÃO será incluído" +
                                    //         " no Sudoku board."
                                    //Log.d(cTAG, strLog)

                                    strToast = "Número NÃO Ok (gabarito)"
                                    //-----------------------------------------------------------------
                                    Toast.makeText(this, strToast, Toast.LENGTH_SHORT).show()
                                    //-----------------------------------------------------------------

                                }
                                //--- Número OK qto ao gabarito
                                else {

                                    //Log.d(cTAG, "-> Número válido; será incluído no Sudoku board.")

                                    //strToast = "Número Ok!"
                                    //----------------------------------------------------------------
                                    //Toast.makeText(this, strToast, Toast.LENGTH_LONG).show()
                                    //----------------------------------------------------------------

                                    //--- Atualiza o Sudoku board
                                    //----------------------------------------------------------------------
                                    pintaCelula(intLinJogar, intColJogar, pincelBranco)
                                    //----------------------------------------------------------------------
                                    escreveCelula(
                                        intLinJogar,
                                        intColJogar,
                                        intNum.toString(),
                                        pincelAzul
                                    )
                                    //----------------------------------------------------------------------
                                    desenhaSudokuBoard(false)
                                    //-----------------------------------
                                    preencheJogo()
                                    //---------------

                                    //--- Salva esse bitmap
                                    //--------------------------------------
                                    copiaBmpByBuffer(bmpMyImage, bmpJogo)
                                    //--------------------------------------

                                    //--- Atualiza a base de dados
                                    arArIntNums[intLinJogar][intColJogar] = intNum
                                    arIntNumsDisp[intNum - 1]--

                                    //--- Atualiza a qtidd disponível para esse número
                                    //-------------------
                                    atualizaNumDisp()
                                    //-------------------

                                    //--- Destaca os números iguais a esse já jogados
                                    //--------------------------
                                    mostraNumsIguais(intNum)
                                    //--------------------------

                                    flagJoga = false

                                }
                            }

                            if (!flagNumValido) {
                                tvErros!!.text = "${++intContaErro}"
                            }

                        }

                        //--- Verifica se fim de jogo (todas as qtidds foram zeradas
                        var flagContJogo = false
                        for (idxVetorNumDisp in 0..8) {

                            if (arIntNumsDisp[idxVetorNumDisp] > 0) flagContJogo = true

                        }
                        //--- Se já foram utilizados todos os números disponíveis, pára o cronometro
                        if (!flagContJogo) {

                            Log.d(cTAG, "-> ${crono.text} - Fim")

                            crono.stop()
                            flagJoga = false

                            btnInicia.text = strInicia
                            btnInicia.isEnabled = false

                        }
                    }

                }
                catch (exc : Exception) {

                    strLog = "Erro: ${exc.message}"
                    Log.d(cTAG, strLog)

                    Toast.makeText(this, strLog, Toast.LENGTH_SHORT).show()

                }

                false

            }

            //------------------------------------------------------------------
            // Listeners para o evento onClick dos buttons
            //------------------------------------------------------------------
            //--- btnInicia
            btnInicia.setOnClickListener {

                strInicia = resources.getString(R.string.inicia)
                strPause = resources.getString(R.string.pause)
                strReInicia = resources.getString(R.string.reinicia)

                //--------------------------------------------------------------
                // Legenda do botão: Inicia ou ReInicia
                //--------------------------------------------------------------
                if (btnInicia.text == strInicia || btnInicia.text == strReInicia) {

                    strLog = if (btnInicia.text == strInicia) strInicia else strReInicia
                    Log.d(cTAG, "-> ${crono.text} - $strLog")

                    iViewSudokuBoard!!.isEnabled = true
                    iViewNumsDisps!!.isEnabled   = true

                    crono.base = SystemClock.elapsedRealtime() + timeStopped
                    //--------------
                    crono.start()
                    //--------------

                    btnInicia.text = strPause

                }

                //--------------------------------------------------------------------------------------
                // Legenda do botão: Pause
                //--------------------------------------------------------------------------------------
                else {

                    Log.d(cTAG, "-> ${crono.text} - $strPause")
                    timeStopped = crono.base - SystemClock.elapsedRealtime()
                    //-------------
                    crono.stop()
                    //-------------

                    iViewSudokuBoard!!.isEnabled = false
                    iViewNumsDisps!!.isEnabled = false

                    btnInicia.text = strReInicia

                }
            }

            //--- Botão de Reset
            btnReset.setOnClickListener {

                strLog = "-> Tap no btn \"Reset\" "
                Log.d(cTAG, strLog)

                androidx.appcompat.app.AlertDialog.Builder(this)

                    .setTitle("Sudoku - Jogo")
                    .setMessage("Tem certeza que quer recomeçar o jogo atual?")

                    .setPositiveButton("Sim") { _, _ ->

                        Log.d(cTAG, "-> \"Sim\" was pressed")

                        tvNivel!!.text = ""
                        tvSubNivel!!.text = ""

                        intContaErro = 0
                        tvErros!!.text = "$intContaErro"

                        Log.d(cTAG, "-> ${crono.text} - Reset")
                        timeStopped = 0
                        //-------------
                        crono.stop()
                        //-------------
                        crono.text = strCronoInic

                        //-----------------------------------------
                        arArIntNums = copiaArArInt(arArIntCopia)
                        //-----------------------------------------

                        arIntNumsDisp = Array(9) { 9 }     // intArrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9)
                        //-------------
                        iniciaJogo()
                        //-------------

                        iViewSudokuBoard!!.isEnabled = false
                        iViewNumsDisps!!.isEnabled = false

                        btnInicia.isEnabled = true
                        btnInicia.text = resources.getString(R.string.inicia)

                    }

                    .setNegativeButton("Não") { _, _ ->

                        Log.d(cTAG, "-> \"Não\" was pressed")

                    }
                    .show()

            }

            //--- Botão de Salvar o jogo
            btnSalvar.setOnClickListener {

                strLog = "-> Tap no btn \"Salvar\" "
                Log.d(cTAG, strLog)

                androidx.appcompat.app.AlertDialog.Builder(this)

                    .setTitle("Sudoku - Jogo")
                    .setMessage("Tem certeza que quer salvar o jogo?")

                    .setPositiveButton("Sim") { _, _ ->

                        Log.d(cTAG, "-> \"Sim\" was pressed")

                        //------------
                        salvaJogo()
                        //------------

                        Log.d(cTAG, "-> Jogos salvos: ")
                        //--------------------------------------------------------------------------
                        val arStrArqsNames = utils
                            .listaExtMemArqDir("/Download/sudoku/Jogos")
                        //--------------------------------------------------------------------------
                        if (arStrArqsNames.isNotEmpty()) {

                            for (strArqName in arStrArqsNames) {
                                Log.d(cTAG, "   - $strArqName")
                            }

                        } else {

                            strLog = "   - Não há arquivos de jogos no dir /Download/sudoku/Jogos"
                            Log.d(cTAG, strLog)

                        }

                    }

                    .setNegativeButton("Não") { _, _ ->

                        Log.d(cTAG, "-> \"Não\" was pressed")

                    }
                    .show()

            }

        } catch (exc: Exception) {

            Log.d(cTAG, "Erro: ${exc.message}")

        }

    }

    //----------------------------------------------------------------------------------------------
    //                                     Funções
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    // Gráficas - Sudoku Board
    //----------------------------------------------------------------------------------------------
    //--- desenhaSudokuBoard
    private fun desenhaSudokuBoard(flagApaga: Boolean) {

        var flCoordXInic: Float
        var flCoordYInic: Float
        var flCoordXFim: Float
        var flCoordYFim: Float
        val flPincelFino = 2.toFloat()
        val flPincelGrosso = 6.toFloat()
        val pincelDesenhar = pincelPreto

        val flagApagaBoard: Boolean = flagApaga

        //--- Redesenha o board a partir do zero
        //flagApagaBoard = true
        if (flagApagaBoard) {

            //-----------------------------------------------------------------------------
            canvasMyImage!!.drawRect(
                0f,
                0f,
                intImgwidth.toFloat(),
                intImgheight.toFloat(),
                pincelBranco
            )
            //-----------------------------------------------------------------------------
        }

        //--- Desenha as linhas horizontais
        for (intLinha in 0..9) {
            flCoordXInic = 0f
            flCoordYInic = (intLinha * intCellheight).toFloat()

            flCoordXFim = (9 * intCellwidth).toFloat()
            flCoordYFim = flCoordYInic

            var flLargPincel = flPincelFino
            if (intLinha % 3 == 0) {
                flLargPincel = flPincelGrosso
                if (flCoordYInic > 0) flCoordYInic--
                if (flCoordYFim > 0) flCoordYFim--
            }
            pincelDesenhar.strokeWidth = flLargPincel
            //--------------------------------------------------------------------------------------
            canvasMyImage!!.drawLine(
                flCoordXInic, flCoordYInic, flCoordXFim, flCoordYFim, pincelDesenhar
            )
            //--------------------------------------------------------------------------------------
        }
        //--- Desenha as linhas verticais
        for (intCol in 0..9) {
            flCoordXInic = (intCol * intCellwidth).toFloat()
            flCoordYInic = 0f
            flCoordXFim = flCoordXInic
            flCoordYFim = (9 * intCellheight).toFloat()
            var flLargPincel = flPincelFino
            if (intCol % 3 == 0) {
                flLargPincel = flPincelGrosso
                if (flCoordXInic > 0) flCoordXInic--
                if (flCoordXFim > 0) flCoordXFim--
            }

            pincelDesenhar.strokeWidth = flLargPincel
            //--------------------------------------------------------------------------------------
            canvasMyImage!!.drawLine(
                flCoordXInic, flCoordYInic, flCoordXFim, flCoordYFim,
                pincelDesenhar
            )
            //--------------------------------------------------------------------------------------
        }

    }

    //--- mostraNumsIguais (canvas do Sudoku board)
    private fun mostraNumsIguais(intNum: Int) {

        //--- Atualiza a imageView do layout
        //----------------------------------------------
        copiaBmpByBuffer(bmpJogo, bmpMyImage)
        //----------------------------------------------
        iViewSudokuBoard!!.setImageBitmap(bmpMyImage)
        //----------------------------------------------
        for (intLin in 0..8) {
            for (intColuna in 0..8) {
                if (arArIntNums[intLin][intColuna] == intNum) {

                    //--- Pinta a célula
                    //------------------------------------------------
                    pintaCelula(intLin, intColuna, pincelPurple200)
                    //------------------------------------------------

                    //--- Escreve o número na célula
                    pincelBranco.textSize = intTamTxt * scale
                    //------------------------------------------------------------------
                    escreveCelula(intLin, intColuna, intNum.toString(), pincelBranco)
                    //------------------------------------------------------------------
                }
            }
        }

        //--- Atualiza a imageView do layout
        //-------------------------------------------
        iViewSudokuBoard!!.setImageBitmap(bmpMyImage)
        //-------------------------------------------

    }

    //--- mostraCelAJogar
    private fun mostraCelAJogar(intLin: Int, intColuna: Int) {

        //--- Atualiza a imageView do layout
        //--------------------------------------
        copiaBmpByBuffer(bmpJogo, bmpMyImage)
        //----------------------------------------------
        iViewSudokuBoard!!.setImageBitmap(bmpMyImage)
        //----------------------------------------------

        //--- Pintura da celula
        //----------------------------------------------
        pintaCelula(intLin, intColuna, pincelLaranja)
        //----------------------------------------------

        //--- Atualiza a imageView do layout
        //----------------------------------------------
        iViewSudokuBoard!!.setImageBitmap(bmpMyImage)
        //----------------------------------------------

    }

    //----------------------------------------------------------------------------------------------
    // Gráficas - numDisp
    //----------------------------------------------------------------------------------------------
    //--- atualiza numDisp (canvasNumDisp)
    private fun atualizaNumDisp() {

        val intOffSet = 3

        //--- Pinta as células
        for (intIdxCel in 0..8) {

            //--- Coordenadas da célula
            //- Canto superior esquerdo do quadrado
            val intXSupEsq = intIdxCel * intCellwidth + intOffSet
            //- Canto inferior direito do quadrado
            val intXInfDir = intXSupEsq + intCellwidth - intOffSet
            val intYInfDir = intCellheight - intOffSet
            //----------------------------------------
            val intQtidd = arIntNumsDisp[intIdxCel]
            //----------------------------------------
            val pincelPintar = if (intQtidd > 0) pincelVerde else pincelBranco
            try {
                //-----------------------------------------------------------------------------------
                canvasNumDisp!!.drawRect(
                    intXSupEsq.toFloat(),
                    intOffSet.toFloat(),
                    intXInfDir.toFloat(),
                    intYInfDir.toFloat(),
                    pincelPintar
                )
                //-----------------------------------------------------------------------------------
            } catch (exc: Exception) {
                Log.d(cTAG, "Erro: " + exc.message)
            }

            //--- Desenha as linhas de separação
            val xSupDir = intXInfDir.toFloat()
            val ySupDir = intOffSet.toFloat()
            pincelPreto.strokeWidth = 5.toFloat()
            //----------------------------------------------------------------------------------
            canvasNumDisp!!.drawLine(
                xSupDir, ySupDir, intXInfDir.toFloat(), intYInfDir.toFloat(),
                pincelPreto
            )
            //----------------------------------------------------------------------------------

            //--- Escreve os números ainda válidos para o jogo
            if (intQtidd > 0) {

                //--- Escreve o número na célula
                val yCoord = intCellheight * 3 / 4
                val xCoord = intCellwidth / 3 + intIdxCel * intCellwidth

                val strTxt = (intIdxCel + 1).toString()
                pincelBranco.textSize = intTamTxt * scale
                //----------------------------------------------------------------------------------
                canvasNumDisp!!.drawText(strTxt, xCoord.toFloat(), yCoord.toFloat(), pincelBranco)
                //----------------------------------------------------------------------------------

            }
        }

        //--- Atualiza a imageView do layout
        //--------------------------------------------
        iViewNumsDisps!!.setImageBitmap(bmpNumDisp)
        //--------------------------------------------

    }

    //----------------------------------------------------------------------------------------------
    // Gráficas - funções comuns aos canvas
    //----------------------------------------------------------------------------------------------
    //--- pintaCelula no canvasMyImage
    private fun pintaCelula(intLinha: Int, intCol: Int, pincelPintar: Paint?) {

        //- Canto superior esquerdo do quadrado
        val intXSupEsq = intCol * intCellwidth
        val intYSupEsq = intLinha * intCellheight
        //- Canto inferior direito do quadrado
        val intXInfDir = intXSupEsq + intCellwidth
        val intYInfDir = intYSupEsq + intCellheight
        //-------------------------------------------------------------------------------
        canvasMyImage!!.drawRect(
            intXSupEsq.toFloat(),
            intYSupEsq.toFloat(),
            intXInfDir.toFloat(),
            intYInfDir.toFloat(),
            pincelPintar!!
        )
        //-------------------------------------------------------------------------------
        desenhaSudokuBoard(false)
        //-----------------------------------

    }

    //--- escreveCelula no canvasMyImage
    private fun escreveCelula(yCell: Int, xCell: Int, strTxt: String, pincel: Paint?) {

        //--- Coordenada Y (linhas)
        //------------------------------------------------------------------
        val yCoord = intCellheight * 3 / 4 + yCell * intCellheight
        //------------------------------------------------------------------

        //--- Coordenada X (colunas)
        //----------------------------------------------------------
        val xCoord = intCellwidth / 3 + xCell * intCellwidth
        //----------------------------------------------------------
        //strLog = "-> Coordenadas (Col = " + xCell + ", Linha: " + yCell + ") : (" + xCoord +
        //        ", " + yCoord + ") = " + strTxt
        //Log.d(cTAG, strLog)

        //-------------------------------------------------------------------------------
        canvasMyImage!!.drawText(strTxt, xCoord.toFloat(), yCoord.toFloat(), pincel!!)
        //-------------------------------------------------------------------------------

        //--- Atualiza a imageView do layout
        //----------------------------------------------
        iViewSudokuBoard!!.setImageBitmap(bmpMyImage)
        //----------------------------------------------

    }

    //--- inicializaObjGraficos
    @RequiresApi(Build.VERSION_CODES.R)
    private fun inicializaObjGraficos() {

        // Grandezas gráficas
        scale = resources.displayMetrics.density

        // Pincéis
        // ContextCompat.getColor(context, R.color.your_color);
        pincelVerde.color =
            ContextCompat.getColor(
                this,
                R.color.verde
            )       // resources.getColor(R.color.verde)
        pincelBranco.color =
            ContextCompat.getColor(
                this,
                R.color.white
            )       // resources.getColor(R.color.white)
        pincelPreto.color =
            ContextCompat.getColor(
                this,
                R.color.black
            )       // resources.getColor(R.color.black)
        pincelAzul.color =
            ContextCompat.getColor(
                this,
                R.color.azul
            )        // resources.getColor(R.color.azul)
        pincelLaranja.color =
            ContextCompat.getColor(
                this,
                R.color.laranja
            )     // resources.getColor(R.color.laranja)
        pincelPurple200.color = ContextCompat.getColor(
            this,
            R.color.purple_200
        )  // resources.getColor(R.color.purple_200)

        // Bit maps
        intImageResource = R.drawable.sudoku_board3
        bmpMyImage = BitmapFactory.decodeResource(resources, intImageResource)
            .copy(Bitmap.Config.ARGB_8888, true)
        bmpJogo = BitmapFactory.decodeResource(resources, intImageResource)
            .copy(Bitmap.Config.ARGB_8888, true)
        bmpInic = BitmapFactory.decodeResource(resources, intImageResource)
            .copy(Bitmap.Config.ARGB_8888, true)
        bmpSudokuBoard = BitmapFactory.decodeResource(resources, intImageResource)
            .copy(Bitmap.Config.ARGB_8888, true)

        bmpNumDisp = BitmapFactory.decodeResource(resources, R.drawable.quadro_nums_disp)
            .copy(Bitmap.Config.ARGB_8888, true)

        // Canvas
        canvasMyImage = Canvas(bmpMyImage!!)
        canvasSudokuBoard = Canvas(bmpSudokuBoard!!)

        canvasNumDisp = Canvas(bmpNumDisp!!)

        //------------------------------------------------------------------------------------------
        // Images Views
        //------------------------------------------------------------------------------------------
        iViewSudokuBoard = findViewById<View>(R.id.ivSudokuBoard) as ImageView
        iViewNumsDisps = findViewById<View>(R.id.ivNumDisp) as ImageView

        //-----------------------------
        determinaGrandezasGraficas()
        //-----------------------------
    }

    //--- apresentaGrandezasGraficas
    @RequiresApi(Build.VERSION_CODES.R)
    private fun determinaGrandezasGraficas() {

        //--- Apresenta as principais grandezas gráficas
        //2021-11-29 17:03:42.169 D: -> Display: Largura: 720 pixels, Altura  : 1280 pixels
        //2021-11-29 17:03:42.171 D: -> Margens: Acima  :  20 pixels, Esquerda:    0 pixels
        //2021-11-29 17:03:42.172 D: -> Image  : Largura: 720 pixels, Altura  :  630 pixels
        //2021-11-29 17:03:42.172 D: -> Célula : Largura:  80 pixels, Altura  :   70 pixels
        try {

            //--- Dimensões do display do Samsung SM-G570M
            /* Código deprecated no Android 30 (R)
			Display display = getWindowManager().getDefaultDisplay();
			Point m_size    = new Point();
			display.getSize(m_size);
			int int_dyWidth  = m_size.x;
			int int_dyHeight = m_size.y; */

            //Log.d(cTAG, "-> Grandezas gráficas:")
            //val displayMetrics = this.resources.displayMetrics
            //val intDyWidth     = displayMetrics.heightPixels
            //val intDyHeight    = displayMetrics.widthPixels
            //strLog = "   -Display: Largura: " + intDyWidth  + " pixels, Altura  : " +
            //                                                               intDyHeight + " pixels"
            //Log.d(cTAG, strLog)

            //--- Margens do board
            intmargTopDp = resources.getDimension(R.dimen.MargemAcima).toInt()
            intMargleftdp = resources.getDimension(R.dimen.MargemEsquerda).toInt()
            intMargtoppx = toPixels2(this, intmargTopDp.toFloat())
            intMargleftpx = toPixels2(this, intMargleftdp.toFloat())
            //strLog = "   -Margens: Acima  :  " + intMargtoppx + " pixels, Esquerda:    " +
            //        intMargleftpx + " pixels"
            //Log.d(cTAG, strLog)

            //--- Imagem Sudoku board
            intImgwidth = bmpSudokuBoard!!.width
            intImgheight = bmpSudokuBoard!!.height
            //strLog = "   -Image  : Largura: " + intImgwidth + " pixels, Altura  :  " +
            //        intImgheight + " pixels"
            //Log.d(cTAG, strLog)

            //--- Células
            intCellwidth = intImgwidth / 9
            intCellheight = intImgheight / 9
            //strLog = "   -Célula : Largura:  " + intCellwidth + " pixels, Altura  :   " +
            //        intCellheight + " pixels"
            //Log.d(cTAG, strLog)

        } catch (exc: Exception) {
            val strErro = "Erro: " + exc.message
            Log.d(cTAG, strErro)
        }
    }

    //--- Converte um valor em dp para pixels
    // https://stackoverflow.com/questions/29664993/how-to-convert-dp-px-sp-among-each-other-especially-dp-and-sp/42108115#42108115
    private fun toPixels2(context: Context, dip: Float): Int {
        val r = context.resources
        val metrics = r.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics).toInt()
    }

    //----------------------------------------------------------------------------------------------
    // Funções para o jogo
    //----------------------------------------------------------------------------------------------
    //--- Determina a qual quadrado menor uma célula pertence
    fun determinaQm(linQM: Int, colQM: Int): Int {

        // Qm = (linha-mod(linha;3))+INT(col/3)  -> Excel/Algoritmos1
        return (linQM - linQM % 3 + colQM / 3)

    }

    //--- Calcula as linhas do QM para um Qm
    private fun calcLinsQM(quadMenor: Int): IntArray {

        // INT(EXT.TEXTO(F17;2;1)/3)*3
        val linInicQM = quadMenor / 3 * 3
        return intArrayOf(linInicQM, linInicQM + 1, linInicQM + 2)
    }

    //--- Calcula as colunas do QM para um Qm
    private fun calcColsQM(quadMenor: Int): IntArray {

        // EXT.TEXTO(F17;2;1)*3-INT(EXT.TEXTO(F17;2;1)/3)*9
        val colInicQM = quadMenor * 3 - quadMenor / 3 * 9
        return intArrayOf(colInicQM, colInicQM + 1, colInicQM + 2)
    }

    //--- verifValidade de um número do Qm para inserção no QM
    fun verifValidade(quadMenor: Int, linQM: Int, colQM: Int, numero: Int): Boolean {
        var flagNumeroOk = true

        //--- Calcula as linhas desse quadrado menor no QM
        //-----------------------------------------
        val linhasQM = calcLinsQM(quadMenor)
        //-----------------------------------------
        //--- Calcula as colunas desse quadrado menor no QM
        //---------------------------------------
        val colsQM = calcColsQM(quadMenor)
        //---------------------------------------

        /*
		//--- Converte a linha do numero gerado do Qm para a do QM
		int linQM = linhasQM[linhaQm];
		//--- Converte a coluna do numero gerado do Qm para a do QM
		int colQM = colsQM[colunaQm];
		*/

        //----------------------------------------------------
        // Verifica se número já existe no Qm da cel tocada
        //----------------------------------------------------
        for (intLinha in linhasQM[0]..linhasQM[2]) {
            for (intColuna in colsQM[0]..colsQM[2]) {
                if (intLinha != intLinJogar || intColuna != intColJogar) {
                    if (arArIntNums[intLinha][intColuna] == numero) {
                        flagNumeroOk = false
                        break
                    }
                }
            }
        }
        //--- Se existe, retorna para gerar um novo numero
        if (!flagNumeroOk) return false

        //--------------------------------------------
        // Verifica se número existe na LINHA do QM
        //--------------------------------------------
        var numeroQM: Int
        for (idxColQM in 0..8) {

            //--- Se a coluna NÃO pertence ao quadMenor atual, verifica se o numero já existe nas
            //    outras linhas do maior, para a mesma coluna.
            if (!colsQMcontem(idxColQM, colsQM)) {
                numeroQM = arArIntNums[linQM][idxColQM]
                if (numero == numeroQM) {
                    flagNumeroOk = false
                    break
                }
            }
        }

        //--- Se existe, retorna para gerar um novo numero
        if (!flagNumeroOk) return false

        //---------------------------------------------------------------------
        // Se não existe na linha, verifica se número existe na COLUNA do QM
        //---------------------------------------------------------------------
        for (idxLinQM in 0..8) {

            //--- Se a linha NÃO pertence ao quadMenor atual, verifica se o numero já existe nas
            //    outras linhas do quadMaior.
            if (!linsQMcontem(idxLinQM, linhasQM)) {
                numeroQM = arArIntNums[idxLinQM][colQM]
                if (numero == numeroQM) {
                    flagNumeroOk = false
                    break
                }
            }
        }
        return flagNumeroOk
    }

    //--- Verifica se a coluna pertence ao quadMenor
    private fun colsQMcontem(idxColQM: Int, colsQM: IntArray): Boolean {
        var flagExiste = false
        for (idxAr in colsQM.indices) {
            if (colsQM[idxAr] == idxColQM) {
                flagExiste = true
                break
            }
        }
        return flagExiste
    }

    //--- Verifica se a linha pertence ao quadMenor
    private fun linsQMcontem(idxLinQM: Int, linhasQM: IntArray): Boolean {
        var flagExiste = false
        for (idxAr in linhasQM.indices) {
            if (linhasQM[idxAr] == idxLinQM) {
                flagExiste = true
                break
            }
        }
        return flagExiste
    }

    //--- listarQM
    private fun listarQM(quadMaior: Array<Array<Int>>) {

        var strDados: String
        for (linha in 0..8) {

            strLog = "linha $linha : "
            strDados = ""
            for (coluna in 0..8) {

                val strTmp = "${quadMaior[linha][coluna]}" + if (coluna < 8) ", " else ""
                strDados += strTmp
                strLog += strTmp

            }
            Log.d(cTAG, strLog)

        }
    }

    //--- iniciaJogo
    private fun iniciaJogo() {

        //*** Nesse ponto, arArIntNums (rascunho do jogo) e arArIntGab (gabarito) deverão estar Ok.

        intContaErro = 0
        tvErros!!.text = "$intContaErro"

        Log.d(cTAG, "-> Jogo:")
        //----------------------
        listarQM(arArIntNums)
        //----------------------
        Log.d(cTAG, "-> Gabarito:")
        //---------------------
        listarQM(arArIntGab)
        //---------------------

        //--- Desenha o SudokuBoard
        //----------------------------------
        desenhaSudokuBoard(true)
        //----------------------------------
        preencheJogo()
        //---------------

        iViewSudokuBoard!!.setImageBitmap(bmpMyImage)

        //--- Apresenta o jogo preparado e prepara o array dos números disponíveis
        flagJoga = false

        //---------------
        preencheJogo()
        //---------------

        iViewSudokuBoard!!.isEnabled = false

        //--- Apresenta os números disponíveis
        iViewNumsDisps!!.isEnabled = false

        Log.d(cTAG, "-> Array qtidd de jogos disponível por número:")
        for (intIdxNum in 0..8) {

            val intQtiJogos = arIntNumsDisp[intIdxNum]
            Log.d(cTAG, String.format("   %s %d %d", "Num", intIdxNum + 1, intQtiJogos))

        }

        //------------------
        atualizaNumDisp()
        //------------------

        //--- Inicializa variáveis locais
        tvNivel!!.text = strNivelJogo
        tvSubNivel!!.text = strSubNivelJogo
        tvClues!!.text = quantZeros(arArIntNums).toString()

        //--- Verifica se fim de jogo
        var flagContJogo = false
        for (idxVetorNumDisp in 0..8) {

            if (arIntNumsDisp[idxVetorNumDisp] > 0) flagContJogo = true

        }
        //--- Se já foram utilizados todos os números disponíveis, pára o cronometro
        if (!flagContJogo) {

            Log.d(cTAG, "-> ${crono.text} - Fim")

            crono.stop()
            flagJoga = false

        }
    }

    //--- PreencheJogo
    private fun preencheJogo() {

        arIntNumsDisp = Array(9) { 9 }     // intArrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9)

        for (intLinha in 0..8) {

            for (intCol in 0..8) {

                //-------------------------------------------
                val intNum = arArIntNums[intLinha][intCol]
                //-------------------------------------------
                if (intNum > 0) {

                    val strTexto = intNum.toString()
                    pincelAzul.textSize = intTamTxt * scale // 50 // 200
                    //------------------------------------------------------
                    escreveCelula(intLinha, intCol, strTexto, pincelAzul) //, canvasMyImage!!)
                    //------------------------------------------------------
                    arIntNumsDisp[intNum - 1]--

                }
            }
        }
        iViewSudokuBoard!!.setImageBitmap(bmpMyImage)
        //------------------------------------
        copiaBmpByBuffer(bmpMyImage, bmpJogo)
        //------------------------------------

    }

    //--- preparaCrono
    private fun preparaCrono(crono: Chronometer) {

        // set color and size of the text
        crono.setTextColor(Color.BLUE)
        crono.setTextSize(TypedValue.COMPLEX_UNIT_IN, 0.15f)

        // Adiciona o cronometro no layout
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        //layoutParams.setMargins(30, 40, 120, 40)
        crono.layoutParams = layoutParams

        val linearLayout = findViewById<LinearLayout>(R.id.crono_layout)
        linearLayout?.addView(crono)

        timeStopped = 0L
        //--------------------------
        crono.text = strCronoInic
        //--------------------------

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
                arArIntCopia[intLin][intCol] = arArIntPreset[intLin][intCol]
            }
        }

        return arArIntTmp

    }

    //--- quantZeros
    private fun quantZeros(arArIntJogo: Array<Array<Int>>): Int {

        var intQtiZeros = 0
        for (idxLin in 0..8) {
            for (idxCol in 0..8) {
                if (arArIntJogo[idxLin][idxCol] == 0) intQtiZeros++
            }
        }
        Log.d(cTAG, "-> Quantidade de Zeros: $intQtiZeros")

        return intQtiZeros

    }

    //--- salvaJogo
    @RequiresApi(Build.VERSION_CODES.O)
    private fun salvaJogo() {

        //----------------------------------------------------------------------
        // Prepara o conteúdo
        //----------------------------------------------------------------------
        //------------------------------------
        val strConteudo = preparaConteudo()
        //------------------------------------

        //----------------------------------------------------------------------
        // Define um nome para o arquivo
        //----------------------------------------------------------------------
        if (strConteudo.isNotEmpty()) {

            var intNumArq = 0
            //--------------------------------------------------------------------------------
            val arStrArqsNames = utils.listaExtMemArqDir("/Download/sudoku/Jogos")
            //--------------------------------------------------------------------------------
            if (arStrArqsNames.isNotEmpty()) {

                for (strArqName in arStrArqsNames) {

                    if (strArqName.contains("jogo_")) {

                        var intNumJogo: Int
                        try {
                            intNumJogo = strArqName.substring(
                                5,
                                strArqName.indexOf('.')
                            ).toInt()
                            if (intNumJogo > intNumArq) intNumArq = intNumJogo

                        } catch (exc: Exception) {
                        }
                    }

                }

            }
            intNumArq++
            val strArqJogo = "jogo_$intNumArq.xml"
            val strArqName = "/sudoku/jogos/$strArqJogo"

            //------------------------------------------------------------------
            // Salva o arquivo
            //------------------------------------------------------------------
            //------------------------------------------------------------------
            val flagEscrita = utils.escExtMemTextFile(strArqName, strConteudo)
            //------------------------------------------------------------------

            strToast = "Escrita arquivo $strArqJogo "
            strToast += if (flagEscrita) "OK!" else "NÃO ok!"
            Toast.makeText(this, strToast, Toast.LENGTH_LONG).show()

            strLog = "-> Escrita arquivo storage/emulated/0/Download$strArqName "
            strLog += if (flagEscrita) "OK!" else "NÃO ok!"
            Log.d(cTAG, strLog)

        }

    }

    //--- preparaConteudo
    var strModelo  : String = ""
    var strTag     : String = ""
    var intIdxInic : Int = 0
    var intIdxFim  : Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    private fun preparaConteudo() : String {

        var strConteudo : String

        //-- Lê o modelo
        val strNomeComPath = "sudoku/docs/modeloArqXmlSudoku1.txt"
        //-------------------------------------------------------------------------------
        val arStrLeitArq: ArrayList<String> = utils.leitExtMemTextFile(strNomeComPath)
        //-------------------------------------------------------------------------------

        //-- Converte o modelo de ArrayList para String
        Log.d(cTAG, "-> modelo arq Sudoku xml")
        for (idxDecl in 0 until arStrLeitArq.size) { strModelo += arStrLeitArq[idxDecl] }
        Log.d(cTAG, strModelo)

        //-- Preenche os campos
        try
        {
            //- header / id
            //-------------------------------------------------------------
            strConteudo = preencheConteudo("<id>", "3")
            //-------------------------------------------------------------

            //- header / nivel
            //---------------------------------------------------------------
            strConteudo += preencheConteudo("<nivel>", strNivelJogo)
            //---------------------------------------------------------------

            //- header / subnivel
            //---------------------------------------------------------------------
            strConteudo += preencheConteudo("<subnivel>", strSubNivelJogo)
            //---------------------------------------------------------------------

            //- body / linha0 até linha8 : jogo preparado
            for (idxLinha in 0 until 9) {

                var strConteudoTmp = ""

                for (idxCol in 0 until 9) {

                    strConteudoTmp += arArIntCopia[idxLinha][idxCol].toString()
                    if (idxCol < 8) strConteudoTmp += ", "

                }

                //--------------------------------------------------------------------------
                strConteudo += preencheConteudo("<linha$idxLinha>", strConteudoTmp)
                //--------------------------------------------------------------------------

            }

            //- jogos / id
            //----------------------------------------------------------------
            strConteudo += preencheConteudo("<id>", "1")
            //----------------------------------------------------------------

            //- jogos / dataHora
            //-------------------------------------------------------------------------
            val strDataHora = utils.LeDataHora("dd/MM/yyyy HH:mm:ss")
            //-------------------------------------------------------------------------
            //------------------------------------------------------------------
            strConteudo += preencheConteudo("<dataHora>", strDataHora)
            //------------------------------------------------------------------

            //- jogos / tempoJogo
            //--------------------------------------------------------------------------
            strConteudo += preencheConteudo("<tempoJogo>", crono.text.toString())
            //--------------------------------------------------------------------------

            //- jogos / erros
            //----------------------------------------------------------------------------
            strConteudo += preencheConteudo("<erros>", tvErros!!.text.toString())
            //----------------------------------------------------------------------------

            //- jogos / status
            val strStatus = if (quantZeros(arArIntNums) == 0) "finalizado" else "ativo"
            //-------------------------------------------------------------
            strConteudo += preencheConteudo("<status>", strStatus)
            //-------------------------------------------------------------

            //- body2 / linha0 até linha8 : jogo no momento do salvamento se ainda ativo
            if (strStatus == "ativo") {

                strConteudo += "</status></jogos><body2>"
                var strConteudoTmp = ""
                for (idxLinha in 0 until 9) {

                    strConteudoTmp += "<linha$idxLinha>"
                    for (idxCol in 0 until 9) {

                        strConteudoTmp += arArIntNums[idxLinha][idxCol].toString()
                        if (idxCol < 8) strConteudoTmp += ", "

                    }
                    strConteudoTmp += "</linha$idxLinha>"
                }
                strConteudo += strConteudoTmp
                strConteudo += "</body2></presets>"

            }

            else {

                //-- Finaliza a preparação do conteúdo
                strConteudo += strModelo.substring(intIdxFim, strModelo.length)

            }
        }
        catch (exc : Exception)
        {

            Toast.makeText(this, "Erro: ${exc.message}", Toast.LENGTH_LONG).show()
            strConteudo = ""

        }

        Log.d(cTAG, "-> Conteudo: $strConteudo")

        return strConteudo

    }

    //--- preencheConteudo
    private fun preencheConteudo(strTag : String, strConteudoTag : String) : String {

        var strConteudoPreenchido = ""

        intIdxInic = intIdxFim
        intIdxFim  = strModelo.indexOf(strTag, intIdxInic, false)
        intIdxFim += strTag.length

        strConteudoPreenchido += strModelo.substring(intIdxInic, intIdxFim)
        strConteudoPreenchido += strConteudoTag

        return strConteudoPreenchido

    }
}
