package com.example.appstudents.facultys

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
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
import com.example.appstudents.data.Faculty
import com.example.appstudents.data.FacultyList
import com.example.appstudents4.repository.FacultyRepository

class FacultyListFragment : Fragment() {
    private lateinit var facultyListViewModel: FacultyListViewModel
    private lateinit var facultyListRecyclerView: RecyclerView

    companion object {
        private var INSTANCE: FacultyListFragment? = null

        fun getInstance(): FacultyListFragment {
            if (INSTANCE == null) {
                INSTANCE = FacultyListFragment()
            }
            return INSTANCE ?: throw IllegalStateException("Отображение списка не создано!")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.list_of_facultys, container, false)
        facultyListRecyclerView = layoutView.findViewById(R.id.rvListFac)
        facultyListRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return layoutView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        facultyListViewModel = ViewModelProvider(this).get(FacultyListViewModel::class.java)
        facultyListViewModel.facultyList.observe(viewLifecycleOwner){
            updateUI(it)
        }
    }

    private inner class FacultysListAdapter(private val items: List<Faculty>) :
        RecyclerView.Adapter<FacultyHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FacultyHolder {
            val view = layoutInflater.inflate(R.layout.faculty_list_element, parent, false)
            return FacultyHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: FacultyHolder, position: Int) {
            holder.bind(items[position])
        }
    }

    private inner class FacultyHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
            private lateinit var faculty: Faculty
            private val nameTextView: TextView = itemView.findViewById(R.id.tvFac)
            private val sizeTextView: TextView = itemView.findViewById(R.id.tvSize)
            private val clLayout:ConstraintLayout = itemView.findViewById(R.id.ClFac)

            fun bind(faculty: Faculty) {
                this.faculty = faculty
                clLayout.setBackgroundColor(context!!.getColor(R.color.white))
                if (faculty.id == facultyListViewModel.faculty.id)
                    clLayout.setBackgroundColor(context!!.getColor(R.color.element))
                nameTextView.text = "${faculty.name}"
                sizeTextView.text = "${faculty.size}"
            }
        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
        override fun onClick(v: View?) {
            facultyListViewModel.setFaculty(faculty)
            facultyListRecyclerView.adapter = FacultysListAdapter(facultyListViewModel.facultyList.value!!.items)
            val pos = FacultyRepository.getInstance().getPosition(faculty)
            val preference=
                PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
            preference.edit().apply() {
                putString("facultyId", faculty.id.toString())
                putInt("facultyPosition", pos)
                apply()
            }
            updateUI(facultyListViewModel.facultyList.value)
            //(requireActivity() as MainActivity).showStudentsList()

           /* facultyListViewModel.setFaculty(faculty)
            (requireActivity() as MainActivity).showFacultyInfo()*/

        }
        override fun onLongClick(v: View?) : Boolean {
            /*facultyListViewModel.setFaculty(faculty)
            facultyListRecyclerView.adapter = FacultysListAdapter(facultyListViewModel.facultyList.value!!.items)
            val preference=
                PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
            preference.edit().apply() {
                putString("facultyId", faculty.id.toString())
                apply()
            }

            (requireActivity() as MainActivity).showStudentsList()
            return true*/
            val pos = FacultyRepository.getInstance().getPosition(faculty)
            val preference=
                PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
            preference.edit().apply() {
                putInt("facultyPosition", pos)
                apply()
            }
            facultyListViewModel.setFaculty(faculty)
            updateUI(facultyListViewModel.facultyList.value)
            (requireActivity() as MainActivity).checkDeleteFaculty(faculty)
            return true
        }

    }


    private fun updateUI(facultyList: FacultyList? = null){
        val pos =
            PreferenceManager.getDefaultSharedPreferences(AppStudentIntendApplication.applicationContext())
                .getInt("facultyPosition", 0)
        if (facultyList==null) return
        facultyListRecyclerView.adapter= FacultysListAdapter((facultyList.items))
        facultyListRecyclerView.layoutManager?.scrollToPosition(pos)
    }

}