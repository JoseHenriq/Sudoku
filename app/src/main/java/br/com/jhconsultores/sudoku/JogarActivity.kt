package br.com.jhconsultores.sudoku

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.lang.Exception
import java.nio.IntBuffer

class JogarActivity : Activity() {

    //----------------------------------------------------------------------------------------------
    //                        Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private var cTAG   = "Sudoku"
    private var strLog = ""

    private var sdkGameGen = SudokuGameGenerator ()

    private var intImageResource = 0
    private var bmpInic: Bitmap? = null // Board vazio Lido a partir do resource/drawable
    private var bmpJogo: Bitmap? = null // Preenchido com o preset ou jogo gerado ANTES dos novos números

    private var myImage        : Bitmap? = null // Preenchido com o preset ou jogo gerado e novos números
    private var bmpNumDisp     : Bitmap? = null
    private var bmpSudokuBoard : Bitmap? = null

    private var canvas           : Canvas? = null
    private var canvasNumDisp    : Canvas? = null
    private var canvasSudokuBoard: Canvas? = null

    private var iViewSudokuBoard: ImageView? = null
    private var iViewNumsDisps  : ImageView? = null

    private var tvNivel : TextView? = null

    private var intTamTxt = 25 // 50 // 200 //
    private var scale     = 0f

    private var pincelVerde   = Paint()
    private var pincelBranco  = Paint()
    private var pincelPreto   = Paint()
    private var pincelAzul    = Paint()
    private var pincelLaranja = Paint()
    private var pincelPurple200 = Paint()

    //--- Medidas
    //- Células do board
    private var intCellwidth  = 0
    private var intCellheight = 0

    //- Margens do board
    private var intmargTopDp  = 0
    private var intMargleftdp = 0
    private var intMargtoppx  = 0
    private var intMargleftpx = 0

    //- Imagem Sudoku board
    private var intImgwidth  = 0
    private var intImgheight = 0

    //--- Para o rotate
    private var intGraus = 0

    //--- Controle de jogadas
    private var intColJogar   = 0
    private var intLinJogar   = 0
    private var flagJoga      = false

