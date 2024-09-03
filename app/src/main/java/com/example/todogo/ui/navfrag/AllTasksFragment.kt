package com.example.todogo.ui.navfrag

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todogo.adapter.TaskAdapter
import com.example.todogo.databinding.FragmentAllTasksBinding
import com.example.todogo.db.TaskDataBase
import com.example.todogo.db.TaskRepositoryImp
import com.example.todogo.ui.handletask.AddTaskActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllTasksFragment : Fragment() {

    private lateinit var binding: FragmentAllTasksBinding
    private lateinit var taskRepositoryImp: TaskRepositoryImp

    private val adapter = TaskAdapter(
        onDeleteTask = { task ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                taskRepositoryImp.deleteTask(task)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), task.taskTitle+" Task Deleted", Toast.LENGTH_LONG).show()
                }
                loadTasks()
            }
        },
        onTaskChecked = { task, isChecked ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                taskRepositoryImp.updateTask(task.copy(isChecked = isChecked))
                taskRepositoryImp.deleteTask(task)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), task.taskTitle+" Task Completed", Toast.LENGTH_LONG).show()
                }
                loadTasks()
            }
        }
    )

    private val taskDB: TaskDataBase by lazy {
        TaskDataBase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllTasksBinding.inflate(inflater, container, false)
        FirebaseApp.initializeApp(requireContext())
        taskRepositoryImp = TaskRepositoryImp(taskDB)

        binding.btnAddNote.setOnClickListener {
            val intent = Intent(context, AddTaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }

        setupRecyclerView()
        loadTasks()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val tasks = taskRepositoryImp.getTasksByUser(FirebaseAuth.getInstance().currentUser?.uid.toString())
            withContext(Dispatchers.Main) {
                if (tasks.isNotEmpty()) {
                    binding.rvNoteList.visibility = View.VISIBLE
                    binding.tvEmptyText.visibility = View.GONE
                    adapter.differ.submitList(tasks)
                } else {
                    binding.rvNoteList.visibility = View.GONE
                    binding.tvEmptyText.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvNoteList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = this@AllTasksFragment.adapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            loadTasks()
        }
    }

    companion object {
        private const val ADD_TASK_REQUEST_CODE = 1
    }
}
