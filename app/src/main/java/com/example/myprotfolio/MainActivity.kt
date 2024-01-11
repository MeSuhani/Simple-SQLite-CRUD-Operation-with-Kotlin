package com.example.myprotfolio

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class MainActivity : AppCompatActivity(),OnItemClickListener{

    private lateinit var ivAdd:ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var msg:String
    private var selectedTaskId: Long = -1

    private companion object{
        private const val STORAGE_PERMISSION_CODE = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ivAdd=findViewById(R.id.add)
        recyclerView=findViewById(R.id.recyclerview)
        ivAdd.setOnClickListener {
            if (checkPermission()){
                intent = Intent(this,AddTaskDetailActivity::class.java)
                startActivity(intent)
            }
            else{
                Log.d("TAG", "onCreate: Permission was not granted, request")
                requestStoragePermission()
            }

        }

        val databaseHandler = DatabaseHandler(this)
        val result=databaseHandler.viewEmployee()
        Log.e("SUHANI","result:- "+result)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter=TaskAdapter(result,this,this)
    }


    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }
    private fun checkPermission(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            Environment.isExternalStorageManager()
        }
        else{
            val write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()){
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (write && read){
                    Log.d("TAG", "onRequestPermissionsResult: External Storage Permission granted")
                    intent = Intent(this,AddTaskDetailActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Log.d("TAG", "onRequestPermissionsResult: External Storage Permission denied...")
                }
            }
        }
    }

    override fun onItemClick(position: Int, tasklist: List<Task>) {
        val clickedView = recyclerView.layoutManager?.findViewByPosition(position)

        if (clickedView != null) {
            val popup = PopupMenu(this, clickedView)
            popup.inflate(R.menu.option)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu1 -> {
                        selectedTaskId = tasklist[position].id
                        UpdateRecord()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.menu2 -> {
                        selectedTaskId = tasklist[position].id
                        DeleteRecord()
                        return@setOnMenuItemClickListener true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun DeleteRecord() {
        val databaseHandler= DatabaseHandler(this)
        databaseHandler.deleteEmployee(Task(selectedTaskId,"","",""))
        val updatedList = databaseHandler.viewEmployee()
        recyclerView.adapter = TaskAdapter(updatedList, this, this)
    }


    private fun UpdateRecord() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.activity_add_task_detail)

        val body = dialog.findViewById<EditText>(R.id.tvtitle)
        val tvdescription = dialog.findViewById<EditText>(R.id.tvdescription)
        val addtask = dialog.findViewById<Button>(R.id.addtask)

        val tvdate = dialog.findViewById<TextView>(R.id.tvdate)
        tvdate.setOnClickListener {
            val today = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, month, dayOfMonth ->
                    val formattedMonth = month + 1
                    val selectedDate = "$dayOfMonth/$formattedMonth/$year"
                    msg = "You Selected: $selectedDate"
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        addtask.setOnClickListener {
            val databaseHandler= DatabaseHandler(this)
            if (body.text.trim().isNotEmpty() && tvdescription.text.trim().isNotEmpty()) {

                if (selectedTaskId != -1L) {
                    val status = databaseHandler.updateEmployee(
                        Task(
                            selectedTaskId,
                            body.text.toString(),
                            tvdescription.text.toString(),
                            msg
                        )
                    )
                    if (status > -1) {
                        Toast.makeText(this, "Record updated", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                        val updatedList = databaseHandler.viewEmployee()
                        recyclerView.adapter = TaskAdapter(updatedList, this, this)
                    }
                } else {
                    Toast.makeText(this, "Selected task ID is invalid", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Title or description cannot be blank", Toast.LENGTH_LONG).show()
            }
        }
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        val databaseHandler = DatabaseHandler(this)
        val result = databaseHandler.viewEmployee()
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = TaskAdapter(result, this, this)
    }
}