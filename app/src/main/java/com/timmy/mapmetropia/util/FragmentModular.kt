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

    /**從當前Fragmen移動到新的Fragment
     * @param fragmentManager
     * @param hereFragment 目前的Fragment
     * @param newfFragment 要去的Fragment
     * @param bundle 有無資料傳遞 無則輸入null
     * @param isBack 是否要返回
     */
    fun changeFragment(
        ft: FragmentTransaction,
        id: Int,
        hereFragment: Fragment?,
        newfFragment: Fragment,
        bundle: Bundle?,
        isBack: Boolean
    ) {
        if (bundle != null) {
            newfFragment.arguments = bundle
        }
        logi(TAG, "TAG現在是===>${hereFragment?.tag ?: "空的！"}")

        ft.replace(id, newfFragment, "tag_" + newfFragment.hashCode())
        if (isBack == true && hereFragment != null) {
            ft.addToBackStack(hereFragment.tag)
        }

        ft.commit()
    }

    /**從當前Fragmen移動到新的Fragment，不刪除當前Fragment。
     * @param fragmentManager
     * @param hereFragment 目前的Fragment
     * @param newfFragment 要去的Fragment
     * @param bundle 有無資料傳遞 無則輸入null
     * @param isBack 是否要返回
     */
    fun changeFragmentNotKillHere(
        ft: FragmentTransaction,
        id: Int,
        hereFragment: Fragment,
        newfFragment: Fragment
    ) {

        if (!newfFragment.isAdded) {    // 先判斷是否被add過
            ft.hide(hereFragment).add(id, newfFragment).commit(); // 隱藏當前的fragment，add下一個到Activity中
        } else {
            ft.hide(hereFragment).show(newfFragment).commit(); // 隱藏當前的fragment，顯示下一個
        }
    }

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
        tag: String,
        bundle: Bundle?,
        isBack: Boolean
    ) {
        if (bundle != null) {
            newfFragment.arguments = bundle
        }
        val ft = fragmentManager.beginTransaction()
        //		ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
        ft.replace(id, newfFragment, tag)
        if (isBack == true && hereFragment != null) {
            ft.addToBackStack(hereFragment.tag)
        }

        ft.commit()
    }

    fun replace(
        fragmentManager: FragmentManager,
        id: Int,
        fragment: Fragment,
        bundle: Bundle?,
        addToBackStack: Boolean
    ) {
        val transaction = fragmentManager.beginTransaction()

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        if (bundle != null) {
            fragment.arguments = bundle
        }
        transaction.replace(id, fragment)
        transaction.commit()

        if (fragmentManager != null) {
            fragmentManager.executePendingTransactions()
        }
    }

    fun add(
        fragmentManager: FragmentManager,
        id: Int,
        hereFragment: Fragment?,
        fragment: Fragment,
        bundle: Bundle?,
        addToBackStack: Boolean
    ) {
        val transaction = fragmentManager.beginTransaction()

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        if (bundle != null) {
            fragment.arguments = bundle
        }

        if (!fragment.isAdded) {
            transaction.add(id, fragment, "tag_" + fragment.hashCode()).commit()
            if (hereFragment != null)
                transaction.hide(hereFragment)
        } else {
            if (hereFragment != null)
                transaction.hide(hereFragment)
            transaction.show(fragment).commitAllowingStateLoss()
        }

//        transaction.replace(id, fragment)
//        transaction.commit()
//
//        if (fragmentManager != null) {
//            fragmentManager.executePendingTransactions()
//        }
    }

    /**清除Ftagment堆疊*/
    fun removeAllFragment(fragmentManager: FragmentManager) {
        try {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            if (fragmentManager.fragments.size > 0) {
                val transaction = fragmentManager.beginTransaction()
                for (cf in fragmentManager.fragments) {
                    transaction.remove(cf)
                }
//            transaction.commitAllowingStateLoss()
            }
            for (i in 0 until fragmentManager.backStackEntryCount) {
                val backStackId = fragmentManager.getBackStackEntryAt(i).id
                fragmentManager.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            val flist = fragmentManager.fragments
            for (i in flist.indices) {
                val f = flist[i] ?: continue
                val fid = f.id
                fragmentManager.popBackStack(fid, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                Log.e(TAG, "popBackStack() ${f.javaClass.canonicalName} $fid")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 當前fragment 是否與 點選的一樣
     */
    fun isNowFragment(context: Context?, id: Int, className: String?): Boolean {
        if (context != null && context is FragmentActivity) {
            return isNowFragment(context.supportFragmentManager, id, className)
        }
        return false
    }

    /**
     * 當前fragment 是否與 點選的一樣
     */
    fun isNowFragment(fm: FragmentManager, id: Int, className: String?): Boolean {
        val nowfragment = fm.findFragmentById(id)

        if (nowfragment!!.javaClass.canonicalName == className) {
            val bundle = nowfragment.arguments
            if (bundle != null) {
                var isReturnTrue = true
//                val keyList = bundleData.getKeyList()
//                for (i in keyList.indices) {
//                    val key = keyList.get(i)
//                    val `val` = bundleData.getString(key)
//                    val bundleVal = bundle.getString(key, null)
//                    if (bundleVal != null && bundleVal == `val` == false) {
//                        isReturnTrue = false
//                    }
//                }

                if (isReturnTrue == true) {
                    return isReturnTrue
                }
            } else {
                return true
            }
        }
        return false
    }
}