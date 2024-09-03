package com.example.todogo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todogo.R
import com.example.todogo.databinding.ItemTaskBinding
import com.example.todogo.db.TaskEntity
import com.example.todogo.ui.handletask.UpdateTaskActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class TaskAdapter(
    private val onDeleteTask: (TaskEntity) -> Unit,
    private val onTaskChecked: (TaskEntity, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private lateinit var context: Context

    private val differCallback = object : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.taskId == newItem.taskId
        }

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        context = parent.context
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = differ.currentList[position]  // Get the task entity

        // Format the deadline timestamp to a readable date string
        val deadlineDate = Date(task.deadline)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.binding.deadlineTextView.text = "Deadline: ${dateFormat.format(deadlineDate)}"

        val currentTime = System.currentTimeMillis()
        val remainingTimeMillis = task.deadline - currentTime
        if (remainingTimeMillis <= 0) {
            holder.binding.timeRemainingTextView.text = "You missed the deadline"
            holder.binding.timeRemainingTextView.setTextColor(context.getColor(R.color.missed_deadline_color))
            holder.bind(task)
        }
        else {
            val remainingDays = TimeUnit.MILLISECONDS.toDays(remainingTimeMillis)
            val remainingHours = TimeUnit.MILLISECONDS.toHours(remainingTimeMillis) % 24
            val remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeMillis) % 60

            val timeRemainingText =
                "Time Remaining: ${remainingDays}d ${remainingHours}h ${remainingMinutes}m"
            holder.binding.timeRemainingTextView.text = timeRemainingText
            holder.bind(task)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TaskEntity) {
            binding.apply {
                tvTitle.text = item.taskTitle
                tvDesc.text = item.taskDescription
                tvCategory.text = item.taskCategory
                taskCard.setCardBackgroundColor(
                    when (item.taskCategory) {
                        "Work" -> context.getColor(R.color.work_category)
                        "Personal" -> context.getColor(R.color.personal_category)
                        else -> context.getColor(R.color.home_category)
                    }
                )

                checkboxDone.isChecked = item.isChecked
                if (item.isChecked) {
                    tvTitle.paintFlags = tvTitle.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                    tvDesc.paintFlags = tvDesc.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    tvTitle.paintFlags = tvTitle.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    tvDesc.paintFlags = tvDesc.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                root.setOnClickListener {
                    val intent = Intent(context, UpdateTaskActivity::class.java).apply {
                        putExtra("TASK_ID", item.taskId)
                    }
                    context.startActivity(intent)
                }

                checkboxDone.setOnCheckedChangeListener { _, isChecked ->
                    onTaskChecked(item, isChecked)
                }

                btnDelete.setOnClickListener {
                    onDeleteTask(item)
                }
            }
        }
    }
}
