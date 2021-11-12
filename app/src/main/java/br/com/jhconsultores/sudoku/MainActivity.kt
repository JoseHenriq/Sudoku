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

        /** Algoritmo 1
        var array = arrayOf<Int>()
        for (idxDiagonal in 0..1) {
            when (idxDiagonal) {
                0 -> arrayPref = arrayOf(0, 4, 8)

                else -> {
                    arrayPref = arrayOf(2, 4, 6)
                    array     = arrayOf()
                }
            }

            for (quadMenor in arrayPref) { array += quadMenor }
            for (quadMenor in 0..8) {
                if (!arrayPref.contains(quadMenor)) { array += quadMenor }
            }
            quadMenores += array
        }

        idxDiagonal = 0
         */

        //-----------
        geraJogo()
        //-----------

    }

    //----------------------------------------------------------------------------------------------
    // Funções
    //----------------------------------------------------------------------------------------------
    private fun geraJogo() {

        //--- Instancializações e inicializações
        var flagErroGer : Boolean
        var numTentativas = 0

        do {

            Log.d(cTAG, "-> Tentativa: ${numTentativas + 1}")

            //----------------------
            inicializaQuadMaior()
            //----------------------
            listaQuadMaior()
            //-----------------

            flagErroGer = false

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

               //--- Para todas as linhas desse quadrado
               for (linha in linhasQuad) {

                   //--- Para todas as colunas desse quadrado
                   for (coluna in colsQuad) {

                       //--- Gera um número aleatório até gerar um número INEXISTENTE nessa linha e
                       //    nessa coluna.
                       var flagExisteQ = false
                       var numero: Int
                       val numDisp = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

                       do {

                           //-------------------------
                           numero = (1..9).random()        // generated random from 1 to 9 included
                           //-------------------------
                           if (numDisp[numero - 1] == 0) {

                               numDisp[numero - 1] = numero
                               Log.d(cTAG, "- numGerado = $numero")

                               //--- Verifica se o número gerado ainda NÃO existe no seu quadrado
                               flagExisteQ = false
                               for (linhaQ in linhasQuad) {

                                   for (colQ in colsQuad) {

                                       if (quadMaior[linhaQ][colQ] == numero) {

                                           flagExisteQ = true
                                           break

                                       }

                                   }

                                   //--- Se existe sai da pesquisa para gerar novo número RND
                                   if (flagExisteQ) break

                                   //--- Se NÃO existe, pesquisa nos seus vizinhos, para os quadrados
                                   //    menores das diagonais secundárias.
                                   else {    // if (!arrayPref.contains(quad)) {

                                       //--- O número não pode existir na mesma linha
                                       for (colQM in 0..8) {
                                           // if (!colsQuad.contains(colQM) && quadMaior[linha][colQM] == numero) {
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
                               }
                           }

                       } while (flagExisteQ && numDisp.contains(0))

                       //--- Se o número está disponível na linha armazena-o no quadrado maior
                       //    (externo); senão gera novo número
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
                           numTentativas++

                       }
                       if (flagErroGer) break

                   }
                   if (flagErroGer) break

               }
               if (flagErroGer) break

               //---------------------------
               listaQuadMaior()
               //---------------------------
               // listaNumDisponiveis()
               //----------------------
           }

        } while (flagErroGer && numTentativas < 10)

    }

    //--- inicializaQuaMaior
    private fun inicializaQuadMaior() {
        quadMaior = arrayOf()
        for (linha in 0..8) {
            var array = arrayOf<Int>()
            for (coluna in 1..9) { array += 0 }
            quadMaior += array
        }
    }

    /*
    //--- inicializaNumDisponiveis
    private fun inicializaNumDisponiveis() {

        //--- Linhas
        for (linha in 0..8) {
            var array = arrayOf<Int>()
            for (coluna in 1..9) { array += coluna }
            numDispLinhas += array
        }

        //--- Colunas
        for (coluna in 0..8) {
            var array = arrayOf<Int>()
            for (linha in 0..8) { array += coluna + 1}
            numDispCols += array
        }

    }
    */

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

    /*
    //--- listaNumDisponiveis
    private fun listaNumDisponiveis() {
        Log.d(cTAG, "-> Números disponíveis nas linhas:")
        for (linha in 0..8) {
            strLog = "linha $linha : "
            for (coluna in 0..8) {
                strLog += "${numDispLinhas[linha][coluna]}" + if (coluna < 8) ", " else ""
            }
            Log.d(cTAG, strLog)
        }

        Log.d(cTAG, "-> Números disponíveis nas colunas:")
        for (coluna in 0..8) {
            strLog = "coluna $coluna : "
            for (linha in 0..8) {
                strLog += "${numDispCols[linha][coluna]}" + if (linha < 8) ", " else ""
            }
            Log.d(cTAG, strLog)
        }

    }
     */

}