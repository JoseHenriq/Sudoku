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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.lang.Exception
import java.nio.IntBuffer

class JogarActivity : Activity() {

    //----------------------------------------------------------------------------------------------
    //                        Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private var cTAG = "CANVAS3"
    private var strLog = ""

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

    private var intTamTxt = 25 // 50 // 200
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

    //--- Preset 1
    private var idxPreset = 0
    private var arArIntPreset1 = arrayOf(
        intArrayOf(0, 0, 4, 6, 0, 5, 8, 0, 0),
        intArrayOf(6, 5, 0, 0, 8, 0, 0, 0, 0),
        intArrayOf(0, 0, 8, 0, 4, 7, 6, 0, 5),
        intArrayOf(2, 8, 0, 3, 5, 6, 0, 0, 0),
        intArrayOf(7, 4, 0, 0, 0, 8, 2, 5, 6),
        intArrayOf(5, 6, 0, 4, 7, 2, 9, 0, 8),
        intArrayOf(8, 2, 5, 7, 0, 4, 3, 6, 0),
        intArrayOf(4, 3, 6, 5, 2, 0, 0, 8, 0),
        intArrayOf(0, 0, 0, 8, 6, 3, 5, 4, 2)
    ) // Linha 8
    private var arArIntPreset2 = arrayOf(
        intArrayOf(0, 6, 0, 7, 0, 8, 1, 9, 2),
        intArrayOf(1, 0, 5, 2, 0, 0, 0, 0, 7),
        intArrayOf(0, 2, 0, 0, 0, 6, 0, 0, 0),
        intArrayOf(0, 5, 0, 9, 3, 0, 0, 4, 0),
        intArrayOf(0, 0, 6, 5, 0, 2, 7, 8, 0),
        intArrayOf(9, 7, 0, 0, 0, 0, 3, 2, 5),
        intArrayOf(0, 0, 7, 4, 0, 0, 8, 0, 6),
        intArrayOf(8, 9, 4, 0, 7, 0, 0, 0, 0),
        intArrayOf(0, 1, 0, 3, 0, 0, 0, 7, 4)
    ) // Linha 8
    private var arArIntPreset3 = arrayOf(
        intArrayOf(0, 0, 3, 5, 0, 0, 4, 9, 0),
        intArrayOf(7, 6, 0, 0, 0, 0, 5, 0, 1),
        intArrayOf(0, 5, 4, 0, 7, 3, 6, 0, 8),
        intArrayOf(0, 1, 0, 0, 0, 0, 3, 0, 0),
        intArrayOf(0, 0, 7, 2, 6, 1, 0, 0, 0),
        intArrayOf(2, 0, 6, 0, 9, 0, 0, 1, 4),
        intArrayOf(6, 3, 2, 8, 5, 0, 0, 0, 0),
        intArrayOf(4, 0, 0, 0, 0, 2, 8, 0, 6),
        intArrayOf(8, 0, 5, 0, 0, 7, 2, 0, 0)
    ) // Linha 8
    private var arArIntPreset4 = arrayOf(
        intArrayOf(9, 0, 0, 8, 4, 1, 3, 0, 0),
        intArrayOf(0, 0, 1, 9, 0, 0, 4, 2, 0),
        intArrayOf(0, 0, 0, 2, 0, 0, 0, 1, 0),
        intArrayOf(8, 7, 0, 1, 0, 0, 5, 4, 0),
        intArrayOf(1, 5, 0, 3, 6, 0, 0, 0, 2),
        intArrayOf(2, 0, 0, 0, 0, 0, 7, 6, 0),
        intArrayOf(7, 2, 0, 0, 0, 5, 1, 9, 0),
        intArrayOf(6, 3, 0, 0, 0, 0, 2, 0, 7),
        intArrayOf(0, 1, 5, 7, 0, 2, 0, 0, 8)
    ) // Linha 8
    private var arArIntNums = arrayOf(
        intArrayOf(0, 0, 4, 6, 0, 5, 8, 0, 0),
        intArrayOf(6, 5, 0, 0, 8, 0, 0, 0, 0),
        intArrayOf(0, 0, 8, 0, 4, 7, 6, 0, 5),
        intArrayOf(2, 8, 0, 3, 5, 6, 0, 0, 0),
        intArrayOf(7, 4, 0, 0, 0, 8, 2, 5, 6),
        intArrayOf(5, 6, 0, 4, 7, 2, 9, 0, 8),
        intArrayOf(8, 2, 5, 7, 0, 4, 3, 6, 0),
        intArrayOf(4, 3, 6, 5, 2, 0, 0, 8, 0),
        intArrayOf(0, 0, 0, 8, 6, 3, 5, 4, 2)
    ) // Linha 8
    private var arArIntCopia = Array(9) { IntArray(9) }

