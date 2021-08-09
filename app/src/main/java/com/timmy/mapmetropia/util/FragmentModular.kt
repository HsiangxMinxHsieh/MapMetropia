package com.timmy.mapmetropia.util

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

object FragmentModular {
    var TAG = "FragmentModular"

    /**從當前Fragmen移動到新的Fragment
     * @param fragmentManager
     * @param hereFragment 目前的Fragment
     * @param newfFragment 要去的Fragment
     * @param bundle 有無資料傳遞 無則輸入null
     * @param isBack 是否要返回
     */
    fun changeFragment(
        fragmentManager: FragmentManager,
        id: Int,
        hereFragment: Fragment?,
        newfFragment: Fragment,
        bundle: Bundle?,
        isBack: Boolean
    ) {
        if (bundle != null) {
            newfFragment.arguments = bundle
        }
        val ft = fragmentManager.beginTransaction()
//        		ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
        ft.replace(id, newfFragment, "tag_" + newfFragment.hashCode())
        if (isBack == true && hereFragment != null) {
            ft.addToBackStack(hereFragment.tag)
        }

        ft.commit()
    }

}