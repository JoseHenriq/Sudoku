package br.com.jhconsultores.sudoku.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

import android.widget.Toast

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import br.com.jhconsultores.sudoku.R
import br.com.jhconsultores.sudoku.adapter.JogoAdapter
import br.com.jhconsultores.sudoku.adapter.JogoClickedListener

import br.com.jhconsultores.utils.Utils

class AdaptarActivity : AppCompatActivity() {

    //--------------------------------------------------------------------------
    //                    Instancializações e inicializações
    //--------------------------------------------------------------------------
    private var cTAG     = "Sudoku"
    private var strLog   = ""
    private var strToast = ""

    //private lateinit var toolBar     : androidx.appcompat.widget.Toolbar
    //private lateinit var progressBar : ProgressBar

    private val itemsListArq  = ArrayList<String>()
    private val itemsListJogo = ArrayList<String>()

    private lateinit var customAdapter: JogoAdapter
    private val utils = Utils()

    //--------------------------------------------------------------------------
    //                                Eventos
    //--------------------------------------------------------------------------
    //--- onCreate MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adaptar)

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

        //------------------------------------------------------------------------------------------
        // Implementa o recycler view
        //------------------------------------------------------------------------------------------
        // 1- referencia um objeto RecyclerView local ao declarado no layout
        val recyclerView: RecyclerView = findViewById(R.id.rv_jogos)

        // 2- RV assume o controle do layout
        val layoutManager          = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager

        // 3- referencia o ArrayList ao ViewHolder

        Log.d(cTAG, "-> Jogos salvos: ")

        //- Prepara os arrays list das infos para o RV
        //---------------------------------------------------------------------------------
        val arStrArqsNames = utils.listaExtMemArqDir("/Download/sudoku/Jogos")
        //---------------------------------------------------------------------------------
        if (arStrArqsNames.isNotEmpty()) {

            //val qtiMaxItens   = 8
            //var intContaItens = 0
            for (strArqName in arStrArqsNames) {

                //if (++intContaItens <= qtiMaxItens) {

                    Log.d(cTAG, "   - $strArqName")

                    //-----------------------------------------------------
                    itemsListArq.add(preparaItensInfosArq(strArqName))
                    //-----------------------------------------------------
                    itemsListJogo.add(preparaItensInfosJogo(strArqName))
                    //-----------------------------------------------------

                //}
                //else break

            }

        } else {

            strLog = "   - Não há arquivos de jogos no dir /Download/sudoku/Jogos"
            Log.d(cTAG, strLog)

        }

        // 4- Listeners para clique em um dos jogos
        //--------------------------------------------------------------------------------------
        customAdapter = JogoAdapter(itemsListArq, itemsListJogo, object : JogoClickedListener {
        //--------------------------------------------------------------------------------------

            //--- Listener para click em um dos jogos
            override fun infoItem (posicao : Int) {

                val itemListArq = itemsListArq[posicao]
                val intIdxInic  = itemListArq.indexOf("Arq: ") + 5
                val intIdxFim   = itemListArq.indexOf(" Data:")
                val strfileName = itemListArq.substring(intIdxInic, intIdxFim)

                strLog = "-> Tapped no infoItem $posicao: $strfileName!"
                Log.d(cTAG, strLog)

                strToast = "Tapped $posicao: $strfileName!"
                Toast.makeText(baseContext, strToast, Toast.LENGTH_SHORT).show()

            }

            override fun jogoItem(posicao : Int) {

                strLog = "-> Tapped no jogoItem do jogo $posicao!"
                Log.d(cTAG, strLog)

                strToast = "Tapped jogoItem $posicao!"
                Toast.makeText(baseContext, strToast, Toast.LENGTH_LONG).show()

            }

        })

        customAdapter.setHasStableIds(true)

        recyclerView.adapter = customAdapter

        //--- Desativa o progressbar
        // progressBar.visibility = View.INVISIBLE

    }

    //--------------------------------------------------------------------------
    //                                Funções
    //--------------------------------------------------------------------------
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

            //--- DataHora
            strPrepInfoArq += "  Data: "
            var strTag      = "<dataHora>"
            var intIdxInic  = strLeitArq.indexOf(strTag) + strTag.length
            var intIdxFim   = strLeitArq.indexOf("</dataHora>")
            strPrepInfoArq += strLeitArq.substring(intIdxInic, intIdxFim)

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
        //--------------------------------------------------
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

}