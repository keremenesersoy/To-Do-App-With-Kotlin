package com.example.todoapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.todoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences


    private val taskSet = mutableSetOf<String>()
    private val taskSetKey = "task_set_key"

    private val taskMap = mutableMapOf<String, Boolean>()
    private val taskMapKey = "task_map_key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = this.getSharedPreferences("com.example.todoapp", Context.MODE_PRIVATE)
        loadTasks()
    }

    fun addTask(view: View) {
        var taskText = binding.editTextTask.text.toString()

        if (taskText.isNotEmpty()) {
            val taskLayout = createTaskLayout(taskText , false)
            binding.linearlayoutTasks.addView(taskLayout)

            saveTask(taskText, isChecked = false)

            binding.editTextTask.text.clear()
        } else {
            Toast.makeText(this, "Lütfen Metin Giriniz!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun createTaskLayout(taskText: String, isChecked: Boolean): View {
        val taskLayout = LinearLayout(this)
        taskLayout.orientation = LinearLayout.HORIZONTAL
        taskLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val taskTextView = TextView(this)
        taskTextView.text = taskText
        taskTextView.textSize = 18f
        taskTextView.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        val checkBox = CheckBox(this)
        checkBox.isChecked = isChecked
        checkBox.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                taskTextView.paintFlags = taskTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                taskTextView.paintFlags = taskTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            saveTask(taskText, checked)
        }

        val deleteButton = Button(this)
        deleteButton.text = "Delete"
        deleteButton.setOnClickListener {
            binding.linearlayoutTasks.removeView(taskLayout)
            removeTask(taskText)
        }

        taskLayout.addView(taskTextView)
        taskLayout.addView(checkBox)
        taskLayout.addView(deleteButton)

        return taskLayout
    }


    private fun loadTasks() {
        val taskSetStr = sharedPref.getString(taskSetKey, null)
        taskSet.clear()

        taskSetStr?.split(",")?.forEach { taskText ->
            taskSet.add(taskText)
            val isChecked = taskMap[taskText] ?: false
            val taskLayout = createTaskLayout(taskText, isChecked)
            binding.linearlayoutTasks.addView(taskLayout)
        }
    }

    private fun saveTask(taskText: String, isChecked: Boolean) {
        taskSet.add(taskText)
        taskMap[taskText] = isChecked

        sharedPref.edit().putString(taskSetKey, taskSet.joinToString(",")).apply()

        val taskMapStr = taskMap.entries.joinToString(",") { (text, checked) -> "$text|$checked" }
        sharedPref.edit().putString(taskMapKey, taskMapStr).apply()
    }

    private fun removeTask(taskText: String) {
        taskSet.remove(taskText)
        taskMap.remove(taskText)

        sharedPref.edit().putString(taskSetKey, taskSet.joinToString(",")).apply()

        val taskMapStr = taskMap.entries.joinToString(",") { (text, checked) -> "$text|$checked" }
        sharedPref.edit().putString(taskMapKey, taskMapStr).apply()
    }

    private fun clearTaskSet() {
        taskSet.clear()
        sharedPref.edit().remove(taskSetKey).apply()
    }




    fun deleteAllTasks(view: View) {
        binding.linearlayoutTasks.removeAllViews()
        clearTaskSet()
    }


}
