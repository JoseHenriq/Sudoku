package br.com.jhconsultores.sudoku.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import android.widget.CheckBox
import android.widget.TextView

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.core.view.children

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import br.com.jhconsultores.sudoku.R
import br.com.jhconsultores.sudoku.adapter.JogoAdapter
import br.com.jhconsultores.sudoku.adapter.JogoClickedListener
import br.com.jhconsultores.sudoku.jogo.SudokuGameGenerator
import br.com.jhconsultores.sudoku.ui.MainActivity.Companion.strApp
import br.com.jhconsultores.sudoku.ui.MainActivity.Companion.strOpcaoJogo

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

    private var itemsListArq    = ArrayList<String>()
    private var itemsListJogo   = ArrayList<String>()
    private var itemsListChkDel = ArrayList<Boolean>()

    private var recyclerView : RecyclerView? = null
    private var chkBtnDelete : CheckBox?     = null

    private lateinit var adaptarToolBar: androidx.appcompat.widget.Toolbar
    
    private var strNivelJogo = "Fácil"
    private var subNivelJogo = 0

    //private lateinit var arItemActionBarMenu: Array<MenuItem>
    //private lateinit var menu : Menu

    //private lateinit var itemActionMenuDel : MenuItem

    var SELECIONAR_TODOS = 0
    var DES_SELECIONAR   = 1
    var DELETA_SELECS    = 2
    var CANCELAR         = 3

    private val utils   = Utils ()
    private val utilsKt = UtilsKt ()
    private val sgg     = SudokuGameGenerator()
    private val main    = MainActivity ()

    //--------------------------------------------------------------------------
    //                                Eventos
    //--------------------------------------------------------------------------
    //--- onCreate AdaptarActivity
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adaptar)

        //------------------------------------------------------------------------------------------
        // Implementa o tool - action Bar
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

                //---------------------------
                itemsListChkDel.add(false)
                //---------------------------

            }

        } else {

            strLog = "   - Não há arquivos de jogos no dir /Download/sudoku/Jogos"
            Log.d(cTAG, strLog)

        }

        chkBtnDelete = findViewById(R.id.chkBoxSelArqDel)

    }

    //--- onResume
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

            //--- Instancia um adapter das listas ao RV passando um objeto interface dos listeners
            //--------------------------------
            atualizaRecyclerView(INVISIBLE)
            //--------------------------------

        } else {

            strLog = "   - Não há arquivos de jogos no dir /Download/sudoku/Jogos"
            Log.d(cTAG, strLog)
        }

    }

    //---------------------------------------------------------------------
    // Action Bar Menu
    //---------------------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        try {

            val infl = getMenuInflater()

            infl.inflate(R.menu.menu_adaptar, menu)
            //val menuItem = menu.getItem(DELETA_SELECS)
            //itemActionMenuDel = menuItem

        }
        catch (exc : Exception) {

            Log.d(cTAG, "-> Erro: ${exc.message}")

        }

