package com.example.myprotfolio

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHandler(context: Context):SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION.toInt()) {

    companion object{
        private const val DATABASE_NAME = "TASKDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "TASK"
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$KEY_TITLE TEXT," +
                "$KEY_DESCRIPTION TEXT," +
                "$KEY_DATE TEXT" +
                ")"
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }
    fun addEmployee(emp: Task): Long {
        return try {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(KEY_TITLE, emp.title)
            contentValues.put(KEY_DESCRIPTION, emp.description)
            contentValues.put(KEY_DATE, emp.duadate)

            val success = db.insert(TABLE_NAME, null, contentValues)
            success
        } catch (e: Exception) {
            // Handle exception, log or throw as needed
            Log.e("TAG", "Error adding employee: ${e.message}")
            -1
        } finally {
            // Database is not closed here
        }
    }



  @SuppressLint("Range")
    fun viewEmployee():List<Task>{
        val empList:ArrayList<Task> = ArrayList<Task>()
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var userId: Long
        var usertitle: String
        var userdescription: String
        var userdate: String
        if (cursor.moveToFirst()) {
            do {
                userId = cursor.getLong(cursor.getColumnIndex("id"))
                usertitle = cursor.getString(cursor.getColumnIndex("title"))
                userdescription = cursor.getString(cursor.getColumnIndex("description"))
                userdate = cursor.getString(cursor.getColumnIndex("date"))
                val emp= Task(userId,usertitle,userdescription,userdate)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }



    fun updateEmployee(emp: Task): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, emp.id)
        contentValues.put(KEY_TITLE, emp.title)
        contentValues.put(KEY_DESCRIPTION, emp.description)
        contentValues.put(KEY_DATE, emp.duadate)

        val success = db.update(TABLE_NAME, contentValues, "$KEY_ID=?", arrayOf(emp.id.toString()))
        db.close()
        return success
    }

     fun deleteEmployee(emp: Task):Int{
         val db = this.writableDatabase
         val contentValues = ContentValues()
         contentValues.put(KEY_ID, emp.id)
         val success = db.delete(TABLE_NAME,"id="+emp.id,null)
         db.close()
         return success
     }
}
