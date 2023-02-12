package com.example.appstudents4.repository

import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.appstudents.AppStudentIntendApplication
import com.example.appstudents.data.Student
import com.example.appstudents.data.StudentList
import com.google.gson.Gson

class StudentRepository {
    companion object {
        private var INSTANCE: StudentRepository? = null

        fun getInstance(): StudentRepository {
            if (INSTANCE == null) {
                INSTANCE = StudentRepository()
            }
            return INSTANCE ?:
            throw IllegalStateException("Repository not initialize")
        }
    }
    var studentsList: MutableLiveData<StudentList> = MutableLiveData()
    var student: MutableLiveData<Student> = MutableLiveData()

    fun setCurrentStudent(position: Int) {
        if (studentsList.value == null || studentsList.value!!.items==null || position<0 || (studentsList.value?.items?.size!! <= position)) {
            return
        }
        else {
            student.postValue(studentsList.value?.items!![position])
        }
    }

    init{
        loadStudents()
    }

    fun loadStudents(){
        val jsonString=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getString("students", null)
        if(!jsonString.isNullOrBlank()){
            val st = Gson().fromJson(jsonString,StudentList::class.java)
            if(st!=null)
                this.studentsList.postValue(st)
        }
    }

    fun saveStudents(){
        val gson = Gson()
        val jsonStudents= gson.toJson(studentsList.value)
        val preference=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
        preference.edit().apply(){
            putString("students",jsonStudents)
            apply()
        }
    }

    fun setCurrentStudent(student: Student) {
        this.student.postValue(student)
    }

    fun addStudent(student:Student) {
        var studentListTmp = studentsList
        if (studentListTmp.value == null) studentListTmp.value = StudentList()
        studentListTmp.value!!.items.add(student)
        studentsList.postValue(studentListTmp.value)
    }

    fun getPosition(student:Student): Int = studentsList.value?.items?.indexOfFirst {
        it.id == student.id
    } ?: -1

    fun updateStudent(student: Student) {
        var studentListTmp = studentsList
        val position = getPosition(student)
        if (position < 0) addStudent(student)
    }

    fun deleteStudent(student: Student) {
        var studentListTmp = studentsList
        if (studentListTmp.value!!.items.remove(student)) {
            studentsList.postValue(studentListTmp.value)
            if (studentsList.value!!.items.isNotEmpty()) {
                setCurrentStudent(studentsList.value!!.items[0])
            }
        }
    }
    fun newStudent(){
        //student.postValue(Student())
        setCurrentStudent(Student())
    }

    fun getPosition() : Int {
        return if (student.value!= null)
            getPosition(student.value!!)
        else 0
    }

    fun deleteAll() {
        var a : MutableLiveData<StudentList> = MutableLiveData()
            studentsList.postValue(a.value)
    }

}