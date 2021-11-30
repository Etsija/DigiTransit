package com.etsija.digitransit.view.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.etsija.digitransit.view.MainActivity
import com.etsija.digitransit.viewmodel.SharedViewModel

abstract class BaseFragment: Fragment() {

    protected val mainActivity: MainActivity
        get() = activity as MainActivity

    protected val sharedViewModel: SharedViewModel by activityViewModels()
}