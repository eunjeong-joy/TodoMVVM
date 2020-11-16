package com.todomvvm.addedittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.todomvvm.R
import com.todomvvm.databinding.AddTaskActBinding
import com.todomvvm.databinding.AddTaskActBindingImpl
import com.todomvvm.databinding.AddtaskFragBinding
import com.todomvvm.util.obtainViewModel
import com.todomvvm.util.setupSnackbar

class AddEditTaskFragment : Fragment() {

    private lateinit var viewDataBinding: AddtaskFragBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        setupActionbar()
        loadData()
    }

    private fun loadData() {
        viewDataBinding.viewmodel?.start(arguments?.getString(ARGUMENT_EDIT_TASK_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.addtask_frag, container, false)
        viewDataBinding = AddtaskFragBinding.bind(root).apply {
//            viewmodel = (activity as AddEditTaskActivity).obtainViewModel()
        }

        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        setHasOptionsMenu(true)
        retainInstance = false
        return viewDataBinding.root
    }

    private fun setupFab() {

    }

    private fun setupActionbar() {

    }

    companion object {
        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"

        fun newInstance() = AddEditTaskFragment()
    }
}