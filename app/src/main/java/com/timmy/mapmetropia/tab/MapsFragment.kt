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
import androidx.databinding.ViewDataBinding
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

    /** ???????????? */
    private var line: Polyline? = null

    /** ???????????? */
    private var spendTimeMark: Marker? = null

    /**????????????*/
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

    }


    lateinit var journeyData: JourneyData
    private fun initData() { // ????????????????????????????????????raw???Json???
        val journeyDataStr = getRaw(mContext, R.raw.transit_data)
        journeyData = journeyDataStr.toGson(JourneyData())

    }

    private fun initView() {
        val calculateDistance = heightPixel.toDouble() // ????????????????????????double???
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
//            clSummary.setViewSizeByDpUnit(widthPixel, clSummaryHeight) // ???????????????????????????????????????Layout???????????????????????????????????????
            val cardCorner = (calculateDistance * 0.03).toInt()

            val clSummaryInBottomHeight = (calculateDistance * 0.03).toInt()
            icBottom.apply {
                slContent.setCornerRadius(cardCorner)
                bottomSheet.maxHeight = (calculateDistance * 0.8).toInt() // ????????????????????????????????? ???0.8(Zeplin????????????)
                vvIosBar.setMarginByDpUnit(0, 4, 0, 0)
                vvIosBar.background = getRoundBg(mContext, rc, R.color.gray)

                val rvTopBottomMargin = (clSummaryInBottomHeight / 2 - calculateDistance * 0.0065).toInt()
                rvJourneySummary.setMarginByDpUnit(15, rvTopBottomMargin, 15, rvTopBottomMargin)
//                rvJourneySummary.setViewSizeByDpUnit(widthPixel, clSummaryInBottomHeight) // ????????????????????????????????????????????????RecyclerView???????????????????????????Margin???????????????????????????
                rvJourneyDetail.apply {
                    setViewSize(widthPixel - 100, (calculateDistance * 0.95).toInt()) // ????????????????????????????????????(??????????????????????????????????????????????????????...Orz)
                    setMarginByDpUnit(15, 0, 15, 0)
                    setPaddingByDpUnit(0, 0, 0, (calculateDistance * 0.02).toInt())
                }
            }

            behavior.peekHeight = ViewTool.DpToPx(mContext, ((clSummaryInBottomHeight + clSummaryHeight) * 1.1).toFloat()) // ?????????????????? // clSummaryInBottom + clSummary

            val cn = 10 //corner
//            btnBuyTicket.background = getRoundBg(mContext, cn, R.color.light_green) // ???????????????background??????????????????????????????????????????
//            btnStartButton.background = getRoundBg(mContext, cn, R.color.theme_blue) // ???????????????background??????????????????????????????????????????

            tvTotalSpendTime.setTextSize(17)
            tvStartTime.setTextSize(14)
            tvEndTime.setTextSize(14)

        }

        initScreenStatus()
    }

    private fun initValue() { //???????????????
        mBinding.apply {
            tvTotalSpendTime.text = mContext.getString(R.string.total_spend_time).format(journeyData.estimatedTime)
            tvBuy.text = mContext.getString(R.string.total_price).format(journeyData.totalPrice.format("#.00"))
            tvStartTime.text = journeyData.startedOn.toTimeInclude12hour()
            tvEndTime.text = journeyData.endedOn.toTimeInclude12hour()
        }

        showBottomView(journeyData)
    }

    /**??????Google???????????????????????????????????????(iOS??????...Orz)??????????????????Sensor??????*/
    private fun initSensor() {
        val mSensorManager: SensorManager = mContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        mSensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        mSensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI)

        //??????????????????(????????????????????????)
        val timeInterval = 1000L
        otherHandler.postDelayed(object : Runnable {
            override fun run() {
                isTimeToUpdate = true
                otherHandler.postDelayed(this, timeInterval)
            }
        }, timeInterval)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun initEvent() {

        mBinding.ivAnchor.setOnClickListener {// ?????????????????????
            zoomToPoint(deviceLocation?.latLng(), true)

        }

        mBinding.ivBack.setOnClickListener { // ??????????????????????????????????????????App???????????????????????????????????????????????????????????????????????????????????????????????????????????? XD
            initScreenStatus()
            initZoomToPathLocation(true)
//            onBackPressed()
        }

        mBinding.btnBuyMaterial.setOnClickListener {
            Toast.makeText(context, "??????????????????????????????", Toast.LENGTH_SHORT).show()
        }

        mBinding.btnStartTripMaterial.setOnClickListener {
            Toast.makeText(context, "????????????????????????????????????", Toast.LENGTH_SHORT).show()

        }

    }

    private fun initScreenStatus() {
        setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED)

