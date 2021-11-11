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
    private var numDispLinhas = arrayOf<Array<Int>>()
    private var numDispCols   = arrayOf<Array<Int>>()

    private var quadDiagonal = arrayOf<Array<Int>>()
    private var idxDiagonal  = 0

    //----------------------------------------------------------------------------------------------
    // Eventos
    //----------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //--- Instancializações e inicializações

        var array = arrayOf(0, 4, 8)
        quadDiagonal += array

        array = arrayOf(2, 4, 6)
        quadDiagonal += array

        //-----------
        geraJogo()
        //-----------

    }

    //----------------------------------------------------------------------------------------------
    // Funções
    //----------------------------------------------------------------------------------------------
    private fun geraJogo() {

        //--- Instancializações e inicializações

        //---------------------
        inicializaQuadMaior()
        //--------------------------
        listaQuadMaior()
        //--------------------------
        inicializaNumDisponiveis()
        //--------------------------
        listaNumDisponiveis()
        //----------------------

        // 1- Gera os quadrados menores independentes (diagonais: Q0, Q4, Q8) ou (Q2, Q4, Q6):
        Log.d(cTAG, "-> Diagonal $idxDiagonal")

        //for (quad in quadDiagonal[idxDiagonal]) {
        for (quad in 0..8) {

            Log.d(cTAG, "   -Q$quad:")

            // INT(EXT.TEXTO(F17;2;1)/3)*3
            val linhaInic  = (quad / 3) * 3
            val linhasQuad = arrayOf(linhaInic, linhaInic + 1, linhaInic + 2)

            for (linha in linhasQuad) {

                // EXT.TEXTO(F17;2;1)*3-INT(EXT.TEXTO(F17;2;1)/3)*9
                val colInic  = quad * 3 - (quad / 3) * 9
                val colsQuad = arrayOf(colInic, colInic + 1, colInic + 2)
                for (coluna in colInic..colInic + 2) {

                    var flagExisteQ : Boolean
                    do {

                        //--- Gera um número aleatório
                        //------------------------------
                        val numero = (1..9).random() // generated random from 1 to 9 included
                        //------------------------------

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
                            //    das diagonais secundárias
                            else if (!quadDiagonal[idxDiagonal].contains(quad)) {

                                //--- O número não pode existir na mesma linha
                                for (colQM in 0..8) {
                                    if (!colsQuad.contains(colQM) && quadMaior[linha][colQM] == numero) {
                                        flagExisteQ = true
                                        break
                                    }
                                }

                                //--- O número não pode existir na mesma coluna
                                if (!flagExisteQ) {

                                    for (linhaQM in 0..8) {

                                        if (!linhasQuad.contains(linha) && quadMaior[linhaQM][coluna] == numero) {
                                            flagExisteQ = true
                                            break
                                        }
                                    }
                                }
                            }
                        }

                        //--- Se o número está disponível na linha armazena-o no quadrado maior
                        //    (externo) e torna-o indisponível; senão gera novo número
                        if (!flagExisteQ) {

                            //--- Armazena-o
                            quadMaior[linha][coluna] = numero
                            strLog = "quadMaior[$linha][$coluna]= $numero "
                            Log.d(cTAG, strLog)

                            //--- Torna-o indisponível nessa linha
                            numDispLinhas[linha][numero - 1] = 0

                            //--- Torna-o indisponível nessa coluna
                            numDispCols[numero - 1][coluna] = 0

                        }


                    } while (flagExisteQ)

                }
            }
        }

        //---------------------------
        listaQuadMaior()
        //---------------------------
        listaNumDisponiveis()
        //----------------------

    }

    //--- inicializaQuaMaior
    private fun inicializaQuadMaior() {
        for (linha in 0..8) {
            var array = arrayOf<Int>()
            for (coluna in 1..9) { array += 0 }
            quadMaior += array
        }
    }

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

}