package com.example.todogo.ui.handletask

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todogo.R
import com.example.todogo.databinding.ActivityUpdateTaskBinding
import com.example.todogo.db.TaskDataBase
import com.example.todogo.db.TaskEntity
import com.example.todogo.db.TaskRepositoryImp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateTaskBinding
    private lateinit var taskRepositoryImp: TaskRepositoryImp

    private val taskDB: TaskDataBase by lazy {
        TaskDataBase.getDatabase(this)
    }

    private var taskId = 0
    private var defaultTitle = ""
    private var defaultDesc = ""
    private var defaultCategory = ""
    private var defaultDeadline: Long = 0

    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0
    private var selectedHour = 0
    private var selectedMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskRepositoryImp = TaskRepositoryImp(taskDB)

        intent.extras?.let {
            taskId = it.getInt("TASK_ID")
        }

        binding.apply {
            lifecycleScope.launch(Dispatchers.IO) {
                val task = taskRepositoryImp.getTaskById(taskId, FirebaseAuth.getInstance().currentUser?.uid.toString())
                defaultTitle = task?.taskTitle ?: ""
                defaultDesc = task?.taskDescription ?: ""
                defaultCategory = task?.taskCategory ?: ""
                defaultDeadline = task?.deadline ?: 0

                withContext(Dispatchers.Main) {
                    edtTitle.setText(defaultTitle)
                    edtDesc.setText(defaultDesc)
                    categorySpinner.setSelection(
                        resources.getStringArray(R.array.spinner_items).indexOf(defaultCategory)
                    )

                    // Set default date and time for pickers
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = defaultDeadline
                    selectedYear = calendar.get(Calendar.YEAR)
                    selectedMonth = calendar.get(Calendar.MONTH)
                    selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
                    selectedHour = calendar.get(Calendar.HOUR_OF_DAY)
                    selectedMinute = calendar.get(Calendar.MINUTE)
                }
            }

            dateCard.setOnClickListener {
                val datePicker = DatePickerDialog(this@UpdateTaskActivity,
                    { _, year, month, dayOfMonth ->
                        selectedYear = year
                        selectedMonth = month
                        selectedDay = dayOfMonth
                        // After selecting the date, show the time picker
                        showTimePicker()
                    },
                    selectedYear,
                    selectedMonth,
                    selectedDay
                )
                datePicker.show()
            }

            timeCard.setOnClickListener {
                showTimePicker()
            }

            btnSave.setOnClickListener {
                val title = edtTitle.text.toString()
                val desc = edtDesc.text.toString()
                val category = categorySpinner.selectedItem.toString()

                if (title.isNotEmpty() && desc.isNotEmpty() && category != "Category") {
                    val calendar = Calendar.getInstance().apply {
                        set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
                    }
                    val deadlineTimestamp = calendar.timeInMillis

                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            taskRepositoryImp.updateTask(
                                TaskEntity(taskId, category, title, desc, false, deadlineTimestamp, FirebaseAuth.getInstance().currentUser?.uid.toString())
                            )
                            withContext(Dispatchers.Main) {
                                setResult(RESULT_OK)
                                finish()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@UpdateTaskActivity, "Error updating task", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@UpdateTaskActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showTimePicker() {
        val timePicker = TimePickerDialog(this,
            { _, hourOfDay, minute ->
                selectedHour = hourOfDay
                selectedMinute = minute
            },
            selectedHour,
            selectedMinute,
            true
        )
        timePicker.show()
    }
}