//        setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED) // ???????????????
    }

    override fun onBackPressed(): Boolean {
        return when { // ??????
            behavior.state != BottomSheetBehavior.STATE_COLLAPSED -> {
                setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED)
                true
            }
            else -> {
                activity?.finish()
                false
            }
        }
    }

    // ?????? GPS ?????????
    private fun openGPS(listener: LocationListener) {
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // ????????????
        if (!checkPermission()) {
            requestPermissions() // ??????????????????
            return
        }

        // ?????? GPS ????????????
        val minTime = 100L
        val minDistance = 0L
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance.toFloat(), listener)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance.toFloat(), listener)
    }

    private fun checkPermission() = EasyPermissions.hasPermissions(mContext, *perms)

    //??????GPS??????
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
        //????????????
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //????????????
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

                    // ?????????????????????(?????????)???????????????????????????
                    if (isTimeToUpdate) {
                        myLocationMark?.rotation = sensorOri.getPicRotationAngle()
                        isTimeToUpdate = false
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }


    /**????????????????????????????????????????????????*/
    private fun initZoomToPathLocation(needAnimation: Boolean = false) {
        zoomToBounds(journeyData.steps.map { it.oriLatLng() }, needAnimation)
    }

    override fun onResume() {
        super.onResume()

        openGPS(locationListener)
        showBottomView(journeyData)
    }

    override fun onPause() {
        super.onPause()
        removeLocationListener()
    }

    /**????????????App?????????????????????GPS?????????????????????*/
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

        //?????????????????????????????????
        mMap.uiSettings.isRotateGesturesEnabled = false
        mMap.uiSettings.isTiltGesturesEnabled = false
