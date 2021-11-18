package br.com.jhconsultores.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import java.security.SecureRandom

//import android.os.CountDownTimer

// import java.security.SecureRandom

class MainActivity : AppCompatActivity() {

    //----------------------------------------------------------------------------------------------
    // Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private val cTAG   = "Sudoku"
    private var strLog = ""

    private var quadMaior = arrayOf<Array<Int>>()

    private var btnGeraJogo   : Button? = null
    private var btnAdaptaJogo : Button? = null

//    private lateinit var ctimer: CountDownTimer
    private  var flagTimeOut    = false
//    private  val timeOutGeracao = 5000L     // mseg

    private var secureTrnd = SecureRandom()

    //----------------------------------------------------------------------------------------------
    // Eventos da MainActivity
    //----------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //--- Instancializações e inicializações
        btnGeraJogo   = findViewById(R.id.btn_GeraJogo)
        btnAdaptaJogo = findViewById(R.id.btn_AdaptaJogo)

        //--- Para teste do CountDownTimer
        //---------------------------------------------------------
        // startTimer(timeOutGeracao, timeOutGeracao/2)
        //---------------------------------------------------------

    }

    //--- Evento tapping no botão de geração de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnGeraJogoClick(view : View?) {

        Log.d(cTAG, "-> Tap no btnGeraJogo")
        //----------------------
        inicQuadMaiorGeracao()
        //----------------------
        //--- Gera um novo jogo
        //-----------
        geraJogo()
        //-----------

    }

    //--- Evento tapping no botão de adaptação de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnAdaptaJogoClick(view : View?) {

        Log.d(cTAG, "-> Tap no btnAdaptaJogo")

        //-------------------------
        inicQuadMaiorAdaptacao()
        //-------------------------

        //--- Adapta jogo
        //-------------
        adaptaJogo()
        //-------------

    }

    /*
    //--- Evento onDestroy: destrói o timer
    override fun onDestroy() {

        //--- ctimer object
        if (ctimer != null) ctimer!!.cancel()

        super.onDestroy()

    }
     */

    //----------------------------------------------------------------------------------------------
    // Funções para a geração de jogos
    //----------------------------------------------------------------------------------------------
    //--- GeraJogo
    private fun geraJogo() {

        //--- Instancializações e inicializações
        var flagJogoOk      = false
        var contaTentaJogo  = 0
        val limTentaJogo    = 150
        var flagQuadMenorOk : Boolean

        //--- Tenta gerar os 9 quadrados menores
        flagTimeOut = false
        //---------------------------------------------------------
        // startTimer(timeOutGeracao, timeOutGeracao/2)
        //---------------------------------------------------------
        while (!flagJogoOk && contaTentaJogo < limTentaJogo && !flagTimeOut) {          //contaTentaJogo < 50) {  // 20) {   // 10) {

            //Log.d(cTAG, "-> Gera o jogo ${contaTentaJogo + 1}")

            for (quad in 0..8) {

                var array = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

                flagQuadMenorOk     = false
                var numTentaGeracao = 0
                while (!flagQuadMenorOk && numTentaGeracao < 50) {

                    //Log.d(cTAG, "   - tenta gerar Qm$quad: ${numTentaGeracao + 1}")

                    //----------------------------
                    array = geraQuadMenor(quad)
                    //----------------------------

                    //----------------------------
                    //listaQuadMenor(quad, array)
                    //----------------------------

                    if (!array.contains(0) && !array.contains(-1)) flagQuadMenorOk = true

                    else { numTentaGeracao++ }

                }

                //--------------------------
                insereQmEmQM(quad, array)
                //--------------------------

            }

            //--- Verifica se jogo válido
            var flagJogoVal = true
            for (idxLinhaQM in 0..8) {

                for (idxColQM in 0..8) {

                    val valCel = quadMaior[idxLinhaQM][idxColQM]
                    if (valCel <= 0 || valCel > 9) flagJogoVal = false

                    if (!flagJogoVal) break
                }

                if (!flagJogoVal) break
            }

            if (flagJogoVal) {

                Log.d(cTAG, "-> Jogo ${contaTentaJogo + 1}: válido!")
                //-----------------
                listaQuadMaior()
                //-----------------
                flagJogoOk = true

            }
            else {

                //-----------------
                //listaQuadMaior()
                //-----------------

                // Log.d(cTAG, "-> Jogo ${contaTentaJogo + 1}: inválido.")
                contaTentaJogo ++

            }

        }
        if (contaTentaJogo >=limTentaJogo) {Log.d(cTAG, "-> Tentou $limTentaJogo jogos. Fim.")}

        //--- Destrói o timer do timeOut da geração
//        if (ctimer != null) { ctimer.cancel() }

    }

    //--- geraQuadMenor[quad]
    private fun geraQuadMenor (quadMenor: Int) : Array <Int> {

        //--- Instancializações e inicializações
        val arQuadMenor = arrayOf (0, 0, 0, 0, 0, 0, 0, 0, 0)
        val numDispCel  = arrayOf (0, 0, 0, 0, 0, 0, 0, 0, 0)

        //--- Para todas as linhas do Qm
        for (linQm in 0..2) {

            //--- Para todas as colunas de Qm
            for (colQm in 0..2) {

                val fimTenta          = 20   // 50  // 10
                var contaTentaGerarQm = 0
                var flagNumOk         = false
                var numero : Int
                while (!flagNumOk && contaTentaGerarQm < fimTenta ) {

                    //--- Gera número aleatório sem repetição
                    //-------------------------
                    numero = (1..9).random()
                    //-------------------------

                    //secureTrnd.setSeed(secureTrnd.generateSeed(16))  // 32  64 128 NÃO OK
                    //numero = secureTrnd.nextInt(9) + 1

                    // Critério1: sem repetição no próprio Qm
                    if (numDispCel[numero - 1] == 0) {

                        arQuadMenor[linQm * 3 + colQm] = numero
                        flagNumOk = true

                    }

                    if (flagNumOk) {

                        // Critério2: verifica se número gerado pode ser inserido no jogo
                        //------------------------------------------------------------
                        flagNumOk = verifValidade(quadMenor, linQm, colQm, numero)
                        //------------------------------------------------------------

                    }
                    if (!flagNumOk) {

                        //--- Se o número gerado NÃO está ok (está presente na mesma linha ou coluna de
                        //    outro bloco) armazena -1 para sinalizar erro.
                        if (++contaTentaGerarQm >= fimTenta) {

                            arQuadMenor[linQm * 3 + colQm] = -1

                        }

                    }

                    //--- Se o número gerado ESTÁ ok armazena no array desse bloco
                    else {

                        arQuadMenor[linQm * 3 + colQm] = numero
                        numDispCel[numero - 1] = numero

                    }

                }
            }
        }

        return arQuadMenor

    }

    //--- inicQuadMaiorGeracao
    private fun inicQuadMaiorGeracao() {

        quadMaior = arrayOf()

        for (linha in 0..8) {

            var array = arrayOf<Int>()
            for (coluna in 0..8) { array += 0 }
            quadMaior += array

        }
        //-----------------
        // listaQuadMaior()
        //-----------------

    }

    //----------------------------------------------------------------------------------------------
    // Funções para a adaptação de jogos
    //----------------------------------------------------------------------------------------------
    //--- AdaptaJogo
    private fun adaptaJogo() {

        //--- Instancializações e inicializações
        var flagJogoOk      = false
        var contaTentaJogo  = 0
        val limTentaJogo    = 50  // 20
        var flagQuadMenorOk : Boolean

        //--- Tenta gerar os 9 quadrados menores
        flagTimeOut = false
        //---------------------------------------------------------
        // startTimer(timeOutGeracao, timeOutGeracao/2)
        //---------------------------------------------------------

        while (!flagJogoOk && contaTentaJogo < limTentaJogo && !flagTimeOut) {          //contaTentaJogo < 50) {  // 20) {   // 10) {

            Log.d(cTAG, "-> Adapta o jogo ${contaTentaJogo + 1}")

            //-------------------------
            inicQuadMaiorAdaptacao()
            //-------------------------

            for (quad in 0..8) {

                var array = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

                flagQuadMenorOk     = false
                var numTentaGeracao = 0
                while (!flagQuadMenorOk && numTentaGeracao < 50) {

                    //Log.d(cTAG, "   - tenta gerar Qm$quad: ${numTentaGeracao + 1}")

                    //------------------------------
                    array = adaptaQuadMenor(quad)
                    //------------------------------

                    // Log.d(cTAG, "-> quadMenor: $quad")
                    //----------------------------
                    //listaQuadMenor(quad, array)
                    //----------------------------

                    if (!array.contains(0) && !array.contains(-1)) flagQuadMenorOk = true

                    else {

                        numTentaGeracao++

                    }

                }

                //--------------------------
                insereQmEmQM(quad, array)
                //--------------------------
                //listaQuadMaior()
                //-----------------

            }

            //--- Verifica se jogo válido
            var flagJogoVal = true
            for (idxLinhaQM in 0..8) {

                for (idxColQM in 0..8) {

                    val valCel = quadMaior[idxLinhaQM][idxColQM]
                    if (valCel <= 0 || valCel > 9) flagJogoVal = false

                    if (!flagJogoVal) break
                }

                if (!flagJogoVal) break
            }

            //-----------------
            listaQuadMaior()
            //-----------------

            if (flagJogoVal) {

                //-----------------
                //listaQuadMaior()
                //-----------------

                Log.d(cTAG, "-> Jogo ${contaTentaJogo + 1}: válido!")
                flagJogoOk = true

            }

            else {

                //-----------------
                // listaQuadMaior()
                //-----------------

                Log.d(cTAG, "-> Jogo ${contaTentaJogo + 1}: inválido.")
                contaTentaJogo ++

            }

        }

        if (contaTentaJogo>=limTentaJogo) {Log.d(cTAG, "-> Tentou $limTentaJogo jogos. Fim.")}

        //--- Destrói o timer do timeOut da geração
//        if (ctimer != null) { ctimer.cancel() }

    }

    /*
    //--- adaptaQuadMenorOLD[quad]
    private fun adaptaQuadMenorOLD (quadMenor: Int) : Array <Int> {

        //--- Instancializações e inicializações
        val arQuadMenor   = arrayOf (0, 0, 0, 0, 0, 0, 0, 0, 0)
        val numDispCel    = arrayOf (0, 0, 0, 0, 0, 0, 0, 0, 0)
        var numPrndGerado : Array<Int>       // arrayOf (0, 0, 0, 0, 0, 0, 0, 0, 0)

        var idxQuadMenor : Int

        //--- Calcula as linhas desse quadrado
        //------------------------------------------
        val linhasQuadQM = calcLinsQM (quadMenor)
        //------------------------------------------
        //--- Calcula as colunas desse quadrado
        //----------------------------------------
        val colsQuadQM = calcColsQM (quadMenor)
        //----------------------------------------

        //--- Prepara o Qm com os números fornecidos
        for (linhaQm in 0..2) {

            val linhaQM = linhasQuadQM[linhaQm]

            //--- Para todas as colunas de Qm
            for (colunaQm in 0..2) {

                val colunaQM = colsQuadQM[colunaQm]

                //--- Calcula o índice do arQuadMenor
                idxQuadMenor = linhaQm * 3 + colunaQm
                val numeroQM = quadMaior[linhaQM][colunaQM]
                if (numeroQM > 0) {
                    arQuadMenor[idxQuadMenor] = numeroQM
                    numDispCel [numeroQM - 1] = numeroQM
                }

            }

        }

        //--- Para todas as linhas do Qm
        for (linQm in 0..2) {

            //--- Para todas as colunas de Qm
            for (colQm in 0..2) {

                //--- Verifica se precisa gerar um número para a adaptação
                idxQuadMenor = linQm * 3 + colQm
                if (arQuadMenor[idxQuadMenor] == 0) {

                    var numero: Int
                    numPrndGerado     = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
                    var qtiPrndGerado = 0
                    var flagNumOk     = false
                    val fimTenta      = 20   // 50  // 10
                    var contaTentaAdaptarQm = 0
                    while (!flagNumOk && contaTentaAdaptarQm < fimTenta) {

                        //--- Gera número aleatório sem repetição
                        //-------------------------
                        numero = (1..9).random()
                        //-------------------------

                        //secureTrnd.setSeed(secureTrnd.generateSeed(16))  // 32  64 128 NÃO OK
                        //numero = secureTrnd.nextInt(9) + 1

                        /*
                        if (!numPrndGerado.contains(numero)) {

                            numPrndGerado[numero - 1] = numero
                            qtiPrndGerado++
                         */

                            // Critério1: se o numero ainda NÃO foi usado no Qm verif se pode usá-lo
                            if (numDispCel[numero - 1] == 0) {

                                //Log.d(cTAG, "-> numero = $numero ")

                                // Critério2: verifica se número gerado pode ser inserido no jogo
                                //-----------------------------------------------------------
                                flagNumOk = verifValidade(quadMenor, linQm, colQm, numero)
                                //-----------------------------------------------------------
                                if (!flagNumOk) {

                                    //--- Se o número gerado NÃO está ok (está presente na mesma linha ou
                                    //    coluna de outro bloco) armazena -1 para sinalizar erro.
                                    if (++contaTentaAdaptarQm >= fimTenta) {

                                        arQuadMenor[linQm * 3 + colQm] = -1

                                    }
                                    Log.d(cTAG, "   numero NÃO ok")

                                }

                                //--- Se o número gerado ESTÁ ok armazena no array desse bloco
                                else {

                                    arQuadMenor[linQm * 3 + colQm] = numero
                                    numDispCel[numero - 1] = numero

                                    var strLog = "numero = $numero numDispCel: "
                                    for (indxNum in 0..8) { strLog += "${numDispCel[indxNum]}, " }
                                    Log.d(cTAG, strLog)

                                    if (!numDispCel.contains(0)) return arQuadMenor

                                }
                            }

                        /*
                            if (qtiPrndGerado >= 9) {

                                var strLog = "-> NÃO foi possível gerar um número"
                                strLog    += " para o Qm$quadMenor!"
                                //Log.d(cTAG, strLog)

                                return arQuadMenor

                            }
                        }
                         */

                    }
                    if (++contaTentaAdaptarQm > fimTenta) {

                        var strLog = "-> NÃO foi possível gerar o Qm$quadMenor"
                        strLog    += " em $contaTentaAdaptarQm vezes!"
                        //Log.d(cTAG, strLog)

                        return arQuadMenor

                    }
                }
            }
        }

        return arQuadMenor

    }
    */

    //--- adaptaQuadMenor[quad]
    private fun adaptaQuadMenor (quadMenor: Int) : Array <Int> {

        //--- Instancializações e inicializações
        val arQuadMenorT = arrayOf (0, 0, 0, 0, 0, 0, 0, 0, 0)   // backup
        val arQuadMenor  = arrayOf (0, 0, 0, 0, 0, 0, 0, 0, 0)   // : Array <Int>  //
        val numDispCel   = arrayOf (0, 0, 0, 0, 0, 0, 0, 0, 0)
        var idxQuadMenor : Int
        var flagNumOk = false

        //------------------------------------------------------------------------------------------
        // Calcula as linhas e as colunas do Qm no QM
        //------------------------------------------------------------------------------------------
        val linsQuadMaior = calcLinsQM (quadMenor)
        val colsQuadMaior = calcColsQM (quadMenor)

        //------------------------------------------------------------------------------------------
        // Transfere os dados do QM para um array
        //------------------------------------------------------------------------------------------
        //--- Para todas as linhas de Qm
        for (linhaQm in 0..2) {

            val linhaQM = linsQuadMaior[linhaQm]

            //--- Para todas as colunas de Qm
            for (colunaQm in 0..2) {

                val colunaQM = colsQuadMaior[colunaQm]

                //--- Calcula o índice do arQuadMenor
                idxQuadMenor = linhaQm * 3 + colunaQm
                val numeroQM = quadMaior[linhaQM][colunaQM]

                //--- Se número > 0, armazena no vetor e sinaliza no controlador dos 9 num Qm
                if (numeroQM > 0) {
                    arQuadMenorT[idxQuadMenor] = numeroQM
                    arQuadMenor[idxQuadMenor]  = numeroQM
                    numDispCel [numeroQM - 1]  = numeroQM
                }
            }
        }

        //---------------------------------------
        // listaQuadMenor(quadMenor, arQuadMenor)
        //---------------------------------------

        //------------------------------------------------------------------------------------------
        // Para as células do Qm que têm 0, gera um num rnd e verifica se ele pode ser usado.
        //------------------------------------------------------------------------------------------
        for (linQm in 0..2) {

            for (colQm in 0..2) {

                val idxNumCel = linQm * 3 + colQm
                val numeroQm  = arQuadMenor[idxNumCel]

                //--- Se nym cel == 0 é necessário se gerar um número rnd adequado para a cel
                if (numeroQm == 0) {

                    var contaTentaGerarRnd = 0
                    val fimContagem        = 50
                    while (!flagNumOk && (contaTentaGerarRnd < fimContagem)) {

                        //--- Gera número aleatório sem repetição
                        //-----------------------------
                        //val numero = (1..9).random()
                        //---------------------------

                        secureTrnd.setSeed(secureTrnd.generateSeed(16))  // 32  64 128 NÃO OK
                        //---------------------------------------------
                        val numero = secureTrnd.nextInt(9) + 1
                        //---------------------------------------------

                        //--- Verifica, caso ele ainda não pertença ao Qm, se pode incluí-lo na célula atual
                        if (!numDispCel.contains(numero)) {

                            //-----------------------------------------------------------
                            flagNumOk = verifValidade(quadMenor, linQm, colQm, numero)
                            //-----------------------------------------------------------
                            if (flagNumOk) {

                                arQuadMenor[idxNumCel] = numero
                                numDispCel[numero - 1] = numero

                                if (!numDispCel.contains(0)) { return arQuadMenor }
                                else flagNumOk = false

                            }
                            else { contaTentaGerarRnd ++ }
                        }
                    }
                }
            }
        }

        return arQuadMenor

    }

    //--- inicQuadMaiorAdaptacao
    private fun inicQuadMaiorAdaptacao() {

        var quadMaiorAdapta = arrayOf<Array<Int>>()
        var array : Array <Int>

        //--- Simula os dados iniciais propostos
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

        //--- Prepara o QM com os números propostos
        quadMaior = arrayOf()
        for (linha in 0..8) {

            array = arrayOf()
            for (coluna in 0..8) { array += quadMaiorAdapta[linha][coluna] }

            quadMaior += array

        }
        //-----------------
        //listaQuadMaior()
        //-----------------

    }

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

    //--- listaQuadMaior
    private fun listaQuadMaior() {

        for (linha in 0..8) {

            strLog = "linha $linha : "
            for (coluna in 0..8) {

                strLog += "${quadMaior[linha][coluna]}" + if (coluna < 8) ", " else ""

            }
            Log.d(cTAG, strLog)
        }
    }

    //--- listaQuadMenor
    private fun listaQuadMenor(quadMenor: Int, array : Array <Int>) {

        var strLog = "Q$quadMenor: "
        for (idxLin in 0..2) {

            for (idxCol in 0..2) {

                strLog += "${array[idxLin * 3 + idxCol]}"
                if (idxLin != 2 || idxCol != 2 ) strLog += ", "

            }
        }

        Log.d(cTAG, strLog)

    }

    //-------------------------------------------------------------------------
    // Ferramentas
    //-------------------------------------------------------------------------
    /*
    //--- startTimer
    private fun startTimer(lngTimeOutMs: Long, lngTimeTicksMs: Long) {

        ctimer = object : CountDownTimer(lngTimeOutMs, lngTimeTicksMs) {

            //--- Ticks
            override fun onTick(millisUntilFinished: Long) {

                Log.d(cTAG, "-> onTick faltam: $millisUntilFinished (mseg) to timeout")

            }

            //--- Finish
            override fun onFinish() {

                cancelTimer()

                Log.d(cTAG, "-> onFinish TimeOut")

                flagTimeOut = true    // Just in case ...

            }
        }
        ctimer.start()
        Log.d(cTAG, "-> partiu ctimer ($lngTimeOutMs, $lngTimeTicksMs)")

    }
     */

    /*
    //--- cancel timer
    fun cancelTimer() {
        if (ctimer != null) ctimer!!.cancel()
    }
     */

}
