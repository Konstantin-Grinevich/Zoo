package com.example.appstudents.facultys

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.appstudents.MyConstants.TAG
import com.example.appstudents.data.Faculty
import com.example.appstudents.data.FacultyList
import com.example.appstudents.data.StudentList
import com.example.appstudents4.repository.FacultyRepository
import com.example.appstudents4.repository.StudentRepository

class FacultyListViewModel : ViewModel() {
    var facultyList: MutableLiveData<FacultyList> = MutableLiveData()

    private var facultyCurrent: Faculty = Faculty()
    val faculty:Faculty
        get() = facultyCurrent


    private var observer : Observer<FacultyList> = Observer<FacultyList>
    { newList ->
        newList?.let{
            Log.d(TAG,"Получен список StudentListViewModel от StudentRepository")
            facultyList.postValue(newList)
        }
    }


    init {
        FacultyRepository.getInstance().faculty.observeForever {
            facultyCurrent= it
            Log.d(TAG, "Получили Faculty и StudentListViewModel")
        }
        FacultyRepository.getInstance().facultyList.observeForever(observer)
        Log.d(TAG, "Подписались StudentListViewModel и StudentRepository")
    }

    fun setFaculty(faculty: Faculty) {
        FacultyRepository.getInstance().setCurrentFaculty(faculty)
    }

    fun getPosition() : Int =
        FacultyRepository.getInstance().getPosition()

}