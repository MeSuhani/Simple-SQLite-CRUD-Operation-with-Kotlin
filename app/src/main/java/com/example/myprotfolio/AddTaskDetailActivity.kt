package com.example.myprotfolio

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.Calendar

class AddTaskDetailActivity : AppCompatActivity() {

    private lateinit var addbtn: Button
    private lateinit var tvtitle: EditText
    private lateinit var tvdescription: EditText
    private lateinit var tvdate: TextView
    private lateinit var msg: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task_detail)

        tvtitle = findViewById(R.id.tvtitle)
        tvdescription = findViewById(R.id.tvdescription)
        tvdate = findViewById(R.id.tvdate)
        addbtn = findViewById(R.id.addtask)

        tvdate.setOnClickListener {
            val today = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, month, dayOfMonth ->
                    val formattedMonth = month + 1
                    val selectedDate = "$dayOfMonth/$formattedMonth/$year"
                    msg = "$selectedDate"
                    if(!msg.isEmpty())
                    {
                        tvdate.text = msg.toString()

                    }
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()

        }

        addbtn.setOnClickListener {
            if (msg.isEmpty()) {
                Toast.makeText(this,"select date",Toast.LENGTH_SHORT).show()
            } else {
                AddData()
            }

        }
    }

    fun AddData() {

        var title: String = tvtitle.text.toString()
        var tvdescription: String = tvdescription.text.toString()
        var tvdate: String = tvdate.text.toString()

        if (!title.isEmpty() && !tvdescription.isEmpty() && !tvdate.isEmpty()) {
            AddDataInDb(title, tvdescription, tvdate)
        }
    }

    private fun AddDataInDb(title: String, description: String, date: String) {
        var databaseHandler: DatabaseHandler? = null
        try {
            val databaseHandler = DatabaseHandler(this)
            val user = Task(-1,title, description, date)
            val result = databaseHandler.addEmployee(user)
            Log.d("TAG", "AddDataInDb: Result = $result")
            finish()
        } catch (e: Exception) {
            Log.e("TAG", "AddDataInDb: Exception - ${e.message}")
        } finally {
            databaseHandler?.close()
            clearField()
        }
    }


    private fun clearField() {
        tvtitle.text.clear()
        tvdescription.text.clear()
        tvdate.text = ""
    }
}