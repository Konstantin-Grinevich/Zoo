package com.example.appstudents.students

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.appstudents.MyConstants.TAG
import com.example.appstudents.data.Student
import com.example.appstudents.data.StudentList
import com.example.appstudents4.repository.StudentRepository

class StudentListViewModel : ViewModel() {
    var studentList: MutableLiveData<StudentList> = MutableLiveData()

    private var studentCurrent: Student=Student()
    val student:Student
        get() = studentCurrent


    private var observer : Observer<StudentList> = Observer<StudentList>
    { newList ->
        newList?.let{
            Log.d(TAG,"Получен список StudentListViewModel от StudentRepository")
            studentList.postValue(newList)
        }
    }


    init {
        StudentRepository.getInstance().student.observeForever {
            studentCurrent= it
            Log.d(TAG, "Получили Student и StudentListViewModel")
        }
        StudentRepository.getInstance().studentsList.observeForever(observer)
        Log.d(TAG, "Подписались StudentListViewModel и StudentRepository")
    }

    fun setStudent(student: Student) {
        StudentRepository.getInstance().setCurrentStudent(student)
    }
    fun getPosition() : Int =
        StudentRepository.getInstance().getPosition()
}