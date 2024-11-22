package com.example.allarmm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmPendingIntent: PendingIntent
    private lateinit var selectTimeTextView: TextView
    private var selectedHour = 0
    private var selectedMinute = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisation des vues
        selectTimeTextView = findViewById(R.id.selectionnerletemps)
        val Enregister = findViewById<Button>(R.id.enregister)
        val Annuller = findViewById<Button>(R.id.annuller)

        // AlarmManager configuration
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Intent pour le BroadcastReceiver
        val intent = Intent(this, AlarmReceiver::class.java)
        alarmPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Sélectionner l'heure
        selectTimeTextView.setOnClickListener {
            showTimePickerDialog()
        }

        // Définir l'alarme
        Enregister.setOnClickListener {
            if (selectedHour != 0 || selectedMinute != 0) {
                setAlarm(selectedHour, selectedMinute)
            } else {
                Toast.makeText(this, "Veuillez d'abord sélectionner une heure.", Toast.LENGTH_SHORT).show()
            }
        }

        // Annuler l'alarme
        Annuller.setOnClickListener {
            cancelAlarm()
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                this.selectedHour = selectedHour
                this.selectedMinute = selectedMinute

                // Mettre à jour le texte affiché
                selectTimeTextView.text = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    selectedHour,
                    selectedMinute
                )
            },
            hour,
            minute,
            true
        ).show()
    }

    private fun setAlarm(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmPendingIntent
        )

        Toast.makeText(
            this,
            "Alarme définie pour ${String.format("%02d:%02d", hour, minute)}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun cancelAlarm() {
        alarmManager.cancel(alarmPendingIntent)
        Toast.makeText(this, "Alarme annulée.", Toast.LENGTH_SHORT).show()
    }
}
