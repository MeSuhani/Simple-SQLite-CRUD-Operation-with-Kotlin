package com.example.myprotfolio

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class TaskAdapter(var tasklist: List<Task>, var context:Context,val itemClickListener: OnItemClickListener):
    RecyclerView.Adapter<TaskAdapter.ViewHolder>()   {




    class ViewHolder(itemview: View): RecyclerView.ViewHolder(itemview)  {

        val tvTilte=itemview.findViewById<TextView>(R.id.tvTitle)
        val tvDescription=itemview.findViewById<TextView>(R.id.tvDescription)
        val tvDate=itemview.findViewById<TextView>(R.id.tvDate)
        val itemclcik=itemview.findViewById<LinearLayout>(R.id.itemclcik)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.task_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return tasklist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvTilte.text=tasklist.get(position).title
        holder.tvDescription.text=tasklist.get(position).description
        holder.tvDate.text=tasklist.get(position).duadate

        holder.itemclcik.setOnClickListener {
            itemClickListener.onItemClick(position,tasklist)

        }
    }


}