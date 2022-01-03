package br.com.jhconsultores.sudoku.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import br.com.jhconsultores.sudoku.R
import br.com.jhconsultores.sudoku.adapter.JogoAdapter
import br.com.jhconsultores.sudoku.adapter.JogoClickedListener
import br.com.jhconsultores.sudoku.jogo.SudokuGameGenerator
import br.com.jhconsultores.sudoku.ui.MainActivity.Companion.strApp
//import br.com.jhconsultores.sudoku.ui.MainActivity.Companion.flagJogoAdaptadoOk

import br.com.jhconsultores.utils.Utils
import br.com.jhconsultores.utils.UtilsKt

class AdaptarActivity : AppCompatActivity() {

    //--------------------------------------------------------------------------
    //                    Instancializações e inicializações
    //--------------------------------------------------------------------------
    private var cTAG     = "Sudoku"
    private var strLog   = ""
    private var strToast = ""

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var customAdapter: JogoAdapter

    private var itemsListArq  = ArrayList<String>()
    private var itemsListJogo = ArrayList<String>()
    private var recyclerView : RecyclerView? = null
    private lateinit var adaptarToolBar: androidx.appcompat.widget.Toolbar
    
    private var strOpcaoJogo = "JogoAdaptado"

    private var strNivelJogo = "Fácil"
    private var subNivelJogo = 0

    private val utils   = Utils ()
    private val utilsKt = UtilsKt ()
    private val sgg     = SudokuGameGenerator()
    private val main    = MainActivity ()

    //--------------------------------------------------------------------------
    //                                Eventos
    //--------------------------------------------------------------------------
    //--- onCreate MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adaptar)

        //------------------------------------------------------------------------------------------
        // Implementa o actionBar
        //------------------------------------------------------------------------------------------
        adaptarToolBar = findViewById(R.id.adaptartoolbar)
        adaptarToolBar.title = strApp +  " - Adaptação"

        setSupportActionBar(adaptarToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //------------------------------------------------------------------------------------------
        // Implementa o recycler view
        //------------------------------------------------------------------------------------------
        // 1- referencia um objeto RecyclerView local ao declarado no layout
        recyclerView = findViewById(R.id.rv_jogos) as RecyclerView

        // 2- RV assume o controle do layout
        layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = layoutManager

        // 3- referencia o ArrayList ao ViewHolder
        Log.d(cTAG, "-> Jogos salvos: ")
        //- Prepara os arrays list das infos para o RV
        //-----------------------------------------------------------------------
        val arStrArqsNames = utils.listaExtMemArqDir("/sudoku/jogos")
        //-----------------------------------------------------------------------
        if (arStrArqsNames.isNotEmpty()) {

            for (strArqName in arStrArqsNames) {

                Log.d(cTAG, "   - $strArqName")

                //-----------------------------------------------------
                itemsListArq.add(preparaItensInfosArq(strArqName))
                //-----------------------------------------------------
                itemsListJogo.add(preparaItensInfosJogo(strArqName))
                //-----------------------------------------------------

            }

        } else {

            strLog = "   - Não há arquivos de jogos no dir /Download/sudoku/Jogos"
            Log.d(cTAG, strLog)

        }

    }

