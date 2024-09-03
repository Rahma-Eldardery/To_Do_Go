package com.example.todogo.ui.handletask

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todogo.R
import com.example.todogo.db.TaskDataBase
import com.example.todogo.db.TaskEntity
import com.example.todogo.db.TaskRepositoryImp
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtDesc: EditText
    private lateinit var btnSave: FloatingActionButton
    private lateinit var categorySpinner: Spinner
    private lateinit var btnSetDate: ImageView
    private lateinit var btnSetTime: ImageView
    private var calendar = Calendar.getInstance()

    private lateinit var taskRepositoryImp: TaskRepositoryImp

    private val taskDB: TaskDataBase by lazy {
        TaskDataBase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContentView(R.layout.activity_add_task)

        edtTitle = findViewById(R.id.edtTitle)
        edtDesc = findViewById(R.id.edtDesc)
        btnSave = findViewById(R.id.btnSave)
        categorySpinner = findViewById(R.id.categorySpinner)

        taskRepositoryImp = TaskRepositoryImp(taskDB)
        btnSetDate = findViewById(R.id.btnSetDate)
        btnSetTime = findViewById(R.id.btnSetTime)


        btnSave.setOnClickListener {
            saveTask()
        }

        btnSetDate.setOnClickListener {
            val datePicker = DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        btnSetTime.setOnClickListener {
            val timePicker = TimePickerDialog(this, { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            timePicker.show()
        }
    }

    private fun saveTask() {
        val title = edtTitle.text.toString()
        val desc = edtDesc.text.toString()
        val category = categorySpinner.selectedItem.toString()
        val deadlineTimestamp = calendar.timeInMillis

        if (title.isNotEmpty() && desc.isNotEmpty() && category != "Category") {
            val taskEntity = TaskEntity(0, category, title, desc, false, deadlineTimestamp , FirebaseAuth.getInstance().currentUser?.uid.toString())

            lifecycleScope.launch(Dispatchers.IO) {
                taskRepositoryImp.insertTask(taskEntity)

                withContext(Dispatchers.Main) {
                    val resultIntent = Intent()
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Title, Category and Description cannot be Empty", Snackbar.LENGTH_LONG).show()
        }
    }

}
