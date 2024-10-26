package com.aduilio.mytasks.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aduilio.mytasks.databinding.ActivityTaskFormBinding
import com.aduilio.mytasks.entity.Task
import com.aduilio.mytasks.service.TaskService
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TaskFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskFormBinding

    private val taskService: TaskService by viewModels()

    private var taskId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e("lifecycle", "TaskForm onCreate")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.extras?.getString(Intent.EXTRA_TEXT)?.let { text ->
            binding.etTitle.setText(text)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        initComponents()
        setValues()
    }

    private fun initComponents() {
        binding.etDate.setOnClickListener{
            initDatePicker()
        }

        binding.etTime.setOnClickListener{
            initTimePicker()
        }
        binding.btSave.setOnClickListener {
            if(formIsValid()){
                val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                val task = Task(
                    title = binding.etTitle.text.toString(),
                    id = taskId,
                    description = binding.etDescription.text.toString(),
                    date = if (binding.etDate.text.isNullOrBlank()) null else LocalDate.parse(binding.etDate.text.toString(),dateFormatter),
                    time = if (binding.etTime.text.isNullOrBlank()) null else LocalTime.parse(binding.etTime.text.toString(),timeFormatter)
                )

                Log.e("valores","Task: $task")
                taskService.save(task).observe(this) { responseDto ->
                    if (responseDto.isError) {
                        Toast.makeText(this, "Erro com o servidor", Toast.LENGTH_SHORT).show()
                    } else {
                        finish()
                    }
                }
            }
        }
    }

    private fun initDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = dateFormat.format(calendar.time)

            binding.etDate.setText(date)
        }, year, month, day)

        datePicker.show()

    }

    private fun initTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = timeFormat.format(calendar.time)

            binding.etTime.setText(time)
        }, hour, minute, true) // `true` para formato 24h

        timePicker.show() // Exibe o dialog ao chamar essa função
    }

    private fun formIsValid(): Boolean {
        if(binding.etTitle.text.isNullOrEmpty()){
            Toast.makeText(this, "O campo de texto deve ser preenchido", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    @Suppress("deprecation")
    private fun setValues() {
        (intent.extras?.getSerializable("task") as Task?)?.let { task ->
            taskId = task.id
            binding.etTitle.setText(task.title)

            if (task.completed) {
                binding.btSave.visibility = View.INVISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()

        Log.e("lifecycle", "TaskForm onStart")
    }

    override fun onResume() {
        super.onResume()

        Log.e("lifecycle", "TaskForm onResume")
    }

    override fun onStop() {
        super.onStop()

        Log.e("lifecycle", "TaskForm onStop")
    }

    override fun onPause() {
        super.onPause()

        Log.e("lifecycle", "TaskForm onPause")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e("lifecycle", "TaskForm onDestroy")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}