    override fun onResume () {

        super.onResume()

        //- Prepara os arrays list das infos para o RV
        itemsListArq  = ArrayList<String>()
        itemsListJogo = ArrayList<String>()

        //-----------------------------------------------------------------------
        val arStrArqsNames = utils.listaExtMemArqDir("/sudoku/jogos")
        //-----------------------------------------------------------------------
        if (arStrArqsNames.isNotEmpty()) {

            for (strArqName in arStrArqsNames) {

                Log.d(cTAG, "   - $strArqName")

                //-----------------------------------------------------
                itemsListArq.add(preparaItensInfosArq(strArqName))
                //-----------------------------------------------------
                itemsListJogo.add(preparaItensInfosJogo(strArqName))
                //-----------------------------------------------------

            }

            //---------------------------------------------------------------------------------------
            customAdapter = JogoAdapter(itemsListArq, itemsListJogo, object : JogoClickedListener {
                //---------------------------------------------------------------------------------------

                //--- Listener para click na info do arquivo de um dos jogos
                override fun infoItem (posicao : Int) {

                    //---------------------------------------------------------------------------------
                    val strfileName = leCampo(itemsListArq[posicao], "Arq: ", " Data:")
                    //---------------------------------------------------------------------------------

                    strToast = "Tapped $posicao: $strfileName!"
                    Toast.makeText(baseContext, strToast, Toast.LENGTH_SHORT).show()

                    //-------------------------
                    adaptaEjogaJogo(posicao)
                    //-------------------------

                }

                //--- Listener para click na info de um dos jogos
                override fun jogoItem(posicao : Int) {

                    //---------------------------------------------------------------------------------
                    val strNivel = leCampo(itemsListJogo[posicao], "Nivel: ", " sub: ")
                    //---------------------------------------------------------------------------------

                    strToast = "Tapped $posicao: $strNivel!"
                    Toast.makeText(baseContext, strToast, Toast.LENGTH_SHORT).show()

                    //-------------------------
                    adaptaEjogaJogo(posicao)
                    //-------------------------

                }

            })

            recyclerView!!.adapter = customAdapter

        } else {

            strLog = "   - Não há arquivos de jogos no dir /Download/sudoku/Jogos"
            Log.d(cTAG, strLog)

        }

    }

    //--------------------------------------------------------------------------
    //                                Funções
    //--------------------------------------------------------------------------
    //--- Adapta jogo selecionado no RV e passa a Jogar o jogo
    var strTextViews  = ""
    var strFileName   = ""
    var strJogo       = ""
    var strStatus     = ""
    var strErro       = "0"
    var strCronoConta = "00:00:00"

    fun adaptaEjogaJogo(idxItemView : Int) {

        strTextViews = ("${itemsListArq[idxItemView]}  ${itemsListJogo[idxItemView]}").trim()
        Log.d(cTAG, "-> item textViews:\n$strTextViews")

        val strTagFim = if (strTextViews.contains(" Jogo")) " Jogo" else "Data:"
        //---------------------------------------------------------------------------
        strFileName = (leCampo(strTextViews, "Arq:", strTagFim)).trim()
        //---------------------------------------------------------------------------
        Log.d(cTAG, "-> nome do arquivo: $strFileName")

        //--- Lê o arquivo selecionado
        //-------------------------------
        strJogo = leitArq(strFileName)
        //-------------------------------
        Log.d(cTAG, "-> dados do jogo salvo:\n$strJogo")

        //---------------------------------------------------------------------------------
        strStatus = (leCampo(strTextViews, "Status:", "Nivel:")).trim()
        //---------------------------------------------------------------------------------
        strLog  = "-> status: "
        strLog += if (strStatus == "ativo") "NÃO" else ""
        strLog += " finalizado"
        Log.d(cTAG, strLog)

        //--- Jogo NÃO finalizado: verifica se reseta o jogo
        if (strStatus == "ativo") {

            AlertDialog.Builder(this)

                .setTitle("Sudoku - Jogo")
                .setMessage("Jogo não finalizado.\nResseta ou continua o jogo?")

                .setPositiveButton("Resseta") { _, _ ->

                    Log.d(cTAG, "-> \"Resseta\" was pressed")

                    //---------------------
                    prepJogoRessetado()
                    //---------------------

                }

                .setNegativeButton("Continua") { _, _ ->

                    Log.d(cTAG, "-> \"Continua\" was pressed")

                    //----------------------
                    prepJogoAContinuar()
                    //----------------------

                }

                .setNeutralButton("Cancela") { _, _ ->

                    Log.d(cTAG, "-> \"Cancela\" was pressed")

                }
                .show()

        }

        //--- Jogo finalizado
        else {

            Log.d(cTAG, "Sudoku - Jogo finalizado.")

            //---------------------
            prepJogoRessetado ()
            //---------------------

        }
    }