    private var arIntNumsDisp = intArrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9)
    private var arArIntGab    = Array(9) { Array(9) {0} }

    private var idxPreset = 0
    //--- Preset 1
    private var arArIntPreset1 = arrayOf(
        arrayOf (0, 0, 4, 6, 0, 5, 8, 0, 0),
        arrayOf (6, 5, 0, 0, 8, 0, 0, 0, 0),
        arrayOf (0, 0, 8, 0, 4, 7, 6, 0, 5),
        arrayOf (2, 8, 0, 3, 5, 6, 0, 0, 0),
        arrayOf (7, 4, 0, 0, 0, 8, 2, 5, 6),
        arrayOf (5, 6, 0, 4, 7, 2, 9, 0, 8),
        arrayOf (8, 2, 5, 7, 0, 4, 3, 6, 0),
        arrayOf (4, 3, 6, 5, 2, 0, 0, 8, 0),
        arrayOf (0, 0, 0, 8, 6, 3, 5, 4, 2))
    //--- Preset 2
    private var arArIntPreset2 = arrayOf(
        arrayOf (0, 6, 0, 7, 0, 8, 1, 9, 2),
        arrayOf (1, 0, 5, 2, 0, 0, 0, 0, 7),
        arrayOf (0, 2, 0, 0, 0, 6, 0, 0, 0),
        arrayOf (0, 5, 0, 9, 3, 0, 0, 4, 0),
        arrayOf (0, 0, 6, 5, 0, 2, 7, 8, 0),
        arrayOf (9, 7, 0, 0, 0, 0, 3, 2, 5),
        arrayOf (0, 0, 7, 4, 0, 0, 8, 0, 6),
        arrayOf (8, 9, 4, 0, 7, 0, 0, 0, 0),
        arrayOf (0, 1, 0, 3, 0, 0, 0, 7, 4))
    //--- Preset 3
    private var arArIntPreset3 = arrayOf(
        arrayOf (0, 0, 3, 5, 0, 0, 4, 9, 0),
        arrayOf (7, 6, 0, 0, 0, 0, 5, 0, 1),
        arrayOf (0, 5, 4, 0, 7, 3, 6, 0, 8),
        arrayOf (0, 1, 0, 0, 0, 0, 3, 0, 0),
        arrayOf (0, 0, 7, 2, 6, 1, 0, 0, 0),
        arrayOf (2, 0, 6, 0, 9, 0, 0, 1, 4),
        arrayOf (6, 3, 2, 8, 5, 0, 0, 0, 0),
        arrayOf (4, 0, 0, 0, 0, 2, 8, 0, 6),
        arrayOf (8, 0, 5, 0, 0, 7, 2, 0, 0))
    //--- Preset 4
    private var arArIntPreset4 = arrayOf(
        arrayOf (9, 0, 0, 8, 4, 1, 3, 0, 0),
        arrayOf (0, 0, 1, 9, 0, 0, 4, 2, 0),
        arrayOf (0, 0, 0, 2, 0, 0, 0, 1, 0),
        arrayOf (8, 7, 0, 1, 0, 0, 5, 4, 0),
        arrayOf (1, 5, 0, 3, 6, 0, 0, 0, 2),
        arrayOf (2, 0, 0, 0, 0, 0, 7, 6, 0),
        arrayOf (7, 2, 0, 0, 0, 5, 1, 9, 0),
        arrayOf (6, 3, 0, 0, 0, 0, 2, 0, 7),
        arrayOf (0, 1, 5, 7, 0, 2, 0, 0, 8))

    //--- Preset para teste1
    private var arArIntNums = arrayOf(
        arrayOf (0, 0, 4, 6, 0, 5, 8, 0, 0),
        arrayOf (6, 5, 0, 0, 8, 0, 0, 0, 0),
        arrayOf (0, 0, 8, 0, 4, 7, 6, 0, 5),
        arrayOf (2, 8, 0, 3, 5, 6, 0, 0, 0),
        arrayOf (7, 4, 0, 0, 0, 8, 2, 5, 6),
        arrayOf (5, 6, 0, 4, 7, 2, 9, 0, 8),
        arrayOf (8, 2, 5, 7, 0, 4, 3, 6, 0),
        arrayOf (4, 3, 6, 5, 2, 0, 0, 8, 0),
        arrayOf (0, 0, 0, 8, 6, 3, 5, 4, 2))

    //--- Preset para teste2
    /*
    val cinema = arrayOf(
        arrayOf(0, 0, 0, 0, 1),
        arrayOf(0, 0, 0, 1, 1),
        arrayOf(0, 0, 1, 1, 1),
        arrayOf(0, 0, 0, 1, 1),
        arrayOf(0, 0, 0, 0, 1))
     */

    /*
    private var arArIntTst = arrayOf (
        arrayOf ( 1, 6, 7, 9, 4, 3, 2, 5, 8 ),
        arrayOf ( 8, 9, 5, 2, 1, 6, 3, 7, 4 ),
        arrayOf ( 2, 4, 3, 7, 8, 5, 1, 9, 6 ),
        arrayOf ( 4, 1, 8, 6, 2, 9, 5, 3, 7 ),
        arrayOf ( 5, 3, 9, 8, 7, 4, 6, 1, 2 ),
        arrayOf ( 7, 2, 6, 5, 3, 1, 4, 8, 9 ),
        arrayOf ( 9, 7, 1, 4, 5, 2, 8, 6, 3 ),
        arrayOf ( 3, 8, 4, 1, 6, 7, 9, 2, 5 ),
        arrayOf ( 6, 5, 2, 3, 9, 8, 7, 4, 1 ))
    */

    private var arArIntCopia = Array(9) { Array(9) {0} }

    private var arIntNumsGab  = ArrayList<Int>()
    private var arIntNumsJogo = ArrayList<Int>()

    private var action = "JogoGerado"

    //val sudoGameGen = SudokuGameGenerator ()

    //----------------------------------------------------------------------------------------------
    //                                     Eventos
    //----------------------------------------------------------------------------------------------
    //--- onCreate MainActivity
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jogar)

        //--- Instancia objetos locais para os objetos XML
        tvNivel       = findViewById<View>(R.id.tv_Nivel) as TextView

        val btnRotate = findViewById<View>(R.id.buttonRotate) as Button
        val btnDraw   = findViewById<View>(R.id.buttonDraw) as Button

        //------------------------------------------------------------------------------------------
        // Objetos gráficos
        //------------------------------------------------------------------------------------------
        // Grandezas gráficas
        scale = resources.displayMetrics.density

        // Pincéis
        // ContextCompat.getColor(context, R.color.your_color);
        pincelVerde.color =
            ContextCompat.getColor(this, R.color.verde)       // resources.getColor(R.color.verde)
        pincelBranco.color =
            ContextCompat.getColor(this, R.color.white)       // resources.getColor(R.color.white)
        pincelPreto.color =
            ContextCompat.getColor(this, R.color.black)       // resources.getColor(R.color.black)
        pincelAzul.color =
            ContextCompat.getColor(this, R.color.azul)        // resources.getColor(R.color.azul)
        pincelLaranja.color =
            ContextCompat.getColor(this, R.color.laranja)     // resources.getColor(R.color.laranja)
        pincelPurple200.color = ContextCompat.getColor(
            this,
            R.color.purple_200
        )  // resources.getColor(R.color.purple_200)

        // Bit maps
        intImageResource = R.drawable.sudoku_board_w360h315
        myImage = BitmapFactory.decodeResource(resources, intImageResource)
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
        canvas = Canvas(myImage!!)
        canvasSudokuBoard = Canvas(bmpSudokuBoard!!)
        canvasNumDisp = Canvas(bmpNumDisp!!)

        //------------------------------------------------------------------------------------------
        // Images Views
        //------------------------------------------------------------------------------------------
        iViewSudokuBoard = findViewById<View>(R.id.imageView1) as ImageView
        iViewNumsDisps   = findViewById<View>(R.id.imageView2) as ImageView

        //------------------------------
        determinaGrandezasGraficas()
        //------------------------------

        //------------------------------------------------------------------------------------------
        // Inicializa dados para deixar o jogo pronto
        //------------------------------------------------------------------------------------------
        //--- Recupera os dados recebidos via intent
        action = intent.action.toString()

        // Armazena o gabarito em um array<int>
        arIntNumsGab  = intent.getIntegerArrayListExtra("GabaritoDoJogo") as ArrayList<Int>
        arIntNumsJogo = intent.getIntegerArrayListExtra("JogoPreparado") as ArrayList<Int>

        // Gabarito inválido
        if (arIntNumsGab.size != 81 || arIntNumsJogo.size != 81) {

            Log.d(cTAG, "-> Erro: array(s) com menos numeros que o necessário (81)")

        }
        // Gabarito e jogo válido
        else {

            var flagJogoOk = true

            //--- "JogoGerado"
            if (action == "JogoGerado") {

                // Armazena o gabarito em um Array<Array<Int>> para processamento local
                for (intLinha in 0..8) {

                    for (intCol in 0..8) {

                        arArIntNums[intLinha][intCol] = arIntNumsJogo[intLinha * 9 + intCol]
                        arArIntGab[intLinha][intCol] = arIntNumsGab[intLinha * 9 + intCol]

                    }
                }
            }

            //--- "JogoAdaptado"
            else if (action == "JogoAdaptado") {

                idxPreset = intent.getIntExtra("intNumPreset", 0)

                if (++idxPreset > 4) idxPreset = 1

                when (idxPreset) {
                    //------------------------------------------------
                    1 -> arArIntNums = copiaArArInt(arArIntPreset1)
                    //------------------------------------------------
                    2 -> arArIntNums = copiaArArInt(arArIntPreset2)
                    //------------------------------------------------
                    3 -> arArIntNums = copiaArArInt(arArIntPreset3)
                    //------------------------------------------------
                    4 -> arArIntNums = copiaArArInt(arArIntPreset4)
                    //------------------------------------------------
                }

                //--- Gera o gabarito para o jogo sugerido (preset)
                arArIntGab = copiaArArInt(arArIntNums)
                //---------------------------------------------------------------
                flagJogoOk = SudokuBackTracking.solveSudoku(arArIntGab, 81)
                //---------------------------------------------------------------

                if (flagJogoOk) Log.d(cTAG, "-> Resolvido utilizando backTracking")
                tvNivel!!.text = "${SudokuBackTracking.intNumBackTracking}"

                if (arArIntNums.size == 81) {

                    for (idxLin in 0..8) {

                        for (idxCol in 0..8) {

                            arIntNumsGab[idxLin * 9 + idxCol] = arArIntNums[idxLin][idxCol]
                            arArIntGab[idxLin][idxCol] = arArIntNums[idxLin][idxCol]

                        }
                    }
                }
            } // Fim de JogoAdaptado

            //-----------------------------------------
            arArIntCopia = copiaArArInt(arArIntNums)
            //-----------------------------------------

            arIntNumsDisp = intArrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9)
            //-------------
            iniciaJogo()
            //-------------

        } // Fim de gabarito recebido via intent ok

        //------------------------------------------------------------------------------------------
        // Listeners para o evento onTouch dos ImageViews
        //------------------------------------------------------------------------------------------
        // SudokuBoard
        iViewSudokuBoard!!.setOnTouchListener { _, event -> //--- Coordenadas tocadas
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
            val imageX = x - viewCoords[0] // viewCoords[0] is the X coordinate
            val imageY = y - viewCoords[1] // viewCoords[1] is the y coordinate
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
                flagJoga = false
                intLinJogar = 0
                intColJogar = 0
                //-------------------------
                mostraNumsIguais(intNum)
                //-------------------------
            } else {
                flagJoga = true
                intLinJogar = intLinha
                intColJogar = intCol
                //------------------------------------------
                mostraCelAJogar(intLinJogar, intColJogar)
                //------------------------------------------
            }
            false
        }

        // Números Disponíveis para se colocar em jogo
        iViewNumsDisps!!.setOnTouchListener { _, event -> //--- Só transfere o número para o board se estiver jogando
            if (flagJoga) {

                //--- Coordenadas do numsDisps tocadas
                val x = event.x.toInt()
                //					int y = (int) event.getY();

                //--- Coordenadas e valor da célula tocada
                val intCol = x / intCellwidth
                //					int intLinha = y / int_CellHeight;
                val intNum = intCol + 1
                val intQtidd = arIntNumsDisp[intNum - 1]
                //strLog = "-> Celula tocada em NumDisp: coluna = " + intCol +
                //        ", qtidd = " + intQtidd
                //Log.d(cTAG, strLog)

                //--- Verifica se ainda tem desse número para jogar e se esse número é válido.
                val flagNumValido : Boolean
                if (intQtidd > 0) {

                    //--- Verifica se num válido
                    // Determina a que Qm o numero pertence
                    //----------------------------------------------------------
                    val intQuadMenor = determinaQm(intLinJogar, intColJogar)
                    //----------------------------------------------------------
                    //Log.d(
                    //    cTAG, "-> linhaJogar= " + intLinJogar + " colJogar= " +
                    //            intColJogar + " Qm = " + intQuadMenor )

                    // Verifica se esse número ainda não existe no seu Qm e nem no seu QM
                    //-------------------------------------------------------------------------------
                    flagNumValido = verifValidade(intQuadMenor, intLinJogar, intColJogar, intNum)
                    //-------------------------------------------------------------------------------
                    if (flagNumValido) {
                        //Log.d(cTAG, "-> Número válido; será incluído no Sudoku board.")

                        //--- Atualiza o Sudoku board
                        //-------------------------------------------------------
                        pintaCelula(intLinJogar, intColJogar, pincelBranco)
                        //--------------------------------------------------------------------------
                        escreveCelula(intLinJogar, intColJogar, intNum.toString(), pincelAzul)
                        //--------------------------------------------------------------------------

                        //--------------------------------------------
                        desenhaSudokuBoard(false, canvas!!)
                        //--------------------------------------------

                        //--- Salva esse bitmap
                        //------------------------------------
                        copiaBmpByBuffer(myImage, bmpJogo)
                        //------------------------------------

                        //--- Atualiza a base de dados
                        arArIntNums[intLinJogar][intColJogar] = intNum
                        arIntNumsDisp[intNum - 1]--

                        //-------------------
                        atualizaNumDisp()
                        //-------------------

                        //--------------------------
                        mostraNumsIguais(intNum)
                        //--------------------------
                        flagJoga = false
                    }
                    //else {
                    //    Log.d(cTAG, "-> Número NÃO válido; NÃO será incluído no Sudoku board.")
                    //}
                }
            }
            false
        }

        //------------------------------------------------------------------------------------------
        // Listeners para o evento onClick dos buttons
        //------------------------------------------------------------------------------------------
        // Texto / Bit Map
        btnDraw.setOnClickListener {

            val strLegTexto  = resources.getString(R.string.texto)
            val strLegBitMap = resources.getString(R.string.bitmap)

            //--------------------------------------------------------------------------------------
            // Legenda do botão: Inicia
            //--------------------------------------------------------------------------------------
            if (btnDraw.text == strLegTexto) {

                iViewSudokuBoard!!.isEnabled = true
                iViewNumsDisps!!.isEnabled   = true

                //--- Prepara para voltar ao bitmap
                btnDraw.text = strLegBitMap

            }

            //--------------------------------------------------------------------------------------
            // Legenda do botão: Reset
            //--------------------------------------------------------------------------------------
            else {

                tvNivel!!.text = ""

                strLog = "-> Tap no btn \"Reset\" "
                Log.d(cTAG, strLog)

                //-----------------------------------------
                arArIntNums = copiaArArInt(arArIntCopia)
                //-----------------------------------------

                arIntNumsDisp = intArrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9)
                //-------------
                iniciaJogo()
                //-------------

                //--- Prepara para voltar ao texto
                btnDraw.text = strLegTexto

            }
        }

        // Rotate
        btnRotate.setOnClickListener {
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            intGraus += 30
            if (intGraus >= 360) intGraus = 0
            val fLargura = (iViewSudokuBoard!!.width shr 1).toFloat() // Divide por 2
            val fAltura = (iViewSudokuBoard!!.height shr 1).toFloat() // Divide por 2
            iViewSudokuBoard!!.pivotX = fLargura
            iViewSudokuBoard!!.pivotY = fAltura
            iViewSudokuBoard!!.rotation = intGraus.toFloat()
            //}
        }

    }

    //----------------------------------------------------------------------------------------------
    //                                     Funções
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    // Gráficas
    //----------------------------------------------------------------------------------------------
    //--- desenhaSudokuBoard
    private fun desenhaSudokuBoard(flagApaga: Boolean, canvasDes: Canvas) {
        var flCoordXInic : Float
        var flCoordYInic : Float
        var flCoordXFim  : Float
        var flCoordYFim  : Float
        val flPincelFino   = 3.toFloat()
        val flPincelGrosso = 7.toFloat()
        val pincelDesenhar = pincelAzul // pincelPreto;
        if (flagApaga) {

            //-----------------------------------------------------------------------------
            canvasDes.drawRect(
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
            canvasDes.drawLine(
                flCoordXInic, flCoordYInic, flCoordXFim, flCoordYFim,
                pincelDesenhar
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
            canvasDes.drawLine(
                flCoordXInic, flCoordYInic, flCoordXFim, flCoordYFim,
                pincelDesenhar
            )
            //--------------------------------------------------------------------------------------
        }

        //--------------------------------------------------
        //iView_SudokuBoard.setImageBitmap(bmpSudokuBoard);
        //--------------------------------------------------
    }

    //--- mostraNumsIguais no Sudoku board
    private fun mostraNumsIguais(intNum: Int) {

        //--- Atualiza a imageView do layout
        //myImage = bmpJogo;
        //------------------------------------
        copiaBmpByBuffer(bmpJogo, myImage)
        //-------------------------------------
        iViewSudokuBoard!!.setImageBitmap(myImage)
        //-------------------------------------
        for (intLin in 0..8) {
            for (intColuna in 0..8) {
                if (arArIntNums[intLin][intColuna] == intNum) {

                    //--- Pinta a célula
                    //--------------------------------------------------
                    pintaCelula(intLin, intColuna, pincelPurple200)
                    //--------------------------------------------------

                    //--- Escreve o número na célula
                    pincelBranco.textSize = intTamTxt * scale
                    //------------------------------------------------------------------------
                    escreveCelula(intLin, intColuna, intNum.toString(), pincelBranco)
                    //------------------------------------------------------------------------
                }
            }
        }

        //--- Atualiza a imageView do layout
        //-------------------------------------------
        iViewSudokuBoard!!.setImageBitmap(myImage)
        //-------------------------------------------
    }

    //--- mostraCelAJogar
    private fun mostraCelAJogar(intLin: Int, intColuna: Int) {

        //--- Atualiza a imageView do layout
        //myImage = bmpJogo;
        //------------------------------------
        copiaBmpByBuffer(bmpJogo, myImage)
        //------------------------------------
        iViewSudokuBoard!!.setImageBitmap(myImage)
        //-----------------------------------

        //--- Pintura da celula
        //-----------------------------------------------
        pintaCelula(intLin, intColuna, pincelLaranja)
        //-----------------------------------------------

        //--- Atualiza a imageView do layout
        //-------------------------------------
        iViewSudokuBoard!!.setImageBitmap(myImage)
        //-------------------------------------
    }

    //--- pintaCelula
    private fun pintaCelula(intLinha: Int, intCol: Int, pincelPintar: Paint?) {
        val intOffSetXSup: Int
        val intOffSetYSup: Int
        val intOffSetXInf: Int
        val intOffSetYInf: Int // = 0;

        when {
            intLinha < 3 -> {
                intOffSetXSup = 4
                intOffSetYSup = 4
                intOffSetXInf = 2
                intOffSetYInf = 2
            }
            intLinha < 6 -> {
                intOffSetXSup = 4
                intOffSetYSup = 4
                intOffSetXInf = 2
                intOffSetYInf = 2
            }
            else -> {
                intOffSetXSup = 4
                intOffSetYSup = 4
                intOffSetXInf = 1
                intOffSetYInf = 0
            }
        }

        //- Canto superior esquerdo do quadrado
        val intXSupEsq = intCol * intCellwidth + intOffSetXSup
        val intYSupEsq = intLinha * intCellheight + intOffSetYSup
        //- Canto inferior direito do quadrado
        val intXInfDir = intXSupEsq + intCellwidth - intOffSetXInf
        val intYInfDir = intYSupEsq + intCellheight - intOffSetYInf
        //-------------------------------------------------------------------------------
        canvas!!.drawRect(
            intXSupEsq.toFloat(),
            intYSupEsq.toFloat(),
            intXInfDir.toFloat(),
            intYInfDir.toFloat(),
            pincelPintar!!
        )
        //-------------------------------------------------------------------------------
    }

    //--- escreveCelula
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

        //------------------------------------------------------------------------
        canvas!!.drawText(strTxt, xCoord.toFloat(), yCoord.toFloat(), pincel!!)
        //------------------------------------------------------------------------

        //--- Atualiza a imageView do layout
        //-------------------------------------------
        iViewSudokuBoard!!.setImageBitmap(myImage)
        //-------------------------------------------

    }

    //--- atualiza numDisp
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
            //------------------------------------------
            val intQtidd   = arIntNumsDisp[intIdxCel]
            //------------------------------------------
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
    // Outras
    //----------------------------------------------------------------------------------------------
    //--- Prepara o jogo conforme Regras
    private fun preparaJogo() {

        //---------------------------------------
        // arArIntNums = copiaArArInt(arArIntTst)
        //---------------------------------------

        Log.d(cTAG, "-> Jogo antes da preparação:")
        //-----------------------
        listarQM (arArIntNums)
        //-----------------------

        //------------------------------------------------------------------------------------------
        // Regra1: todos os Qm devem conter pelo menos dois zeros
        //------------------------------------------------------------------------------------------
        for (quadMenor in 0..8) {

            val arLinsQM = calcLinsQM(quadMenor)
            val arColsQM = calcColsQM(quadMenor)

            val arIntCelQM = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            for (idxLin in 0..2) {
                for (idxCol in 0..2) {

                    arIntCelQM[idxLin * 3 + idxCol] =
                                                    arArIntNums[arLinsQM[idxLin]][arColsQM[idxCol]]
                }
            }
            var intQtiZeros = 0
            val arIntNumRnd = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
            var flagQmOk = false
            while (!flagQmOk) {

                var flagNumOk = false
                while (!flagNumOk) {

                    //--- Gera número aleatório sem repetição
                    //-----------------------------
                    val numRnd = (1..9).random()
                    //-----------------------------
                    if (arIntNumRnd[numRnd - 1] > 0) {

                        arIntNumRnd[numRnd - 1] = 0
                        arIntCelQM [numRnd - 1] = 0

                        flagNumOk = true
                        if (++intQtiZeros > 1) flagQmOk = true

                    }
                }
            }

            for (linMenor in 0..2) {

                for (colMenor in 0..2) {

                    val valCel = arIntCelQM[linMenor * 3 + colMenor]
                    arArIntNums[arLinsQM[linMenor]][arColsQM[colMenor]] = valCel

                }
            }
        }
        Log.d(cTAG, "-> Jogo após a preparação conforme a Regra1:")
        //-----------------------
        listarQM (arArIntNums)
        //-----------------------

        //------------------------------------------------------------------------------------------
        // Regra2: todas as linhas devem conter pelo menos dois zeros
        //------------------------------------------------------------------------------------------
        for (intLinha in 0..8) {

            val arIntNumRnd = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

            //--- Lê a linha toda e armazena-a num vetor
            val arIntCelLin = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            for (intCol in 0..8) {arIntCelLin[intCol] = arArIntNums[intLinha][intCol]}

            //--- Conta qtos zeros essa linha já tem
            var intQtiZeros = 0
            for (intCol in 0..8) { if (arIntCelLin[intCol] == 0) intQtiZeros++ }

            //--- Enqto NÃO tiver pelo menos 2 zeros na linha, gera um índice aleatório e se o vetor
            //    que controla os numRND tiver nesse índice valor diferente de zero, zera-o.
            while (intQtiZeros < 2) {

                var flagNumOk = false
                while (!flagNumOk) {

                    //--- Gera número aleatório sem repetição
                    //-----------------------------
                    val numRnd = (1..9).random()
                    //-----------------------------
                    if (arIntNumRnd[numRnd - 1] > 0) {

                        arIntNumRnd[numRnd - 1] = 0
                        if (arIntCelLin [numRnd - 1] > 0) { arIntCelLin [numRnd - 1] = 0 }

                        flagNumOk = true
                        intQtiZeros ++

                    }
                }
            }

            //--- Retorna as células à linha
            for (idxColQM in 0..8) { arArIntNums[intLinha][idxColQM] = arIntCelLin[idxColQM] }

        }
        Log.d(cTAG, "-> Jogo após a preparação conforme a Regra2:")
        //-----------------------
        listarQM (arArIntNums)
        //-----------------------

        //------------------------------------------------------------------------------------------
        // Regra3: todas as colunas devem conter pelo menos dois zeros
        //------------------------------------------------------------------------------------------
        for (intCol in 0..8) {

            //--- Vetor para evitar o uso de idx repetidos
            val arIntNumRnd = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

            //--- Lê a coluna toda e armazena-a num vetor
            val arIntCelCol = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            for (intLinha in 0..8) {arIntCelCol[intLinha] = arArIntNums[intLinha][intCol]}

            //--- Conta qtos zeros essa linha já tem
            var intQtiZeros = 0
            for (intLinha in 0..8) { if (arIntCelCol[intLinha] == 0) intQtiZeros++ }

            //--- Enqto NÃO tiver pelo menos 2 zeros na linha, gera um índice aleatório e se o vetor
            //    que controla os numRND tiver nesse índice valor diferente de zero, zera-o.
            while (intQtiZeros < 2) {

                var flagNumOk = false
                while (!flagNumOk) {

                    //--- Gera número aleatório sem repetição
                    //-----------------------------
                    val numRnd = (1..9).random()
                    //-----------------------------
                    if (arIntNumRnd[numRnd - 1] > 0) {

                        arIntNumRnd[numRnd - 1] = 0
                        if (arIntCelCol [numRnd - 1] > 0) { arIntCelCol [numRnd - 1] = 0 }

                        flagNumOk = true
                        intQtiZeros ++

                    }
                }
            }

            //--- Retorna as células à coluna
            for (idxLinhaQM in 0..8) { arArIntNums[idxLinhaQM][intCol] = arIntCelCol[idxLinhaQM] }

        }
        Log.d(cTAG, "-> Jogo após a preparação conforme a Regra3:")
        //-----------------------
        listarQM (arArIntNums)
        //-----------------------

        //------------------------------------------------------------------------------------------
        // Regra4: completa as casas com zero conforme o nível do jogo
        //------------------------------------------------------------------------------------------
        //--- Para a Regra4, determina a qtidd de Zeros no jogo
        //var intQtiZeros = 0
        //for (idxLin in 0..8) {
        //    for (idxCol in 0..8) { if (arArIntNums[idxLin][idxCol] == 0) intQtiZeros++ }
        //}
        //Log.d(cTAG, "-> Quantidade de Zeros após a Regra3: $intQtiZeros")

        //------------------------------------------
        var intQtiZeros = quantZeros(arArIntNums)
        //------------------------------------------

        /* Regra1 zera pelo menos 2 células para cada Qm;
           Regra2 zera pelo menos 2 células a cada linha e
           Regra3 zera pelo menos 2 células a cada coluna
           a qtidd de zeros totalizou 26. Ficou muito fácil a solução.

           Nos 4 presets-exemplos, a qtidd de zeros está entre 32 e 43
         */

        val intQtiMaxZeros = 40

        //--- Vetor para evitar repetição de números Rnd
        var arIntNumRnd = Array(81) { 0 }

        for (idxConta in 0..80) {
            arIntNumRnd [idxConta] = idxConta + 1
            //Log.d(cTAG, "   [$idxConta] = ${arIntNumRnd [idxConta]}")
        }
        while (intQtiZeros < intQtiMaxZeros) {

            //Log.d(cTAG, "-> intQtiZeros = $intQtiZeros intQtiMaxZeros = $intQtiMaxZeros")

            var flagNumOk = false
            while (!flagNumOk) {

                //--- Gera número aleatório sem repetição
                //------------------------------
                val numRnd = (0..81).random()
                //------------------------------

                //Log.d(cTAG, "-> numRnd = $numRnd arIntNumRnd[numRnd]=${arIntNumRnd[numRnd]}" )
                if (arIntNumRnd[numRnd] > 0) {

                    arIntNumRnd[numRnd] = 0

                    val intLinha  = numRnd / 9
                    val intColuna = numRnd % 9
                    if (arArIntNums[intLinha][intColuna] > 0) {

                        arArIntNums[intLinha][intColuna] = 0
                        intQtiZeros ++

                    }
                    //Log.d(cTAG, "-> linha = $intLinha coluna = $intColuna" )

                    flagNumOk = true

                }
            }
        }
        Log.d(cTAG, "-> Jogo após a preparação conforme a Regra4:")
        //-----------------------
        listarQM (arArIntNums)
        //------------------------
        quantZeros(arArIntNums)
        //------------------------

    }

    //--- Determina a qual quadrado menor uma célula pertence
    private fun determinaQm(linQM: Int, colQM: Int): Int {

        // Qm = (linha-mod(linha;3))+INT(col/3)  -> Excel/Algoritmos1
        return linQM - linQM % 3 + colQM / 3
    }

    //--- verifValidade de um número do Qm para inserção no QM
    private fun verifValidade(quadMenor: Int, linQM: Int, colQM: Int, numero: Int): Boolean {
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

    //--- copiaBmpByBuffer
    private fun copiaBmpByBuffer(bmpSrc: Bitmap?, bmpDest: Bitmap?) {
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
        val arArIntTmp = Array (9) {Array (9) { 0 } }
        //-------------------------------------------------------

        for (intLin in 0..8) {
            for (intCol in 0..8) {
                arArIntTmp  [intLin][intCol] = arArIntPreset[intLin][intCol]
                arArIntCopia[intLin][intCol] = arArIntPreset[intLin][intCol]
            }
        }

        return arArIntTmp

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
            val displayMetrics = this.resources.displayMetrics
            val intDyWidth     = displayMetrics.heightPixels
            val intDyHeight    = displayMetrics.widthPixels

            //strLog = "   -Display: Largura: " + intDyWidth  + " pixels, Altura  : " +
            //                                                               intDyHeight + " pixels"
            //Log.d(cTAG, strLog)

            //--- Margens do board
            intmargTopDp  = resources.getDimension(R.dimen.MargemAcima).toInt()
            intMargleftdp = resources.getDimension(R.dimen.MargemEsquerda).toInt()
            intMargtoppx  = toPixels2(this, intmargTopDp.toFloat())
            intMargleftpx = toPixels2(this, intMargleftdp.toFloat())
            //strLog = "   -Margens: Acima  :  " + intMargtoppx + " pixels, Esquerda:    " +
            //        intMargleftpx + " pixels"
            //Log.d(cTAG, strLog)

            //--- Imagem Sudoku board
            intImgwidth  = bmpSudokuBoard!!.width
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

    //--- listarQM
    private fun listarQM (quadMaior: Array<Array<Int>>) {

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

        }
    }

    //--- quantZeros
    private fun quantZeros(arArIntJogo : Array <Array <Int>>) : Int{

        var intQtiZeros = 0
        for (idxLin in 0..8) {
            for (idxCol in 0..8) {
                if (arArIntJogo[idxLin][idxCol] == 0) intQtiZeros++
            }
        }
        Log.d(cTAG, "-> Quantidade de Zeros: $intQtiZeros")

        return intQtiZeros

    }

    //--- iniciaJogo
    private fun iniciaJogo() {

        //*** Nesse ponto, arArIntNums (rascunho do jogo) e arArIntGab (gabarito) deverão estar Ok.
        Log.d(cTAG, "-> Inicia o jogo")
        Log.d(cTAG, "-> Jogo:")
        //----------------------
        listarQM(arArIntNums)
        //----------------------
        Log.d(cTAG, "-> Gabarito:")
        //---------------------
        listarQM(arArIntGab)
        //---------------------

        //--- Desenha o SudokuBoard
        //-------------------------------------------------------
        desenhaSudokuBoard(true, canvasSudokuBoard!!)
        //-------------------------------------------------------
        copiaBmpByBuffer(bmpSudokuBoard, myImage)
        copiaBmpByBuffer(bmpSudokuBoard, bmpInic)
        copiaBmpByBuffer(myImage, bmpJogo)

        iViewSudokuBoard!!.setImageBitmap(myImage)

        //--- Apresenta os números disponíveis
        flagJoga = false
        arIntNumsDisp = intArrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9)

        for (intLinha in 0..8) {

            for (intCol in 0..8) {

                //-------------------------------------------
                val intNum = arArIntNums[intLinha][intCol]
                //-------------------------------------------
                if (intNum > 0) {

                    val strTexto = intNum.toString()
                    pincelAzul.textSize = intTamTxt * scale // 50 // 200
                    //------------------------------------------------------
                    escreveCelula(intLinha, intCol, strTexto, pincelAzul)
                    //------------------------------------------------------
                    arIntNumsDisp[intNum - 1]--

                }
            }
        }
        iViewSudokuBoard!!.setImageBitmap(myImage)

        //------------------------------------
        copiaBmpByBuffer(myImage, bmpJogo)
        //------------------------------------

        iViewSudokuBoard!!.isEnabled = false
        iViewNumsDisps!!.isEnabled   = false

        Log.d(cTAG, "-> Array qtidd de jogos disponível por número:")
        for (intIdxNum in 0..8) {

            val intQtiJogos = arIntNumsDisp[intIdxNum]
            Log.d(cTAG, String.format("   %s %d %d", "Num", intIdxNum + 1, intQtiJogos))

        }

        //------------------
        atualizaNumDisp()
        //------------------

        //--- Inicializa variável local
        tvNivel!!.text = "${SudokuBackTracking.intNumBackTracking}"

    }

}
