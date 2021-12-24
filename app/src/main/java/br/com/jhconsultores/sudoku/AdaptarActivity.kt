package br.com.jhconsultores.sudoku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AdaptarActivity : AppCompatActivity() {

    //--------------------------------------------------------------------------
    //                    Instancializações e inicializações
    //--------------------------------------------------------------------------
    private var cTAG = "Sudoku"
    private var strLog = ""
    private var strToast = ""

    private lateinit var toolBar: androidx.appcompat.widget.Toolbar

    //--------------------------------------------------------------------------
    //                                Eventos
    //--------------------------------------------------------------------------
    //--- onCreate MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adaptar)

        //--- Ativa o actionBar
        toolBar = findViewById(R.id.toolbar)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)




    }

}