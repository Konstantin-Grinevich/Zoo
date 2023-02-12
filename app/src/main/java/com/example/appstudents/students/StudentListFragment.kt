package com.example.appstudents.students

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appstudents.AppStudentIntendApplication
import com.example.appstudents.MainActivity
import com.example.appstudents.R
import com.example.appstudents.data.Student
import com.example.appstudents.data.StudentList
import com.example.appstudents4.repository.FacultyRepository
import com.example.appstudents4.repository.StudentRepository

class StudentListFragment : Fragment() {
    private lateinit var studentListViewModel: StudentListViewModel
    private lateinit var studentListRecyclerView: RecyclerView

    companion object {
        private var INSTANCE: StudentListFragment? = null

        fun getInstance(): StudentListFragment {
            if (INSTANCE == null) {
                INSTANCE = StudentListFragment()
            }
            return INSTANCE ?: throw IllegalStateException("Отображение списка не создано!")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.list_of_students, container, false)
        studentListRecyclerView = layoutView.findViewById(R.id.rvList)
        studentListRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return layoutView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentListViewModel = ViewModelProvider(this).get(StudentListViewModel::class.java)
        var filteredStudents = getFilteredList(studentListViewModel.studentList.value)
        if (filteredStudents.isNotEmpty()) {
            studentListViewModel.setStudent(filteredStudents[0])
        }
        studentListViewModel.studentList.observe(viewLifecycleOwner){
            updateUI(it)
        }
    }

    private inner class StudentsListAdapter(private val items: List<Student>)
        : RecyclerView.Adapter<StudentHolder>(){
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): StudentHolder {
            val view = layoutInflater.inflate(R.layout.student_list_element, parent, false)
            return StudentHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: StudentHolder, position: Int) {
            holder.bind(items[position])
        }
    }

    private inner class StudentHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
            private lateinit var student: Student
            private val fioTextView: TextView = itemView.findViewById(R.id.tvFIO)
            private val ageTextView: TextView = itemView.findViewById(R.id.tvAge)
            private val groupTextView: TextView = itemView.findViewById(R.id.tvGroup)
            private val clLayout:ConstraintLayout = itemView.findViewById(R.id.clCL)

            fun bind(student: Student) {
                this.student = student
                clLayout.setBackgroundColor(context!!.getColor(R.color.white))
                if (student.id == studentListViewModel.student.id)
                    clLayout.setBackgroundColor(context!!.getColor(R.color.element))
                fioTextView.text = "${student.firstName} ${student.lastName} ${student.middleName}"
                groupTextView.text = student.group
                ageTextView.text = student.age.toString()
            }
        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?) : Boolean {
           /* studentListViewModel.setStudent(student)
            updateUI(studentListViewModel.studentList.value, studentListRecyclerView.verticalScrollbarPosition)
            (requireActivity() as MainActivity).showStudentDescription()
            return true*/
            studentListViewModel.setStudent(student)
            val pos = StudentRepository.getInstance().getPosition(student)
            val preference=
                PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
            preference.edit().apply() {
                putInt("studentPosition", pos)
                apply()
            }
            updateUI(studentListViewModel.studentList.value)
            (requireActivity() as MainActivity).checkDeleteStudent(student)
            return true
        }

        override fun onClick(v: View?) {
            studentListViewModel.setStudent(student)
            val pos = StudentRepository.getInstance().getPosition(student)
            val preference=
                PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
            preference.edit().apply() {
                putInt("studentPosition", pos)
                apply()
            }
            updateUI(studentListViewModel.studentList.value, studentListRecyclerView.layoutManager?.getPosition(v!!)!!)
            (requireActivity() as MainActivity).showStudentInfo()
        }
    }


    private fun updateUI(studentList: StudentList? = null, position: Int=0){
        if (studentList == null) return
        var filteredStudents = getFilteredList(studentList)
        val pos =
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getInt("studentPosition", 0)
        studentListRecyclerView.adapter= StudentsListAdapter((filteredStudents))
        studentListRecyclerView.layoutManager?.scrollToPosition(pos)
    }

    fun getFilteredList(studentList: StudentList? = null) : MutableList<Student> {
        val pos =
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getInt("studentPosition", 0)
        if (studentList == null) return mutableListOf()
        val id =
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getString("facultyId", null)
        var filteredStudents: MutableList<Student> = mutableListOf()
        var i = 0
        studentList?.items!!.forEach { item ->
            if (item.idFac == id) {
                filteredStudents.add(item)
            }
            i++
            if (pos == i) {
                val preference =
                    PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                preference.edit().apply() {
                    putInt("studentPosition", pos)
                    apply()
                }
            }
        }
        return filteredStudents
    }

}