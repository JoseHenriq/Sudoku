package br.com.jhconsultores.sudoku.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log

import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import android.view.MotionEvent
import android.view.View.*
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import br.com.jhconsultores.sudoku.R
import br.com.jhconsultores.sudoku.jogo.SudokuGameGenerator
import br.com.jhconsultores.utils.*

@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {

    //----------------------------------------------------------------------------------------------
    //                         Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private val cTAG     = "Sudoku"
    private var strLog   = ""
    private var strToast = ""

    //--- Objetos gráficos
    private lateinit var ivSudokuBoardMain: ImageView
    private lateinit var ivNumDisp        : ImageView
    private lateinit var ivQtiNumDisp     : ImageView

    private var bmpMyImageInic: Bitmap? = null
    private var bmpMyImageBack: Bitmap? = null
    private var bmpMyImage    : Bitmap? = null

    private var bmpNumDisp: Bitmap? = null

    private var intCellwidth  = 0
    private var intCellheight = 0

    private var pincelVerde   = Paint()
    private var pincelBranco  = Paint()
    private var pincelPreto   = Paint()
    private var pincelAzul    = Paint()
    private var pincelLaranja = Paint()

    private var intTamTxt = 25
    private var scale     = 0f

    private var canvasMyImage: Canvas? = null
    private var canvasNumDisp: Canvas? = null

    //--- Textos
    private lateinit var tvContaNums : TextView
    private lateinit var tvContaClues: TextView

    //--- Botões principais
    private lateinit var btnGeraJogo  : Button
    private lateinit var btnAdaptaJogo: Button
    private lateinit var btnJogaJogo  : Button
    private lateinit var btnTestaRV   : Button

    //--- Radio Buttons
    private lateinit var groupRBnivel: RadioGroup
    private lateinit var rbFacil     : RadioButton
    private lateinit var rbMedio     : RadioButton
    private lateinit var rbDificil   : RadioButton
    private lateinit var rbMuitoDificil: RadioButton

    private lateinit var groupRBadapta: RadioGroup
    private lateinit var rbPreset: RadioButton
    private lateinit var rbEdicao: RadioButton

    private lateinit var progressBar: ProgressBar

    private lateinit var edtViewSubNivel: EditText
    private var flagAdaptaPreset = true

    //--- Objetos para o jogo
    private var quadMaior = arrayOf<Array<Int>>()

    private var strOpcaoJogo = "JogoGerado"
    private var strNivelJogo = "Fácil"
    private var nivelJogo    = 0
    private var subNivelJogo = 0
    private var nivelTotalJogo = 0

    // Núm     0   22               31        41          51       61     81
    // clues  81   59               50        40          30       20      0
    //        |----|----------------|---------|-----------|--------|-------|
    //        |xxxx|  MUITO DIFÍCIL | DIFÍCIL |   MÉDIO   |  FÁCIL |xxxxxxx|
    //        |----|----------------|---------|-----------|--------|-------|

    private val FACIL = 20
    private val MEDIO = 30
    private val DIFICIL       = 40
    private val MUITO_DIFICIL = 50

    private var quadMaiorAdapta = Array(9) { Array(9) { 0 } }
    private var arArIntNums     = Array(9) { Array(9) { 0 } }
    private var arIntQtiNumDisp = Array(9) { 9 }
    private val arIntNumsDisp   = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

    private lateinit var txtDadosJogo: TextView

    //--- Controle de jogadas
    private var intColJogar = 0
    private var intLinJogar = 0

    private var flagBoardSel = false

    //--- Arquivos jogos
    private var arStrTags   = Array(3) { "" }
    private var arArStrTags = Array(3) { Array(9) { "" } }

    //--- Classes externas
    private var sgg       = SudokuGameGenerator()
    private var jogarJogo = JogarActivity()
    private val utils     = Utils()
    private val utilsKt   = UtilsKt()

    companion object {

        const val cTAG         = "Sudoku"
        var flagJogoAdaptadoOk = false
        var flagJogoGeradoOk   = false

    }

    //----------------------------------------------------------------------------------------------
    // Eventos e listeners da MainActivity
    //----------------------------------------------------------------------------------------------
    //--- onCreate
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //--- Instancializações e inicializações
        tvContaNums  = findViewById(R.id.ContaNums)
        tvContaClues = findViewById(R.id.ContaClues)

        btnGeraJogo   = findViewById(R.id.btn_GerarJogo)
        btnAdaptaJogo = findViewById(R.id.btn_AdaptarJogo)
        btnJogaJogo   = findViewById(R.id.btn_JogarJogo)

        groupRBnivel   = findViewById(R.id.radioGrpNivel)
        rbFacil        = findViewById(R.id.nivelFacil)
        rbMedio        = findViewById(R.id.nivelMédio)
        rbDificil      = findViewById(R.id.nivelDifícil)
        rbMuitoDificil = findViewById(R.id.nivelMuitoDifícil)
        //-----------------------------
        prepRBniveis(true)
        //-----------------------------
        edtViewSubNivel = findViewById(R.id.edtViewSubNivel)

        groupRBadapta = findViewById(R.id.radioGrpAdapta)
        rbPreset      = findViewById(R.id.preset)
        rbEdicao      = findViewById(R.id.edicao)
        txtDadosJogo  = findViewById(R.id.txtJogos)

        groupRBadapta.visibility = INVISIBLE

        tvContaNums.text  =  "0"
        tvContaClues.text = getString(R.string.valor81)

        //--- Objetos gráficos
        //--------------------
        inicializaObjGraf()
        //--------------------

        //----------------------------------------------------------------------
        // Implementa o Progress Bar
        //----------------------------------------------------------------------
        progressBar = ProgressBar(this)
        progressBar.visibility = INVISIBLE

        //setting height and width of progressBar
        progressBar.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        //accessing our relative layout where the progressBar will add up
        val layout = findViewById<RelativeLayout>(R.id.relLayoutProgBar)
        // Add ProgressBar to our layout
        layout?.addView(progressBar)

        //------------------------------------------------
        // Listener para mudança do subnivel (editView)
        //------------------------------------------------
        //https://www.tutorialkart.com/kotlin-android/android-edittext-on-text-change/
        edtViewSubNivel.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                // tvSample.setText("Text in EditText : $s")

                if (s.isNotEmpty())
                //---------------------------
                    verifMudancaNivel(nivelJogo)
                //---------------------------

            }

        })

        //------------------------------------------------------------------------------------------
        // Listener para os eventos onTouch dos ImageViews (só utilizados na edição)
        //------------------------------------------------------------------------------------------
        ivSudokuBoardMain.setOnTouchListener { _, event -> //--- Coordenadas tocadas

            if (groupRBadapta.isVisible && rbEdicao.isChecked) {

                Log.d(cTAG, "-> SudokuBoard:")

                val x = event.x.toInt()
                val y = event.y.toInt()
                Log.d(cTAG, "touched x: $x")
                Log.d(cTAG, "touched y: $y")

                //-------------------------
                editaIVSudokuBoard(x, y)
                //-------------------------

            }

            false

        }

        //------------------------------------------------------------------------------------------
        // Listener para os eventos dos buttons: declarados em res/layout/activity_main.xml
        //------------------------------------------------------------------------------------------
        // fun btnGeraJogoClick  (view: View?) {}
        // fun btnAdaptaJogoClick(view: View?) {}
        // fun btnJogaJogoClick  (view: View?) {}

        // https://www.geeksforgeeks.org/add-ontouchlistener-to-imageview-to-perform-speech-to-text-in-android/
        ivNumDisp.setOnTouchListener { _, motionEvent ->

            Log.d(cTAG, "-> ivNumDisp:")

            when (motionEvent.action) {

                MotionEvent.ACTION_UP -> {

                    Log.d(cTAG, "   - ACTION_UP")

                }

                MotionEvent.ACTION_DOWN -> {

                    Log.d(cTAG, "   - ACTION_DOWN")
                    val x = motionEvent.x.toInt()
                    Log.d(cTAG, "   touched x: $x")

                    //-------------------
                    editaIVNumDisp(x)
                    //-------------------

                }

            }
            false
        }

        //-----------------------------------------------------------------------------------
        // Verifica se permitidos os acessos aos recursos do Android (AndroidManifest.xml)
        //-----------------------------------------------------------------------------------
        VerificaPermissoesAcessoAPI()
        //------------------------------

    }

    @SuppressLint("RestrictedApi")
    override fun onResume() {

        super.onResume()

        if (rbEdicao.isChecked) {
            txtDadosJogo.text = ""
        }

        //--- Ativa o progress bar
        progressBar.visibility = VISIBLE

        //--- Aguarda o teclado ser mostrado para poder escondê-lo
        val waitTime = 3000L  // milisegundos
        Handler(Looper.getMainLooper()).postDelayed(
            {

                //--- Desativa o progress bar
                progressBar.visibility = INVISIBLE

                //--- Esconde o teclado
                //----------------------------------
                utils.EscondeTeclado(this)
                //----------------------------------

            },
            waitTime )  // value in milliseconds

        //--- Torna inválidos os jogos gerado e adaptado
//        flagJogoGeradoOk   = false
        flagJogoAdaptadoOk = false

        groupRBadapta.visibility = INVISIBLE
        //-----------------------------
        prepRBniveis(true)
        //-----------------------------
        edtViewSubNivel.isEnabled = true

        //-----------------------------
        visibilidadeViews(INVISIBLE)
        //-----------------------------

    }

    //----------------------------------------------------------------------------------------------
    // Funções para o atendimento ao tapping nos botões (declaradas no xml)
    //----------------------------------------------------------------------------------------------
    //--- Evento tapping no botão de geração de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnGeraJogoClick(view: View?) {

        strLog = "-> Tap no btnGeraJogo"
        Log.d(cTAG, strLog)

        flagJogoGeradoOk = false

        //--- Ativa o progress bar
        progressBar.visibility = VISIBLE

        txtDadosJogo.text = String.format("%s", "Aguarde ...")

        //--- Continua a adaptação após um tempo para atualização da UI (progress bar e txtDadosJogo)
        val waitTime = 100L  // milisegundos
        Handler(Looper.getMainLooper()).postDelayed(
            {

                //-----------------------------
                visibilidadeViews(INVISIBLE)
                //-----------------------------

                rbPreset.isChecked = true

                strOpcaoJogo = "JogoGerado"

                sgg.txtDados = ""

                //--------------------------
                prepRBniveis(true)
                //--------------------------
                edtViewSubNivel.isEnabled = true

                groupRBadapta.visibility = INVISIBLE

                txtDadosJogo.text = ""
                sgg.txtDados = ""

                //--- Gera um novo jogo
                nivelJogo = when {

                    rbFacil.isChecked -> {
                        strNivelJogo = "Fácil"
                        FACIL
                    }

                    rbMedio.isChecked -> {
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

                    subNivelJogo = edtViewSubNivel.text.toString().toInt()
                    nivelTotalJogo = nivelJogo + subNivelJogo

                    //-----------------------------------------
                    quadMaior = sgg.geraJogo(nivelTotalJogo)
                    //-----------------------------------------
                    preencheSudokuBoard(quadMaior)
                    //-------------------------------

                    txtDadosJogo.text = ""

                    //--- Desativa o progress bar
                    progressBar.visibility = INVISIBLE

                    flagJogoGeradoOk = true

                    // **** O array preparado (quadMaior) será enviado pelo listener do botão JogaJogo ****
                } else {

                    txtDadosJogo.text = ""

                    //--- Desativa o progress bar
                    progressBar.visibility = INVISIBLE

                    //--------------------------------------------------------------------------------------
                    Toast.makeText(
                        this, "Não é possível gerar o jogo sem subnivel!",
                        Toast.LENGTH_SHORT
                    ).show()
                    //--------------------------------------------------------------------------------------
                }

            },

            waitTime
        )  // value in milliseconds

    }

    //--- Evento tapping no botão de adaptação de jogo
    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("UNUSED_PARAMETER")
    fun btnAdaptaJogoClick(view: View?) {

        strLog = "-> Tap no btnAdaptaJogo"
        Log.d(cTAG, strLog)

        flagJogoAdaptadoOk = false

        strOpcaoJogo = "JogoAdaptado"
        txtDadosJogo.text = ""
        sgg.txtDados = ""

        strLog = "   - rbAdapta: "
        strLog += if (rbPreset.isChecked) "Preset" else "Edição"
        Log.d(cTAG, strLog)

        //---------------------------
        visibilidadeViews(VISIBLE)
        //---------------------------
        groupRBadapta.visibility = VISIBLE

        //------------------------------
        prepRBniveis(false)
        //------------------------------
        edtViewSubNivel.isEnabled = false

        //--- Continua a adaptação após um tempo para atualização da UI (progress bar e txtDadosJogo)
        val waitTime = 100L  // milisegundos
        Handler(Looper.getMainLooper()).postDelayed({

            androidx.appcompat.app.AlertDialog.Builder(this)

                .setTitle("Sudoku - Adaptar Jogo")
                .setMessage("Carrega ou Edita jogo?")

                .setPositiveButton("Carrega") { _, _ ->

                    Log.d(cTAG, "-> \"Carrega\" was pressed")

                    //---------------
                    adaptaPreset()
                    //---------------

                }

                .setNegativeButton("Edita") { _, _ ->

                    Log.d(cTAG, "-> \"Edita\" was pressed")

                    //---------------
                    edicaoPreset()
                    //---------------

                }
                .show()

        },
        waitTime)  // value in milliseconds

    }

    //--- Evento tapping no botão para jogar o jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnJogaJogoClick(view: View?) {

        strLog = "-> Tap no btnJogaJogo"
        Log.d(cTAG, strLog)

        //--- Se não tiver jogo válido, informa ao usuário
        if (!flagJogoGeradoOk && !flagJogoAdaptadoOk) {

            val strToast = "Não há jogo válido!"
            //----------------------------------------------------------------
            Toast.makeText(this, strToast, Toast.LENGTH_LONG).show()
            //----------------------------------------------------------------

            Log.d(cTAG, "-> $strToast")

        }
        //--- Se tiver jogo válido, finaliza a preparação do jogo
        else {

            //-----------------------------
            //visibilidadeViews(INVISIBLE)
            //-----------------------------

            //txtDadosJogo.text = if (strOpcaoJogo == "JogoAdaptado")
            //    String.format("%s%d", "Preset #", sgg.intJogoAdaptar) else ""
            //sgg.txtDados = ""

            //-----------------------
            finalizaEdicaoPreset()
            //-----------------------

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
            intent.putExtra("strCronoConta", "00:00")
            intent.putExtra("strErro", "0")

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
    //--- rbJogoFacil
    fun rbJogoFacil(view: View?) {

        flagJogoGeradoOk = false

        strLog = "-> onClick rbJogoFacil"
        Log.d(cTAG, strLog)

        //-----------------------
        verifMudancaNivel(FACIL)
        //-----------------------

    }

    //--- rbJogoMedio
    fun rbJogoMedio(view: View?) {

        flagJogoGeradoOk = false

        strLog = "-> onClick rbJogoMedio"
        Log.d(cTAG, strLog)

        //-----------------------
        verifMudancaNivel(MEDIO)
        //-----------------------

    }

    //--- rbJogoDificil
    fun rbJogoDificil(view: View?) {

        flagJogoGeradoOk = false

        strLog = "-> onClick rbJogoDificil"
        Log.d(cTAG, strLog)

        //---------------------------
        verifMudancaNivel(DIFICIL)
        //---------------------------

    }

    //--- rbJogoMuitoDificil
    fun rbJogoMuitoDificil(view: View?) {

        flagJogoGeradoOk = false

        strLog = "-> onClick rbJogoMuitoDificil"
        Log.d(cTAG, strLog)

        //---------------------------------
        verifMudancaNivel(MUITO_DIFICIL)
        //---------------------------------

    }

    //----------------------------------------------------------------------------------------------
    // Funções para o atendimento de tapping nos radioButtons de escolha da adaptação de jogo
    //----------------------------------------------------------------------------------------------
    //--- RBpresetClick
    @RequiresApi(Build.VERSION_CODES.O)
    fun RBpresetClick(view: View?) {

        strLog = "-> onClick rbPreset"
        Log.d(cTAG, strLog)

        flagJogoGeradoOk = false

        //-----------------------------
        visibilidadeViews(INVISIBLE)
        //-----------------------------

        /*
        if (!flagAdaptaPreset) {

            //-------------------------
            btnAdaptaJogoClick(view)
            //-------------------------
            flagAdaptaPreset = true

        }
         */

        //---------------
        adaptaPreset()
        //---------------

    }

    //--- RBedicaoClick
    fun RBedicaoClick(view: View?) {

        strLog = "-> onClick rbEdicao"
        Log.d(cTAG, strLog)

        //---------------
        edicaoPreset()
        //---------------

    }

    //----------------------------------------------------------------------------------------------
    //                           Funções edição de jogos
    //----------------------------------------------------------------------------------------------
    //--- mostraCelAEditar
    private fun mostraCelAEditar(intLinha: Int, intCol: Int) {

        //-----------------------------------------------------
        utilsKt.copiaBmpByBuffer(bmpMyImageBack, bmpMyImage)
        //-----------------------------------------------------

        //--- Pinta de laranja a célula tocada
        canvasMyImage = Canvas(bmpMyImage!!)
        //---------------------------------------------
        pintaCelula(intLinha, intCol, pincelLaranja)
        //---------------------------------------------

    }

    //--- pintaCelula no canvasMyImage
    private fun pintaCelula(intLinha: Int, intCol: Int, pincelPintar: Paint?) {

        //- Canto superior esquerdo do quadrado
        val flXSupEsq = (intCol * intCellwidth).toFloat()
        val flYSupEsq = (intLinha * intCellheight).toFloat()
        //- Canto inferior direito do quadrado
        val flXInfDir = flXSupEsq + intCellwidth.toFloat()
        val flYInfDir = flYSupEsq + intCellheight.toFloat()
        //------------------------------------------------------------------------------------------
        canvasMyImage?.drawRect(
            toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir), toPx(flYInfDir),
            pincelPintar!!
        )
        //------------------------------------------------------------------------------------------
        ivSudokuBoardMain.setImageBitmap(bmpMyImage)

        //--- DEBUG: atualiza a qtidd de númDisp
        //-----------------------
        //atualizaIVQtiNumDisp()
        //-----------------------

    }

    //--- preparaIVNumDisp
    private fun preparaIVNumDisp() {

        //--- Instancializações e inicializações
        var flXSupEsq: Float //  = 0f
        var flYSupEsq: Float //  = 0f

        var flXInfDir: Float //  = 0f
        var flYInfDir: Float //  = 0f

        //--- Torna visíveis as images Views
        //-----------------------------
        visibilidadeViews(VISIBLE)
        //-----------------------------

        //--- Pinta-o e preenche-o
        bmpNumDisp = BitmapFactory.decodeResource(resources, R.drawable.quadro_nums_disp)
            .copy(Bitmap.Config.ARGB_8888, true)
        canvasNumDisp = Canvas(bmpNumDisp!!)

        //- Canto superior esquerdo do retângulo
        flXSupEsq = 0f
        flYSupEsq = 0f

        //- Canto inferior direito do quadrado
        flXInfDir = flXSupEsq + 9 * intCellwidth.toFloat()
        flYInfDir = intCellheight.toFloat()
        //------------------------------------------------------------------------------------------
        canvasNumDisp!!.drawRect(
            toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir),
            toPx(flYInfDir), pincelVerde
        )
        //------------------------------------------------------------------------------------------

        //--- Desenha-o
        val pincelFino = 2.toFloat()
        val pincelGrosso = 6.toFloat()
        // Linha horizontal superior
        pincelPreto.strokeWidth = pincelGrosso
        flXSupEsq = 0f
        flYSupEsq = 0f
        flXInfDir = (9 * intCellwidth).toFloat()
        flYInfDir = 0f
        //------------------------------------------------------------------------------------------
        canvasNumDisp!!.drawLine(
            toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir),
            toPx(flYInfDir), pincelPreto
        )
        //------------------------------------------------------------------------------------------
        // Linha horizontal inferior
        flXSupEsq = 0f
        flYSupEsq = intCellheight.toFloat()
        flXInfDir = (9 * intCellwidth).toFloat()
        flYInfDir = flYSupEsq
        //------------------------------------------------------------------------------------------
        canvasNumDisp!!.drawLine(
            toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir),
            toPx(flYInfDir), pincelPreto
        )
        //------------------------------------------------------------------------------------------
        // Linhas verticais
        for (idxCel in 0..9) {

            pincelPreto.strokeWidth = if (idxCel % 3 == 0) pincelGrosso else pincelFino

            //- Canto superior esquerdo do retângulo
            flXSupEsq = (idxCel * intCellwidth).toFloat()
            flYSupEsq = 0f
            //- Canto inferior direito do quadrado
            flXInfDir = flXSupEsq
            flYInfDir = intCellheight.toFloat()
            //--------------------------------------------------------------------------------------
            canvasNumDisp!!.drawLine(
                toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir),
                toPx(flYInfDir), pincelPreto
            )
            //--------------------------------------------------------------------------------------

        }

        //--- Escreve os números
        pincelBranco.textSize = intTamTxt * scale
        for (numDisp in 1..9) {

            val strNum = numDisp.toString()
            val idxNum = numDisp - 1

            val yCoord = intCellheight * 3 / 4
            val xCoord = intCellwidth / 3 + idxNum * intCellwidth

            //--------------------------------------------------------------------------------------
            canvasNumDisp!!.drawText(
                strNum, toPx(xCoord.toFloat()), toPx(yCoord.toFloat()),
                pincelBranco
            )
            //--------------------------------------------------------------------------------------

        }

        //--- Apaga os números que já foram utilizados
        for (idxNumDisp in (0..8)) {

            if (arIntQtiNumDisp[idxNumDisp] == 0) {

                //- Canto superior esquerdo do quadrado
                flXSupEsq = idxNumDisp * intCellwidth.toFloat()
                flYSupEsq = 0f

                //- Canto inferior direito do quadrado
                flXInfDir = flXSupEsq + intCellwidth.toFloat()
                flYInfDir = (intCellheight + 1).toFloat()
                //----------------------------------------------------------------------------------
                canvasNumDisp!!.drawRect(
                    toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir),
                    toPx(flYInfDir), pincelBranco
                )
                //----------------------------------------------------------------------------------

            }

        }
        //--- Atualiza a image view
        ivNumDisp.setImageBitmap(bmpNumDisp)

    }

    //----------------------------------------------------------------------------------------------
    //                              Funções gráficas
    //----------------------------------------------------------------------------------------------
    //--- inicializaObjGraf
    private fun inicializaObjGraf() {

        intTamTxt = 25
        scale = resources.displayMetrics.density

        ivSudokuBoardMain = findViewById(R.id.ivSudokuBoardMain)
        ivNumDisp = findViewById(R.id.imageView3)
        //ivQtiNumDisp         = findViewById(R.id.imageView4)

        //-----------------------------
        visibilidadeViews(INVISIBLE)
        //-----------------------------

        bmpMyImage = BitmapFactory.decodeResource(resources, R.drawable.sudoku_board3)
            .copy(Bitmap.Config.ARGB_8888, true)
        bmpMyImageInic = BitmapFactory.decodeResource(resources, R.drawable.sudoku_board3)
            .copy(Bitmap.Config.ARGB_8888, true)
        bmpMyImageBack = BitmapFactory.decodeResource(resources, R.drawable.sudoku_board3)
            .copy(Bitmap.Config.ARGB_8888, true)

        intCellwidth = bmpMyImage!!.width / 9
        intCellheight = bmpMyImage!!.height / 9

        canvasMyImage?.setBitmap(bmpMyImage!!)

        pincelVerde.color = ContextCompat.getColor(this, R.color.verde)
        pincelBranco.color = ContextCompat.getColor(this, R.color.white)
        pincelPreto.color = ContextCompat.getColor(this, R.color.black)
        pincelAzul.color = ContextCompat.getColor(this, R.color.azul)
        pincelLaranja.color = ContextCompat.getColor(this, R.color.laranja)

    }

    //----------------------------------------------------------------------------------------------
    // SudokuBoard
    //----------------------------------------------------------------------------------------------
    //--- preencheSudokuBoard
    private fun preencheSudokuBoard(arArIntJogo: Array<Array<Int>>) {

        //--- Objetos gráficos
        // Paint
        bmpMyImage = BitmapFactory.decodeResource(resources, R.drawable.sudoku_board3)
            .copy(Bitmap.Config.ARGB_8888, true)

        //--- Escreve nas células
        intCellwidth = bmpMyImage!!.width / 9
        intCellheight = bmpMyImage!!.height / 9

        //intCellwidth  = toPx((bmpMyImage!!.width).toFloat()).toInt()  / 9
        //intCellheight = toPx((bmpMyImage!!.height).toFloat()).toInt() / 9

        val canvasMyImage = Canvas(bmpMyImage!!)

        for (intLinha in 0..8) {

            for (intCol in 0..8) {

                //-------------------------------------------
                val intNum = arArIntJogo[intLinha][intCol]
                //-------------------------------------------
                if (intNum > 0) {

                    val strTexto = intNum.toString()

                    pincelAzul.textSize = intTamTxt * scale

                    //--- Coordenada Y (linhas)
                    //--------------------------------------------------------------
                    val yCoord = intCellheight * 3 / 4 + intLinha * intCellheight
                    //--------------------------------------------------------------

                    //--- Coordenada X (colunas)
                    //------------------------------------------------------
                    val xCoord = intCellwidth / 3 + intCol * intCellwidth
                    //------------------------------------------------------

                    //------------------------------------------------------------------------------
                    canvasMyImage.drawText(
                        strTexto, toPx(xCoord.toFloat()), toPx(yCoord.toFloat()),
                        pincelAzul
                    )
                    //------------------------------------------------------------------------------

                    //------------------------------------------------------------------------------
                    //canvasMyImage.drawText(strTexto, xCoord.toFloat(), yCoord.toFloat(),
                    //    pincelAzul)
                    //------------------------------------------------------------------------------
                }
            }
        }
        //-----------------------------------------------------
        utilsKt.copiaBmpByBuffer(bmpMyImage, bmpMyImageBack)
        //-----------------------------------------------------

        ivSudokuBoardMain.setImageBitmap(bmpMyImage)

        //--- DEBUG: atualiza a qtidd de númDisp
        //-----------------------
        //atualizaIVQtiNumDisp()
        //-----------------------

    }

    private var intNum = 0

    //--- editaIVSudokuBoard
    private fun editaIVSudokuBoard(coordX: Int, coordY: Int) {

        //--- OffSets das coordenadas na Janela (???)
        val viewCoords = IntArray(2)

        ivSudokuBoardMain.getLocationOnScreen(viewCoords)
        Log.d(cTAG, "viewCoord x: " + viewCoords[0])
        Log.d(cTAG, "viewCoord y: " + viewCoords[1])

        //--- Coordenadas reais (???)
        val imageX = coordX - viewCoords[0] // viewCoords[0] is the X coordinate
        val imageY = coordY - viewCoords[1] // viewCoords[1] is the y coordinate
        Log.d(cTAG, "Real x: $imageX")
        Log.d(cTAG, "Real y: $imageY")

        //--- Coordenadas da célula tocada
        intColJogar = coordX / intCellwidth
        intLinJogar = coordY / intCellheight

        if (intLinJogar < 9 && intColJogar < 9) {

            //-----------------------------------------------
            intNum = arArIntNums[intLinJogar][intColJogar]
            //-----------------------------------------------
            strLog = "-> Celula tocada: linha = " + intLinJogar + ", coluna = " + intColJogar +
                    ", numero = " + intNum
            Log.d(cTAG, strLog)

            //--- Se tocou numa célula com número, solicita ao usuário que confirme sua sobreescrita
            if (intNum > 0) {

                //-----------------
                verifEdicaoCel()
                //-----------------

            }
            //--- Tocou numa célula sem número
            else {

                //---------------------------
                editaIVSudokuBoard_c1()
                //---------------------------

            }
        }
    }

    //--- verifEdicaoCel
    private fun verifEdicaoCel() {

        androidx.appcompat.app.AlertDialog.Builder(this)

            .setTitle("Sudoku - Edição")
            .setMessage("Você selecionou uma célula já ocupada!")

            .setPositiveButton("Limpe-a") { _, _ ->

                //Toast.makeText(applicationContext, "OK was pressed", Toast.LENGTH_LONG).show()
                Log.d(cTAG, "-> \"Limpe-a\" was pressed")

                //-------------
                zeraCelula()
                //-------------

            }

            .setNegativeButton("Sobrescreva-a") { _, _ ->

                //Toast.makeText(applicationContext, "Cancel was pressed", Toast.LENGTH_LONG).show()
                Log.d(cTAG, "-> \"Sobrescreva-a\" was pressed")

                //-------------
                zeraCelula()
                //------------------------
                editaIVSudokuBoard_c1()
                //------------------------

            }

            .setNeutralButton("Esqueça") { _, _ ->

                //Toast.makeText(applicationContext, "Neutral was pressed", Toast.LENGTH_LONG).show()
                Log.d(cTAG, "-> \"Esqueça\" was pressed")

                //--------------
                voltaEdicao()
                //--------------

            }
            .show()

    }

    //--- editaIVSudokuBoard_c1
    private fun editaIVSudokuBoard_c1() {

        //-------------------------------------------
        mostraCelAEditar(intLinJogar, intColJogar)
        //-------------------------------------------
        flagBoardSel = true

    }

    //--- zeraCelula
    private fun zeraCelula() {

        intNum = arArIntNums[intLinJogar][intColJogar]
        arArIntNums[intLinJogar][intColJogar] = 0
        arIntQtiNumDisp[intNum - 1]++

        val intQtiZeros = tvContaClues.text.toString().toInt()
        val intQtiNums  = 81 - intQtiZeros

        tvContaNums.text  = (intQtiNums - 1).toString()
        tvContaClues.text = (intQtiZeros + 1).toString()

        //--------------
        voltaEdicao()
        //--------------

    }

    //--- volta à edição
    private fun voltaEdicao() {

        //--- ivSudokuBoardMain
        //---------------------------------
        preencheSudokuBoard(arArIntNums)
        //---------------------------------

        //--- ivNumDisp
        //-------------------
        preparaIVNumDisp()
        //-------------------

    }

    //----------------------------------------------------------------------------------------------
    // NumDisp
    //----------------------------------------------------------------------------------------------
    //--- editaIVNumDisp
    private fun editaIVNumDisp(coordX: Int) {

        //--- Inicializações e inicializações
        val flXSupEsq: Float
        val flYSupEsq: Float
        val flXInfDir: Float
        val flYInfDir: Float

        //--- Se tem célula selecionada no Sudoku Board, trata a edição da célula
        if (flagBoardSel) {

            //--- Determina a célula e o número tocado
            val cellX = coordX / intCellwidth
            val intNum = arIntNumsDisp[cellX]

            if (intLinJogar < 9 && intColJogar < 9 && cellX < 9) {

                //--- Se tocou numa célula já com número; solicita ao usuário que confirme sua sobreescrita.
                if (arArIntNums[intLinJogar][intColJogar] > 0) {

                    //-----------------
                    verifEdicaoCel()
                    //-----------------

                }
                //--- Tocou numa célula ainda sem número
                else {

                    //--- Se ainda tem número desse disponível e ele é válido para o jogo, escreve-o no board
                    if (arIntQtiNumDisp[cellX] > 0) {

                        //--- Verifica se válido
                        //--------------------------------------------------
                        jogarJogo.arArIntNums = copiaArArInt(arArIntNums)
                        //---------------------------------------------------------
                        val Qm = jogarJogo.determinaQm(intLinJogar, intColJogar)
                        //---------------------------------------------------------------------------
                        val flagNumVal =
                            jogarJogo.verifValidade(Qm, intLinJogar, intColJogar, intNum)
                        //---------------------------------------------------------------------------

                        //--- Se válido e ainda tem número desse disponível escreve-o no board
                        if (flagNumVal) {

                            arIntQtiNumDisp[cellX]--

                            //--- Adiciona-o ao board
                            arArIntNums[intLinJogar][intColJogar] = intNum
                            //---------------------------------
                            preencheSudokuBoard(arArIntNums)
                            //--------------------------------------------------
                            val intQtiZeros = utilsKt.quantZeros(arArIntNums)
                            //--------------------------------------------------
                            tvContaClues.text = intQtiZeros.toString()
                            tvContaNums.text = (81 - intQtiZeros).toString()

                            flagJogoAdaptadoOk = (intQtiZeros > 19 && intQtiZeros < 60)

                            //--- Sinaliza se já posicionou os 9 desse número
                            if (arIntQtiNumDisp[cellX] == 0) {

                                //- Canto superior esquerdo do quadrado
                                flXSupEsq = cellX * intCellwidth.toFloat()
                                flYSupEsq = 0f

                                //- Canto inferior direito do quadrado
                                flXInfDir = flXSupEsq + intCellwidth.toFloat()
                                flYInfDir = (intCellheight + 1).toFloat()
                                //------------------------------------------------------------------
                                canvasNumDisp!!.drawRect(
                                    toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir),
                                    toPx(flYInfDir), pincelBranco
                                )
                                //------------------------------------------------------------------
                                ivNumDisp.setImageBitmap(bmpNumDisp)

                            }

                            flagBoardSel = false

                        }
                        //--- Número NÃO válido
                        else {

                            strToast = "Número NÃO OK!\n(linha, coluna ou quadro)"
                            //-----------------------------------------------------------------
                            Toast.makeText(this, strToast, Toast.LENGTH_SHORT).show()
                            //-----------------------------------------------------------------

                        }
                    }
                }
            }
        }
        //--- Não há célula selecionada no Sudoku Board
        else {

            //--------------------------------------------------------------------------------------
            Toast.makeText(
                this, "Selecione uma célula no Sudoku board!",
                Toast.LENGTH_LONG
            ).show()
            //--------------------------------------------------------------------------------------

        }
    }

    //----------------------------------------------------------------------------------------------
    //                                      Funções
    //----------------------------------------------------------------------------------------------
    //--- visibilidadeViews
    private fun visibilidadeViews(visibilidade: Int) {

        //--- DEBUG
        //ivQtiNumDisp.visibility = INVISIBLE  //visibilidade

        ivNumDisp.visibility = visibilidade
        tvContaClues.visibility = visibilidade
        tvContaNums.visibility = visibilidade

        findViewById<TextView>(R.id.legContaNums).visibility = visibilidade
        findViewById<TextView>(R.id.legContaClues).visibility = visibilidade

    }

    //lateinit var document : Document

    //--- inicQuadMaiorAdaptacao
    @RequiresApi(Build.VERSION_CODES.O)
    private fun inicQuadMaiorAdaptacao(jogoAdaptar: Int) {

        var array: Array<Int>
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
            // 4 -> run {
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
            for (intCol in 0..8) {
                arArIntTmp[intLin][intCol] = arArIntPreset[intLin][intCol]
            }
        }

        return arArIntTmp

    }

    //--- Prepara rbNivel
    private fun prepRBniveis(habOuDesab: Boolean) {

        val intIdxChild = groupRBnivel.childCount - 1
        if (intIdxChild > 0) {

            for (idxRB in 0..intIdxChild) {
                groupRBnivel.getChildAt(idxRB).isEnabled = habOuDesab
            }

        }
    }

    //--- verifMudancaNivel
    private fun verifMudancaNivel(intNivel: Int) {

        if (strOpcaoJogo == "JogoGerado") {

            if (edtViewSubNivel.text.toString().isNotEmpty()) {

                val intNivelTotal = intNivel + edtViewSubNivel.text.toString().toInt()
                if (intNivelTotal != nivelJogo) {

                    flagJogoGeradoOk = false
                    flagJogoAdaptadoOk = false

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

    //----------------------------------------------------------------------------------------------
    //                                      Debug
    //----------------------------------------------------------------------------------------------
    /*
    private fun atualizaIVQtiNumDisp() {

        //--- Pinta-o e preenche-o
        val bmpQtiNumDisp = BitmapFactory.decodeResource(resources, R.drawable.quadro_nums_disp)
            .copy(Bitmap.Config.ARGB_8888, true)
        val canvasQtiNumDisp = Canvas(bmpQtiNumDisp)

        for (idxQtiNum in (0..8)) {

            val strTxt = arIntQtiNumDisp[idxQtiNum].toString()

            val yCoord = intCellheight * 3 / 4
            val xCoord = intCellwidth / 3 + idxQtiNum * intCellwidth

            pincelPreto.textSize = intTamTxt * scale

            //--------------------------------------------------------------------------------------
            canvasQtiNumDisp.drawText(
                strTxt, toPx(xCoord.toFloat()), toPx(yCoord.toFloat()),
                pincelPreto
            )
            //--------------------------------------------------------------------------------------

        }
        //-------------------------------------------
        ivQtiNumDisp.setImageBitmap(bmpQtiNumDisp)
        //-------------------------------------------

    }
    */

    //--- separaArq
    private fun separaArq(arStrLeit: ArrayList<String>, strTag: String): String {

        var strTmp = ""
        var strReturn = ""

        val strTagInic = "<$strTag>"
        val strTagFim = "</$strTag>"

        for (idxCampo in arStrLeit.indices) {
            strTmp += arStrLeit[idxCampo]
        }

        val intIdxInic = strTmp.indexOf(strTagInic)
        val intIdxFim = strTmp.indexOf(strTagFim) + strTagFim.length

        if (intIdxInic > -1 && intIdxFim > -1 && intIdxFim > intIdxInic) {

            strReturn += strTmp.substring(intIdxInic, intIdxFim)

        }

        return strReturn

    }

    //--- separaCampo
    private fun separaCampo(strCampo: String, strSubTag: String): String {

        val strTmp: String
        var strReturn = ""

        val strSubTagInic = "<$strSubTag>"
        val strSubTagFim = "</$strSubTag>"

        val intIdxInic = strCampo.indexOf(strSubTagInic) + strSubTagInic.length
        val intIdxFim = strCampo.indexOf(strSubTagFim)

        if (intIdxInic > -1 && intIdxFim > -1 && intIdxFim > intIdxInic) {

            strTmp = strCampo.substring(intIdxInic, intIdxFim)
            strReturn = strTmp.trimStart()

        }

        return strReturn

    }

    //--- Converte um valor em dp para pixels (px)
    private fun toPx(dip: Float): Float {
        return (dip)
    }

    /*
    //--- leituraPreset em resources/raw
    private fun leResRawPreset(numPreset : Int) : Array < Array <Int> > {

        var arArIntJogoAdaptar = arrayOf < Array <Int>> ()  //= Array(9) { Array(9) { 0 } }

        var flagLeituraParcialOk = false
        var flagLeituraPresetOk  = false
        val strNomeArq = "preset_$numPreset"

        //--- Obtém lista de arquivos em resource / raw
        //-------------------------------------------------------
        val arStrNomeArqRaw: Array<String> = utils.ListRaw()
        //-------------------------------------------------------
        Log.d(cTAG, "-> Arquivos raw (listRaw):")
        for (idxFileName in arStrNomeArqRaw.indices) {

            val strFileNameDir = arStrNomeArqRaw[idxFileName]
            Log.d(cTAG, "$idxFileName: $strFileNameDir")

            flagLeituraParcialOk = (strFileNameDir == strNomeArq)
            if (flagLeituraParcialOk) break

        }

        //--- Leitura de arquivo
        if (!flagLeituraParcialOk) return (Array(9) { Array(9) { 0 } })
        else {

            Log.d(cTAG, "-> Arquivo $strNomeArq:")
            //--------------------------------------------------------------------------------------
            val arStrLeitArqRaw: ArrayList<String> = utils.LeituraFileRaw(this, strNomeArq)
            //--------------------------------------------------------------------------------------
            for (idxDecl in 0 until arStrLeitArqRaw.size) {

                Log.d(cTAG, "   $idxDecl: ${arStrLeitArqRaw[idxDecl]}")

            }

            //--- Prepara os arrays com os tags e subtags
            arStrTags = arrayOf("header", "body", "jogos")
            arArStrTags[0] = arrayOf("id", "nivel", "subnivel")  //, "", "", "", "", "", "")
            arArStrTags[1] = arrayOf(
                "linha0", "linha1", "linha2", "linha3", "linha4", "linha5",
                "linha6", "linha7", "linha8"
            )
            arArStrTags[2] =
                arrayOf("id", "dataHora", "tempoJogo", "erros", "status")   //, "", "", "")

            //--- Obtém os campos entre os tags principais
            for (idxTag in arStrTags.indices) {

                var strTAG = arStrTags[idxTag]

                /*
                strLog  = "-> Obtém "
                strLog += if (strTAG == "jogos") "os " else "o "
                strLog += "$strTAG"
                Log.d(cTAG, strLog)
                 */

                //--------------------------------------------------
                val strCampo = separaArq(arStrLeitArqRaw, strTAG)
                //--------------------------------------------------
                if (strCampo == "") Log.d(cTAG, "Esse arquivo não contém o tag \"$strTAG\"")
                else {

                    //Log.d(cTAG, strCampo)

                    when (idxTag) {

                        0 -> { // "home"

                            Log.d(cTAG, "-> Tag: ${arStrTags[idxTag]}")

                            for (idxSubTag in 0 until 3) {

                                strTAG = arArStrTags[0][idxSubTag]
                                strLog = "   - subTag: $strTAG  conteúdo: "

                                //------------------------------------------------
                                val strSubCampo = separaCampo(strCampo, strTAG)
                                //------------------------------------------------

                                strLog += strSubCampo
                                Log.d(cTAG, strLog)

                            }
                        }

                        1 -> { // "body"

                            Log.d(cTAG, "-> Tag: ${arStrTags[idxTag]}")
                            for (idxSubTag in 0 until 9) {

                                strTAG = arArStrTags[1][idxSubTag]
                                strLog = "   - subTag: $strTAG  conteúdo: "

                                //------------------------------------------------
                                val strSubCampo = separaCampo(strCampo, strTAG)
                                //------------------------------------------------

                                strLog += strSubCampo
                                Log.d(cTAG, strLog)

                                var array = arrayOf<Int>()

                                val lstChNum : List<String> = strSubCampo.split(",")

                                lstChNum.forEach () {

                                   array += (it.trim()).toInt()

                                }
                                arArIntJogoAdaptar += array

                            }
                        }

                        2 -> { // "jogos"

                            Log.d(cTAG, "-> Tag: ${arStrTags[idxTag]}")
                            for (idxSubTag in 0 until 5) {

                                strTAG = arArStrTags[2][idxSubTag]
                                strLog = "   - subTag: $strTAG  conteúdo: "

                                //------------------------------------------------
                                val strSubCampo = separaCampo(strCampo, strTAG)
                                //------------------------------------------------

                                strLog += strSubCampo
                                Log.d(cTAG, strLog)

                            }
                        }

                        else -> {
                        }

                    }
                }
            }

        }
        return arArIntJogoAdaptar
    }
    */

    //--- leExtMem:  Download/sudoku/ do smartphone
    private fun leExtMemDownload(numPreset: Int): Array<Array<Int>> {

        var arArIntJogoAdaptar = arrayOf<Array<Int>>()  //= Array(9) { Array(9) { 0 } }

        var flagLeituraParcialOk = false
        //var flagLeituraPresetOk  = false
        val strNomeArq = "preset_$numPreset"

        //--- Obtém lista de arquivos em Download
        val strPath = "/sudoku/jogos"
        //-------------------------------------------------------------------
        val arStrNomeArq: Array<String> = utils.listaExtMemArqDir(strPath)
        //-------------------------------------------------------------------
        Log.d(cTAG, "-> Arquivos Sudoku :")
        for (idxFileName in arStrNomeArq.indices) {

            val strFileNameDir = arStrNomeArq[idxFileName]
            Log.d(cTAG, "$idxFileName: $strFileNameDir")

            flagLeituraParcialOk = (strFileNameDir == strNomeArq)
            if (flagLeituraParcialOk) break

        }

        //--- Leitura de arquivo
        if (!flagLeituraParcialOk) return (Array(9) { Array(9) { 0 } })
        else {

            Log.d(cTAG, "-> Arquivo $strNomeArq:")
            val strNomeComPath = "sudoku/jogos/$strNomeArq"
            //----------------------------------------------------------------------------
            val arStrLeitArq: ArrayList<String> = utils.leitExtMemTextFile(strNomeComPath)
            //----------------------------------------------------------------------------
            for (idxDecl in 0 until arStrLeitArq.size) {

                Log.d(cTAG, "   $idxDecl: ${arStrLeitArq[idxDecl]}")

            }

            //--- Prepara os arrays com os tags e subtags
            arStrTags = arrayOf("header", "body", "jogos")
            arArStrTags[0] = arrayOf("id", "nivel", "subnivel")  //, "", "", "", "", "", "")
            arArStrTags[1] = arrayOf(
                "linha0", "linha1", "linha2", "linha3", "linha4", "linha5",
                "linha6", "linha7", "linha8"
            )
            arArStrTags[2] =
                arrayOf("id", "dataHora", "tempoJogo", "erros", "status")   //, "", "", "")

            //--- Obtém os campos entre os tags principais
            for (idxTag in arStrTags.indices) {

                var strTAG = arStrTags[idxTag]

                /*
                strLog  = "-> Obtém "
                strLog += if (strTAG == "jogos") "os " else "o "
                strLog += "$strTAG"
                Log.d(cTAG, strLog)
                 */

                //-----------------------------------------------
                val strCampo = separaArq(arStrLeitArq, strTAG)
                //-----------------------------------------------
                if (strCampo == "") Log.d(cTAG, "Esse arquivo não contém o tag \"$strTAG\"")
                else {

                    //Log.d(cTAG, strCampo)

                    when (idxTag) {

                        0 -> { // "home"

                            Log.d(cTAG, "-> Tag: ${arStrTags[idxTag]}")

                            for (idxSubTag in 0 until 3) {

                                strTAG = arArStrTags[0][idxSubTag]
                                strLog = "   - subTag: $strTAG  conteúdo: "

                                //------------------------------------------------
                                val strSubCampo = separaCampo(strCampo, strTAG)
                                //------------------------------------------------

                                strLog += strSubCampo
                                Log.d(cTAG, strLog)

                            }
                        }

                        1 -> { // "body"

                            Log.d(cTAG, "-> Tag: ${arStrTags[idxTag]}")
                            for (idxSubTag in 0 until 9) {

                                strTAG = arArStrTags[1][idxSubTag]
                                strLog = "   - subTag: $strTAG  conteúdo: "

                                //------------------------------------------------
                                val strSubCampo = separaCampo(strCampo, strTAG)
                                //------------------------------------------------

                                strLog += strSubCampo
                                Log.d(cTAG, strLog)

                                var array = arrayOf<Int>()

                                val lstChNum: List<String> = strSubCampo.split(",")

                                lstChNum.forEach {

                                    array += (it.trim()).toInt()

                                }
                                arArIntJogoAdaptar += array

                            }
                        }

                        2 -> { // "jogos"

                            Log.d(cTAG, "-> Tag: ${arStrTags[idxTag]}")
                            for (idxSubTag in 0 until 5) {

                                strTAG = arArStrTags[2][idxSubTag]
                                strLog = "   - subTag: $strTAG  conteúdo: "

                                //------------------------------------------------
                                val strSubCampo = separaCampo(strCampo, strTAG)
                                //------------------------------------------------

                                strLog += strSubCampo
                                Log.d(cTAG, strLog)

                            }
                        }

                        else -> {
                        }

                    }
                }
            }

        }

        return arArIntJogoAdaptar

    }

    //----------------------------------------------------------------------------------------------
    //                                        Permissões
    //----------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------
    // Verifica se Permissions do Manifest estão granted
    //---------------------------------------------------------------------
    private fun VerificaPermissoesAcessoAPI(): Boolean {

        var strMsgDebug = "--> main "
        strMsgDebug += "Verifica permissões"

        Log.d(cTAG, strMsgDebug)

        //-----------------------------------------------------------------------
        val flagPermissoesOk: Boolean = utils.VerificaPermissoes(this)
        //-----------------------------------------------------------------------
        strMsgDebug = if (flagPermissoesOk) "Permission Granted!" else "Permission unGranted"

        Toast.makeText(this, strMsgDebug, Toast.LENGTH_SHORT).show()

        Log.d(cTAG, strMsgDebug)

        return flagPermissoesOk

    }

    //----------------------------------------------------------------------------------------------
    //                                 Funções Jogo selecionado
    //----------------------------------------------------------------------------------------------
    //--- preparaJogoSelec
    private fun preparaJogoSelec(strJogoSelec: String) {

        //--- Continua a adaptação após um tempo para atualização da UI (progress bar e txtDadosJogo)
        val waitTime = 100L  // milisegundos
        Handler(Looper.getMainLooper()).postDelayed(
            {

                //-----------------------------
                visibilidadeViews(INVISIBLE)
                //-----------------------------
                groupRBadapta.visibility = VISIBLE

                //----------------------------------------------
                quadMaiorAdapta = extraeQMSelec(strJogoSelec)
                //----------------------------------------------

                sgg.quadMaiorRet = copiaArArInt(quadMaiorAdapta)

                //---------------------------------------
                quadMaior = sgg.adaptaJogoAlgoritmo2()
                //---------------------------------------

                //--- Apresenta o nível e o subnível do preset
                nivelJogo = (sgg.intQtiZeros / 10) * 10
                subNivelJogo = sgg.intQtiZeros % 10
                val rbNivelJogo: RadioButton = when (nivelJogo) {

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

                txtDadosJogo.text = String.format("%s%d", "Preset #", sgg.intJogoAdaptar)

                flagJogoAdaptadoOk = true

                //--- Desativa o progress bar
                progressBar.visibility = INVISIBLE

            },
            waitTime
        )  // value in milliseconds

    }

    //--- extraeQMSelec
    private fun extraeQMSelec(strJogoSelec: String): Array<Array<Int>> {

        var arArJogoSelec = Array(9) { Array(9) { 0 } }

        return arArJogoSelec

    }

    //--- adaptaPreset
    private fun adaptaPreset() {

        // --- Prepara o preset para se conseguir o gabarito do jogo

        /*
        if (++sgg.intJogoAdaptar > 6) sgg.intJogoAdaptar = 1
        txtDadosJogo.text = String.format("%s%d %s","Preset #",sgg.intJogoAdaptar,"aguarde ...")
        */

        //--- Continua a adaptação após um tempo para atualização da UI (progress bar e txtDadosJogo)
        val waitTime = 100L  // milisegundos
        Handler(Looper.getMainLooper()).postDelayed({

            //-----------------------------
            visibilidadeViews(INVISIBLE)
            //-----------------------------
            groupRBadapta.visibility = VISIBLE

            rbPreset.isChecked = true

            //--- Desativa o progress bar
            progressBar.visibility = INVISIBLE

        },
        waitTime)  // value in milliseconds

        //--- Prepara o preset para se conseguir o gabarito do jogo
        strLog = "-> Presset is checked"
        Log.d(cTAG, strLog)

        //--- Prepara a Intent para chamar AdaptarActivity
        val intent = Intent(this, AdaptarActivity::class.java)
        intent.action = "InstanciaRVJogosSalvos"
        //----------------------
        startActivity(intent)
        //----------------------

    }

    //--- edicaoPreset()
    private fun edicaoPreset() {

        rbEdicao.isChecked = true

        flagJogoGeradoOk = false

        txtDadosJogo.text = ""

        tvContaNums.text = "0"
        tvContaClues.text = resources.getString(R.string.valor81)

        arArIntNums = Array(9) { Array(9) { 0 } }
        arIntQtiNumDisp = Array(9) { 9 }

        ivNumDisp.isEnabled = true
        ivSudokuBoardMain.isEnabled = true

        //------------------------------------------------------------------------------------------
        // Image view dos números disponíveis
        //------------------------------------------------------------------------------------------
        //-------------------
        preparaIVNumDisp()
        //-------------------

        //------------------------------------------------------------------------------------------
        // Image view do jogo
        //------------------------------------------------------------------------------------------
        bmpMyImage = BitmapFactory.decodeResource(resources, R.drawable.sudoku_board3)
            .copy(Bitmap.Config.ARGB_8888, true)
        ivSudokuBoardMain.setImageBitmap(bmpMyImage)

        //-----------------------------------------------------
        utilsKt.copiaBmpByBuffer(bmpMyImage, bmpMyImageBack)
        //-----------------------------------------------------

        //--- DEBUG: atualiza a qtidd de númDisp
        //-----------------------
        // atualizaIVQtiNumDisp()
        //-----------------------

    }

    //--- finalizaEdicaoPreset
    private fun finalizaEdicaoPreset() {

        //---------------------------
        visibilidadeViews(VISIBLE)
        //---------------------------

        quadMaior = utilsKt.copiaArArInt(arArIntNums)

        var flagEdicaoOK = false
        val intQtiZeros  = utilsKt.quantZeros(quadMaior)    //tvContaClues.text.toString().toInt()

        val intNivel    = intQtiZeros / 10
        val intSubNivel = intQtiZeros % 10
        //---------- ---------- ----------------------
        // intNivel     Nivel      números / Zeros
        //---------- ---------- ----------------------
        //    2       fácil     de: 61 / 20  a 52 / 29
        //    3       médio     de: 51 / 30  a 42 / 39
        //    4       difícil   de: 41 / 40  a 32 / 49
        //    5       muito dif de: 31 / 50  a 22 / 59

        when {

            intQtiZeros > 59 -> {
                strToast = "Para gerar jogo editar mais do que 21 números!"
            }
            intQtiZeros < 20 -> {
                strToast = "Para gerar jogo editar menos do que 62 números!"
            }
            else -> {

                strNivelJogo = when (intNivel) {

                    2 -> {
                        rbFacil.isChecked = true
                        "Fácil"
                    }
                    3 -> {
                        rbMedio.isChecked = true
                        "Médio"
                    }
                    4 -> {
                        rbDificil.isChecked = true
                        "Difícil"
                    }
                    else -> {
                        rbMuitoDificil.isChecked = true
                        "Muito Difícil"
                    }
                }
            }

        }

        edtViewSubNivel.setText(intSubNivel.toString())
        strToast = "Gera jogo:\n- nível: $strNivelJogo subnível: $intSubNivel"

        flagEdicaoOK = true

        Toast.makeText(this, strToast, Toast.LENGTH_LONG).show()

        //--- Se edição OK gera o gabarito para o jogo editado
        if (flagEdicaoOK)
        {

            sgg.quadMaiorRet = copiaArArInt(quadMaior)
            arArIntNums      = copiaArArInt(quadMaior)
            //---------------------------------------
            quadMaior = sgg.adaptaJogoAlgoritmo2()
            //---------------------------------------

            flagJogoAdaptadoOk = true

        }

        // **** O array preparado (quadMaior) e o gabarito (sgg.quadMaiorRet) serão enviados
        // **** pelo listener do botão JogaJogo.
    }

}