    private var Action = "JogoGerado"

    //----------------------------------------------------------------------------------------------
    //                                     Eventos
    //----------------------------------------------------------------------------------------------
    //--- onCreate MainActivity
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jogar)

        //--- Recupera os dados recebidos via intent
        /*
        intent.putExtra ("intNumPreset", intJogoAdaptar)
        intent.setAction("JogoAdaptado")      ou intent.setAction("JogoGerado")
        intent.putIntegerArrayListExtra("GabaritoDoJogo", arIntNumsJogo)
         */

        Action = intent.action.toString()
        if (Action.equals("JogoAdaptado")) {
            idxPreset = intent.getIntExtra("intNumPreset", 0)
        }

        var arIntNumsGab = ArrayList <Int> ()
        arIntNumsGab     = intent.getIntegerArrayListExtra("GabaritoDoJogo") as ArrayList<Int>

        //--- Armazena o gabarito em um array<array<int>>
        if (Action.equals("JogoGerado")) {

            if (arIntNumsGab.size != 81) {

                Log.d(cTAG, "-> Erro: array com menos numeros que o necessário (81)")

            }
            else {

                for (intLinha in 0..8) {

                    for (intCol in 0..8) {

                        arArIntNums[intLinha][intCol] = arIntNumsGab[intLinha * 9 + intCol]

                    }
                }
            }
        }
        else if (Action.equals("JogoAdaptado")) {



        }
        else {


        }

        //------------------------------------------------------------------------------------------
        // Objetos gráficos
        //------------------------------------------------------------------------------------------
        // Grandezas gráficas
        scale = resources.displayMetrics.density

        // Pincéis
        // ContextCompat.getColor(context, R.color.your_color);
        pincelVerde.color     = ContextCompat.getColor(this, R.color.verde)       // resources.getColor(R.color.verde)
        pincelBranco.color    = ContextCompat.getColor(this, R.color.white)       // resources.getColor(R.color.white)
        pincelPreto.color     = ContextCompat.getColor(this, R.color.black)       // resources.getColor(R.color.black)
        pincelAzul.color      = ContextCompat.getColor(this, R.color.azul)        // resources.getColor(R.color.azul)
        pincelLaranja.color   = ContextCompat.getColor(this, R.color.laranja)     // resources.getColor(R.color.laranja)
        pincelPurple200.color = ContextCompat.getColor(this, R.color.purple_200)  // resources.getColor(R.color.purple_200)

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
        canvas            = Canvas(myImage!!)
        canvasSudokuBoard = Canvas(bmpSudokuBoard!!)
        canvasNumDisp     = Canvas(bmpNumDisp!!)

        //------------------------------------------------------------------------------------------
        // Images Views
        //------------------------------------------------------------------------------------------
        iViewSudokuBoard = findViewById<View>(R.id.imageView1) as ImageView
        iViewNumsDisps   = findViewById<View>(R.id.imageView2) as ImageView

        //------------------------------
        determinaGrandezasGraficas()
        //------------------------------

        //--- Desenha o SudokuBoard
        //------------------------------------------------------
        desenhaSudokuBoard(true, canvasSudokuBoard!!)
        //------------------------------------------------------
        copiaBmpByBuffer(bmpSudokuBoard, myImage)
        iViewSudokuBoard!!.setImageBitmap(myImage)

        //--- Listeners para o evento onTouch dos ImageViews
        // SudokuBoard
        iViewSudokuBoard!!.setOnTouchListener { _, event -> //--- Coordenadas tocadas
            val x = event.x.toInt()
            val y = event.y.toInt()
            Log.d(cTAG, "touched x: $x")
            Log.d(cTAG, "touched y: $y")

            //--- OffSets das coordenadas na Janela (???)
            val viewCoords = IntArray(2)
            iViewSudokuBoard!!.getLocationOnScreen(viewCoords)
            Log.d(cTAG, "viewCoord x: " + viewCoords[0])
            Log.d(cTAG, "viewCoord y: " + viewCoords[1])

            //--- Coordenadas reais (???)
            val imageX = x - viewCoords[0] // viewCoords[0] is the X coordinate
            val imageY = y - viewCoords[1] // viewCoords[1] is the y coordinate
            Log.d(cTAG, "Real x: $imageX")
            Log.d(cTAG, "Real y: $imageY")

            //--- Coordenadas da célula tocada
            val intCol = x / intCellwidth
            val intLinha = y / intCellheight
            //-------------------------------------------
            val intNum = arArIntNums[intLinha][intCol]
            //-------------------------------------------
            strLog = "-> Celula tocada: linha = " + intLinha + ", coluna = " + intCol +
                    ", numero = " + intNum
            Log.d(cTAG, strLog)

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
                strLog = "-> Celula tocada em NumDisp: coluna = " + intCol +
                        ", qtidd = " + intQtidd
                Log.d(cTAG, strLog)

                //--- Verifica se ainda tem desse número para jogar e se esse número é válido.
                val flagNumValido : Boolean
                if (intQtidd > 0) {

                    //--- Verifica se num válido
                    // Determina a que Qm o numero pertence
                    //----------------------------------------------------------
                    val intQuadMenor = determinaQm(intLinJogar, intColJogar)
                    //----------------------------------------------------------
                    Log.d(
                        cTAG, "-> linhaJogar= " + intLinJogar + " colJogar= " +
                                intColJogar + " Qm = " + intQuadMenor
                    )
                    // Verifica se esse número ainda não existe no seu Qm e nem no seu QM
                    //-------------------------------------------------------------------------------
                    flagNumValido = verifValidade(intQuadMenor, intLinJogar, intColJogar, intNum)
                    //-------------------------------------------------------------------------------
                    if (flagNumValido) {
                        Log.d(cTAG, "-> Número válido; será incluído no Sudoku board.")

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
                    } else {
                        Log.d(cTAG, "-> Número NÃO válido; NÃO será incluído no Sudoku board.")
                    }
                }
            }
            false
        }

        //------------------------------------------------------------------------------------------
        // Buttons
        //------------------------------------------------------------------------------------------
        //--- Instancia objetos locais para os objetos XML
        val btnRotate = findViewById<View>(R.id.buttonRotate) as Button
        val btnDraw = findViewById<View>(R.id.buttonDraw) as Button

        //--- Listeners para o evento onClick dos buttons
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

        // Texto / Bit Map
        btnDraw.setOnClickListener {

            val strLegTexto  = resources.getString(R.string.texto)
            val strLegBitMap = resources.getString(R.string.bitmap)

            //--------------------------------------------------------------------------------------
            // Legenda do botão: Inicia
            //--------------------------------------------------------------------------------------
            if (btnDraw.text == strLegTexto) {

                iViewSudokuBoard!!.setImageBitmap(bmpSudokuBoard)
                copiaBmpByBuffer(bmpSudokuBoard, myImage)
                copiaBmpByBuffer(myImage, bmpJogo)

                //--- Escreve um preset para adaptação de jogo no Sudoku board
                flagJoga = false
                arIntNumsDisp = intArrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9)

                if (Action.equals("JogoAdaptado")) {

                    if (++idxPreset > 4) idxPreset = 1

                    when (idxPreset) {
                        //--------------------------------------------
                        1 -> arArIntNums = copiaArArInt(arArIntPreset1)
                        //--------------------------------------------
                        2 -> arArIntNums = copiaArArInt(arArIntPreset2)
                        //--------------------------------------------
                        3 -> arArIntNums = copiaArArInt(arArIntPreset3)
                        //--------------------------------------------
                        4 -> arArIntNums = copiaArArInt(arArIntPreset4)
                        //--------------------------------------------
                    }
                }

                for (intLinha in 0..8) {
                    for (intCol in 0..8) {
                        //-------------------------------------------
                        val intNum = arArIntNums[intLinha][intCol]
                        //-------------------------------------------
                        if (intNum > 0) {
                            val strTexto = intNum.toString()
                            pincelAzul.textSize = intTamTxt * scale // 50 // 200
                            //-------------------------------------------------------
                            escreveCelula(intLinha, intCol, strTexto, pincelAzul)
                            //-------------------------------------------------------
                            arIntNumsDisp[intNum - 1]--
                        }
                    }
                }
                iViewSudokuBoard!!.setImageBitmap(myImage)
                Log.d(cTAG, "-> Array qtidd de jogos disponível por número:")
                for (intIdxNum in 0..8) {
                    val intQtiJogos = arIntNumsDisp[intIdxNum]
                    Log.d(cTAG, String.format("   %s %d %d", "Num", intIdxNum + 1, intQtiJogos))
                }

                //-------------------
                atualizaNumDisp()
                //-------------------
                try {

                    //bmpJogo = myImage;   //.copy(Bitmap.Config.ARGB_8888, false);
                    //------------------------------------
                    copiaBmpByBuffer(myImage, bmpJogo)
                    //------------------------------------
                } catch (exc: Exception) {
                    Log.d(cTAG, "Erro: " + exc.message)
                }

                //--- Canvas
                canvasNumDisp = Canvas(bmpNumDisp!!)
                for (intIndx in 0..8) {
                    pincelBranco.textSize = intTamTxt * scale // 50 // 200
                    //-------------------
                    atualizaNumDisp()
                    //-------------------
                }
                iViewNumsDisps!!.setImageBitmap(bmpNumDisp)

                //--- Prepara para voltar ao bitmap
                btnDraw.text = strLegBitMap

            }

            //--- Reset
            else {

                /*
				arArIntNums = new int [][]{

						// C0 C1 C2 C3 C4 C5 C6 C7 C8 <- colunas
						{0, 0, 4, 6, 0, 5, 8, 0, 0},    // Linha 0
						{6, 5, 0, 0, 8, 0, 0, 0, 0},    // Linha 1
						{0, 0, 8, 0, 4, 7, 6, 0, 5},    // Linha 2
						{2, 8, 0, 3, 5, 6, 0, 0, 0},    // Linha 3
						{7, 4, 0, 0, 0, 8, 2, 5, 6},    // Linha 4
						{5, 6, 0, 4, 7, 2, 9, 0, 8},    // Linha 5
						{8, 2, 5, 7, 0, 4, 3, 6, 0},    // Linha 6
						{4, 3, 6, 5, 2, 0, 0, 8, 0},    // Linha 7
						{0, 0, 0, 8, 6, 3, 5, 4, 2}};   // Linha 8
				 */

                //------------------------------------------
                arArIntNums = copiaArArInt(arArIntCopia)
                //------------------------------------------

                // Inicializa o board com os quadrados Sudoku
                copiaBmpByBuffer(bmpSudokuBoard, myImage)

                //--- Atualiza imageView do layout
                //-------------------------------------------
                iViewSudokuBoard!!.setImageBitmap(myImage)
                //-------------------------------------------

                //bmpInic = myImage; //.copy(Bitmap.Config.ARGB_8888, false);
                //------------------------------------
                copiaBmpByBuffer(myImage, bmpInic)
                //------------------------------------
                //bmpJogo = myImage; //.copy(Bitmap.Config.ARGB_8888, false);
                //------------------------------------
                copiaBmpByBuffer(myImage, bmpJogo)
                //------------------------------------

                //--- Atualiza imageView dos números disponíveis
                bmpNumDisp = BitmapFactory.decodeResource(resources, R.drawable.quadro_nums_disp)
                    .copy(Bitmap.Config.ARGB_8888, true)
                //--------------------------------------
                iViewNumsDisps!!.setImageBitmap(bmpNumDisp)
                //--------------------------------------
                arIntNumsDisp = intArrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9)

                //--- Prepara para voltar ao texto
                btnDraw.text = strLegTexto
            }
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
        //-------------------------------------
        iViewSudokuBoard!!.setImageBitmap(myImage)
        //-------------------------------------
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
        strLog = "-> Coordenadas (Col = " + xCell + ", Linha: " + yCell + ") : (" + xCoord +
                ", " + yCoord + ") = " + strTxt
        Log.d(cTAG, strLog)

        //-------------------------------------------------
        canvas!!.drawText(strTxt, xCoord.toFloat(), yCoord.toFloat(), pincel!!)
        //-------------------------------------------------

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
            val intQtidd = arIntNumsDisp[intIdxCel]
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
                //--- Escreve o valor na célula
                val strTxt = (intIdxCel + 1).toString()
                //--------------------------------------------------------------
                canvasNumDisp!!.drawText(strTxt, xCoord.toFloat(), yCoord.toFloat(), pincelBranco)
                //--------------------------------------------------------------
            }
        }

        //--- Atualiza a imageView do layout
        //--------------------------------------
        iViewNumsDisps!!.setImageBitmap(bmpNumDisp)
        //--------------------------------------
    }

    //----------------------------------------------------------------------------------------------
    // Outras
    //----------------------------------------------------------------------------------------------
    //--- Determina a qual quadrado menor as coordenadas da célula pertencem
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
    private fun copiaArArInt(arArIntPreset: Array<IntArray>): Array<IntArray> {
        val arArIntTmp = Array(9) { IntArray(9) }
        for (intLin in 0..8) {
            for (intCol in 0..8) {
                arArIntTmp[intLin][intCol] = arArIntPreset[intLin][intCol]
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

            val displayMetrics = this.resources.displayMetrics
            val intDyWidth    = displayMetrics.heightPixels
            val intDyHeight   = displayMetrics.widthPixels

            strLog = "-> Display: Largura: " + intDyWidth  + " pixels, Altura  : " +
                                                                           intDyHeight + " pixels"
            Log.d(cTAG, strLog)

            //--- Margens do board
            intmargTopDp  = resources.getDimension(R.dimen.MargemAcima).toInt()
            intMargleftdp = resources.getDimension(R.dimen.MargemEsquerda).toInt()
            intMargtoppx  = toPixels2(this, intmargTopDp.toFloat())
            intMargleftpx = toPixels2(this, intMargleftdp.toFloat())
            strLog = "-> Margens: Acima  :  " + intMargtoppx + " pixels, Esquerda:    " +
                    intMargleftpx + " pixels"
            Log.d(cTAG, strLog)

            //--- Imagem Sudoku board
            intImgwidth  = bmpSudokuBoard!!.width
            intImgheight = bmpSudokuBoard!!.height
            strLog = "-> Image  : Largura: " + intImgwidth + " pixels, Altura  :  " +
                    intImgheight + " pixels"
            Log.d(cTAG, strLog)

            //--- Células
            intCellwidth = intImgwidth / 9
            intCellheight = intImgheight / 9
            strLog = "-> Célula : Largura:  " + intCellwidth + " pixels, Altura  :   " +
                    intCellheight + " pixels"
            Log.d(cTAG, strLog)
        } catch (exc: Exception) {
            val strErro = "Erro: " + exc.message
            Log.d(cTAG, strErro)
        }
    }

    // Converte um valor de dp para pixels
    // https://stackoverflow.com/questions/29664993/how-to-convert-dp-px-sp-among-each-other-especially-dp-and-sp/42108115#42108115
    private fun toPixels2(context: Context, dip: Float): Int {
        val r = context.resources
        val metrics = r.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics).toInt()
    }
}