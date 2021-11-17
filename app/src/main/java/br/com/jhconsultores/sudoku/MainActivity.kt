package br.com.jhconsultores.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

// import java.security.SecureRandom

class MainActivity : AppCompatActivity() {

    //----------------------------------------------------------------------------------------------
    // Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private val cTAG   = "Sudoku"
    private var strLog = ""

    private var quadMaior = arrayOf<Array<Int>>()

    private var btnGeraJogo : Button? = null

//    private var secureTrnd = SecureRandom()

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
    fun btnGeraJogoClick(view : View?) {

        Log.d(cTAG, "-> Tap no btnJogaJogo")

        //--- Gera jogo
        //-----------
        geraJogo()
        //-----------

    }

    //--- GeraJogo
    private fun geraJogo() {

        //--- Instancializações e inicializações
        //----------------------
        inicializaQuadMaior()
        //----------------------
        listaQuadMaior()
        //-----------------

        //--- Tenta gerar os 9 quadrados menores
        var flagJogoOk      = false
        var contaTentaJogo  = 0
        var flagQuadMenorOk : Boolean

        while (!flagJogoOk && contaTentaJogo < 50) {  // 20) {   // 10) {

            Log.d(cTAG, "-> Gera o jogo ${contaTentaJogo + 1}")

            for (quad in 0..8) {

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

                    else { numTentaGeracao++ }

                }

                //--------------------------
                insereQmEmQM(quad, array)
                //--------------------------
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

                        // Critérios2: verifica se número gerado pode ser inserido no jogo
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
