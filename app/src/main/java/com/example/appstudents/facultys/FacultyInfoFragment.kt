package com.example.appstudents.facultys

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.preference.PreferenceManager
import com.example.appstudents.AppStudentIntendApplication
import com.example.appstudents.MainActivity
import com.example.appstudents.R
import com.example.appstudents.data.Faculty
import com.google.gson.Gson

private const val FILL_FIELD_MESSAGE = "Поле должно быть заполнено"
private const val NOT_NUMERIC_FIELD_MESSAGE = "Поле должно быть числовым"

class FacultyInfoFragment : Fragment() {

    private lateinit var facultyInfoViewModel: FacultyInfoViewModel
    private lateinit var etName: EditText
    private lateinit var etSize: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    companion object {
        private var INSTANCE: FacultyInfoFragment? = null
        fun getInstance(): FacultyInfoFragment {
            if(INSTANCE == null){
                INSTANCE = FacultyInfoFragment()
            }
            return INSTANCE ?: throw IllegalStateException("Отобраение не создано!")
        }
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.faculty_info, container, false)
        etName = view.findViewById(R.id.facName)
        etSize = view.findViewById(R.id.myid)
        btnSave = view.findViewById(R.id.btnSaveFac)
        btnSave.setOnClickListener {
            if (validateFields()) {
                saveFaculty()
                closeFragment()
            }
        }
        btnCancel = view.findViewById(R.id.btnCancleFac)
        btnCancel.setOnClickListener{
            closeFragment()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        facultyInfoViewModel = ViewModelProvider(this).get(FacultyInfoViewModel::class.java)
        facultyInfoViewModel.faculty.observe(viewLifecycleOwner){
            updateUI(it)
        }
        preloadFaculty()
    }

    override fun onAttach(context: Context) {
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

    fun saveFaculty() {
        facultyInfoViewModel.save(
            etName.text.toString(),
            etSize.text.toString()
        )
    }

    fun updateUI(faculty: Faculty){
        etName.setText(faculty.name)
        etSize.setText(faculty.size)
    }

    private fun closeFragment(){
        (requireActivity() as MainActivity).showFacultyList()
    }

    private fun validateFields() : Boolean {
        var isValidate = true

        when {
            etName.text.toString().isBlank() -> {
                etName.error = FILL_FIELD_MESSAGE
                isValidate = false
            }
            etSize.text.toString().isBlank() -> {
                etSize.error = FILL_FIELD_MESSAGE
                isValidate = false
            }
            etSize.text.toString().toIntOrNull() == null -> {
                etSize.error = NOT_NUMERIC_FIELD_MESSAGE
                isValidate = false
            }
        }
        return isValidate
    }

    fun presave() {
        val gson = Gson()
        val temp = Faculty()
        if (this::etName.isInitialized) {
            temp.name = etName.text.toString()
            temp.size = etSize.text.toString()
        }
        var jsonFaculty = gson.toJson(temp)
        val preference =
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
        preference.edit().apply {
            putString("presaveFaculty", jsonFaculty)
            apply()
        }
    }

    fun preloadFaculty(): String {
        val jsonString =
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getString("presaveFaculty", null)
        if (!jsonString.isNullOrBlank()) {
            val fac = Gson().fromJson(jsonString, Faculty::class.java)
            if (fac != null) {
                etName.setText(fac.name)
                etSize.setText(fac.size)
                return fac.name
            }
        }
        return ""
    }

    fun clearBuffer() {
        etName.setText("")
        etSize.setText("")
        PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext()).edit().apply {
            putString("presaveFaculty", null)
            apply()
        }
    }
}