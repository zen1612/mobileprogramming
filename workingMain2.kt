package com.example.finalsproject

import android.app.DatePickerDialog
import android.content.Intent
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.widget.Toolbar
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.AdapterView


class MainActivity : AppCompatActivity() {
    val dataModelList = mutableListOf<BookModel>()
    private lateinit var auth: FirebaseAuth

    private val allBooks = mutableListOf<BookModel>()
    private val filteredBooks = mutableListOf<BookModel>()
    private lateinit var rvAdapter: rvAdapter

    private fun applyCategoryFilter() {
        val selected =
            findViewById<Spinner>(R.id.filterCategory)
                .selectedItem.toString()

        filteredBooks.clear()

        if (selected == "All") {
            filteredBooks.addAll(allBooks)
        } else {
            filteredBooks.addAll(
                allBooks.filter { it.category == selected }
            )
        }

        rvAdapter.notifyDataSetChanged()
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                Toast.makeText(this, "Redirecting to profile...", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }

            R.id.action_signout -> {
                auth = FirebaseAuth.getInstance()

                if (auth.currentUser != null) {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)

            val filterSpinner = findViewById<Spinner>(R.id.filterCategory)

            val categories = listOf(
                "All",
                "Horror",
                "Romance",
                "Comedy",
                "Sci-Fi",
                "Fantasy",
                "Action",
                "Classical",
                "Educational"
            )

            filterSpinner.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
            )

            filterSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: android.view.View?,
                        position: Int,
                        id: Long
                    ) {
                        applyCategoryFilter()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            
            



            val writeBTN = findViewById<ImageView>(R.id.writeBTN)
            writeBTN.setOnClickListener {
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
                val mBuilder = AlertDialog.Builder(this).setView(mDialogView).setTitle("Add Book")

                val mAlertDialog = mBuilder.show()

                val spinner = mDialogView.findViewById<Spinner>(R.id.spinnerCategory)

                val categories = listOf(
                    "Horror",
                    "Romance",
                    "Comedy",
                    "Sci-Fi",
                    "Fantasy",
                    "Action",
                    "Classical",
                    "Educational"
                )

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    categories
                )

                spinner.adapter = adapter


                val saveBTN = mAlertDialog.findViewById<Button>(R.id.saveBTN)
                saveBTN?.setOnClickListener {

                    val database = Firebase.database
                    val myRef = database.getReference("Books")


                    val bookAuthorText =
                        mAlertDialog.findViewById<EditText>(R.id.bookAuthorInput)?.text.toString()
                    val bookTitleText =
                        mAlertDialog.findViewById<EditText>(R.id.bookTitleInput)?.text.toString()
                    val ratingText =
                        mAlertDialog.findViewById<EditText>(R.id.ratingInput)?.text.toString()
                    val reviewText =
                        mAlertDialog.findViewById<EditText>(R.id.reviewInput)?.text.toString()

                    val statusGroup =
                        mAlertDialog.findViewById<RadioGroup>(R.id.statusRadioGroup)
                    val statusReading =
                        statusGroup?.checkedRadioButtonId == R.id.radioReading

                    val statusWantToRead =
                        statusGroup?.checkedRadioButtonId == R.id.radioWantToRead

                    val statusCompleted =
                        statusGroup?.checkedRadioButtonId == R.id.radioCompleted

                    val selectedCategory = spinner.selectedItem.toString()


                    val model = BookModel( bookAuthorText,
                        bookTitleText,
                        reviewText,
                        ratingText,
                        statusReading,
                        statusWantToRead,
                        statusCompleted,
                        selectedCategory)
                    myRef.push().setValue(model)

                    mAlertDialog.dismiss()

                }

            }

            val database = Firebase.database
            val myRef = database.getReference("Books")

            val rv = findViewById<RecyclerView>(R.id.rv)
            rvAdapter = rvAdapter(filteredBooks)

            rv.adapter = rvAdapter
            rv.layoutManager = LinearLayoutManager(this)


            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    allBooks.clear()
                    for (data in snapshot.children) {
                        val book = data.getValue(BookModel::class.java)
                        if (book != null) {
                            book.id = data.key ?: ""
                            allBooks.add(book)
                        }
                    }
                    Log.d("Data", dataModelList.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Data", "Failed to read value.", error.toException())
                }

            })


        }
    }