    //--- prepJogoRessetado
    private fun prepJogoRessetado () {

        strErro       = "0"
        strCronoConta = "00:00"

        //------------------------------------------
        finalizaPrepEIniciaJogo(false)
        //------------------------------------------

    }
    //--- prepJogoAContinuar
    private fun prepJogoAContinuar () {

        //---------------------------------------------------------------
        strErro = leCampo(strJogo, "<erros>", "</erros>")
        //----------------------------------------------------------------------------
        strCronoConta = leCampo(strJogo, "<tempoJogo>", "</tempoJogo>")
        //----------------------------------------------------------------------------

        //-----------------------------------------
        finalizaPrepEIniciaJogo(true)
        //-----------------------------------------

    }

    //--- finalizaPrepEIniciaJogo
    private var quadMaiorAdapta = Array(9) { Array(9) { 0 } }
    private var quadMaior       = Array(9) { Array(9) { 0 } }
    private var strSubNivelJogo = "0"

    private fun finalizaPrepEIniciaJogo(flagContinua : Boolean) {

        //--- Instancializações e inicializações
        var strTagInic  = ""
        var strTagFim   = ""
        val intQtiZeros = utilsKt.quantZeros(quadMaior)

        //--- Nível
        strTagInic = "<nivel>"
        strTagFim  = "</nivel>"
        //-------------------------------------------------------
        strNivelJogo = leCampo(strJogo, strTagInic, strTagFim)
        //-------------------------------------------------------
        //--- Subnível
        strTagInic = "<subnivel>"
        strTagFim  = "</subnivel>"
        //----------------------------------------------------------
        strSubNivelJogo = leCampo(strJogo, strTagInic, strTagFim)
        //----------------------------------------------------------

        //--- Leitura da matriz bidimensional-proposta de jogo
        strTagInic = if (flagContinua) "<body2>"  else "<body>"
        strTagFim  = if (flagContinua) "</body2>" else "</body>"

        //------------------------------------------------------
        var strLeit = leCampo(strJogo, strTagInic, strTagFim)
        //------------------------------------------------------

        if (strLeit.isEmpty()) {

            strLog = "Jogo inválido!!!"
            Log.d(cTAG, "-> $strLog")

            //--------------------------------------------------------------
            Toast.makeText(this, strLog, Toast.LENGTH_LONG).show()
            //--------------------------------------------------------------

            return

        }

        for (idxLin in 0..8) {
            //--------------------------------------------------------------------------------------
            val strLinhaQM   = leCampo(strLeit, "<linha$idxLin>", "</linha$idxLin>")
            //--------------------------------------------------------------------------------------
            val arStrLinhaQM = strLinhaQM.split(", ",",")

            for (idxCol in 0..8) {
                val strDado = arStrLinhaQM[idxCol].trim()
                if (strDado.isDigitsOnly()) {
                    quadMaiorAdapta[idxLin][idxCol] = strDado.toInt()
                }
            }
        }

        //--- Envia o jogo gerado para ser usado como gabarito
        //---------------------------------------------------------
        sgg.quadMaiorRet = utilsKt.copiaArArInt(quadMaiorAdapta)
        //---------------------------------------------------------
        //--- Gera o gabarito! em um array unidimensional
        //---------------------------------------
        quadMaior = sgg.adaptaJogoAlgoritmo2()
        //---------------------------------------
        val arIntNumsGab = ArrayList<Int>()
        for (idxLin in 0..8) {
            Log.d(cTAG, "idxLin = $idxLin")
            for (idxCol in 0..8) {
                //-------------------------------------------------
                arIntNumsGab += sgg.quadMaiorRet[idxLin][idxCol]
                //-------------------------------------------------
            }
        }

        //--- Prepara o jogo preparado para ser jogado em um array unidimensional
        val arIntNumsJogo = ArrayList<Int>()
        for (idxLin in 0..8) {
            for (idxCol in 0..8) {
                //-------------------------------------------
                arIntNumsJogo += quadMaior[idxLin][idxCol]
                //-------------------------------------------
            }
        }

        //----------------------------------------------------------
        val flagJogoValido = main.verificaSeJogoValido(quadMaior)
        //----------------------------------------------------------

        if (!flagJogoValido) {

            //flagJogoAdaptadoOk = false

            Toast.makeText(this, "Jogo adaptado inválido!", Toast.LENGTH_SHORT).show()

        }
        else {

            //flagJogoAdaptadoOk = true

            //--- Prepara a Intent para chamar JogarActivity
            val intent    = Intent(this, JogarActivity::class.java)
            intent.action = strOpcaoJogo

            intent.putExtra("strNivelJogo"   , strNivelJogo)
            intent.putExtra("strSubNivelJogo", strSubNivelJogo)
            intent.putExtra("strCronoConta"  , strCronoConta)
            intent.putExtra("strErro"        , strErro)
            intent.putIntegerArrayListExtra("GabaritoDoJogo", arIntNumsGab)
            intent.putIntegerArrayListExtra("JogoPreparado" , arIntNumsJogo)
            //----------------------
            startActivity(intent)
            //----------------------

        }

    }

