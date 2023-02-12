package com.example.appstudents.students

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.example.appstudents.AppStudentIntendApplication
import com.example.appstudents.MyConstants
import com.example.appstudents.data.Student
import com.example.appstudents4.repository.StudentRepository
import java.util.*

class StudentInfoViewModel : ViewModel() {
    var student : MutableLiveData<Student> = MutableLiveData()
    init {
        StudentRepository.getInstance().student.observeForever {
            student.postValue(it)
        }
    }

    fun save(
        lastName: String = "",
        firstName: String = "",
        middeleName: String = "",
        birthDate: Date = Date(),
        faculty: String = "",
        group: String = "",
        idFac: String? = ""
    ) {
        if (student.value == null) {
            val pos =
                PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                    .getInt("studentPosition", -1)
            if (pos == -1) student.value = Student() else student.value =
                StudentRepository.getInstance().studentsList.value!!.items[pos]
        }
        student.value!!.lastName = lastName
        student.value!!.firstName = firstName
        student.value!!.middleName = middeleName
        student.value!!.birthDate = birthDate
        student.value!!.faculty = faculty
        student.value!!.group = group
        student.value!!.idFac = idFac
        StudentRepository.getInstance().updateStudent(student.value!!)

        val preference=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
        preference.edit().apply() {
            putInt("studentPosition", -1)
            apply()
        }
    }

}