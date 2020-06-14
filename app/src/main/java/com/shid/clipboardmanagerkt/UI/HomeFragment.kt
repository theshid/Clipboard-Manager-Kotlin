package com.shid.clipboardmanagerkt.UI

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shid.clipboardmanagerkt.Adapters.ClipAdapter
import com.shid.clipboardmanagerkt.Database.ClipDatabase
import com.shid.clipboardmanagerkt.Model.ClipEntry
import com.shid.clipboardmanagerkt.R
import com.shid.clipboardmanagerkt.Service.AutoListenService
import com.shid.clipboardmanagerkt.Utils.SharedPref
import com.shid.clipboardmanagerkt.ViewModel.MainViewModel
import com.shid.clipboardmanagerkt.ViewModel.MainViewModelFactory
import java.io.Serializable

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
    private lateinit var mSwitch: SwitchCompat
    private lateinit var sharedPref: SharedPref

    private lateinit var layoutView: View
    private lateinit var rootLayout: LinearLayout
    private lateinit var mAdapter: ClipAdapter
    private var isServiceOn: Boolean = false
    val clipList = listOf<ClipEntry>()
    lateinit var emptyView:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        layoutView = inflater.inflate(R.layout.home_fragment, container, false)
        setViewModel()
        setUI(layoutView)
        checkPref()
        handleAutoListen()
        // Inflate the layout for this fragment
        return layoutView
    }

    private fun setUI(view: View) {
        val application = requireNotNull(this.activity).application
        val dataSource = ClipDatabase.getInstance(application).clipDao
        val viewModelFactory = MainViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        emptyView = view.findViewById(R.id.empty_view)
        rootLayout = view.findViewById<LinearLayout>(R.id.rootLayout)
        // Set the RecyclerView to its corresponding view

        // Set the RecyclerView to its corresponding view
        val mRecyclerView: RecyclerView = view!!.findViewById(R.id.recyclerView)
        mSwitch = view!!.findViewById(R.id.switch1)


        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize the adapter and attach it to the RecyclerView

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = ClipAdapter(context!!, this)
        mRecyclerView.adapter = mAdapter
        viewModel.allClips.observe(viewLifecycleOwner, Observer {
            it.let {
                mAdapter.mClipEntries = it
            }
        })

        mAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onChanged() {
                super.onChanged()
                checkEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkEmpty()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                checkEmpty()
            }

            fun checkEmpty() {
                emptyView.visibility = if (mAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        val decoration = DividerItemDecoration(context, VERTICAL)
        mRecyclerView.addItemDecoration(decoration)

        val simpleItemTouchCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    viewModel.deleteClip(clipList[viewHolder.adapterPosition])
                    Toast.makeText(context, "Clip deleted", Toast.LENGTH_LONG).show()
                }

            }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)

    }

    private fun checkPref() {
        sharedPref = context?.let { SharedPref(it) }!!
        if (sharedPref.loadSwitchState()) {
            mSwitch.isChecked = true
            startAutoService()
        } else {
            mSwitch.isChecked = false
            stopAutoService()
        }
    }

    private fun checkIntent() {
        val intent = activity?.intent
        if (intent?.getBooleanExtra("service_on", true)!!) {
            Log.d(
                "Fragment",
                "value of intent " + intent.getBooleanExtra("service_on", true)
            )

            sharedPref.setSwitch(false)
            mSwitch.isChecked = false
            stopAutoService()
        }
    }

    private fun handleAutoListen() {
        mSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                sharedPref.setSwitch(true)
                startAutoService()
            } else {
                sharedPref.setSwitch(false)
                stopAutoService()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        checkIntent()
    }

    private fun stopAutoService() {
        isServiceOn =false
        activity?.stopService(Intent(activity, AutoListenService::class.java))
    }

    private fun startAutoService() {
        isServiceOn = true
        val intentService = Intent(activity, AutoListenService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity!!.startForegroundService(intentService)
        } else {
            activity!!.startService(intentService)
        }
        Toast.makeText(activity, "ShidClip AutoListen enabled...", Toast.LENGTH_SHORT).show()
    }

    private fun setViewModel() {


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
        showOptionDialog(itemId)
    }

    private fun showOptionDialog(clipId: Int) {

        val dialog: AlertDialog.Builder = AlertDialog.Builder(context!!)
        dialog.setTitle("Menu")
        dialog.setMessage("Choose your option")
        Log.d("TAG", "value of position: $clipId")

        val inflater = LayoutInflater.from(context)
        val option_layout = inflater.inflate(R.layout.dialog_layout, null)

        dialog.setView(option_layout)

        dialog.setPositiveButton(
            "Copy"
        ) { dialog, _ ->
            dialog.dismiss()
            val clips: List<ClipEntry> = clipList //check this
            val clipEntry = clips[clipId]
            val clipName: String = clipEntry.entry
            val clipboardManager =
                activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Copied text", clipName)
            clipboardManager.setPrimaryClip(clipData)
            Snackbar.make(rootLayout, "Text has been copied", Snackbar.LENGTH_SHORT)
                .show()

            if (isServiceOn) {
                stopAutoService()
                val handler = Handler()
                handler.postDelayed({ startAutoService() }, 500)
            }
        }

        dialog.setNegativeButton(
            "Edit Clip"
        ) { dialog, which ->
            dialog.dismiss()
            showEditDialog(clipId)
        }

        dialog.show()
    }

    private fun showEditDialog(clipId: Int) {
        val clips: List<ClipEntry> = clipList
        val intent = Intent(context, EditActivity::class.java)

        intent.putExtra("position", clipId)

        intent.putExtra("list", clips as Serializable)
        startActivity(intent)
    }
}