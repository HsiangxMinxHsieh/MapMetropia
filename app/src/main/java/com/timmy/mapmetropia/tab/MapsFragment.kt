package com.timmy.mapmetropia.tab

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import com.timmy.mapmetropia.R
import com.timmy.mapmetropia.databinding.FragmentMapsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import com.timmy.mapmetropia.base.BaseFragment
import com.timmy.mapmetropia.base.BaseRecyclerViewDataBindingAdapter
import com.timmy.mapmetropia.databinding.AdapterBottomJourneyDetailBinding
import com.timmy.mapmetropia.databinding.AdapterBottomJourneySummaryBinding
import com.timmy.mapmetropia.listener.BackListener
import com.timmy.mapmetropia.model.JourneyData
import com.timmy.mapmetropia.uitool.*
import com.timmy.mapmetropia.util.*

class MapsFragment : BaseFragment<FragmentMapsBinding>(FragmentMapsBinding::inflate), BackListener, OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    /** google map */
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    /** 繪製路線 */
    private var line: Polyline? = null

    /** 花費時間 */
    private var spendTimeMark: Marker? = null

    /**圓角半徑*/
    private val rc by lazy { 100 } // roundCorner

    private val behavior by lazy { BottomSheetBehavior.from(mBinding.flSheet) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        initView()

        initValue()

        initSensor()

        initMap()

        initEvent()

//        initScreenSatus()
    }


    lateinit var journeyData: JourneyData
    private fun initData() { // 初始化所有資料，此為讀入raw的Json檔
        val journeyDataStr = getRaw(mContext, R.raw.transit_data)
//        logi("initData", "初始化資料時，journeyDataStr 是=>${journeyDataStr}")

        journeyData = journeyDataStr.toGson(JourneyData())

        logi("initData", "journeyData轉換完成，是=>${journeyData.toJson()}")


    }


    /**因為Google地圖內沒有方法取得現在方位(iOS卻有...Orz)，因此要設定Sensor使用*/
    private fun initSensor() {
        val mSensorManager: SensorManager = mContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        mSensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        mSensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI)

        //延遲更新操作(避免抖動的太劇烈)
        val timeInterval = 1000L
        otherHandler.postDelayed(object : Runnable {
            override fun run() {
                isTimeToUpdate = true
                otherHandler.postDelayed(this, timeInterval)
            }
        }, timeInterval)
    }

    private fun initView() {
        screenRatio //必須於此取一次，staticRatio才會有值
        val calculateDistance = heightPixel.toDouble() // 公用距離，為高度double值
        mBinding.apply {
            ivBack.apply {
                background = getRoundBg(mContext, rc, R.color.transparent, R.color.theme_blue, 2)
                setViewSizeByDpUnit(36, 36)
                setMarginByDpUnit((calculateDistance * 0.005).toInt(), (calculateDistance * 0.02).toInt(), 0, 0)
            }
            ivAnchor.apply {
                background = getRoundBg(mContext, rc, R.color.white, R.color.gray, 2)
                setViewSizeByDpUnit(48, 48)
                setMarginByDpUnit(0, 0, (calculateDistance * 0.02).toInt(), (calculateDistance * 0.036).toInt())
            }
            val clSummaryHeight = (calculateDistance * 0.05).toInt()
//            clSummary.setViewSizeByDpUnit(widthPixel, clSummaryHeight) // 不知道為什麼，設定這個會讓Layout裡面的約束失效、內容消失。
            val cardCorner = (calculateDistance * 0.03).toInt()

            val clSummaryInBottomHeight = (calculateDistance * 0.031).toInt()
            icBottom.apply {
                slContent.setCornerRadius(cardCorner)
                bottomSheet.maxHeight = (calculateDistance * 0.8).toInt() // 最大高度設置為螢幕高的 約0.8(Zeplin計算結果)
                vvIosBar.setMarginByDpUnit(0, 4, 0, 0)
                vvIosBar.background = getRoundBg(mContext, rc, R.color.gray)

                val rvTopBottomMargin = (clSummaryInBottomHeight / 2 - calculateDistance * 0.008).toInt()
                rvJourneySummary.setMarginByDpUnit(16, rvTopBottomMargin, 16, rvTopBottomMargin)
//                rvJourneySummary.setViewSizeByDpUnit(widthPixel, clSummaryInBottomHeight) // 不知道為什麼，設定這個會讓裡面的RecyclerView異常，只好改用設定Margin的方式來調整大小。

            }

            behavior.peekHeight = ViewTool.DpToPx(mContext, ((clSummaryInBottomHeight + clSummaryHeight) * 1.1).toFloat()) // 偷看高度設定 // clSummaryInBottom + clSummary

            val cn = 10 //corner
            llBuy.background = getRectangleBg(mContext, cn, cn, cn, cn, R.color.light_green, 0, 0)
            llStartTrip.background = getRectangleBg(mContext, cn, cn, cn, cn, R.color.theme_blue, 0, 0)

            tvTotalSpendTime.setTextSize(17)
            tvTotalSpendMoneyButton.setTextSize(16)
            tvStartTime.setTextSize(14)
            tvEndTime.setTextSize(14)
            tvStartButton.setTextSize(16)

        }

        initScreenStatus()
    }

    private fun initEvent() {

        mBinding.ivAnchor.setOnClickListener {// 定位到本機位置
            zoomToPoint(deviceLocation?.latLng(), true)

        }

        mBinding.ivBack.setOnClickListener { // 本來返回鍵做成可以離開這這個App，後來覺得沒有案鍵可以回到我精心畫的路徑圖有點可惜，所以現在會回到路徑圖 XD
            initScreenStatus()
            initZoomToPathLocation(true)
//            onBackPressed()
        }

        mBinding.llBuy.setOnClickListener {
            logi("llBuy", "llBuy 被點擊到了！")
        }

    }

    private fun initScreenStatus() {
        setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED)
    }

    private fun initValue() { //填值進畫面
        mBinding.apply {
            tvTotalSpendTime.text = mContext.getString(R.string.total_spend_time).format(journeyData.estimatedTime)
            tvTotalSpendMoneyButton.text = mContext.getString(R.string.total_price).format(journeyData.totalPrice.format("#.00"))
            tvStartTime.text = journeyData.startedOn.toTimeInclude12hour()
            tvEndTime.text = journeyData.endedOn.toTimeInclude12hour()
        }

        showBottomView(journeyData)
    }


    private fun initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        if (this::mMap.isInitialized)
            return

        val mapFragment = SupportMapFragment.newInstance()
        mapFragment.getMapAsync(this@MapsFragment)
        childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
    }

    override fun onBackPressed(): Boolean {
        logi("onBackPressed", "firstShowBottom 是=>$firstShowBottom")
        return when {
            firstShowBottom -> {
                false
            }
            behavior.state != BottomSheetBehavior.STATE_HIDDEN -> {
                setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)
                true
            }
            else -> {
                activity?.finish()
                logi("onBackPressed", "else被呼較 ")
                false
            }
        }
    }

    // 開啟 GPS 監聽器
    private fun openGPS(listener: LocationListener) {
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // 確認權限
        if (!checkPermission()) {
            requestPermissions() // 請求權限授權
            return
        }

        // 進行 GPS 監聽程序
        val minTime = 100L
        val minDistance = 0L
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance.toFloat(), listener)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance.toFloat(), listener)
    }

    private fun checkPermission() = EasyPermissions.hasPermissions(mContext, *perms)

    //請求GPS權限
    private val PERMISSIONS_REQUEST_CODE = 457

    private val perms by lazy {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
//            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
            this,
            mContext.getString(R.string.splash_no_permission),
            PERMISSIONS_REQUEST_CODE,
            *perms
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //權限被拒
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //權限允許
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            openGPS(locationListener)
        }
    }

    var isTimeToUpdate = true
    var sensorOri = 0f
    private val sensorEventListener by lazy {
        object : SensorEventListener {
            var mGravity: FloatArray = FloatArray(10)
            var mGeomagnetic: FloatArray = FloatArray(10)

            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) {
                    return
                }
                if (event.sensor.type === Sensor.TYPE_ACCELEROMETER) mGravity = event.values
                if (event.sensor.type === Sensor.TYPE_MAGNETIC_FIELD) mGeomagnetic = event.values
                val rotateArray = FloatArray(9)
                val success = SensorManager.getRotationMatrix(rotateArray, FloatArray(9), mGravity, mGeomagnetic)
                if (success) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotateArray, orientation)
                    sensorOri = orientation[0] // orientation contains: azimut, pitch and roll

                    // 若不要即時更新(會抖動)，將下方三行註解。
                    if (isTimeToUpdate) {
                        myLocationMark?.rotation = sensorOri.getPicRotationAngle()
                        isTimeToUpdate = false
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }


    /**題目要求，一開始縮放到路徑的位置*/
    private fun initZoomToPathLocation(needAnimation: Boolean = false) {
        zoomToBounds(journeyData.steps.map { it.oriLatLng() }, needAnimation)
    }

    override fun onResume() {
        super.onResume()

        openGPS(locationListener)

    }

    override fun onPause() {
        super.onPause()
        removeLocationListener()
    }

    /**解決離開App後還在持續監聽GPS導致耗電問題。*/
    private fun removeLocationListener() {
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(locationListener)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
//    private var isFirstEnterMap = true
    override fun onMapReady(googleMap: GoogleMap) {
        loge(TAG, "onMapReady")
        mMap = googleMap

        //關閉地圖旋轉與傾斜相機
        mMap.uiSettings.isRotateGesturesEnabled = false
        mMap.uiSettings.isTiltGesturesEnabled = false
//
        //初始化相機位置
        initZoomToPathLocation()

        // 繪製首尾Marker
        addLocationCustomMarker(journeyData.steps.first().destinationLatLng(), MarkerType.Departure)
        addLocationCustomMarker(journeyData.steps.last().destinationLatLng(), MarkerType.Destination)

        drawPathsOnMap(journeyData.steps.map { it.polyline })

        mMap.setOnMarkerClickListener {
            if (it == myLocationMark) {
                return@setOnMarkerClickListener true
            }

            return@setOnMarkerClickListener true
        }
    }


    //遞迴方法，依序針對每個step的polyLine畫線。
    private fun drawPathsOnMap(lines: List<String>) {

        if (lines.isEmpty())
            return
        drawPolyLine(lines[0], mContext.getColor(R.color.theme_blue))
        drawPathsOnMap(lines.subList(1, lines.size))
    }


    private val locationListener by lazy {
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (context == null)
                    return
                deviceLocation = location
//                logi(TAG, "現在的位置是===>$location")
                addMyLocationMethod(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

        }
    }

    var deviceLocation: Location? = null
    var myLocationMark: Marker? = null


    private fun addMyLocationMethod(myLocation: Location): Marker? {

        myLocationMark?.remove()
        myLocationMark = addLocationCustomMarker(myLocation.latLng(), MarkerType.DeviceLoation)

        myLocationMark?.rotation = sensorOri.getPicRotationAngle()
//        logi(TAG,"取到的角度資訊是===>${myLocation.bearing}")

        return myLocationMark
    }


    private fun showFailMessageMethod() {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(mContext, "路徑規劃失敗！請稍後再試！", Toast.LENGTH_SHORT).show()
        }
    }

    private fun zoomToPoint(latLng: LatLng?, needAnimation: Boolean = false, zoom: Float = 18f) {
        if (latLng == null) {
            Toast.makeText(mContext, "無法取得裝置當前位置，請再試一次。", Toast.LENGTH_SHORT).show()
            return
        }
        if (needAnimation)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    private fun zoomToBounds(latLng: LatLng, latLng2: LatLng, needAnimation: Boolean = false) {
        zoomToBounds(mutableListOf(latLng, latLng2), needAnimation)
    }

    private fun zoomToBounds(list: List<LatLng>, needAnimation: Boolean = false, lambda: () -> Unit = {}) {
        val builder = LatLngBounds.builder()
        list.forEach {
            builder.include(it)
        }
        val bounds = builder.build()
        val padding = (heightPixel * 0.1).toInt()// 縮放地圖距離邊界

        if (needAnimation) {
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, padding),
                object : GoogleMap.CancelableCallback {
                    override fun onFinish() {
                        lambda()
                    }

                    override fun onCancel() {

                    }
                })
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
            lambda()
        }
    }


    //依照不同Marker新增的需要，有不同的判斷，共有三個地方需要增加自定義Marker
    enum class MarkerType {
        DeviceLoation, // 裝置
        Departure,
        Destination
    }

    private fun addLocationCustomMarker(latLng: LatLng, addType: MarkerType): Marker? {

        val view: View = when (addType) {
            MarkerType.Departure -> LayoutInflater.from(mContext).inflate(R.layout.marker_departure_layout, null).apply {
                background = getRoundBg(mContext, rc, R.color.theme_blue, R.color.white, 5)
                setViewSizeByDpUnit(30, 30)
            }
            MarkerType.Destination -> LayoutInflater.from(mContext).inflate(R.layout.marker_destination_layout, null).apply {
                setViewSizeByDpUnit(34, 34)
            }
            MarkerType.DeviceLoation -> LayoutInflater.from(mContext).inflate(R.layout.marker_location_layout, null)
        }

        val bitmapDescriptor: BitmapDescriptor = fromView(mContext, view) ?: return null
        val option = MarkerOptions()
            .position(latLng)
            .icon(bitmapDescriptor)
            .zIndex(5f)
            .draggable(false)

        return mMap.addMarker(option)
    }

    private fun drawPolyLine(encodeedPath: String, color: Int) {
        mMap.addPolyline(PolylineOptions().addAll(PolyUtil.decode(encodeedPath))).apply {
            this.width = 10f
            this.color = color
            this.isGeodesic = false
        }
    }

    var firstShowBottom = true
    private var bottomSheetStatus = BottomSheetBehavior.STATE_HIDDEN

    private fun setBottomSheetState(state: Int) {

        mBinding.flSheet.postDelayed({

//            logi(TAG, "現在要設定的狀態是===>$state")

            behavior.state = state
            bottomSheetStatus = state
            firstShowBottom = false

        }, 300L)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showBottomView(journeyData: JourneyData?) {
        if (journeyData == null)
            return

        //設定顯示內容
        mBinding.icBottom.apply {
            //RecyclerView初始化
            rvJourneySummary.apply {
//                setMarginByDpUnit(10, 16, 10, 16)
//                layoutParams = ConstraintLayout.LayoutParams(widthPixel, heightPixel)
//                overScrollMode = View.OVER_SCROLL_NEVER

//                layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
//                requestDisallowInterceptTouchEvent(true)
                adapter = JourneySummaryAdapter(mContext).apply {
                    addItem(journeyData.steps)
                }
            }
            logi("showBottomView", "rvJourneySummary 的寬高是=>[${rvJourneySummary.layoutParams.width},${rvJourneySummary.layoutParams.height}]")


            rvJourneyDetail.apply {


            }
            setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED)
        }

    }

    /** 底部視圖的上方Summary Adapter */
    class JourneySummaryAdapter(val context: Context) :
        BaseRecyclerViewDataBindingAdapter<JourneyData.Step>(context, R.layout.adapter_bottom_journey_summary) {
        override fun initViewHolder(viewHolder: ViewHolder) {
            val binding = viewHolder.binding as AdapterBottomJourneySummaryBinding
//            binding.clContent.background = getRectangleBg(context, 0, 0, 0, 0, R.color.theme_green, 0, 0)
//            binding.clContent.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
            binding.ivMode.setViewSizeByDpUnit(20, 20)
            val cn = 4 //corner

//            binding.tvWalkTime.background = getRectangleBg(context, cn, cn, cn, cn, R.color.transparent, R.color.transparent, 1)
            binding.tvWalkTime.setTextSize(14)
            binding.tvShortNoBus.background = getRectangleBg(context, cn, cn, cn, cn, R.color.transparent, R.color.theme_blue, 1)
            binding.tvShortNoBus.setTextSize(14)
            binding.tvShortNoTram.background = getRectangleBg(context, cn, cn, cn, cn, R.color.transparent, R.color.theme_red, 1)
            binding.tvShortNoTram.setTextSize(14)

            binding.ivNext.setMarginByDpUnit(6, 0, 6, 0)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, data: JourneyData.Step) {
            val binding = viewHolder.binding as AdapterBottomJourneySummaryBinding

            binding.setShowText(data, position)
            Glide.with(context)
                .load(data.mode.getIconDrawable())
                .into(binding.ivMode)

            binding.ivNext.isVisible = position != itemCount - 1// 最後一個的ivNext要隱藏
        }

        override fun onItemClick(view: View, position: Int, data: JourneyData.Step): Boolean {
            return false
        }

        override fun onItemLongClick(view: View, position: Int, data: JourneyData.Step): Boolean {
            return false
        }

        /**依照dataMode決定顯示什麼*/
        fun AdapterBottomJourneySummaryBinding.setShowText(data: JourneyData.Step, position: Int) {

            when (data.mode) {
                StepMode.Walk.content -> {
                    this.tvWalkTime.isVisible = true
                    this.tvShortNoBus.isVisible = false
                    this.tvShortNoTram.isVisible = false
//                    logi("setShowText", "這筆data的mode是=>${data.mode},花費時間是=>${data.estimatedTime},路徑編號是=>${data.shortNameNo}")
                    this.tvWalkTime.text = ((data.estimatedTime.toDouble() / 60).format("#")).toString()
                    logi("setShowText", " (data.estimatedTime.toDouble() / 60) 計算值是 =>${(data.estimatedTime.toDouble() / 60)}")
                    logi("setShowText", " this.tvWalkTime.text  設定完成後是 =>${this.tvWalkTime.text}")

                }
                StepMode.Bus.content -> {
                    this.tvWalkTime.isVisible = false
                    this.tvShortNoBus.isVisible = true
                    this.tvShortNoTram.isVisible = false
                    this.tvShortNoBus.text = data.shortNameNo
                }
                StepMode.Tram.content -> {
                    this.tvWalkTime.isVisible = false
                    this.tvShortNoBus.isVisible = false
                    this.tvShortNoTram.isVisible = true
                    this.tvShortNoTram.text = data.shortNameNo

                }
            }

        }

        fun String.getIconDrawable() = when (this) {
            StepMode.Walk.content -> R.drawable.ic_icon_mode_walk
            StepMode.Bus.content -> R.drawable.ic_icon_mode_bus
            StepMode.Tram.content -> R.drawable.ic_icon_mode_tram
            else -> 0
        }
    }

    enum class StepMode(val content: String) {
        Walk("walk"),
        Bus("bus"),
        Tram("tram"),


    }

    /** 底部視圖的詳細行程的 Adapter */
    class JourneyDetailAdapter(val context: Context) :
        BaseRecyclerViewDataBindingAdapter<JourneyData.Step>(context, R.layout.adapter_bottom_journey_detail) {
        override fun initViewHolder(viewHolder: ViewHolder) {
            val binding = viewHolder.binding as AdapterBottomJourneyDetailBinding

        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, data: JourneyData.Step) {
            val binding = viewHolder.binding as AdapterBottomJourneyDetailBinding
        }

        override fun onItemClick(view: View, position: Int, data: JourneyData.Step): Boolean {
            return false
        }

        override fun onItemLongClick(view: View, position: Int, data: JourneyData.Step): Boolean {
            return false
        }

    }

}
