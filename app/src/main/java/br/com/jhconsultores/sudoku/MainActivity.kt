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

    //----------------------------------------------------------------------------------------------
    // Funções
    //----------------------------------------------------------------------------------------------
    /*
    private fun geraJogoOLD() {

        //--- Instancializações e inicializações
        var flagErroGer : Boolean
        var numTentaQM = 0         // Tentativas de geração do quadrado maior
        var numTentaQm : Int       // Tentativas de geração do quadrado menor

        //--- Tenta 10x gerar o quadrado Maior Sodoku
        do {

            Log.d(cTAG, "-> Tentativa: ${numTentaQM + 1}")

            //----------------------
            inicializaQuadMaior()
            //----------------------
            listaQuadMaior()
            //-----------------

            flagErroGer = false

            //--- Para todos os 9 quadrados menores
            for (quad in 0..8) {

               Log.d(cTAG, "-> Q$quad:")

               //--- Calcula as linhas desse quadrado
               // INT(EXT.TEXTO(F17;2;1)/3)*3
               val linhaInic = (quad / 3) * 3
               val linhasQuad = arrayOf(linhaInic, linhaInic + 1, linhaInic + 2)

               //--- Calcula as colunas desse quadrado
               // EXT.TEXTO(F17;2;1)*3-INT(EXT.TEXTO(F17;2;1)/3)*9
               val colInic  = quad * 3 - (quad / 3) * 9
               val colsQuad = arrayOf(colInic, colInic + 1, colInic + 2)

               //--- Para todas as linhas desse quadrado tenta 10x a geração do quadrado menor
               numTentaQm = 0
               do {

                   Log.d(cTAG, "-> numTentaQm = $numTentaQm + 1")

                   var flagExisteQ = false
                   var numDisp     = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)  // Evita repetição de número gerado

                   for (linha in linhasQuad) {

                       //--- Para todas as colunas desse quadrado
                       for (coluna in colsQuad) {

                           //--- Gera um número aleatório até gerar um número INEXISTENTE nessa linha e
                           //    nessa coluna.
                           numDisp = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)  // Evita repetição de número gerado

                           //----------------------------------
                           val numero: Int = (1..9).random()        // generated random from 1 to 9 included
                           //----------------------------------

                           //--- Só tenta utilizar o numero gerado se ele ainda não o foi
                           if (numDisp[numero - 1] == 0) {

                               numDisp[numero - 1] = numero
                               Log.d(cTAG, "- numGerado = $numero")

                               //--- Verifica se o número gerado ainda NÃO existe no quadrado menor
                               do {

                                   flagExisteQ = false
                                   for (linhaQ in linhasQuad) {
                                       for (colQ in colsQuad) {
                                           if (quadMaior[linhaQ][colQ] == numero) {
                                               flagExisteQ = true
                                               break
                                           }
                                       }
                                       if (flagExisteQ) break
                                   }

                                   //--- Se existe sai da pesquisa para gerar novo número RND
                                   if (flagExisteQ) break

                                   //--- Se NÃO existe, pesquisa nos seus vizinhos.
                                   else {

                                       //--- O número não pode existir na mesma linha
                                       for (colQM in 0..8) {

                                           if ((colQM != coluna) && (quadMaior[linha][colQM] == numero)) {
                                               flagExisteQ = true
                                               break
                                           }

                                       }

                                       //--- O número não pode existir na mesma coluna
                                       if (!flagExisteQ) {

                                           for (linhaQM in 0..8) {

                                               if ((linhaQM != linha) && (quadMaior[linhaQM][coluna] == numero)) {
                                                   flagExisteQ = true
                                                   break
                                               }

                                           }
                                       }
                                   }
                                   if (flagExisteQ) numTentaQm++

                               } while (numTentaQm < 10 && !flagExisteQ)

                           }

                           //--- Se o número está disponível na linha armazena-o no quadrado maior
                           //    (externo); senão gera novo número.
                           if (!flagExisteQ) {

                               //--- Armazena-o
                               quadMaior[linha][coluna] = numero
                               strLog = "quadMaior[$linha][$coluna]= $numero "
                               Log.d(cTAG, strLog)

                           }

                           //--- Já tentou todos os números e não conseguiu achar um que não existia: sai!
                           else {

                               Log.d(cTAG, "NÃO conseguiu achar um número válido para:")
                               Log.d(cTAG, "quad = $quad linha = $linha coluna = $coluna")

                               flagErroGer = true
                               numTentaQM++

                           }
                           if (flagErroGer) break

                       }
                       if (flagErroGer) break

                   }

               } while (flagExisteQ && numDisp.contains(0))

                //-----------------
               listaQuadMaior()
               //-----------------

               if (flagErroGer) break
           }

        } while (flagErroGer && numTentaQM < 10)

    }
    */

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
            var quad        = 0
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
                else if (++numTentaQm >= 10) {  numTentaQM++ }

            } // fim de Enqto não gerar Qm válido em até 10 tentativas

            //-----------------
            listaQuadMaior()
            //-----------------

        } // fim de Enqto não gerar QM válido em até 10 tentativas

        Log.d(cTAG, if (numTentaQM >= 10) "-> NÃO gerou o jogo!" else "-> Gerou o jogo!")

        //--- Verifica a validade do jogo
        var qtiCols = 0
        var qtiLin  = 0
        for (indxLin in 0..8) {

            strLog = "-> Linha: $indxLin faltam os numeros: "
            var numDisp = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            for (indxCol in 0..8) {

                val valCel = quadMaior[indxLin][indxCol]
                numDisp[valCel - 1] = valCel

            }


            for (indxCol in 0..8) {

                if (numDisp[indxCol] == 0) {

                    strLog += "${indxCol + 1} "
                    qtiCols++

                }

            }
            if (qtiCols > 0) Log.d(cTAG, strLog)

        }

        for (indxCol in 0..8) {

            strLog = "-> Coluna: $indxCol faltam os numeros: "
            var numDisp = arrayOf ( 0, 0, 0, 0, 0, 0, 0, 0, 0 )
            for (indxLin in 0..8) {

                val valCel          = quadMaior[indxLin][indxCol]
                numDisp[valCel - 1] = valCel

            }

            for (indxLin in 0..8) {

                if (numDisp[indxLin] == 0) {

                    strLog += "${indxLin + 1} "
                    qtiLin ++

                }

            }
            if (qtiLin > 0) Log.d(cTAG, strLog)

        }

        Log.d(cTAG, if (qtiLin != 0 || qtiCols != 0) "-> Jogo NÃO válido!" else "-> Jogo Válido!")

    }

    //--- geraQm
    private fun geraQm (quadMenor : Int) : Boolean {

        //--- Instancializações e inicializações
        Log.d(cTAG, "-> Q$quadMenor:")

        //--- Calcula as linhas desse quadrado
        // INT(EXT.TEXTO(F17;2;1)/3)*3
        val linhaInic = (quadMenor / 3) * 3
        val linhasQuad = arrayOf(linhaInic, linhaInic + 1, linhaInic + 2)

        //--- Calcula as colunas desse quadrado
        // EXT.TEXTO(F17;2;1)*3-INT(EXT.TEXTO(F17;2;1)/3)*9
        val colInic  = quadMenor * 3 - (quadMenor / 3) * 9
        val colsQuad = arrayOf(colInic, colInic + 1, colInic + 2)

        var flagGeracao = true

        //--- Tenta gerar os números para as linhas e colunas do quadMenor
        var numDispQm : Array<Int> //= arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)  // Evita repetição de número gerado

        var idxLinhaQ = 0
        var idxColQ   = 0

        var linQ : Int
        var colQ : Int

        while (flagGeracao && idxLinhaQ < 3) {

            linQ = linhasQuad[idxLinhaQ++]

            idxColQ = 0
            while (flagGeracao && idxColQ < 3) {

                colQ = colsQuad[idxColQ++]

                //--- Gera um número aleatório até gerar um número INEXISTENTE nessa linha e
                //    nessa coluna. Se não conseguir gerar um, reinicia a geração para esse quad.
                numDispQm = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
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
                    //--- Se numero já foi gerado para essa célula, tenta gerar outro (1,2,3 ... 9).
                    if (numDispQm[numero - 1] != 0) {

                        flagNumeroExiste = true
                        numTentaGerarNum++

                    }
                    //--- Senão, verifica se ele existe nesse quadrado ou, nessa linha ou nessa coluna.
                    else {

                        flagNumeroExiste = false

                        //--- Sinaliza número gerado
                        numDispQm[numero - 1] = numero
                        Log.d(cTAG, "- numGerado = $numero")

                        //--------------------------------------------------------------------------
                        // Verifica se o número gerado ainda NÃO existe no quadrado menor
                        //--------------------------------------------------------------------------
                        var idxLinQ = 0
                        while (!flagNumeroExiste && idxLinQ < 3) {

                            val linhaQ1 = linhasQuad[idxLinQ]

                            var idxColunQ = 0
                            while (!flagNumeroExiste && idxColunQ < 3) {

                                val colQ1 = colsQuad[idxColunQ]

                                if (quadMaior[linhaQ1][colQ1] == numero) {

                                    strLog  = "-> Número existente no Qm: $quadMenor lin =  "
                                    strLog += "$linhaQ1 col = $colQ1"
                                    Log.d(cTAG, strLog)

                                    flagNumeroExiste = true

                                }
                                else idxColunQ++

                            }
                            if (idxColunQ >= 3) idxLinQ++

                        }

                    } // fim da pesquisa se o número gerado já existe no quadMenor

                    //--------------------------------------------------------------------------
                    // Verifica se o número gerado ainda NÃO existe na linha ou na coluna
                    //--------------------------------------------------------------------------
                    //--- Se NÃO existe, pesquisa nos seus vizinhos.
                    if (!flagNumeroExiste) {

                        //--- O número não pode existir na mesma linha
                        var idxColQM = 0
                        while (!flagNumeroExiste && idxColQM <= 8) {

                            if ((idxColQM != colQ) && (quadMaior[linQ][idxColQM] == numero)) {

                                strLog  = "-> Número existente na mesma linha do QM: "
                                strLog += "$quadMenor lin = $linQ col = $idxColQM"
                                Log.d(cTAG, strLog)

                                flagNumeroExiste = true

                            }
                            idxColQM ++

                        }

                        //--- O número não pode existir na mesma coluna
                        if (!flagNumeroExiste) {

                            var idxLinQM = 0
                            while (!flagNumeroExiste && idxLinQM <= 8) {

                                if ((idxLinQM != linQ) && (quadMaior[idxLinQM][colQ] == numero)) {

                                    strLog  = "-> Número existente na mesma coluna do QM: "
                                    strLog += "$quadMenor lin = $idxLinQM col = $colQ"
                                    Log.d(cTAG, strLog)

                                    flagNumeroExiste = true

                                }
                                idxLinQM ++

                            }
                        }

                    } // fim da pesquisa se o número gerado já existe nos vizinhos

                    //--- Se o número está disponível, armazena-o no quadrado maior
                    //    (externo); senão gera novo número.
                    if (!flagNumeroExiste) {

                        //--- Armazena-o
                        quadMaior[linQ][colQ] = numero

                        strLog = "quadMaior[$linQ][$colQ]= $numero "
                        Log.d(cTAG, strLog)

                    }
                    //--- Se número existente
                    else {
                        //--- Se JÁ tentou todos os números, sai por erro
                        if (!numDispQm.contains(0)) return (false)

                        //--- Se ainda pode tentar, volta ao gerador de num RND; senão retorna false
                        else { if ((++numTentaGerarNum) >= limTentativaGeracaoRND) return (false) }

                    }

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