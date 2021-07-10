package com.example.gathr.fragments
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gathr.R
import com.example.gathr.ViewModel.ReminderViewModel
import com.example.gathr.databinding.FragmentCreateReminderBinding
import com.example.gathr.entities.Reminder
import com.example.gathr.utils.AlarmReceiver
import com.example.gathr.utils.NotificationHelper
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class CreateReminderFragment : Fragment() {
    lateinit var binding: FragmentCreateReminderBinding
    lateinit var viewModel: ReminderViewModel
    private var calendar: Calendar= Calendar.getInstance()
    private lateinit var alarmManager: AlarmManager
    lateinit var pendingIntent: PendingIntent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateReminderBinding.inflate(layoutInflater)
        binding.btnDone.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                checkParameter()
            }
        })

        binding.btnBack.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                requireActivity().supportFragmentManager.popBackStack()
            }
        })

        binding.etDate.transformIntoDatePicker(requireContext(),"MM/dd/yyyy",Date())
        binding.etTime.setOnClickListener(object :View.OnClickListener {
            override fun onClick(v: View?) {
                transformIntoTimePicker(requireContext())
            }
        })

        return binding.root

    }

    private fun checkParameter() {
        if(binding.etTitle.text.toString().isEmpty()) {

            Snackbar.make(binding.root, "Please Enter Title", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()

        } else if (binding.etDate.text.toString().isEmpty()) {

            Snackbar.make(binding.root, "Please Enter Date", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

        } else if (binding.etTime.text.toString().isEmpty()) {

            Snackbar.make(binding.root, "Please Enter Time", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()

        } else {
            val reminder = Reminder(
                binding.etTitle.text.toString(),
                binding.etDate.text.toString(),
                binding.etTime.text.toString(),
                true
            )

            viewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)
            viewModel.insertReminder(reminder)
            setAlarm(reminder)
            Log.i("dataisnew","${reminder} sent")
            requireActivity().supportFragmentManager.popBackStack()

        }
    }

    private fun setAlarm(reminder: Reminder) {
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(),AlarmReceiver::class.java)
        intent.putExtra("title",reminder.reminderTitle)
        Log.i("dataisnew","${reminder.reminderTitle} is sent")
        pendingIntent = PendingIntent.getBroadcast(requireContext(),0,intent,0)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP , calendar.timeInMillis,0,pendingIntent)
        Toast.makeText(requireContext(), "Reminder Set", Toast.LENGTH_SHORT).show()
    }

    private fun EditText.transformIntoDatePicker(context: Context, format: String, minDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.UK)
                setText(sdf.format(myCalendar.time))
            }

        setOnClickListener {
            DatePickerDialog(
                context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                minDate?.time?.also { datePicker.minDate = it }
                show()
            }
        }

        calendar[Calendar.YEAR] = myCalendar.get(Calendar.YEAR)
        calendar[Calendar.MONTH] = myCalendar.get(Calendar.MONTH)
        calendar[Calendar.DAY_OF_MONTH] = myCalendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun transformIntoTimePicker(context:Context){
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mcurrentTime[Calendar.MINUTE]
        val mTimePicker: TimePickerDialog = TimePickerDialog(context,{ timePicker, selectedHour, selectedMinute ->
            binding.etTime.setText("$selectedHour:$selectedMinute")
            calendar[Calendar.HOUR_OF_DAY] = selectedHour
            calendar[Calendar.MINUTE] = selectedMinute  },hour,minute,true) //Yes 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
    }


}