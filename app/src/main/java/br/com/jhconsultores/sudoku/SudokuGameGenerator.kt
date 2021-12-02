package br.com.jhconsultores.sudoku

import android.annotation.SuppressLint
import android.util.Log

class SudokuGameGenerator {

    //----------------------------------------------------------------------------------------------
    //                         Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private val cTAG   = "Sudoku"
    private var strLog = ""
    
    var txtDados = ""

    private var quadMaiorRet = arrayOf<Array<Int>>()
    var intJogoAdaptar = 1

    //----------------------------------------------------------------------------------------------
    // Funções para a geração de jogos; preset int[9][9] = { 0, 0, ..., 0 }
    //----------------------------------------------------------------------------------------------
    //--- GeraJogo
    @SuppressLint("SetTextI18n")
    fun geraJogo() : Array<Array<Int>> {

        var flagJogoOk      = false
        var contaTentaJogo  = 0
        val limTentaJogo    = 150
        var flagQuadMenorOk : Boolean

        //quadMaiorRet = arrayOf()

        while (!flagJogoOk && contaTentaJogo < limTentaJogo) {  // && !flagTimeOut) {          //contaTentaJogo < 50) {  // 20) {   // 10) {

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

                    val valCel = quadMaiorRet[idxLinhaQM][idxColQM]
                    if (valCel <= 0 || valCel > 9) flagJogoVal = false

                    if (!flagJogoVal) break
                }

                if (!flagJogoVal) break
            }

