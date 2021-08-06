package com.timmy.mapmetropia

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

import com.google.android.gms.maps.GoogleMap
import com.timmy.mapmetropia.base.BaseActivity
import com.timmy.mapmetropia.databinding.ActivityMainBinding
import com.timmy.mapmetropia.listener.BackListener
import com.timmy.mapmetropia.tab.MapsFragment
import com.timmy.mapmetropia.uitool.UserInterfaceTool
import com.timmy.mapmetropia.util.FragmentModular
import com.timmy.mapmetropia.util.loge
import com.timmy.mapmetropia.util.logi


class MainActivity : BaseActivity<ActivityMainBinding>({ ActivityMainBinding.inflate(it) }) {
    val TAG = javaClass.simpleName
    private val activity = this
    private val context: Context = this

    val mapsFragment by lazy { MapsFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {

        loge(TAG,"widthPixel=>$widthPixel")
        loge(TAG,"heightPixel=>$heightPixel")

        //全屏模式設置
        UserInterfaceTool.hideSystemUI(mBinding.root)
        window.statusBarColor = context.getColor(R.color.transparent)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息欄
        //tab 設定
        FragmentModular.changeFragment(
            supportFragmentManager,
            R.id.fl_content,
            null,
            mapsFragment,
            null,
            false
        )
    }

    override fun onBackPressed() {
        loge(TAG, "onBackPressed")
        val fm = supportFragmentManager
        val f = fm.findFragmentById(R.id.fl_content)
        logi("onBackPressed","(f as? BackListener)?.onBackPressed() == true 是=>${ (f as? BackListener)?.onBackPressed() == true}")
        logi("onBackPressed","(f as? BackListener) 是=>${ (f as? BackListener)}")
        if (f != null && (f as? BackListener)?.onBackPressed() == true) {
            //如果 Fragment 有自訂 onBackPressed() 則會執行 否則會傳回 false 跳至下行else if
            loge(TAG, "onBackPressed $f")
        } else if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
        } else {
            super.onBackPressed()
        }
    }


}