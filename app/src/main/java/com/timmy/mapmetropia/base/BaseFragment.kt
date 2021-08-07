package com.timmy.mapmetropia.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlin.properties.Delegates


typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {
    open val TAG = javaClass.simpleName

    open val heightPixel by lazy { mContext.resources.displayMetrics.heightPixels }
    open val widthPixel by lazy { mContext.resources.displayMetrics.widthPixels }
    open val screenRatio by lazy {
        widthPixel.toDouble() / heightPixel.toDouble() }

    private var _binding: VB? = null
    val mBinding get() = _binding!!

    val mContext: Context
        get() = this@BaseFragment.requireContext()

    val otherHandlerThread = HandlerThread(javaClass.simpleName + "_otherHandlerThread")
    val otherHandler by lazy {
        otherHandlerThread.start()
        Handler(otherHandlerThread.looper)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflate.invoke(inflater, container, false)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}