            if (flagJogoVal) {

                val strTmp = "-> Jogo ${contaTentaJogo + 1}: válido!"
                Log.d(cTAG, strTmp)

                txtDados = "${txtDados}\n$strTmp"

                //----------------------
                listaQM(quadMaiorRet)
                //----------------------
                flagJogoOk = true

            }
            else { contaTentaJogo ++ }

        }
        if (contaTentaJogo >=limTentaJogo) {
            Log.d(cTAG, "-> Tentou $limTentaJogo jogos. Fim.")}

        return quadMaiorRet
        
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
                        //-----------------------------------------------------------
                        flagNumOk = verifValidade(quadMenor, linQm, colQm, numero)
                        //-----------------------------------------------------------

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

    //--- inicquadMaiorRetGeracao
    fun inicQuadMaiorGeracao() {

        quadMaiorRet = arrayOf()

        for (linha in 0..8) {

            var array = arrayOf<Int>()
            for (coluna in 0..8) { array += 0 }
            quadMaiorRet += array

        }
    }

    //----------------------------------------------------------------------------------------------
    // Funções para a adaptação de jogos
    //----------------------------------------------------------------------------------------------
    //--- AdaptaJogo algoritmo 2
    @SuppressLint("SetTextI18n")
    fun adaptaJogoAlgoritmo2 () : Array<Array<Int>> {

        val limAdaptaJogo   = 1    //20
        var contaAdaptaJogo = 0

        quadMaiorRet = arrayOf()

        while (++contaAdaptaJogo <= limAdaptaJogo) {

            Log.d(cTAG, "-> Adapta o jogo $contaAdaptaJogo:")
            Log.d(cTAG, "   - preset $intJogoAdaptar")

            val strTmp = "\n-> Adapta jogo com preset $intJogoAdaptar"

            txtDados = "${txtDados}$strTmp"

            //---------------------------------------
            inicQuadMaiorAdaptacao(intJogoAdaptar)
            //---------------------------------------
            listaQM(quadMaiorRet)
            //----------------------

            //---------------------------------------------------------------------------------
            val flagJogoOk = SudokuBackTracking.solveSudoku(quadMaiorRet, quadMaiorRet.size)
            //---------------------------------------------------------------------------------
            val intNumBackTracking = SudokuBackTracking.intNumBackTracking
            if (flagJogoOk) {

                strLog  = "-> Jogo com preset $intJogoAdaptar adaptado"
                strLog += " ( backTracking = $intNumBackTracking ):"
                Log.d(cTAG, strLog)

                txtDados = "${txtDados}\n$strLog"

                listaQM(quadMaiorRet)
            }
            else {

                strLog = "-> Jogo com preset $intJogoAdaptar NÃO adaptado!"
                Log.d(cTAG, strLog)

            }
        }

        return quadMaiorRet

    }

    //--- inicQuadMaiorAdaptacao
    private fun inicQuadMaiorAdaptacao(jogoAdaptar : Int) {

        var quadMaiorAdapta = arrayOf<Array<Int>>()
        var array : Array <Int>

        //--- Simula os dados iniciais propostos
        when (jogoAdaptar) {

            1 -> run {

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

            2 -> run {

                for (linha in 0..8) {

                    // array = arrayOf<Int>()
                    array = when (linha) {

                        0 -> arrayOf(0, 6, 0, 7, 0, 8, 1, 9, 2)
                        1 -> arrayOf(1, 0, 5, 2, 0, 0, 0, 0, 7)
                        2 -> arrayOf(0, 2, 0, 0, 0, 6, 0, 0, 0)
                        3 -> arrayOf(0, 5, 0, 9, 3, 0, 0, 4, 0)
                        4 -> arrayOf(0, 0, 6, 5, 0, 2, 7, 8, 0)
                        5 -> arrayOf(9, 7, 0, 0, 0, 0, 3, 2, 5)
                        6 -> arrayOf(0, 0, 7, 4, 0, 0, 8, 0, 6)
                        7 -> arrayOf(8, 9, 4, 0, 7, 0, 0, 0, 0)
                        else -> arrayOf(0, 1, 0, 3, 0, 0, 0, 7, 4)

                    }
                    quadMaiorAdapta += array
                }
            }

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

            // 4
            else -> run {

                for (linha in 0..8) {

                    // array = arrayOf<Int>()
                    array = when (linha) {

                        0 -> arrayOf(9, 0, 0, 8, 4, 1, 3, 0, 0)
                        1 -> arrayOf(0, 0, 1, 9, 0, 0, 4, 2, 0)
                        2 -> arrayOf(0, 0, 0, 2, 0, 0, 0, 1, 0)
                        3 -> arrayOf(8, 7, 0, 1, 0, 0, 5, 4, 0)
                        4 -> arrayOf(1, 5, 0, 3, 6, 0, 0, 0, 2)
                        5 -> arrayOf(2, 0, 0, 0, 0, 0, 7, 6, 0)
                        6 -> arrayOf(7, 2, 0, 0, 0, 5, 1, 9, 0)
                        7 -> arrayOf(6, 3, 0, 0, 0, 0, 2, 0, 7)
                        else -> arrayOf(0, 1, 5, 7, 0, 2, 0, 0, 8)

                    }
                    quadMaiorAdapta += array
                }
            }
        }

        //--- Prepara o QM com os números propostos
        quadMaiorRet = arrayOf()
        for (linha in 0..8) {

            array = arrayOf()
            for (coluna in 0..8) { array += quadMaiorAdapta[linha][coluna] }

            quadMaiorRet += array

        }
        //-----------------
        //listaQuadMaior()
        //-----------------

    }

    //----------------------------------------------------------------------------------------------
    // Funções para jogar o jogo
    //----------------------------------------------------------------------------------------------
    fun jogaJogo () {

        strLog = "\n-> Jogar Sudoku"
        Log.d(cTAG, strLog)
        txtDados = "${txtDados}$strLog"

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

    //--- insere Qm no QM
    private fun insereQmEmQM (quadMenor : Int, array : Array <Int>) {

        // Converte os quadrados menores no quadMaiorRet
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

                quadMaiorRet[linhasQuadQM[linMenor]][colsQuadQM[colMenor]] = valCel

            }
        }
    }

    //--- listaquadMaiorRet
    @SuppressLint("SetTextI18n")
    fun listaQM (quadMaior: Array<Array<Int>>) {

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

            val strDadosTmp = "\n" + strDados
            txtDados = "${txtDados}$strDadosTmp"

        }
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

                numeroQM = quadMaiorRet[linQM][idxColQM]
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

                    numeroQM = quadMaiorRet[idxLinQM][colQM]
                    if (numero == numeroQM) {

                        flagNumeroOk = false
                        break

                    }
                }
            }
        }

        return flagNumeroOk

    }
}