    //--- preparaItemInfoArq
    private fun preparaItensInfosArq(strArqName : String) : String {

        var strPrepInfoArq = ""

        try {

            //--- Leitura do Arquivo
            //-------------------------------------
            val strLeitArq = leitArq(strArqName)
            //-------------------------------------

            //--- Nome do arquivo
            strPrepInfoArq = "Arq: $strArqName"

            //--- Opção de jogo - 02/01/2022 - vers 8.4
            var intIdxFim   = 0
            var strTag      = "<opcaoJogo>"
            var intIdxInic  = strLeitArq.indexOf(strTag)
            if (intIdxInic > 0) {
                intIdxInic     += strTag.length
                intIdxFim       = strLeitArq.indexOf("</opcaoJogo>")
                strPrepInfoArq += "  " + strLeitArq.substring(intIdxInic, intIdxFim)
            }

            //--- DataHora
            strPrepInfoArq += "  Data: "
            strTag          = "<dataHora>"
            intIdxInic      = strLeitArq.indexOf(strTag) + strTag.length
            intIdxFim       = strLeitArq.indexOf("</dataHora>")
            strPrepInfoArq += strLeitArq.substring(intIdxInic, intIdxFim)
            //--- Status
            strPrepInfoArq += "  Status: "
            strTag          = "<status>"
            intIdxInic      = strLeitArq.indexOf(strTag) + strTag.length
            intIdxFim       = strLeitArq.indexOf("</status>")
            strPrepInfoArq += strLeitArq.substring(intIdxInic, intIdxFim)

        }
        catch (exc : Exception) {

            Log.d(cTAG, "Erro: ${exc.message}")

        }

        return strPrepInfoArq

    }

    //--- preparaItemInfoJogo
    private fun preparaItensInfosJogo(strArqName : String) : String {

        var strPrepInfoJogo = ""

        try {

            //--- Leitura do Arquivo
            //-------------------------------------
            val strLeitArq = leitArq(strArqName)
            //-------------------------------------

            //--- Nivel
            strPrepInfoJogo  = "Nivel: "
            var strTag       = "<nivel>"
            var intIdxInic   = strLeitArq.indexOf(strTag) + strTag.length
            var intIdxFim    = strLeitArq.indexOf("</nivel>")
            strPrepInfoJogo += strLeitArq.substring(intIdxInic, intIdxFim)

            //--- SubNivel
            strPrepInfoJogo += "  sub: "
            strTag           = "<subnivel>"
            intIdxInic       = strLeitArq.indexOf(strTag) + strTag.length
            intIdxFim        = strLeitArq.indexOf("</subnivel>")
            strPrepInfoJogo += strLeitArq.substring(intIdxInic, intIdxFim)

            //--- Erros
            strPrepInfoJogo += "  Erros: "
            strTag           = "<erros>"
            intIdxInic       = strLeitArq.indexOf(strTag) + strTag.length
            intIdxFim        = strLeitArq.indexOf("</erros>")
            strPrepInfoJogo += strLeitArq.substring(intIdxInic, intIdxFim)

            //--- tempo de jogo
            strPrepInfoJogo  += "  tempo: "
            strTag           = "<tempoJogo>"
            intIdxInic       = strLeitArq.indexOf(strTag) + strTag.length
            intIdxFim        = strLeitArq.indexOf("</tempoJogo>")
            strPrepInfoJogo += strLeitArq.substring(intIdxInic, intIdxFim)
        }

        catch (exc : Exception) {

            Log.d(cTAG, "Erro: ${exc.message}")

        }

        return strPrepInfoJogo

    }