//
        //?????????????????????
        initZoomToPathLocation()

        // ????????????Marker
        val departureMarker = addLocationCustomMarker(journeyData.steps.first().destinationLatLng(), MarkerType.Departure)
        val destinationMarker = addLocationCustomMarker(journeyData.steps.last().destinationLatLng(), MarkerType.Destination)

        drawPathsOnMap(journeyData.steps.map { it.polyline })

        mMap.setOnMarkerClickListener {
            if (it == myLocationMark) {
                return@setOnMarkerClickListener true
            }
            if (it == departureMarker || it == destinationMarker)
                setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)

            return@setOnMarkerClickListener true
        }
    }


    //?????????????????????????????????step???polyLine?????????
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

        return myLocationMark
    }


    private fun zoomToPoint(latLng: LatLng?, needAnimation: Boolean = false, zoom: Float = 18f) {
        if (latLng == null) {
            Toast.makeText(mContext, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show()
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
        val padding = (heightPixel * 0.1).toInt()// ????????????????????????

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


    //????????????Marker??????????????????????????????????????????????????????????????????????????????Marker
    enum class MarkerType {
        DeviceLoation, // ??????
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

    private var bottomSheetStatus = BottomSheetBehavior.STATE_COLLAPSED

    private fun setBottomSheetState(state: Int) {

        mBinding.flSheet.postDelayed({

            behavior.state = state
            bottomSheetStatus = state

        }, 300L)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showBottomView(journeyData: JourneyData?) {
        if (journeyData == null)
            return

        //??????????????????
        mBinding.icBottom.apply {

            //RecyclerView?????????
            rvJourneySummary.apply {
                adapter = JourneySummaryAdapter(mContext).apply {
                    addItem(journeyData.steps)
                }
            }

            //??????Behavior???????????????????????????RecyclerView????????????
            //???????????????????????????????????????????????????????????????RecyclerView?????????
            vvScrollTouch.setOnTouchListener { v, event ->
                rvJourneyDetail.requestDisallowInterceptTouchEvent(true)
                false
            }

            rvJourneyDetail.apply {
                isNestedScrollingEnabled = true

                adapter = JourneyDetailAdapter(mContext).apply {
                    val list = journeyData.steps.toMutableList() // ??????????????????????????????????????????????????????????????????????????????????????????
                    list.add(0, list[0])
                    list.add(list.last())
                    addItem(list)

                    listener = object : JourneyDetailAdapter.ClickListener {
                        override fun click(data: JourneyData.Step) {
                            setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED)
                        }
                    }
                }
            }
            setBottomSheetState(bottomSheetStatus)

        }

    }

    /** ?????????????????????Summary Adapter */
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
            binding.tvShortNoBus.background = getRoundBg(context, cn, R.color.transparent, R.color.theme_blue, 1)
            binding.tvShortNoBus.setTextSize(14)
            binding.tvShortNoTram.background = getRoundBg(context, cn, R.color.transparent, R.color.theme_red, 1)
            binding.tvShortNoTram.setTextSize(14)

            binding.ivNext.setMarginByDpUnit(6, 0, 6, 0)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, data: JourneyData.Step) {
            val binding = viewHolder.binding as AdapterBottomJourneySummaryBinding

            binding.setShowText(data, position)
            Glide.with(context)
                .load(data.mode.getIconDrawable())
                .into(binding.ivMode)

            binding.ivNext.isVisible = position != itemCount - 1// ???????????????ivNext?????????
        }

        override fun onItemClick(view: View, position: Int, data: JourneyData.Step): Boolean {
            return false
        }

        override fun onItemLongClick(view: View, position: Int, data: JourneyData.Step): Boolean {
            return false
        }

        /**??????dataMode??????????????????*/
        private fun AdapterBottomJourneySummaryBinding.setShowText(data: JourneyData.Step, position: Int) {

            when (data.mode) {
                StepMode.Walk.content -> {
                    this.tvWalkTime.isVisible = true
                    this.tvShortNoBus.isVisible = false
                    this.tvShortNoTram.isVisible = false
                    this.tvWalkTime.text = data.estimateToMin()

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

    /** ?????????????????????????????? Adapter */
    class JourneyDetailAdapter(val context: Context) :
        BaseRecyclerViewDataBindingAdapter<JourneyData.Step>(context, R.layout.adapter_bottom_journey_detail) {

        private val heightPixel by lazy { context.resources.displayMetrics.heightPixels }

        interface ClickListener {
            fun click(data: JourneyData.Step)
        }

        var listener: ClickListener? = null

        override fun initViewHolder(viewHolder: ViewHolder) {
            val binding = viewHolder.binding as AdapterBottomJourneyDetailBinding
            //????????????include??????
            val cn = 100 // corner
            val ir = 11 // icon radius
            val nr = 4 //number radius
            binding.icDeparture.apply {
                ivTitle.background = getRoundBg(context, cn, R.color.theme_blue)
                vvGrayPoint1.background = getRoundBg(context, cn, R.color.gray_point)
                tvDepartureName.setTextSize(14)
                tvStartTime.setTextSize(14)
            }
            binding.icWalk.apply {
                ivTitle.background = getRoundBg(context, ir, R.color.icon_back_gray)
                vvGrayPoint1.background = getRoundBg(context, cn, R.color.gray_point)
                vvGrayPoint2.background = getRoundBg(context, cn, R.color.gray_point)
                tvWalkStatus.setTextSize(14)
                btnPreview.apply {
//                    background = getRoundBg(context, 7, R.color.btn_back_gray)
                    setTextSize(13)
                }
            }
            binding.icBus.apply {
                //??????Icon???
                vvGrayPoint1.background = getRoundBg(context, cn, R.color.gray_point)
                ivTitle1.background = getRoundBg(context, ir, R.color.icon_back_dark)
                vvBusRound.background = getRoundBg(context, cn, R.color.theme_blue)
                ivTitle2.background = getRoundBg(context, ir, R.color.icon_back_dark)
                vvGrayPoint2.background = getRoundBg(context, cn, R.color.gray_point)

                //??????Content???
                val intervalTop = (heightPixel / 200) + 3 // ???????????????
                tvBusName.setTextSize(14)
                tvStartTime.setTextSize(14)
                tvArrvingStatus.apply {
                    setMarginByDpUnit(0, intervalTop, 0, 0)
                    setTextSize(14)
                }
                tvWheeler.setTextSize(14)
                tvEndTime.setTextSize(14)
                vvAnchorContentBottom.setMarginByDpUnit(0, intervalTop, 0, 0)
                tvBusNo.apply {
                    setMarginByDpUnit(0, intervalTop, 0, 0)
                    setTextSize(14)
                    background = getRoundBg(context, nr, R.color.transparent, R.color.theme_blue, 1)
                }
            }
            binding.icTram.apply {
                //??????Icon???
                vvGrayPoint1.background = getRoundBg(context, cn, R.color.gray_point)
                ivTitle1.background = getRoundBg(context, ir, R.color.icon_back_dark)
                vvBusRound.background = getRoundBg(context, cn, R.color.theme_red)
                ivTitle2.background = getRoundBg(context, ir, R.color.icon_back_dark)
                vvGrayPoint2.background = getRoundBg(context, cn, R.color.gray_point)

                //??????Content???
                val intervalTop = (heightPixel / 200) + 3 // ???????????????
                tvTramName.setTextSize(14)
                tvStartTime.setTextSize(14)
                tvArrvingStatus.apply {
                    setMarginByDpUnit(0, intervalTop, 0, 0)
                    setTextSize(14)
                }
                tvWheeler.setTextSize(14)
                tvEndTime.setTextSize(14)
                vvAnchorContentBottom.setMarginByDpUnit(0, intervalTop, 0, 0)
                tvTramNo.apply {
                    setMarginByDpUnit(0, intervalTop, 0, 0)
                    setTextSize(14)
                    background = getRoundBg(context, nr, R.color.transparent, R.color.theme_red, 1)
                }

            }
            binding.icDestination.apply {
                vvGrayPoint1.background = getRoundBg(context, cn, R.color.gray_point)
                tvDestinationName.setTextSize(14)
                tvEndTime.setTextSize(14)
            }
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, data: JourneyData.Step) {
            val binding = viewHolder.binding as AdapterBottomJourneyDetailBinding
            // IncludeLayoutArray
            val icArray = arrayOf(binding.icDeparture, binding.icWalk, binding.icBus, binding.icTram, binding.icDestination)
            hideAllIcInLayout(icArray) // ????????????IncludeLayout??????????????????ItemViewType?????????
            val type = getItemViewType(position)
            showBindingByType(type, icArray)

            //?????????????????????
            when (type) {
                0 -> {// ??????
                    binding.icDeparture.apply {
                        tvDepartureName.text = data.destinationName
                        tvStartTime.text = data.startedOn.toTimeInclude12hour()
                    }
                }
                1 -> { //??????
                    binding.icWalk.apply {
//                        val duration = (data.endedOn-data.startedOn)
                        tvWalkStatus.text = context.getString(R.string.walk_content).format(data.estimateToMin(), data.distance.meterToMilesOrFit())
                        // ?????? preview ????????????
                        btnPreviewMaterial.setOnClickListener {
                            Toast.makeText(context, "???${position}???????????????????????????${data.distance.meterToMilesOrFit()}, ??????${data.estimateToMin()}?????????", Toast.LENGTH_SHORT).show()
                            listener?.click(data)

                        }
                    }
                }
                2 -> { //??????
                    binding.icBus.apply {
                        tvWheeler.text = data.destinationName
                        tvBusName.text = data.shortName
                        tvStartTime.text = data.startedOn.toTimeInclude12hour()
                        tvArrvingStatus.text = data.arrive.getArriveTime()
                        tvEndTime.text = data.endedOn.toTimeInclude12hour()
                        tvBusNo.text = data.shortNameNo
                        ivTicket.setOnClickListener {
                            Toast.makeText(context, "???${position}?????????????????????????????????${data.price}??????, ??????${data.estimateToMin()}?????????", Toast.LENGTH_SHORT).show()
                            listener?.click(data)
                        }
                    }

                }
                3 -> { //??????
                    binding.icTram.apply {
                        tvWheeler.text = data.destinationName
                        tvTramName.text = data.shortName
                        tvStartTime.text = data.startedOn.toTimeInclude12hour()
                        tvArrvingStatus.text = data.arrive.getArriveTime()
                        tvEndTime.text = data.endedOn.toTimeInclude12hour()
                        tvTramNo.text = data.shortNameNo
                        ivTicket.setOnClickListener {
                            Toast.makeText(context, "???${position}??????????????????????????????${data.price}??????, ??????${data.estimateToMin()}?????????", Toast.LENGTH_SHORT).show()
                            listener?.click(data)
                        }
                    }
                }
                4 -> { // ??????
                    binding.icDestination.apply {
                        tvDestinationName.text = data.destinationName
                        tvEndTime.text = data.endedOn.toTimeInclude12hour()
                    }
                }
            }
        }

        /**??????????????????????????????????????? Arriving (??????3??????) ??? Arrive in X min*/
        private fun Int.getArriveTime()
                : String {
            val calculateTime = this.toLong() * DateTool.oneSec //?????????????????????????????????

            val interval = (calculateTime - Date().time) / DateTool.oneMin
            return when {
                interval < 0 -> "Arrive in $interval min" //?????????????????????????????????
                interval < 3 -> "Arriving"
                else -> "Arrive in $interval min"
            }
        }

        private fun showBindingByType(
            type: Int, icArray: Array<ViewDataBinding>
        ) {

            when (type) {
                0 -> icArray[0].root.isVisible = true
                1 -> icArray[1].root.isVisible = true
                2 -> icArray[2].root.isVisible = true
                3 -> icArray[3].root.isVisible = true
                4 -> icArray[4].root.isVisible = true
            }
        }

        private fun hideAllIcInLayout(
            layout: Array<ViewDataBinding>
        ) {
            layout.forEach { it.root.isVisible = false }
        }


        override fun getItemViewType(position: Int)
                : Int {
            return when {
                position == 0 -> 0 // ??????
                position == itemCount - 1 -> 4 // ??????
                getItemData(position).mode == StepMode.Walk.content -> 1
                getItemData(position).mode == StepMode.Bus.content -> 2
                getItemData(position).mode == StepMode.Tram.content -> 3
                else -> -1
            } // other
        }

        override fun onItemClick(
            view: View, position: Int, data: JourneyData.Step
        )
                : Boolean {
            return true
        }

        override fun onItemLongClick(
            view: View, position: Int, data: JourneyData.Step
        )
                : Boolean {
            return false
        }

    }

}

