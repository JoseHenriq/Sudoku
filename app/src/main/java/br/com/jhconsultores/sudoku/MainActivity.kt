package br.com.jhconsultores.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    //----------------------------------------------------------------------------------------------
    // Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private val cTAG   = "Sudoku"
    private var strLog = ""

    private var quadMaior   = arrayOf<Array<Int>>()

    private val quadMenoresP1 = arrayOf (0, 4, 8)
    private val quadMenoresP2 = arrayOf (1, 3, 2, 6, 5, 7)

    var btnGeraJogo : Button? = null

    //----------------------------------------------------------------------------------------------
    // Eventos
    //----------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //--- Instancializações e inicializações
        btnGeraJogo = findViewById(R.id.btn_GeraJogo)

    }

    //--- Evento tapping no botão
    fun btnGeraJogoClick(view: View?) {

        Log.d(cTAG, "-> Tap no btnJogaJogo")

        //--- Gera jogo
        //-----------
        geraJogo1()
        //-----------

    }

    //--- GeraJogo1
    private fun geraJogo1() {

        //--- Instancializações e inicializações
        //----------------------
        inicializaQuadMaior()
        //----------------------
        listaQuadMaior()
        //-----------------

        //--- Tenta gerar os 9 quadrados menores

        // Para quadrados menores da diagonal principal do QM NÃO precisa verificar repetições
        var strLog = "-> Quadrado maior para "

        /*
        for (quad in quadMenoresP1) {

            strLog += "Q$quad"
            if (quadMenoresP1.indexOf(quad) < quadMenoresP1.lastIndex) strLog += ", "

            var array = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

            //----------------------------
            array = geraQuadMenor(quad)
            //----------------------------
            listaQuadMenor(quad, array)
            //----------------------------
            insereQmEmQM(quad, array)
            //----------------------------

        }

        //--- Apresenta o jogo gerado para Q0,Q4 e Q8
        Log.d(cTAG, strLog)
        //-----------------
        listaQuadMaior()
        //-----------------

        //--- Outros quadrados precisarão ser checados quanto às repetições
        var flagQuadMenorOk = false
        for (quad in quadMenoresP2) {
        */

        var flagJogoOk      = false
        var contaTentaJogo  = 0
        var flagQuadMenorOk = false

        while (!flagJogoOk && contaTentaJogo < 20) {

            Log.d(cTAG, "-> Gera o jogo ${contaTentaJogo + 1}")

            for (quad in 0..8) {
            //for (quad in 0..7) {

                var array = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

                flagQuadMenorOk = false
                var numTentaGeracao = 0
                while (!flagQuadMenorOk && numTentaGeracao < 50) {

                    Log.d(cTAG, "   - tenta gerar Qm$quad: ${numTentaGeracao + 1}")

                    //----------------------------
                    array = geraQuadMenor(quad)
                    //----------------------------

                    //----------------------------
                    listaQuadMenor(quad, array)
                    //----------------------------

                    if (!array.contains(0) && !array.contains(-1)) flagQuadMenorOk = true
                    else {
                        numTentaGeracao++
                    }

                }

                //---------------------------
                insereQmEmQM(quad, array)
                //---------------------------
                listaQuadMaior()
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

            if (flagJogoVal) {

                Log.d(cTAG, "-> Jogo ${contaTentaJogo + 1}: válido!")
                flagJogoOk = true

            }

            else {

                Log.d(cTAG, "-> Jogo ${contaTentaJogo + 1}: inválido.")
                contaTentaJogo ++

            }

        }

    }

    //--- geraQuadMenor[quad]
    private fun geraQuadMenor (quadMenor: Int) : Array <Int> {

        //--- Instancializações e inicializações
        val arQuadMenor = arrayOf (0, 0, 0, 0, 0, 0, 0, 0, 0)
        var numDispCel  = arrayOf (0, 0, 0, 0, 0, 0, 0, 0, 0)

        //--- Para todas as linhas do Qm
        for (linQm in 0..2) {

            //--- Para todas as colunas de Qm
            for (colQm in 0..2) {

                val fimTenta          = 50
                var contaTentaGerarQm = 0
                var flagNumOk         = false
                var numero            = 0
                while (!flagNumOk && contaTentaGerarQm < fimTenta ) {

                    //--- Gera número aleatório sem repetição
                    //-------------------------
                    numero = (1..9).random()
                    //-------------------------

                    // Critério1: sem repetição no próprio Qm
                    if (numDispCel[numero - 1] == 0) {

                        arQuadMenor[linQm * 3 + colQm] = numero
                        flagNumOk = true

                    }

                    if (flagNumOk) {

                        // Critérios2: NÃO necessário para os Qm da diagonal principal do QM (0, 4, 8)
                        //if (quadMenor != 0 && quadMenor != 4 && quadMenor != 8) {

                        // Critérios2: usado para todos os Qm
                            //------------------------------------------------------------
                            flagNumOk = verifValidade1(quadMenor, linQm, colQm, numero)
                            //------------------------------------------------------------

                        //}

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

    //--- insere Qm no QM
    private fun insereQmEmQM (quadMenor : Int, array : Array <Int>) {

        // Converte os quadrados menores no quadMaior
        var valCel : Int

        //--- Calcula as linhas desse quadrado
        //------------------------------------------
        var linhasQuadQM = calcLinsQM (quadMenor)
        //------------------------------------------

        //--- Calcula as colunas desse quadrado
        //----------------------------------------
        var colsQuadQM = calcColsQM (quadMenor)
        //----------------------------------------

        for (linMenor in 0..2) {

            for (colMenor in 0..2) {

                valCel = array[linMenor * 3 + colMenor]

                quadMaior[linhasQuadQM[linMenor]][colsQuadQM[colMenor]] = valCel

            }
        }
    }

    //--- verifValidade de um número do Qm para inserção no QM
    private fun verifValidade1(quadMenor : Int, linhaQm : Int, colunaQm : Int,
                                                                          numero : Int) : Boolean {
        var flagNumeroOk = true

        //--- Calcula as linhas desse quadrado menor no QM
        //--------------------------------------
        var linhasQM = calcLinsQM (quadMenor)
        //--------------------------------------
        //--- Calcula as colunas desse quadrado menor no QM
        //------------------------------------
        var colsQM = calcColsQM (quadMenor)
        //------------------------------------

        //--- Converte a linha do numero gerado para o Qm para o do QM
        var linQM = linhasQM[linhaQm]
        //--- Converte a coluna do numero gerado para o Qm para a do QM
        var colQM = colsQM[colunaQm]

        //--- Verifica se número existe na LINHA do QM
        var numeroQM = 0
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

    //--- verifValidade do QM
    /*
    private fun verifValidade(quadMenor : Int, array : Array <Int>) : Boolean {

        var flagNumeroExiste = false

        //--- Verfica se o quadrado gerado pode ser usada nesse jogo
        var numero : Int

        //--- Calcula as linhas desse quadrado
        //-------------------------------------------
        var linhasQuadQM = calcLinsQM (quadMenor)
        //-------------------------------------------

        //--- Calcula as colunas desse quadrado
        //-----------------------------------------
        var colsQuadQM = calcColsQM (quadMenor)
        //-----------------------------------------

        var linQM = 0
        var colQM = 0

        for (linMenor in 0..2) {

            linQM = linhasQuadQM[linMenor]

            for (colMenor in 0..2) {

                numero = array[linMenor * 3 + colMenor]

                colQM  = colsQuadQM[colMenor]

                //--- O número não pode existir na mesma linha (linQM)
                var idxColQM2 = 0
                while (!flagNumeroExiste && idxColQM2 <= 8) {

                    if ((idxColQM2 != colQM) && (quadMaior[linQM][idxColQM2] == numero)) {

                        strLog = "-> Número $numero: existente na lin = $linQM col = $idxColQM2"
                        Log.d(cTAG, strLog)

                        flagNumeroExiste = true

                    }
                    idxColQM2++

                }

                //--- O número não pode existir na mesma coluna (colQM)
                if (!flagNumeroExiste) {

                    var idxLinQM2 = 0
                    while (!flagNumeroExiste && idxLinQM2 <= 8) {

                        if ((idxLinQM2 != linQM) && (quadMaior[idxLinQM2][colQM] == numero)) {

                            strLog = "-> Número $numero: existente na lin = $linQM col = $idxColQM2"
                            Log.d(cTAG, strLog)

                            flagNumeroExiste = true

                        }
                        idxLinQM2++

                    }
                }

            }

        }

        return !flagNumeroExiste

    }
    */

    //--- Calcula as linhas do QM para um Qm
    private fun calcLinsQM (quadMenor : Int) : Array <Int> {

        // INT(EXT.TEXTO(F17;2;1)/3)*3
        val linInicQM = (quadMenor / 3) * 3
        val arLinsQM  = arrayOf(linInicQM, linInicQM + 1, linInicQM + 2)

        return arLinsQM

    }

    //--- Calcula as colunas do QM para um Qm
    private fun calcColsQM (quadMenor : Int) : Array <Int> {

        // EXT.TEXTO(F17;2;1)*3-INT(EXT.TEXTO(F17;2;1)/3)*9
        val colInicQM = quadMenor * 3 - (quadMenor / 3) * 9
        val arColsQM  = arrayOf(colInicQM, colInicQM + 1, colInicQM + 2)

        return arColsQM

    }

    //--- GeraJogo
    /*
    private fun geraJogo() {

        Log.d(cTAG, "-> Gera o jogo.")

        //--- Instancializações e inicializações

        //--- Tenta gerar o quadrado Maior Sodoku até 10x
        var flagGerouQM = false
        var numTentaQM  = 0         // Tentativas de geração do quadrado maior
        while (!flagGerouQM && numTentaQM < 10) {

            Log.d(cTAG, "-> Tentativa geração do QM: ${numTentaQM + 1}")

            //----------------------
            inicializaQuadMaior()
            //----------------------
            listaQuadMaior()
            //-----------------

            //--- Tenta gerar cada quadrado menor válido até 10x
            var quad = 0

            var flagGerouQm = false
            var numTentaQm  = 0         // Tentativas de geração do quadrado menor
            while (!flagGerouQm && numTentaQm < 10) {

                Log.d(cTAG, "-> Tentativa geração do Q$quad: ${numTentaQm + 1}")

                //---------------------------
                flagGerouQm = geraQm(quad)
                //---------------------------

                //--- Gerou Qm válido
                if (flagGerouQm) {

                    //--- Gerou quadrado menor; passa ao px quadrado menor. Se gerou os 9, QM OK!
                    if (++quad >= 9) {

                        flagGerouQM = true

                    }

                    //--- Não gerou TODOS os Qm ainda; passa ao próximo Qm.
                    else {

                        flagGerouQm = false
                        numTentaQm  = 0

                    }
                }

                //--- Não gerou Qm válido: se já tentou mais do que 10x, tenta novo QM
                else numTentaQm ++

            } // fim de Enqto não gerar Qm válido em até 10 tentativas

            //-----------------
            listaQuadMaior()
            //-----------------

            //--- Se gerou QM, verifica se válido (NÃO poderão ter 0x em nenhuma célula)
            if (flagGerouQM) {

                Log.d(cTAG, "-> Tentativa de gerar QM = ${numTentaQM + 1}")

                //--- Verifica a validade do jogo
                var qtiCols = 0
                var qtiLin  = 0

                //--- Colunas
                for (indxLin in 0..8) {

                    strLog = "-> Linha: $indxLin faltam os números: "
                    var numDisp = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
                    for (indxCol in 0..8) {

                        val valCel = quadMaior[indxLin][indxCol]
                        if (valCel > 0) { numDisp[valCel - 1] = valCel }

                    }


                    for (indxCol in 0..8) {

                        if (numDisp[indxCol] == 0) {

                            strLog += "${indxCol + 1} "
                            qtiCols++

                        }

                    }
                    if (qtiCols > 0) Log.d(cTAG, strLog)

                }

                //--- Linhas
                for (indxCol in 0..8) {

                    strLog = "-> Coluna: $indxCol faltam os números: "
                    var numDisp = arrayOf ( 0, 0, 0, 0, 0, 0, 0, 0, 0 )
                    for (indxLin in 0..8) {

                        val valCel = quadMaior[indxLin][indxCol]
                        if (valCel > 0) { numDisp[valCel - 1] = valCel }

                    }

                    for (indxLin in 0..8) {

                        if (numDisp[indxLin] == 0) {

                            strLog += "${indxLin + 1} "
                            qtiLin ++

                        }

                    }
                    if (qtiLin > 0) Log.d(cTAG, strLog)

                }

                if (qtiLin != 0 || qtiCols != 0) {

                    Log.d(cTAG, "-> Jogo NÃO válido!")
                    flagGerouQM = false
                    numTentaQM ++

                }
                else { Log.d(cTAG, "-> Jogo Válido!") }

            }

            //--- Se não gerou QM
            else numTentaQM ++

        } // fim de Enqto não gerar QM válido em até 10 tentativas

    }
    */

    //--- geraQm
    /*
    private fun geraQm (quadMenor : Int) : Boolean {

        //--- Instancializações e inicializações
        Log.d(cTAG, "-> Q$quadMenor:")

        //--- Calcula as linhas desse quadrado
        // INT(EXT.TEXTO(F17;2;1)/3)*3
        val linhaInicQM  = (quadMenor / 3) * 3
        val linhasQuadQM = arrayOf(linhaInicQM, linhaInicQM + 1, linhaInicQM + 2)

        //--- Calcula as colunas desse quadrado
        // EXT.TEXTO(F17;2;1)*3-INT(EXT.TEXTO(F17;2;1)/3)*9
        val colInicQM  = quadMenor * 3 - (quadMenor / 3) * 9
        val colsQuadQM = arrayOf(colInicQM, colInicQM + 1, colInicQM + 2)

        //--- Define uma sequência para a geração
        val seqPref    = arrayOf(0, 4, 8)
        val seqGeracao = arrayOf(0, 4, 8, 2, 6, 1, 3, 5, 7)

        //--- Tenta gerar os números para as linhas e colunas do quadMenor
        var numDispCel : Array<Int>

        //--- Índices da linha e da coluna do Qm
        var linQm = 0
        var colQm = 0

        //--- Índices da linha e da coluna do QM
        var linQM : Int
        var colQM : Int

        while (linQm < 3) {

            //------------------------------
            linQM = linhasQuadQM[linQm++]
            //------------------------------

            colQm = 0
            while (colQm < 3) {

                //----------------------------
                colQM = colsQuadQM[colQm++]
                //----------------------------

                // <<< já definidos: [linQm, colQm] e [linQM, colQM]

                numDispCel = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

                //--- Gera um número aleatório até gerar um número INEXISTENTE nessa linha e
                //    nessa coluna. Se não conseguir gerar um, reinicia a geração para esse quad.

                //--- Se numDispCel[0] = 0 -> o número 1 AINDA não foi gerado e assim sucessivamente
                var numTentaGerarNum       = 0

                var flagNumeroExiste       = true
                val limTentativaGeracaoRND = 50

                while (flagNumeroExiste && numTentaGerarNum < limTentativaGeracaoRND) {

                    //------------------------------------------------------------------------------
                    // Gera número aleatório
                    //------------------------------------------------------------------------------
                    //----------------------------------
                    val numero: Int = (1..9).random()   // generated random from 1 to 9 included
                    //----------------------------------

                    //------------------------------------------------------------------------------
                    // Verifica se esse número ainda não foi gerado para essa célula
                    //------------------------------------------------------------------------------
                    //--- Se numero já gerado, tenta gerar outro (1,2,3 ... 9).
                    if (numDispCel[numero - 1] != 0) {

                        flagNumeroExiste = true
                        numTentaGerarNum++

                    }
                    //--- Senão, verifica se ele existe no Qm ou na linha ou coluna do QM.
                    else {

                        flagNumeroExiste = false

                        //--- Sinaliza número gerado
                        numDispCel[numero - 1] = numero
                        Log.d(cTAG, "- numGerado = $numero")

                        //--------------------------------------------------------------------------
                        // Verifica se o número gerado ainda NÃO existe no quadrado menor
                        //--------------------------------------------------------------------------
                        var idxLinQm = 0
                        while (!flagNumeroExiste && idxLinQm < 3) {

                            val linhaQM1 = linhasQuadQM[idxLinQm]

                            var idxColunQm = 0
                            while (!flagNumeroExiste && idxColunQm < 3) {

                                val colQM1 = colsQuadQM[idxColunQm]
                                if (quadMaior[linhaQM1][colQM1] == numero) {

                                    //strLog  = "-> Número existente: lin = $linhaQM1 col = $colQM1"
                                    //Log.d(cTAG, strLog)

                                    flagNumeroExiste = true

                                }
                                else idxColunQm++

                            }
                            if (idxColunQm >= 3) idxLinQm++

                        } // final de verif de número já existente no quadMenor

                        //--------------------------------------------------------------------------
                        // Verifica se o número gerado NÃO existe na linha e nem na coluna QM
                        //--------------------------------------------------------------------------
                        //--- Se NÃO existe, pesquisa nos seus vizinhos.
                        if (!flagNumeroExiste) {

                            //--- O número não pode existir na mesma linha (linQM)
                            var idxColQM2 = 0
                            while (!flagNumeroExiste && idxColQM2 <= 8) {

                                if ((idxColQM2 != colQM) &&
                                                          (quadMaior[linQM][idxColQM2] == numero)) {

                                    strLog  = "-> Número existente na mesma linha do QM: "
                                    strLog += "$quadMenor lin = $linQM col = $idxColQM2"
                                    Log.d(cTAG, strLog)

                                    flagNumeroExiste = true

                                }
                                idxColQM2 ++

                            }

                            //--- O número não pode existir na mesma coluna (colQM)
                            if (!flagNumeroExiste) {

                                var idxLinQM2 = 0
                                while (!flagNumeroExiste && idxLinQM2 <= 8) {

                                    if ((idxLinQM2 != linQM) &&
                                                          (quadMaior[idxLinQM2][colQM] == numero)) {

                                        strLog  = "-> Número existente na mesma coluna do QM: "
                                        strLog += "$quadMenor lin = $idxLinQM2 col = $colQM"
                                        Log.d(cTAG, strLog)

                                        flagNumeroExiste = true

                                    }
                                    idxLinQM2 ++

                                }
                            }

                        } // fim da pesquisa se o número gerado já existe nos vizinhos

                        //--------------------------------------------------------------------------
                        // Se o número está disponível, armazena-o no quadrado maior (externo);
                        // senão, gera novo número.
                        //--------------------------------------------------------------------------
                        if (!flagNumeroExiste) {

                            //--- Armazena-o
                            quadMaior[linQM][colQM] = numero

                            strLog = "quadMaior[$linQM][$colQM]= $numero "
                            Log.d(cTAG, strLog)

                        }

                        //--- Se número existente
                        else {

                            //--- Se gerou todos os números, entre o 1 e 9
                            if (!numDispCel.contains(0)) {

                                numTentaGerarNum ++

                                //--- Se já tentou o limite, retorna com erro
                                if (numTentaGerarNum >= limTentativaGeracaoRND) { return (false) }

                                //--- Tenta outra geração
                                /*
                                else {

                                    linQm = 0
                                    colQm = 0

                                    //----------------------------
                                    colQM = colsQuadQM[colQm++]
                                    //----------------------------
                                    linQM = linhasQuadQM[linQm++]
                                    //------------------------------

                                    numDispCel = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

                                }
                                */

                            }
                        }

                    } // fim se o número gerado já existe no quadMenor

                } // fim se número ainda não gerado

            } // fim do loop para as colunas do quadMenor

        } // fim do loop para as linhas do quadMenor

        return (true)

    }
    */

    //--- inicializaQuadMaior
    private fun inicializaQuadMaior() {

        quadMaior = arrayOf()

        for (linha in 0..8) {

            var array = arrayOf<Int>()
            for (coluna in 0..8) { array += 0 }
            quadMaior += array

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

}