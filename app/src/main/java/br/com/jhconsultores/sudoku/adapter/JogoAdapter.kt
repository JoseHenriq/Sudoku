package br.com.jhconsultores.sudoku.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import br.com.jhconsultores.sudoku.R

class JogoAdapter(private val itemsListArq  : ArrayList<String>,
                  private val itemsListJogo : ArrayList<String>,
                  private val listener      : JogoClickedListener) :
                                                          RecyclerView.Adapter<JogosViewHolder>() {

    //--------------------------------------------------------------------------
    // Instancializações e inicializações
    //--------------------------------------------------------------------------
    private val cTAG = "Sudoku"

    //--------------------------------------------------------------------------
    // Eventos
    //--------------------------------------------------------------------------
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JogosViewHolder {

        val view = LayoutInflater.from(parent.context)
                                            .inflate(R.layout.jogos_item, parent, false)

        return JogosViewHolder(view)

    }

    override fun onBindViewHolder(holder: JogosViewHolder, position: Int) {

        holder.arqTxt.text  = itemsListArq [position]
        holder.jogoTxt.text = itemsListJogo[position]

        val strDado    = itemsListArq [position]
        val intIdxInic = strDado.indexOf("Status: ") + 8
        val strStatus  = strDado.substring(intIdxInic)

        if (strStatus.contains("ativo")) {

        }

        //--- Listeners
        holder.arqTxt.setOnClickListener {

            Log.d(cTAG, "JogoAdapter - posição: $position")

            //----------------------------
            listener.infoItem(position)
            //----------------------------

        }

        holder.jogoTxt.setOnClickListener {

            //----------------------------
            listener.jogoItem(position)
            //----------------------------

        }

    }

    //----------------------------------------------------------------------------------------------
    // Funções
    //----------------------------------------------------------------------------------------------
    // override fun getItemCount(): Int = if(itemsListArq.isNullOrEmpty()) 0 else itemsListArq.size
    override fun getItemCount(): Int {

        Log.d(cTAG, "getItemCount: ${itemsListArq.size}")

        return itemsListArq.size

    }
}

//------------------------------------------------------------------------------
// Classe interna
//------------------------------------------------------------------------------
class JogosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val arqTxt  : TextView = itemView.findViewById(R.id.card_Arq_txt)    // !!
    val jogoTxt : TextView = itemView.findViewById(R.id.card_Jogo_txt)   // !!

}
