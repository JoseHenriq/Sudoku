package br.com.jhconsultores.sudoku.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import br.com.jhconsultores.sudoku.R
import br.com.jhconsultores.sudoku.ui.AdaptarActivity
//import br.com.jhconsultores.sudoku.ui.AdaptarActivity.Companion.strSelJogo

// https://developer.android.com/guide/topics/ui/layout/recyclerview

class JogoAdapter(private val arLstArq:  ArrayList<String>,
                  private val arLstJogo: ArrayList<String>,
                  private val listener: JogoClickedListener) :
                                                RecyclerView.Adapter<JogoAdapter.ViewHolder>() {

    //val adaptarJogo = AdaptarActivity()

    companion object {

        const val cTAG = "Sudoku"

    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val arqTxt  : TextView
        val jogoTxt : TextView

        init {

            arqTxt  = view.findViewById(R.id.card_Arq_txt)
            jogoTxt = view.findViewById(R.id.card_Jogo_txt)

        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).
                                        inflate(R.layout.jogos_item, viewGroup, false)
        return ViewHolder(view)

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.arqTxt.text  = arLstArq[position]
        viewHolder.jogoTxt.text = arLstJogo[position]

        //--- Colore o card conforme o status do jogo
        val strDado    = arLstArq [position]
        val intIdxInic = strDado.indexOf("Status: ") + 8
        val strStatus  = strDado.substring(intIdxInic)
        val cardColor : Long = if (strStatus.contains("ativo")) 0xFFA5F55C else 0xFF3D91E4
        viewHolder.arqTxt.setBackgroundColor  (cardColor.toInt())
        viewHolder.jogoTxt.setBackgroundColor (cardColor.toInt())

        //--- Declara os listeners de tap nos textos do rv
        viewHolder.arqTxt.setOnClickListener {

            Log.d(cTAG, "-> arqTxt $position" )

            //--------------------------------------
            //adaptarJogo.adaptaEjogaJogo(position)
            //--------------------------------------

            //----------------------------
            listener.infoItem(position)
            //----------------------------

        }

        viewHolder.jogoTxt.setOnClickListener {

            Log.d(cTAG, "-> jogoTxt $position")

            //--------------------------------------
            //adaptarJogo.adaptaEjogaJogo(position)
            //--------------------------------------

            //----------------------------
            listener.jogoItem(position)
            //----------------------------

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = arLstArq.size

    /*
    private fun SendBroadcast() {

        Intent().also { intent ->

            intent.setAction(strSelJogo)
            intent.putExtra("jogoSelecionado", position)
            intent.putExtra("strDadosJogo",
                "${arLstArq[position]} ${arLstJogo[position]}")
            //----------------------------------------
//            JogoAdapter.context.sendBroadcast(intent)
            //----------------------------------------
        }

    }
     */
}

