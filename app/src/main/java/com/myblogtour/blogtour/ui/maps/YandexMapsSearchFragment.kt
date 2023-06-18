package com.myblogtour.blogtour.ui.maps

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.myblogtour.blogtour.R
import com.myblogtour.blogtour.databinding.FragmentSearchYandexMapsBinding
import com.myblogtour.blogtour.utils.BaseFragment
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError


class YandexMapsSearchFragment :
    BaseFragment<FragmentSearchYandexMapsBinding>(FragmentSearchYandexMapsBinding::inflate),
    UserLocationObjectListener, Session.SearchListener,
    CameraListener {

    private lateinit var locationmapkit: UserLocationLayer
    private lateinit var searchSession: Session
    private lateinit var searchManager: SearchManager
    private var startSearch = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MapKitFactory.initialize(requireActivity())
        SearchFactory.initialize(requireActivity())

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
        val mapKit = MapKitFactory.getInstance()
        locationmapkit = mapKit.createUserLocationLayer(binding.mapview.mapWindow)
        locationmapkit.isVisible = true
        locationmapkit.setObjectListener(this)
        locationmapkit.isAutoZoomEnabled = true
        binding.mapview.map.addCameraListener(this@YandexMapsSearchFragment)

        binding.searchEdit.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                startSearch = true
                submitQuery(binding.searchEdit.text.toString())
                closeKeyBoard()
            }
            false
        }
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPOsition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean,
    ) {
        if (finished) {
            if (startSearch){
                submitQuery(binding.searchEdit.text.toString())
            }
        }
    }

    private fun submitQuery(query: String) {
        searchSession = searchManager.submit(query,
            VisibleRegionUtils.toPolygon(binding.mapview.map.visibleRegion),
            SearchOptions(),
            this@YandexMapsSearchFragment)
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        locationmapkit.setAnchor(PointF((binding.mapview.width() * 0.5).toFloat(),
            (binding.mapview.height() * 0.5).toFloat()),
            PointF((binding.mapview.width() * 0.5).toFloat(),
                (binding.mapview.height() * 0.83).toFloat())
        )
//        val picIcon = userLocationView.pin.useCompositeIcon()
//        picIcon.setIcon("icon",
//            ImageProvider.fromResource(requireActivity(), R.drawable.search_result),
//            IconStyle().setAnchor(
//                PointF(0f, 0f)
//            ).setRotationType(RotationType.ROTATE).setZIndex(0f).setScale(1f))
//        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x6600001
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    override fun onSearchResponse(response: Response) {
        val mapObjCollection = binding.mapview.map.mapObjects
        mapObjCollection.clear()
        for (searchResult in response.collection.children) {
            val point = searchResult.obj!!.geometry[0].point
            if (point != null) {
                mapObjCollection.addPlacemark(Point(point.latitude, point.longitude),
                    ImageProvider.fromResource(requireActivity(), R.drawable.search_result))
            }
        }
    }

    override fun onSearchError(error: Error) {
        if (error is RemoteError) {
            toast("Remote error")
        } else if (error is NetworkError) {
            toast("Network error")
        }
    }

    private fun toast(mess: String) {
        Toast.makeText(requireActivity(), mess, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        binding.mapview.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun closeKeyBoard() {
        requireActivity().currentFocus?.let { view ->
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}