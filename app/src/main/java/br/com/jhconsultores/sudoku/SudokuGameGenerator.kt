package br.com.jhconsultores.sudoku

import android.annotation.SuppressLint
import android.util.Log
import br.com.jhconsultores.sudoku.SudokuBackTracking.intNumBackTracking

class SudokuGameGenerator {

    //----------------------------------------------------------------------------------------------
    //                         Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private val cTAG   = "Sudoku"
    private var strLog = ""
    
    var txtDados = ""

    var quadMaiorRet   = arrayOf<Array<Int>>()
    var intJogoAdaptar = 0

    var flagJogoGeradoOk   = false
    var flagJogoAdaptadoOk = false

    private var arArIntNums = Array(9) { Array(9) {0} }

    //----------------------------------------------------------------------------------------------
    //                      Gera Jogos (preset int[9][9] = { 0, 0, ..., 0 })
    //----------------------------------------------------------------------------------------------
    //--- GeraJogo
    @SuppressLint("SetTextI18n")
    fun geraJogo(nivelJogo : Int) : Array<Array<Int>> {

        flagJogoGeradoOk    = false

        var contaTentaJogo  = 0
        val limTentaJogo    = 150
        var flagQuadMenorOk : Boolean

        //-----------------------
        inicQuadMaiorGeracao()
        //-----------------------

        while (!flagJogoGeradoOk && contaTentaJogo < limTentaJogo) {  // && !flagTimeOut) {          //contaTentaJogo < 50) {  // 20) {   // 10) {

            //Log.d(cTAG, "-> Gera o jogo ${contaTentaJogo + 1}")

            for (quad in 0..8) {

                var array = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

                flagQuadMenorOk     = false
                var numTentaGeracao = 0
                while (!flagQuadMenorOk && numTentaGeracao < 50) {

                    //----------------------------
                    array = geraQuadMenor(quad)
                    //----------------------------

                    if (!array.contains(0) && !array.contains(-1)) flagQuadMenorOk = true

                    else { numTentaGeracao++ }

                }

                //--------------------------
                insereQmEmQM(quad, array)
                //--------------------------

            }

            //--- Verifica se o jogo Gerado é válido
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

                txtDados = ""

                //------------------------------------
                listaQM(quadMaiorRet, false)
                //------------------------------------
                flagJogoGeradoOk = true

            }
            else { contaTentaJogo ++ }

        }

        //--- Não conseguiu gerar um jogo
        if (contaTentaJogo >=limTentaJogo) {

            Log.d(cTAG, "-> Tentou $limTentaJogo jogos. Fim.")

        }

        //--- Conseguiu!
        else {
            //-----------------------------------------
            arArIntNums = copiaArArInt(quadMaiorRet)   // quadMaiorRet: jogo gerado (gabarito)
            //-----------------------------------------

            //--- Prepara arArIntNums para o jogo: deixa com zeros onde o usuário irá jogar;
            //    a qti de zeros será tão maior quanto o grau de dificuldade for maior.
            //-----------------------
            preparaJogo(nivelJogo)      // arArIntNums: jogo preparado para ser jogado
            //-----------------------

            Log.d(cTAG, "-> Jogo gerado preparado:")
            //-----------------------------------
            listaQM(arArIntNums, true)
            //-----------------------------------

            //--- Atribui um nível ao jogo: resolve o jogo pelo algoritmo backTracking;
            //    considerarei como o "nível" do jogo, quantas vezes foi necessária a recursão.
            //---------------------------------------------
            val arArIntCopia = copiaArArInt(arArIntNums)
            intNumBackTracking = 0
            //--------------------------------------------------------------------------------
            val flagSolOk = SudokuBackTracking.solveSudoku(arArIntCopia, arArIntCopia.size)
            //--------------------------------------------------------------------------------
            if (flagSolOk) {

                Log.d(cTAG, "-> Gabarito gerado pelo Backtracking:")
                //-------------------------------------
                listaQM(arArIntCopia, false)
                //-------------------------------------

                val intQtiZeros = quantZeros(arArIntNums)

                val strQtiZerosPad = intQtiZeros.toString().padStart(4)
                strLog   = String.format ( "%s %s", "-> Quantidade de clues:", strQtiZerosPad)
                Log.d(cTAG, strLog)
                txtDados = "${txtDados}\n$strLog"

                val strNivelJogoPad = intNumBackTracking.toString().padStart(4)
                strLog   = String.format( "%s %s", "-> Nível do jogo gerado:", strNivelJogoPad)
                Log.d(cTAG, strLog)
                txtDados = "${txtDados}\n$strLog"

            }
        }

