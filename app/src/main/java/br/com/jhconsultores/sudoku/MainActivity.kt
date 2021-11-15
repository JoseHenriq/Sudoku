package br.com.jhconsultores.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    //----------------------------------------------------------------------------------------------
    // Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private val cTAG   = "Sudoku"
    private var strLog = ""

    private var quadMaior     = arrayOf<Array<Int>>()
    //private var numDispLinhas = arrayOf<Array<Int>>()
    //private var numDispCols   = arrayOf<Array<Int>>()

    //private var arrayPref   = arrayOf<Int>()
    //private var quadMenores = arrayOf<Array<Int>>()
    //private var idxDiagonal = 0

    //----------------------------------------------------------------------------------------------
    // Eventos
    //----------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //--- Instancializações e inicializações

        //--- Gera jogo
        //-----------
        geraJogo()
        //-----------

    }

    //--- GeraJogo
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

                    strLog = "-> Linha: $indxLin faltam os numeros: "
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

                    strLog = "-> Coluna: $indxCol faltam os numeros: "
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

    //--- geraQm
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

                //--- Gera um número aleatório até gerar um número INEXISTENTE nessa linha e
                //    nessa coluna. Se não conseguir gerar um, reinicia a geração para esse quad.

                //--- Se numDispCel[0] = 0 -> o número 1 AINDA não foi gerado e assim sucessivamente
                numDispCel = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
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

                                    strLog  = "-> Número existente: lin = $linhaQM1 col = $colQM1"
                                    Log.d(cTAG, strLog)

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

                            //--- Sinaliza que aproveitará o numero
                            numDispCel[numero - 1]  = numero

                            strLog = "quadMaior[$linQM][$colQM]= $numero "
                            Log.d(cTAG, strLog)

                        }

                        //--- Se número existente
                        else {
                            //--- Se gerou todos os números, entre o 1 e 9 limTentativaGeracaoRND vezes, sai por erro
                            if (!numDispCel.contains(0) &&
                                                   (++numTentaGerarNum) >= limTentativaGeracaoRND) {
                                return (false)
                            }
                        }

                    } // fim se o número gerado já existe no quadMenor

                } // fim se número ainda não gerado

            } // fim do loop para as colunas do quadMenor

        } // fim do loop para as linhas do quadMenor

        return (true)

    }

    //--- inicializaQuadMaior
    private fun inicializaQuadMaior() {
        quadMaior = arrayOf()
        for (linha in 0..8) {
            var array = arrayOf<Int>()
            for (coluna in 1..9) { array += 0 }
            quadMaior += array
        }
    }

    //--- listaQuadMaior
    private fun listaQuadMaior() {
        Log.d(cTAG, "-> Quadrado maior:")
        for (linha in 0..8) {
            strLog = "linha $linha : "
            for (coluna in 0..8) {
                strLog += "${quadMaior[linha][coluna]}" + if (coluna < 8) ", " else ""
            }
            Log.d(cTAG, strLog)
        }
    }

}