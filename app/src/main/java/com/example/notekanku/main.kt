package com.example.notekanku

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notekanku.Adapter.NotesAdapter
import com.example.notekanku.Database.NoteDatabase
import com.example.notekanku.databinding.ActivityMain2Binding
import com.example.notekanku.model.Note
import com.example.notekanku.model.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

class main : AppCompatActivity(), NotesAdapter.noteitemClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var  database: NoteDatabase
    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NotesAdapter
    lateinit var selectedNote : Note

    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if (result.resultCode == Activity.RESULT_OK){

            val note = result.data?.getSerializableExtra("note")as? Note
            if (note != null){

                viewModel.updateNote(note)

            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initDateNow()


        //instal UI
        initUI()


        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        viewModel.allnotes.observe(this) { list ->

            list?.let {

                adapter.updateList(list)
            }

        }

        database = NoteDatabase.getDatabase(this)

        binding.buttonMore.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    private fun initDateNow(){
        var calendar = Calendar.getInstance().time
        var dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
        var formatDate: String = dateFormat.format(calendar)

        binding.tvDate.text = formatDate

    }

    private fun initUI() {

        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapter = NotesAdapter(this, this)
        binding.recycleView.adapter = adapter

        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

            if (result.resultCode == Activity.RESULT_OK){

                val note = result.data?.getSerializableExtra("note") as? Note
                if (note != null){

                    viewModel.insertnote(note)

                }

            }


        }

        binding.fbAddNote.setOnClickListener {

            val intent = Intent(this,AddNote::class.java)
            getContent.launch(intent)


        }

//        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//
//                return false
//
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//
//                if (newText != null){
//
//                    adapter.filterList(newText)
//
//                }
//
//                return true
//
//            }
//
//
//        })


    }

    override fun onItemclicked(note: Note) {

        val intent = Intent(this@main,AddNote::class.java)
        intent.putExtra("current_note",note)
        updateNote.launch(intent)



    }

    override fun onLongItemClicked(note: Note, cardView: CardView) {
        selectedNote = note
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(this,cardView)
        popup.setOnMenuItemClickListener(this@main)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        if (item?.itemId == R.id.delete_note){

            viewModel.deleteNote(selectedNote)
            return true

        }
        return false
    }





}
