package com.example.appstudents.facultys

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.example.appstudents.AppStudentIntendApplication
import com.example.appstudents.data.Faculty
import com.example.appstudents.data.Student
import com.example.appstudents4.repository.FacultyRepository
import com.example.appstudents4.repository.StudentRepository
import java.util.*

class FacultyInfoViewModel : ViewModel() {
    var faculty : MutableLiveData<Faculty> = MutableLiveData()
    init {
        FacultyRepository.getInstance().faculty.observeForever {
            faculty.postValue(it)
        }
    }
     fun save(name: String = "", size: String = "") {
         if (faculty.value == null) {
             val pos =
                 PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                     .getInt("facultyPosition", -1)
             if (pos == -1) faculty.value = Faculty() else faculty.value =
                 FacultyRepository.getInstance().facultyList.value!!.items[pos]
         }

         if (this.faculty.value == null) this.faculty.value = Faculty()
         this.faculty.value!!.name = name
         this.faculty.value!!.size = size
         FacultyRepository.getInstance().updateFaculty(this.faculty.value!!)

         val preference=
             PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
         preference.edit().apply() {
             putInt("facultyPosition", -1)
             apply()
         }
     }

}