        txtDados = "${txtDados}\n"

        return arArIntNums   // Jogo preparado
        
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
    private fun inicQuadMaiorGeracao() {

        quadMaiorRet = arrayOf()

        for (linha in 0..8) {

            var array = arrayOf<Int>()
            for (coluna in 0..8) { array += 0 }
            quadMaiorRet += array

        }
    }

    //----------------------------------------------------------------------------------------------
    //                            A partir de presets gera jogo
    //----------------------------------------------------------------------------------------------
    //--- AdaptaJogo algoritmo 2
    @SuppressLint("SetTextI18n")
    fun adaptaJogoAlgoritmo2 () : Array<Array<Int>> {

        val limAdaptaJogo   = 1    //20
        var contaAdaptaJogo = 0
        var arArIntJogo     = Array(9) { Array(9) {0} }

        flagJogoAdaptadoOk  = false

        while (++contaAdaptaJogo <= limAdaptaJogo) {

            Log.d(cTAG, "-> Preset $intJogoAdaptar")
            Log.d(cTAG, "   - tenta adaptar o jogo ($contaAdaptaJogo)")

            //val strTmp = "\n-> Adapta jogo com preset $intJogoAdaptar"

            // txtDados = "${txtDados}$strTmp"
            txtDados = ""

            //---------------------------------------
            //inicQuadMaiorAdaptacao(intJogoAdaptar)
            //---------------------------------------

            //--- Preset enviado pelo MainActivity
            //-------------------------------------
            listaQM(quadMaiorRet, false)
            //-------------------------------------
            arArIntJogo = copiaArArInt(quadMaiorRet)

            intNumBackTracking = 0
            //-------------------------------------------------------------------------------------
            flagJogoAdaptadoOk = SudokuBackTracking.solveSudoku(quadMaiorRet, quadMaiorRet.size)
            //-------------------------------------------------------------------------------------
            val intNumBackTracking = SudokuBackTracking.intNumBackTracking
            if (flagJogoAdaptadoOk) {

                strLog  = "-> Jogo com preset $intJogoAdaptar adaptado"
                Log.d(cTAG, strLog)

                txtDados = strLog

                Log.d(cTAG, "-> Gabarito do jogo (adaptação do preset):")
                //txtDados = "${txtDados}\n-> Gabarito:"
                //-------------------------------------
                listaQM(quadMaiorRet, false)
                //-------------------------------------

                Log.d(cTAG, "-> Jogo (preset):")
                //txtDados = "${txtDados}\n-> Preset:"
                //-------------------------------------
                listaQM(arArIntJogo, true)
                //-------------------------------------

                val intQtiZeros = quantZeros(arArIntJogo)

                val strQtiZerosPad = intQtiZeros.toString().padStart(4)
                strLog   = String.format ( "%s %s", "-> Quantidade de clues:", strQtiZerosPad)
                Log.d(cTAG, strLog)
                txtDados = "${txtDados}\n$strLog"

                val strNivelJogoPad = intNumBackTracking.toString().padStart(4)
                strLog   = String.format( "%s %s", "-> Nível do jogo gerado:", strNivelJogoPad)
                Log.d(cTAG, strLog)
                txtDados = "${txtDados}\n$strLog"

            }
            else {

                strLog = "-> Jogo com preset $intJogoAdaptar NÃO adaptado!"
                Log.d(cTAG, strLog)

            }
        }

        return arArIntJogo

    }

