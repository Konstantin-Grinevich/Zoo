package com.example.appstudents4.repository

import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.appstudents.AppStudentIntendApplication
import com.example.appstudents.data.Faculty
import com.example.appstudents.data.FacultyList
import com.google.gson.Gson

class FacultyRepository {
    companion object {
        private var INSTANCE: FacultyRepository? = null

        fun getInstance(): FacultyRepository {
            if (INSTANCE == null) {
                INSTANCE = FacultyRepository()
            }
            return INSTANCE ?:
            throw IllegalStateException("Repository not initialize")
        }
    }
    var facultyList: MutableLiveData<FacultyList> = MutableLiveData()
    var faculty: MutableLiveData<Faculty> = MutableLiveData()

    fun setCurrentFaculty(position: Int) {
        if (facultyList.value == null || facultyList.value!!.items==null || position<0 || (facultyList.value?.items?.size!! <= position)) {
            return
        }
        else {
            faculty.postValue(facultyList.value?.items!![position])
        }
    }

    init{
        loadFacultys()
    }

    fun loadFacultys(){
        val jsonString=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getString("faculty", null)
        if(!jsonString.isNullOrBlank()){
            val fc = Gson().fromJson(jsonString,FacultyList::class.java)
            if(fc!=null)
                this.facultyList.postValue(fc)
        }
    }

    fun saveFaculty(){
        val gson = Gson()
        val jsonStudents= gson.toJson(facultyList.value)
        val preference=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
        preference.edit().apply(){
            putString("faculty",jsonStudents)
            apply()
        }
    }

    fun setCurrentFaculty(faculty: Faculty) {
        this.faculty.postValue(faculty)
    }

    fun addFaculty(faculty:Faculty) {
        var facultyListTmp = facultyList
        if (facultyListTmp.value == null) facultyListTmp.value = FacultyList()
        facultyListTmp.value!!.items.add(faculty)
        facultyList.postValue(facultyListTmp.value)
    }

    fun getPosition(faculty:Faculty): Int = facultyList.value?.items?.indexOfFirst {
        it.id == faculty.id
    } ?: -1

    fun updateFaculty(faculty: Faculty) {
        var facultyListTmp = facultyList
        val position = getPosition(faculty)
        if (position < 0) addFaculty(faculty)
    }

    fun deleteFaculty(faculty: Faculty) {
        var FacultyListTmp = facultyList
        if (FacultyListTmp.value!!.items.remove(faculty)) {
            facultyList.postValue(FacultyListTmp.value)
            if (facultyList.value!!.items.isNotEmpty()) {
                setCurrentFaculty(facultyList.value!!.items[0])
            }
        }
    }
    fun newFaculty(){
        //student.postValue(Student())
        setCurrentFaculty(Faculty())
    }

    fun getPosition() : Int {
        return if (faculty.value!= null)
            getPosition(faculty.value!!)
        else 0
    }

}