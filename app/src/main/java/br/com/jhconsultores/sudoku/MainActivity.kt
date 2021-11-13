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

        //------------
        geraJogo1()
        //------------

    }

    //----------------------------------------------------------------------------------------------
    // Funções
    //----------------------------------------------------------------------------------------------
    private fun geraJogo() {

        //--- Instancializações e inicializações
        var flagErroGer : Boolean
        var numTentaQM = 0         // Tentativas de geração do quadrado maior
        var numTentaQm = 0         // Tentativas de geração do quadrado menor

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

    private fun geraJogo1() {

        //--- Instancializações e inicializações
        var numTentaQM = 0         // Tentativas de geração do quadrado maior
        //----------------------
        inicializaQuadMaior()
        //----------------------
        listaQuadMaior()
        //-----------------

        //--- Tenta 10x gerar o quadrado Maior Sodoku
        do {

            Log.d(cTAG, "-> Tentativa geração do QM: ${numTentaQM + 1}")

            //--- Para os 9 quadrados menores
            var flagGerouQm : Boolean
            var quad = 0
            do {

                //---------------------------
                flagGerouQm = geraQm(quad)
                //---------------------------
                if (flagGerouQm) quad++

            } while (flagGerouQm && quad < 9)

            var strLog = if (!flagGerouQm) "-> NÃO g" else "-> G"
            strLog += "erou o Qm : $quad"
            Log.d(cTAG, strLog)

            if (!flagGerouQm) numTentaQM++

            //-----------------
            listaQuadMaior()
            //-----------------

        } while (numTentaQM < 10)

        Log.d(cTAG, if (numTentaQM >= 10) "-> NÃO gerou o jogo!" else "-> Gerou o jogo!")

    }

    //--- geraQm
    private fun geraQm (quadMenor : Int) : Boolean {

        //--- Instancializações e inicializações
        var numTentaQm = 0         // Tentativas de geração de quadrado menor

        Log.d(cTAG, "-> Q$quadMenor:")

        //--- Calcula as linhas desse quadrado
        // INT(EXT.TEXTO(F17;2;1)/3)*3
        val linhaInic = (quadMenor / 3) * 3
        val linhasQuad = arrayOf(linhaInic, linhaInic + 1, linhaInic + 2)

        //--- Calcula as colunas desse quadrado
        // EXT.TEXTO(F17;2;1)*3-INT(EXT.TEXTO(F17;2;1)/3)*9
        val colInic  = quadMenor * 3 - (quadMenor / 3) * 9
        val colsQuad = arrayOf(colInic, colInic + 1, colInic + 2)

        //--- Tenta 10x a geração do quadrado menor
        var flagErroGer = false
        do {

            flagErroGer = false

            Log.d(cTAG, "-> numTentaQm = ${numTentaQm + 1}")
//
            //--- Tenta gerar os números para as linhas e colunas do quadMenor
            var numDispQm = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)  // Evita repetição de número gerado
            for (linha in linhasQuad) {

                for (coluna in colsQuad) {

                    //--- Gera um número aleatório até gerar um número INEXISTENTE nessa linha e
                    //    nessa coluna. Se não conseguir gerar um, reinicia a geração para esse quad.
                    numDispQm         = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
                    var flagNumExiste = false

                    do {

                        //----------------------------------
                        val numero: Int = (1..9).random()   // generated random from 1 to 9 included
                        //----------------------------------

                        //--- Só tenta utilizar o numero gerado se ele ainda não o foi
                        if (numDispQm[numero - 1] != 0) flagNumExiste = true

                        else {

                            //--- Sinaliza que tentará usar esse número gerado
                            numDispQm[numero - 1] = numero
                            Log.d(cTAG, "- numGerado = $numero")

                            //--- Verifica se o número gerado ainda NÃO existe no quadrado menor
                            flagNumExiste = false
                            for (linhaQ in linhasQuad) {
                                for (colQ in colsQuad) {
                                    if (quadMaior[linhaQ][colQ] == numero) {
                                        flagNumExiste = true
                                        break
                                    }
                                }
                                if (flagNumExiste) break

                            } // fim da pesquisa se o número gerado já existe no quadMenor

                            //--- Se existe, sai da pesquisa para gerar novo número RND
                            if (flagNumExiste) break

                            //--- Se NÃO existe, pesquisa nos seus vizinhos.
                            else {

                                //--- O número não pode existir na mesma linha
                                for (colQM in 0..8) {

                                    if ((colQM != coluna) && (quadMaior[linha][colQM] == numero)) {
                                        flagNumExiste = true
                                        break
                                    }

                                }

                                //--- O número não pode existir na mesma coluna
                                if (!flagNumExiste) {

                                    for (linhaQM in 0..8) {

                                        if ((linhaQM != linha) &&
                                                           (quadMaior[linhaQM][coluna] == numero)) {
                                            flagNumExiste = true
                                            break
                                        }

                                    }
                                }

                            } // fim da pesquisa se o número gerado já existe nos vizinhos

                            //--- Se o número está disponível na linha armazena-o no quadrado maior
                            //    (externo); senão gera novo número.
                            if (!flagNumExiste) {

                                //--- Armazena-o
                                quadMaior[linha][coluna] = numero
                                strLog = "quadMaior[$linha][$coluna]= $numero "
                                Log.d(cTAG, strLog)

                            }
                            //--- Se número existente e JÁ tentou todos os números sai por erro
                            else if (!numDispQm.contains(0)) flagErroGer = true

                        } // fim se número ainda não gerado

                    } while (flagNumExiste && numDispQm.contains(0))

                    if (flagErroGer) break

                } // fim do loop para as colunas do quadMenor

                if (flagErroGer)break

            } // fim do loop para as linhas do quadMenor

            if (flagErroGer) numTentaQm++

        } while (numTentaQm < 10)

        return (numTentaQm < 10)

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