    //--- leitArq
    private fun leitArq(strArqName : String) : String {

        //--- Lê o arquivo
        //------------------------------------------------
        val strNomeComPath = "sudoku/Jogos/$strArqName"
        //--------------------------------------------------------------------------------
        val arStrsLeitArq: ArrayList<String> = utils.leitExtMemTextFile(strNomeComPath)
        //--------------------------------------------------------------------------------

        //--- Converte o arq de ArrayList para String
        var strLeitArq = ""
        for (strLidaArq in arStrsLeitArq) { strLeitArq += strLidaArq }

        //--- Retorna
        return strLeitArq.trimStart()

    }

    //--- leCampo
    private fun leCampo(itemList : String, tagInic : String, tagFim : String) : String {

        var strRetorno = ""

        try {
            val intIdxInic = itemList.indexOf(tagInic) + tagInic.length
            val intIdxFim = if (tagFim.isEmpty()) itemList.length else itemList.indexOf(tagFim)

            strRetorno = itemList.substring(intIdxInic, intIdxFim)
        }
        catch (exc : Exception) {

            Log.d(cTAG, "-> Não existe esse campo!")
            strRetorno = ""

        }
        return strRetorno

    }

    //private lateinit var toolBar     : androidx.appcompat.widget.Toolbar
    //private lateinit var progressBar : ProgressBar

    //------------------------------------------------------------------------------------------
    // Implementa o Progress Bar
    //------------------------------------------------------------------------------------------
    /*
    progressBar = ProgressBar(this)

    //setting height and width of progressBar
    progressBar.layoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT)

    //accessing our relative layout where the progressBar will add up
    val layout = findViewById<RelativeLayout>(R.id.layoutProgBar)
    // Add ProgressBar to our layout
    layout?.addView(progressBar)

    //--- Ativa o progressBar
    progressBar.visibility = View.VISIBLE
    */

    //------------------------------------------------------------------------------------------
    // Implementa o actionBar
    //------------------------------------------------------------------------------------------
    /*
    toolBar = findViewById(R.id.toolbar)
    setSupportActionBar(toolBar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    */

    /*
    //---------------------------------------------------------------------------------------
    customAdapter = JogoAdapter(itemsListArq, itemsListJogo, object : JogoClickedListener {
    //---------------------------------------------------------------------------------------

        //--- Listener para click na info do arquivo de um dos jogos
        override fun infoItem (posicao : Int) {

            //---------------------------------------------------------------------------------
            val strfileName = leCampo(itemsListArq[posicao], "Arq: ", " Data:")
            //---------------------------------------------------------------------------------

            strToast = "Tapped $posicao: $strfileName!"
            Toast.makeText(baseContext, strToast, Toast.LENGTH_SHORT).show()

            //-------------------------
            adaptaEjogaJogo(posicao)
            //-------------------------

        }

        //--- Listener para click na info de um dos jogos
        override fun jogoItem(posicao : Int) {

            //---------------------------------------------------------------------------------
            val strNivel = leCampo(itemsListJogo[posicao], "Nivel: ", " sub: ")
            //---------------------------------------------------------------------------------

            strToast = "Tapped $posicao: $strNivel!"
            Toast.makeText(baseContext, strToast, Toast.LENGTH_SHORT).show()

            //-------------------------
            adaptaEjogaJogo(posicao)
            //-------------------------

        }

    })

    recyclerView!!.adapter = customAdapter
    */

    //--- Desativa o progressbar
    // progressBar.visibility = View.INVISIBLE

}