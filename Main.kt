package com.example.mobileprogrammingfinals

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    val dataModelList = mutableListOf<BookModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val writeBTN = findViewById<ImageView>(R.id.writeBTN)
        writeBTN.setOnClickListener{
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_book, null)
            val mBuilder = AlertDialog.Builder(this).setView(mDialogView).setTitle("Book History")

            val mAlertDialog = mBuilder.show()


            val saveBTN = mAlertDialog.findViewById<Button>(R.id.saveBTN)
            saveBTN?.setOnClickListener {

                val database = Firebase.database
                val myRef = database.getReference("My History")

                val bookTitle = mAlertDialog.findViewById<EditText>(R.id.bookTitleInput)?.text.toString()
                val bookAuthor = mAlertDialog.findViewById<EditText>(R.id.bookAuthorInput)?.text.toString()

                val model = BookModel(bookTitle, bookAuthor)
                myRef.push().setValue(model)

                mAlertDialog.dismiss()

            }

        }

        val database = Firebase.database
        val myRef = database.getReference("Books")

        val rv = findViewById<RecyclerView>(R.id.rv)
        val adapter_list = rvAdapter(dataModelList)

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter_list

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                dataModelList.clear()
                for(dataModel in dataSnapshot.children){
                    Log.d("Data", dataModel.toString())
                    dataModelList.add(dataModel.getValue(BookModel::class.java)!!)
                }
                adapter_list.notifyDataSetChanged()
                Log.d("Data", dataModelList.toString())
                Toast.makeText(
                    baseContext,
                    "Data updated",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Data", "Failed to read value.", error.toException())
                Toast.makeText(
                    baseContext,
                    "Failed",
                    Toast.LENGTH_SHORT,
                ).show()
            }

        })


    }
}