//        itemActionMenuDel.isEnabled = false

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        //--- Tapping no menu do actionBar
        R.id.tresmore -> {

            Log.d(cTAG, "-> Tap no tresmore actionBar menu")

            //------------------------------
            atualizaRecyclerView(VISIBLE)
            //------------------------------

            true

        }

        //--- Tapping no item 'Selecionar todos' do menu do actionBar
        R.id.action_selecionarTodos -> {

            // User chose the "Settings" item, show the app settings UI...
            Log.d(cTAG, "-> Tap em actionBar / Selecionar Todos")

            for (idxChecked in itemsListChkDel.indices) {

                itemsListChkDel[idxChecked] = true

            }

            //------------------------------
            atualizaRecyclerView(VISIBLE)
            //------------------------------
 //           itemActionMenuDel.isEnabled = true

            true

        }

        //--- Tapping no item 'Des_selecionar' do menu do actionBar
        R.id.action_desSelecionar -> {

            // User chose the "Favorite" action, mark the current item as a favorite...
            Log.d(cTAG, "-> Tap em actionBar / Des_selecionar")

            for (idxChecked in itemsListChkDel.indices) {

                itemsListChkDel[idxChecked] = false

            }

            //------------------------------
            atualizaRecyclerView(VISIBLE)
            //------------------------------

//            itemActionMenuDel.isEnabled = false

            true

        }

        //--- Tapping no item 'Deletar selecionados' do menu do actionBar
        R.id.action_deletar_sels-> {

            // User chose the "Favorite" action, mark the current item as a favorite...
            Log.d(cTAG, "-> Tap em actionBar / Deletar selecionados")

            // TODO: código para a deleção dos jogos selecionados

            //------------------------------
            atualizaRecyclerView(VISIBLE)
            //------------------------------

//            itemActionMenuDel.isEnabled = false

            true

        }

        //--- Tapping no item 'Cancelar' do menu do actionBar
        R.id.action_cancelar -> {

            // User chose the "Favorite" action, mark the current item  as a favorite...
            Log.d(cTAG, "-> Tap em actionBar / Cancelar")

            //--------------------------------
            atualizaRecyclerView(INVISIBLE)
            //--------------------------------

//            itemActionMenuDel.isEnabled = false

            true

        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)

        }

    }

    //--------------------------------------------------------------------------
    //                                Funções
    //--------------------------------------------------------------------------
    //--- Adapta jogo selecionado no RV e passa a Jogar o jogo
    var strTextViews  = ""
    var strFileName   = ""
    var salvaFileName = ""
    var strJogo       = ""
    var strStatus     = ""
    var strErro       = "0"
    var strCronoConta = "00:00:00"

    fun adaptaEjogaJogo(idxItemView : Int) {

        strTextViews = ("${itemsListArq[idxItemView]}  ${itemsListJogo[idxItemView]}").trim()

        Log.d(cTAG, "-> item textViews:\n$strTextViews")

        val strTagFim = if (strTextViews.contains(" Jogo")) " Jogo" else "Data:"
        //------------------------------------------------------------------------
        strFileName   = (leCampo(strTextViews, "Arq:", strTagFim)).trim()
        //------------------------------------------------------------------------
        salvaFileName = strFileName
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
        //--- Gera o gabarito! em um array bidimensional
        //---------------------------------------
        quadMaior = sgg.adaptaJogoAlgoritmo2()
        //---------------------------------------
        //--- Prepara o gabarito, em um array unidimensional
        val arIntNumsGab = ArrayList<Int>()
        for (idxLin in 0..8) {
            Log.d(cTAG, "idxLin = $idxLin")
            for (idxCol in 0..8) {
                //-------------------------------------------------
                arIntNumsGab += sgg.quadMaiorRet[idxLin][idxCol]
                //-------------------------------------------------
            }
        }

        //--- Prepara o jogo à ser jogado, em um array unidimensional
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

            strOpcaoJogo = "JogoPresetado: $salvaFileName"

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
            strPrepInfoArq += "\nData: "
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

    //--- adaptaRecyclerView
    private fun atualizaRecyclerView(intVisibilidde : Int) {

        //------------------------------------------------------------------------------------------
        customAdapter = JogoAdapter(itemsListArq, itemsListJogo, itemsListChkDel, intVisibilidde,
                                                                     object : JogoClickedListener {
        //------------------------------------------------------------------------------------------

            //--- Listener para click na info do arquivo de um dos jogos
            override fun infoItem(posicao: Int) {

                //------------------------------------------------------------------------------
                val strfileName = leCampo(itemsListArq[posicao], "Arq: ", " Data:")
                //------------------------------------------------------------------------------

                strToast = "Tapped $posicao: $strfileName!"
                //Toast.makeText(baseContext, strToast, Toast.LENGTH_SHORT).show()
                Log.d(cTAG, "-> $strToast")

                //-------------------------
                adaptaEjogaJogo(posicao)
                //-------------------------

            }

            //--- Listener para click na info de um dos jogos
            override fun jogoItem(posicao: Int) {

                //------------------------------------------------------------------------------
                val strNivel = leCampo(itemsListJogo[posicao], "Nivel: ", " sub: ")
                //------------------------------------------------------------------------------

                // strToast = "Tapped $posicao: $strNivel!"
                //Toast.makeText(baseContext, strToast, Toast.LENGTH_SHORT).show()
                Log.d(cTAG, "-> $strToast")

                //-------------------------
                adaptaEjogaJogo(posicao)
                //-------------------------

            }

            //--- Listener para click no del sel check box de um dos jogos
            override fun checkBoxItem(posicao: Int, isChecked: Boolean) {

                Log.d(cTAG, "-> itemsChkDel: $itemsListChkDel")

                itemsListChkDel[posicao] = isChecked

                //--- Habilita ou desabilita a opção de deleção de jogo(s)

//                itemActionMenuDel.isEnabled = false
                for (idxItem in itemsListChkDel.indices) {

                    if (itemsListChkDel[idxItem]) {

//                        itemActionMenuDel.isEnabled = true
                        break

                    }
                }
            }

        })

        recyclerView!!.adapter = customAdapter

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