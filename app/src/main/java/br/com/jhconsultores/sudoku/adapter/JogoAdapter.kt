package br.com.jhconsultores.sudoku.adapter

import android.annotation.SuppressLint
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import br.com.jhconsultores.sudoku.R

//class NotasAdapter(private val listener: NotaClickedListener): RecyclerView.Adapter<NotasViewHolder>() {
class JogoAdapter(private val itemsListArq  : ArrayList<String>,
                  private val itemsListJogo : ArrayList<String> ) :
                                                          RecyclerView.Adapter<JogosViewHolder>() {

    //----------------------------------------------------------------------------------------------
    // Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    //private var mCursor: Cursor? = null
    private val cTAG = "Sudoku"

    //----------------------------------------------------------------------------------------------
    // Eventos
    //----------------------------------------------------------------------------------------------
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JogosViewHolder {

        val view = LayoutInflater.from(parent.context)
                                            .inflate(R.layout.jogos_item, parent, false)

        return JogosViewHolder(view)

    }

    override fun onBindViewHolder(holder: JogosViewHolder, position: Int) {

        /*
        mCursor?.moveToPosition(position)

        holder.tituloTxt.text = mCursor?.getString(mCursor?.getColumnIndex(TITLE_NOTAS) as Int)
        holder.notaTxt.text   = mCursor?.getString(mCursor?.getColumnIndex(DESCRIPTION_NOTAS) as Int)
        */

        holder.arqTxt.text  = itemsListArq [position]
        holder.jogoTxt.text = itemsListJogo[position]

        //--- Listeners
        /*
        holder.excluirBtn.setOnClickListener {

            mCursor?.moveToPosition(position)
            listener.notaRemoveItem(mCursor as Cursor)
            //notifyDataSetChanged()
            notifyItemChanged(position)

        }

        holder.itemView.setOnClickListener {

            listener.notaClickeItem(mCursor as Cursor)

        }
        */

        holder.adaptarBtn.setOnClickListener {

            Log.d(cTAG, "-> tapped no RV ítem!")
        }

    }

    //----------------------------------------------------------------------------------------------
    // Funções
    //----------------------------------------------------------------------------------------------
    //override fun getItemCount(): Int = if(mCursor != null) mCursor?.count as Int else 0
    override fun getItemCount(): Int = if(itemsListArq.isNullOrEmpty()) 0 else itemsListArq.size

    /*
    @SuppressLint("NotifyDataSetChanged")
    fun setCursor(novoCursor: Cursor?){

        notifyDataSetChanged()

    }
     */

}

//------------------------------------------------------------------------------
// Classe interna
//------------------------------------------------------------------------------
class JogosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val arqTxt     = itemView.findViewById<TextView>(R.id.card_Arq_txt)    // !!
    val jogoTxt    = itemView.findViewById<TextView>(R.id.card_Jogo_txt)   // !!
    val adaptarBtn = itemView.findViewById<Button>(R.id.card_adaptar_btn)  // !!

}
