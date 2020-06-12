package com.shid.clipboardmanagerkt.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shid.clipboardmanagerkt.Adapters.ClipAdapter
import com.shid.clipboardmanagerkt.Database.ClipDatabase
import com.shid.clipboardmanagerkt.Model.ClipEntry
import com.shid.clipboardmanagerkt.R
import com.shid.clipboardmanagerkt.ViewModel.MainViewModel
import com.shid.clipboardmanagerkt.ViewModel.MainViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), ClipAdapter.ItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var layoutView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutView = inflater.inflate(R.layout.home_fragment, container, false)
        setViewModel()
        setUI()
        // Inflate the layout for this fragment
        return layoutView
    }

    private fun setUI() {
        var emptyView: TextView = view!!.findViewById(R.id.empty_view)
        // Set the RecyclerView to its corresponding view

        // Set the RecyclerView to its corresponding view
        val mRecyclerView: RecyclerView = view!!.findViewById(R.id.recyclerView)
        var mSwitch: SwitchCompat = view!!.findViewById(R.id.switch1)
        var rootLayout = view!!.findViewById<LinearLayout>(R.id.rootLayout)
        var clips = listOf<ClipEntry>()
        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize the adapter and attach it to the RecyclerView

        // Initialize the adapter and attach it to the RecyclerView
        val mAdapter = ClipAdapter(context!!, this)
        mRecyclerView.adapter = mAdapter
        clips =
    }

    private fun setViewModel() {
        val application = requireNotNull(this.activity).application
        val dataSource = ClipDatabase.getInstance(application).clipDao
        val viewModelFactory = MainViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClickListener(itemId: Int) {

    }
}