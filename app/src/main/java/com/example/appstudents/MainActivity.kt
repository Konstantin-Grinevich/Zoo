package com.example.appstudents

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.appstudents.MyConstants.FACULTY_INFO_FRAGMENT_TAG
import com.example.appstudents.MyConstants.FACULTY_LIST_FRAGMENT_TAG
import com.example.appstudents.MyConstants.STUDENT_INFO_FRAGMENT_TAG
import com.example.appstudents.MyConstants.STUDENT_LIST_FRAGMENT_TAG
import com.example.appstudents.data.Faculty
import com.example.appstudents.data.Student
import com.example.appstudents.facultys.FacultyInfoFragment
import com.example.appstudents.facultys.FacultyListFragment
import com.example.appstudents.students.StudentInfoFragment
import com.example.appstudents.students.StudentListFragment
import com.example.appstudents4.repository.FacultyRepository
import com.example.appstudents4.repository.StudentRepository
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private var miAdd : MenuItem? = null
    private var miChange : MenuItem? = null

    private var miAddFac : MenuItem? = null
    private var miChangeFac : MenuItem? = null
    private var miInfoFac : MenuItem? = null
    private var miBack : MenuItem? = null
    private var miInfoStud : MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkFragment()
        val actionBar = supportActionBar
        actionBar!!.title = "Зоопарк"


        val callback = object : OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed() {
                val jsonString=
                    PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                        .getString("activeFragment", null)
                val fc = Gson().fromJson(jsonString, String::class.java)
                when {
                    fc == STUDENT_LIST_FRAGMENT_TAG -> showFacultyList()
                    fc == STUDENT_INFO_FRAGMENT_TAG -> showStudentsList()
                    fc == FACULTY_INFO_FRAGMENT_TAG -> showFacultyList()
                    fc == FACULTY_LIST_FRAGMENT_TAG -> checkLogout()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    fun checkFragment() {
        val jsonString=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getString("activeFragment", null)
        if(!jsonString.isNullOrBlank()) {
            val fc = Gson().fromJson(jsonString, String::class.java)
            when {
                fc == STUDENT_LIST_FRAGMENT_TAG -> showStudentsList()
                fc == STUDENT_INFO_FRAGMENT_TAG -> {
                    showStudentInfo()
                }
                fc == FACULTY_INFO_FRAGMENT_TAG -> {
                    showFacultyInfo()
                }
                fc == FACULTY_LIST_FRAGMENT_TAG -> showFacultyList()
            }
        }
        else{
            showFacultyList()
        }
    }

    private fun checkLogout(){
        AlertDialog.Builder(this)
            .setTitle("Выход!")
            .setMessage("Вы действительно хотите выйти из приложения?")

            .setPositiveButton("Да"){_, _ ->
                finish()
            }
            .setNegativeButton("Нет", null)
            .setCancelable(true)
            .create()
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val fragment=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getString("activeFragment", null)

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        miAdd = menu.findItem(R.id.miAdd)
        miChange = menu.findItem(R.id.miChange)

        miAddFac = menu.findItem(R.id.miAddFac)

        miChangeFac = menu.findItem(R.id.miChangeFac)
        miInfoFac = menu.findItem(R.id.miInfoFac)
        miBack = menu.findItem(R.id.miBack)
        miInfoStud = menu.findItem(R.id.miInfoStud)

        miAdd?.isVisible = false
        miChange?.isVisible = false
        miBack?.isVisible = false
        miInfoStud?.isVisible = false
        miBack?.isVisible = false

        miAddFac?.isVisible = true
        miChangeFac?.isVisible = false
        miInfoFac?.isVisible = false
        if (!fragment.isNullOrBlank()) {
            val fc = Gson().fromJson(fragment, String::class.java)
            when (fc) {
                STUDENT_INFO_FRAGMENT_TAG -> {
                    miAdd?.isVisible = false
                    miAddFac?.isVisible = false
                }
                FACULTY_INFO_FRAGMENT_TAG -> miAddFac?.isVisible = false
                FACULTY_LIST_FRAGMENT_TAG -> {
                    if (FacultyRepository.getInstance().facultyList.value != null) {
                        miChangeFac?.isVisible = true
                        miInfoFac?.isVisible = true
                    }
                }
                STUDENT_LIST_FRAGMENT_TAG -> {
                    miBack?.isVisible = true
                    miAddFac?.isVisible = false
                    miAdd?.isVisible = true
                }
            }

        }
        return true
    }

    fun checkDeleteStudent(student : Student) {
        val s = student.lastName + " "+
                student.firstName + " "+
                student.middleName
        AlertDialog.Builder(this)
            .setTitle("УДАЛЕНИЕ!")
            .setMessage("Вы действительно хотите удалить животное $s ?")

            .setPositiveButton("Да"){_, _ ->
                StudentRepository.getInstance().deleteStudent(student)
                if (StudentRepository.getInstance().studentsList.value!!.items.isEmpty()) {
                    miChange?.isVisible = false
                    miInfoStud?.isVisible = false

                    miChangeFac?.isVisible = false
                    miInfoFac?.isVisible = false
                } else checkStudentList()
            }
            .setNegativeButton("Нет", null)
            .setCancelable(true)
            .create()
            .show()
    }

    fun checkDeleteFaculty(faculty : Faculty){
        val s = faculty.name
        AlertDialog.Builder(this)
            .setTitle("УДАЛЕНИЕ!")
            .setMessage("Вы действительно хотите удалить вольер $s ?")

            .setPositiveButton("Да"){_, _ ->
                FacultyRepository.getInstance().deleteFaculty(faculty)
                if (FacultyRepository.getInstance().facultyList.value!!.items.isEmpty()) {
                    miChange?.isVisible = false
                    miBack?.isVisible = false
                    miInfoStud?.isVisible = false

                    miChangeFac?.isVisible = false
                    miInfoFac?.isVisible = false
                }
            }
            .setNegativeButton("Нет", null)
            .setCancelable(true)
            .create()
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.miAdd -> {
                showNewStudent()
                true
            }
            R.id.miChange -> {
                showStudentInfo()
                true
            }
            R.id.miAddFac -> {
                showNewFaculty()
                true
            }
            R.id.miChangeFac -> {
                showFacultyInfo()
                true
            }
            R.id.miInfoFac -> {
                showStudentsList()
                true
            }
            R.id.miBack -> {
                showFacultyList()
                true
            }
            R.id.miInfoStud -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showNewStudent(){
        StudentRepository.getInstance().newStudent()
        showStudentInfo()
    }

    fun showNewFaculty(){
        FacultyRepository.getInstance().newFaculty()
        showFacultyInfo()
    }

    fun showStudentsList() {
        val qq = StudentRepository.getInstance().studentsList.value
        if (StudentRepository.getInstance().studentsList.value != null) {
            checkStudentList()

        } else {

            miAdd?.isVisible = true
            miChange?.isVisible = false
            miBack?.isVisible = true
            miInfoStud?.isVisible = false

            miAddFac?.isVisible = false
            miChangeFac?.isVisible = false
            miInfoFac?.isVisible = false
        }

        val preference=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
        preference.edit().apply() {
            putString("activeFragment", STUDENT_LIST_FRAGMENT_TAG)
            apply()
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, StudentListFragment.getInstance(),STUDENT_LIST_FRAGMENT_TAG)
            .commit()
    }

    fun showStudentInfo(){

        miAdd?.isVisible = false
        miChange?.isVisible = false
        miBack?.isVisible = false
        miInfoStud?.isVisible = false

        miAddFac?.isVisible = false
        miChangeFac?.isVisible = false
        miInfoFac?.isVisible = false

        val preference=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
        preference.edit().apply() {
            putString("activeFragment", STUDENT_INFO_FRAGMENT_TAG)
            apply()
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, StudentInfoFragment.getInstance(),STUDENT_INFO_FRAGMENT_TAG)
            .addToBackStack(null)
            .commit()

    }



    fun showFacultyInfo(){
        miAdd?.isVisible = false
        miChange?.isVisible = false
        miBack?.isVisible = false
        miInfoStud?.isVisible = false

        miAddFac?.isVisible = false
        miChangeFac?.isVisible = false
        miInfoFac?.isVisible = false
        val preference=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
        preference.edit().apply() {
            putString("activeFragment", FACULTY_INFO_FRAGMENT_TAG)
            apply()
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, FacultyInfoFragment.getInstance(), FACULTY_INFO_FRAGMENT_TAG)
            .addToBackStack(null)
            .commit()
    }

    fun showFacultyList() {
        if (FacultyRepository.getInstance().facultyList.value == null) {
            miAdd?.isVisible = false
            miChange?.isVisible = false
            miBack?.isVisible = false
            miInfoStud?.isVisible = false

            miAddFac?.isVisible = true
            miChangeFac?.isVisible = true
            miInfoFac?.isVisible = true
        } else {

            miAdd?.isVisible = false
            miChange?.isVisible = false
            miBack?.isVisible = false
            miInfoStud?.isVisible = false

            miAddFac?.isVisible = true
            miChangeFac?.isVisible = true
            miInfoFac?.isVisible = true
        }
        val preference=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
        preference.edit().apply() {
            putString("activeFragment", FACULTY_LIST_FRAGMENT_TAG)
            apply()
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, FacultyListFragment.getInstance(), FACULTY_LIST_FRAGMENT_TAG)
            .commit()
    }

    fun checkStudentList() {
        val id =
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getString("facultyId", null)
        var filteredStudents: MutableList<Student> = mutableListOf()
        StudentRepository.getInstance().studentsList.value!!.items.forEach { item ->
            if (item.idFac == id) {
                filteredStudents.add(item)
            }
        }

        if (filteredStudents.isEmpty()) {
            miAdd?.isVisible = true
            miChange?.isVisible = false
            miBack?.isVisible = true
            miInfoStud?.isVisible = false

            miAddFac?.isVisible = false
            miChangeFac?.isVisible = false
            miInfoFac?.isVisible = false
        } else {

            miAdd?.isVisible = true
            miChange?.isVisible = true
            miBack?.isVisible = true
            miInfoStud?.isVisible = false

            miAddFac?.isVisible = false
            miChangeFac?.isVisible = false
            miInfoFac?.isVisible = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveData()
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        loadData()
        super.onRestoreInstanceState(savedInstanceState, persistentState)
    }

    private fun loadData() {
        StudentRepository.getInstance().loadStudents()
        FacultyRepository.getInstance().loadFacultys()
    }

    private fun saveData(){
        StudentRepository.getInstance().saveStudents()
        FacultyRepository.getInstance().saveFaculty()
    }

    override fun onStop() {
        saveData()
        StudentInfoFragment.getInstance().presave()
        FacultyInfoFragment.getInstance().presave()
        super.onStop()
    }

    override fun onDestroy() {
        val preference=
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
        preference.edit().apply(){
            putString("activeFragment", null)
            apply()
        }
        StudentInfoFragment.getInstance().clearBuffer()
        FacultyInfoFragment.getInstance().clearBuffer()
        super.onDestroy()
    }
}