    /*
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
    */

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
    // flagShow: se true libera a apresentação do QM na tela do smartphone
    @SuppressLint("SetTextI18n")
    fun listaQM (quadMaior: Array<Array<Int>>, flagShow : Boolean) {

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
            //val strDadosTmp = strDados

            if (flagShow) txtDados = "${txtDados}$strDadosTmp"

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
            }
        }

        return arArIntTmp

    }

    //--- Prepara o jogo gerado (arArIntNums[9][9]) conforme as Regras 1,2,3,4
    private fun preparaJogo(nivelJogo : Int) {

        //Log.d(cTAG, "-> Jogo antes da preparação:")
        //-----------------------
        //listaQM (arArIntNums)
        //-----------------------

        //------------------------------------------------------------------------------------------
        // Regra1: todos os Qm devem conter pelo menos dois zeros cada
        //------------------------------------------------------------------------------------------
        var qtiZerosNivel = 0
        for (quadMenor in 0..8) {

            //--- Determina as coordenadas (3x3) do QM para esse Qm
            val arLinsQM = calcLinsQM(quadMenor)
            val arColsQM = calcColsQM(quadMenor)

            //--- Preenche esse Qm com os números gerados (QM)
            val arIntCelQM = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            for (idxLin in 0..2) {
                for (idxCol in 0..2) {

                    arIntCelQM[idxLin*3 + idxCol] = arArIntNums[arLinsQM[idxLin]][arColsQM[idxCol]]

                }
            }

            //--- Aleatóriamente preenche 2 células do Qm com zeros
            var intQtiZeros = 0
            val arIntNumRnd = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

            var flagQmOk    = false
            while (!flagQmOk) {

                var flagNumOk = false
                while (!flagNumOk) {

                    //--- Gera número aleatório sem repetição
                    //-----------------------------
                    val numRnd = (1..9).random()
                    //-----------------------------
                    if (arIntNumRnd[numRnd - 1] > 0) {

                        arIntNumRnd[numRnd - 1] = 0
                        flagNumOk = true

                        //--- Insere clues, se inseriu dois zeros passa ao px Qm
                        arIntCelQM [numRnd - 1] = 0
                        if (++intQtiZeros > 1) flagQmOk = true

                    }
                }
            }

            //--- Transfere os valores gerados do Qm ao QM (jogo a ser jogado)
            for (linMenor in 0..2) {

                for (colMenor in 0..2) {

                    val valCel = arIntCelQM[linMenor * 3 + colMenor]
                    arArIntNums[arLinsQM[linMenor]][arColsQM[colMenor]] = valCel

                    //--- Computa os zeros inseridos para definição do nível Fácil
                    if (valCel == 0 && (++qtiZerosNivel >= nivelJogo)) break

                }
            }
        }
        Log.d(cTAG, "-> Jogo após a preparação conforme a R1 (Qm's):")
        //-------------------------------------
        listaQM (arArIntNums, false)
        //-------------------------------------
        Log.d(cTAG, "   - R1: qti clues = $qtiZerosNivel")

        //------------------------------------------------------------------------------------------
        // Regra2: cada linha devem conter pelo menos dois zeros
        //------------------------------------------------------------------------------------------
        for (intLinha in 0..8) {

            val arIntNumRnd = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

            //--- Lê a linha toda no QM e armazena-a num vetor
            val arIntCelLin = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            for (intCol in 0..8) {arIntCelLin[intCol] = arArIntNums[intLinha][intCol]}

            //--- Conta qtos zeros essa linha já tem
            var intQtiZeros = 0
            for (intCol in 0..8) { if (arIntCelLin[intCol] == 0) intQtiZeros++ }

            //--- Enqto NÃO tiver pelo menos 2 zeros na linha, gera um índice aleatório e se o vetor
            //    que controla os numRND tiver nesse índice valor diferente de zero, zera-o.
            while (intQtiZeros < 2 && qtiZerosNivel < nivelJogo) {

                var flagNumOk = false
                while (!flagNumOk) {

                    //--- Gera número aleatório sem repetição
                    //-----------------------------
                    val numRnd = (1..9).random()
                    //-----------------------------
                    if (arIntNumRnd[numRnd - 1] > 0) {

                        arIntNumRnd[numRnd - 1] = 0
                        if (arIntCelLin [numRnd - 1] > 0) {

                            arIntCelLin [numRnd - 1] = 0
                            qtiZerosNivel ++

                        }

                        flagNumOk = true
                        intQtiZeros ++

                    }
                }
            }
            //--- Retorna as células à linha
            for (idxColQM in 0..8) { arArIntNums[intLinha][idxColQM] = arIntCelLin[idxColQM] }
        }
        Log.d(cTAG, "-> Jogo após a preparação conforme R2 (lin):")
        //-------------------------------------
        listaQM (arArIntNums, false)
        //-------------------------------------
        Log.d(cTAG, "   - R2: qti clues = $qtiZerosNivel")

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
            while (intQtiZeros < 2 && qtiZerosNivel < nivelJogo) {

                var flagNumOk = false
                while (!flagNumOk) {

                    //--- Gera número aleatório sem repetição
                    //-----------------------------
                    val numRnd = (1..9).random()
                    //-----------------------------
                    if (arIntNumRnd[numRnd - 1] > 0) {

                        arIntNumRnd[numRnd - 1] = 0
                        if (arIntCelCol [numRnd - 1] > 0) {

                            arIntCelCol [numRnd - 1] = 0
                            qtiZerosNivel++

                        }

                        flagNumOk = true
                        intQtiZeros ++

                    }
                }
            }
            //--- Retorna as células à coluna
            for (idxLinhaQM in 0..8) { arArIntNums[idxLinhaQM][intCol] = arIntCelCol[idxLinhaQM] }

        }
        Log.d(cTAG, "-> Jogo após a preparação conforme R3 (col):")
        //-------------------------------------
        listaQM (arArIntNums, false)
        //-------------------------------------
        Log.d(cTAG, "   - R3: qti clues = $qtiZerosNivel")

        //------------------------------------------------------------------------------------------
        // Regra4: para os níveis médios completa as casas com zero conforme o nível do jogo
        //------------------------------------------------------------------------------------------

        //--- Para a Regra4, determina a qtidd de Zeros no jogo
        //------------------------------------------
        var intQtiZeros = quantZeros(arArIntNums)
        //------------------------------------------
        //--- Inicializa um vetor para evitar repetição de números Rnd
        val arIntNumRnd = Array(81) { 0 }
        for (idxConta in 0..80) { arIntNumRnd[idxConta] = idxConta + 1 }

        while (intQtiZeros < nivelJogo) {

            var flagNumOk = false
            while (!flagNumOk) {

                //--- Gera número aleatório sem repetição
                //---------------------------------
                val numRndGen =
                    (1..81).random()     // Gera os números aleatórios de 1 a 81 inclus.
                //---------------------------------

                //Log.d(cTAG, "-> numRnd = $numRnd arIntNumRnd[numRnd]=${arIntNumRnd[numRnd]}" )
                val numRnd = numRndGen - 1
                if (arIntNumRnd[numRnd] > 0) {

                    arIntNumRnd[numRnd] = 0

                    val intLinha = numRnd / 9
                    val intColuna = numRnd % 9
                    if (arArIntNums[intLinha][intColuna] > 0) {

                        arArIntNums[intLinha][intColuna] = 0
                        intQtiZeros++

                    }
                    //Log.d(cTAG, "-> linha = $intLinha coluna = $intColuna" )

                    flagNumOk = true

                }
            }
        }
        strLog   = "-> Jogo após a preparação conforme R4 (QM):"
        Log.d(cTAG, strLog)
        //------------------------------------
        listaQM(arArIntNums, false)
        //------------------------------------
        Log.d(cTAG, "   - R4: qti clues = $intQtiZeros")

        txtDados = "-> Jogo preparado R(1,2,3,4):"

    }

    //--- quantZeros
    private fun quantZeros(arArIntJogo : Array <Array <Int>>) : Int{

        var intQtiZeros = 0
        for (idxLin in 0..8) {
            for (idxCol in 0..8) {
                if (arArIntJogo[idxLin][idxCol] == 0) intQtiZeros++
            }
        }
        //Log.d(cTAG, "-> Quantidade de Zeros: $intQtiZeros")

        return intQtiZeros

    }

}