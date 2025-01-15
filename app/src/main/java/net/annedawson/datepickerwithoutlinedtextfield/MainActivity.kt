package net.annedawson.datepickerwithoutlinedtextfield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.annedawson.datepickerwithoutlinedtextfield.ui.theme.DatePickerWithOutlinedTextFieldTheme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DatePickerWithOutlinedTextFieldTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DateApp(
                        name = "Date Picker With Outlined Text Field",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun DateApp(name: String, modifier: Modifier = Modifier) {

    Column(modifier = modifier) {
        Text(
            text = "Date Picker With Outlined Text Field",
            modifier = modifier
        )

        DatePickerWithOutlinedTextField()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithOutlinedTextField() {
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var dateText by remember { mutableStateOf(convertMillisToDate(selectedDateMillis)) }
    var isError by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = dateText,
            onValueChange = { newText ->
                dateText = newText
                try {
                    selectedDateMillis = convertDateToMillis(newText)
                    isError = false
                } catch (e: ParseException) {
                    isError = true
                }
            },
            label = { Text("Select Date") },
            isError = isError,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(onClick = { showDatePicker = true }) {
            Text("Open Date Picker")
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDateMillis = it
                            selectedDateMillis = epochToLocalTimeZoneConvertor(selectedDateMillis)
                            dateText = convertMillisToDate(selectedDateMillis)
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun convertDateToMillis(dateString: String): Long {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.parse(dateString)?.time ?: throw ParseException("Invalid date format", 0)
}

fun epochToLocalTimeZoneConvertor(epoch: Long): Long {
    val epochCalendar = Calendar.getInstance()
    epochCalendar.timeZone = TimeZone.getTimeZone("UTC")
    epochCalendar.timeInMillis = epoch
    val converterCalendar = Calendar.getInstance()
    converterCalendar.set(
        epochCalendar.get(Calendar.YEAR),
        epochCalendar.get(Calendar.MONTH),
        epochCalendar.get(Calendar.DATE),
        epochCalendar.get(Calendar.HOUR_OF_DAY),
        epochCalendar.get(Calendar.MINUTE),
    )
    converterCalendar.timeZone = TimeZone.getDefault()
    return converterCalendar.timeInMillis
}

@Preview(showBackground = true)
@Composable
fun DateAppPreview() {
    DatePickerWithOutlinedTextFieldTheme {
        DateApp("DatePicker with OutlinedTextField")
    }
}