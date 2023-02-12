package com.example.appstudents.students

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.preference.PreferenceManager
import com.example.appstudents.AppStudentIntendApplication
import com.example.appstudents.MainActivity
import com.example.appstudents.R
import com.example.appstudents.data.Student
import com.example.appstudents.data.StudentList
import com.example.appstudents4.repository.FacultyRepository
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.*

private const val FILL_FIELD_MESSAGE = "Поле должно быть заполнено"

class StudentInfoFragment : Fragment() {

    private lateinit var studentInfoViewModel: StudentInfoViewModel
    private lateinit var etLastName: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etMiddleName: EditText
    private lateinit var etFaculty: EditText
    private lateinit var etGroup: EditText
    private lateinit var dpDate: DatePicker
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    companion object {
        private var INSTANCE: StudentInfoFragment? = null
        fun getInstance(): StudentInfoFragment {
            if(INSTANCE == null){
                INSTANCE = StudentInfoFragment()
            }
            return INSTANCE ?: throw IllegalStateException("Отобраение не создано!")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.student_info, container, false)
        etLastName = view.findViewById(R.id.lastName)
        etFirstName = view.findViewById(R.id.firstName)
        etMiddleName = view.findViewById(R.id.middleName)
        etFaculty = view.findViewById(R.id.faculty)
        etGroup = view.findViewById(R.id.group)
        dpDate = view.findViewById(R.id.date)
        btnSave = view.findViewById(R.id.btnOk)
        btnSave.setOnClickListener{
            if (validateFields()) {
                saveStudent()
                closeFragment()
            }
        }
        btnCancel = view.findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener{
            closeFragment()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentInfoViewModel = ViewModelProvider(this).get(StudentInfoViewModel::class.java)
        studentInfoViewModel.student.observe(viewLifecycleOwner){
            updateUI(it)
        }
        preloadStudent()
    }

    override fun onAttach(context: Context){
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    closeFragment()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    fun saveStudent() {
        val dateBirth = GregorianCalendar(dpDate.year, dpDate.month, dpDate.dayOfMonth)
        val idFac=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getString("facultyId", null)
        studentInfoViewModel.save(
            etLastName.text.toString(),
            etFirstName.text.toString(),
            etMiddleName.text.toString(),
            dateBirth.time,
            etFaculty.text.toString(),
            etGroup.text.toString(),
            idFac
        )
    }
    fun updateUI(student: Student){
        etLastName.setText(student.lastName)
        etFirstName.setText(student.firstName)
        etMiddleName.setText(student.middleName)
        val dateBirth = GregorianCalendar()
        dateBirth.time = student.birthDate
        dpDate.updateDate(dateBirth.get(Calendar.YEAR),dateBirth.get(Calendar.MONTH),dateBirth.get(Calendar.DAY_OF_MONTH))
        etFaculty.setText(student.faculty)
        etGroup.setText(student.group)
    }

    private fun closeFragment(){
        (requireActivity() as MainActivity).showStudentsList()
    }

    private fun validateFields() : Boolean {
        var isValidate = true
        when {
            etLastName.text.toString().isBlank() -> {
                etLastName.error = FILL_FIELD_MESSAGE
                isValidate = false
            }
            etFirstName.text.toString().isBlank() -> {
                etFirstName.error = FILL_FIELD_MESSAGE
                isValidate = false
            }
            etMiddleName.text.toString().isBlank() -> {
                etMiddleName.error = FILL_FIELD_MESSAGE
                isValidate = false
            }
            etFaculty.text.toString().isBlank() -> {
                etFaculty.error = FILL_FIELD_MESSAGE
                isValidate = false
            }
            etGroup.text.toString().isBlank() -> {
                etGroup.error = FILL_FIELD_MESSAGE
                isValidate = false
            }
        }
        return isValidate
    }

    fun presave() {
        val gson = Gson()
        val idFac=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getString("facultyId", null)
        if (idFac != null) {
            val temp = Student(idFac = idFac)
            if (this::etFirstName.isInitialized) {
                temp.firstName = etFirstName.text.toString()
                temp.lastName = etLastName.text.toString()
                temp.middleName = etMiddleName.text.toString()
                val dateBirth = GregorianCalendar(dpDate.year, dpDate.month, dpDate.dayOfMonth)
                temp.birthDate = dateBirth.time
                temp.faculty = etFaculty.text.toString()
                temp.group = etGroup.text.toString()
            }
            var jsonStudents = gson.toJson(temp)
            val preference =
                PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
            preference.edit().apply {
                putString("presaveStudent", jsonStudents)
                apply()
            }
        }
    }

    fun preloadStudent(): String {
        val jsonString =
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getString("presaveStudent", null)
        if (!jsonString.isNullOrBlank()) {
            val st = Gson().fromJson(jsonString, Student::class.java)
            val dateBirth = GregorianCalendar()
            if (st != null) {
                etFirstName.setText(st.firstName)
                etLastName.setText(st.lastName)
                etMiddleName.setText(st.middleName)
                etFaculty.setText(st.faculty)
                etGroup.setText(st.group)
                dateBirth.time = st.birthDate
                dpDate.updateDate(
                    dateBirth.get(Calendar.YEAR),
                    dateBirth.get(Calendar.MONTH),
                    dateBirth.get(Calendar.DAY_OF_MONTH)
                )
                return st.firstName
            }
        }
        return ""
    }

    fun clearBuffer() {
        if (this::etFirstName.isInitialized) {
            etFirstName.setText("")
            etLastName.setText("")
            etMiddleName.setText("")
            etFaculty.setText("")
            etGroup.setText("")
            val dateBirth = GregorianCalendar()
            dpDate.updateDate(
                dateBirth.get(Calendar.YEAR),
                dateBirth.get(Calendar.MONTH),
                dateBirth.get(Calendar.DAY_OF_MONTH)
            )
        }
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .edit().apply {
                putString("presaveStudent", null)
                apply()
            }

    }

}