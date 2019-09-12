package park.bika.com.parkapplication.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import park.bika.com.parkapplication.BuildConfig;
import park.bika.com.parkapplication.Constant;
import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.ThirdPartyMapType;
import park.bika.com.parkapplication.adapters.AdvertisementAdapter;
import park.bika.com.parkapplication.adapters.ModalAdapter;
import park.bika.com.parkapplication.bean.Advertisement;
import park.bika.com.parkapplication.bean.ModalBean;
import park.bika.com.parkapplication.main.MainActivity;
import park.bika.com.parkapplication.main.SafePaymentActivity;
import park.bika.com.parkapplication.utils.BdAndGcjUtil;
import park.bika.com.parkapplication.utils.CalcUtil;
import park.bika.com.parkapplication.utils.InputMethodUtils;
import park.bika.com.parkapplication.utils.NetworkConnectUtil;
import park.bika.com.parkapplication.utils.ShareUtil;
import park.bika.com.parkapplication.utils.StringUtil;
import park.bika.com.parkapplication.utils.ToastUtil;
import park.bika.com.parkapplication.view.ChildListView;
import park.bika.com.parkapplication.view.MapFrameLayout;
import park.bika.com.parkapplication.view.SearchView;

/**
 * @作者 huangzujun
 * @日期 2019/7/16
 * @描述 首页 Fragment
 */
public class MainFragment extends BaseFragment implements View.OnClickListener, SensorEventListener, OnGetSuggestionResultListener {

    public static final int MAX_PAGE_PARK_INFO_NUMBER = 100;
    public static final int PERMISSIONS_REQUEST_LOCATION = 0x000700;
    public static final int SHOW_TIME_COUNT = 0x000001;//计时

    private MainActivity mainAct;
    private SearchView search_content;
    private ImageView mIVMainAdd, iv_main_map_refresh;
    private TextView tv_main_park_address, main_park_more,
            tv_main_park_count, main_navigation, tv_main_park_tip,
            main_park_name, main_park_pay, main_park_num, main_park_price, tv_park_time;
    private ChildListView lv_advertisement;
    private FloatingActionButton mFloatBtn;
    private NestedScrollView mNSVScroll;
    private LinearLayout ll_park_time;//停车计时容器
    private MapView mMapView;
    private AutoCompleteTextView keyWorldsView; //搜索词提示视图

    private int mCurrentDirection = 0,//定位方向信息
            searchPosition = 0; //搜索候选下标
    private Integer locationRadius = Constant.SEARCH_RADIUS; //搜索半径300m内的
    private boolean isChooseAddress = false; //是否自选地址
    private boolean isMapLoaded = false;      //地图是否加载完成
    private double lastX = 0;
    private Integer curParkCount = 0,
            parkingSecond = 0;//停车总时长，单位秒
    private double chooseParkingDistance,//选择的停车点距离
            parkPrice = 3.00; //停车价格/小时
    private MyLocationListener mLocationListener = new MyLocationListener();
    private ArrayAdapter<String> sugAdapter = null; //搜索Adapter
    public LocationClient mLocationClient;
    private BaiduMap mBaiduMap;  //地图控制器对象
    private double mCurrentLat;  //当前纬度
    private double mCurrentLon;  //当前经度
    private PoiInfo toPoiInfo;     //目的信息（包含经纬度）
    private double chooseLat;    //自选纬度
    private double chooseLon;    //自选经度
    private String mCurAddress = "", curCity = "重庆";
    private MyNotifyLister notifyLister = new MyNotifyLister();
    private float mCurrentAccracy;
    private MyLocationData locData;
    private SensorManager mSensorManager; //传感器
    private PoiSearch mPoiSearch; //搜索模块
    private SuggestionSearch mSuggestionSearch; //地点输入提示检索
    private List<SuggestionResult.SuggestionInfo> suggestionInfos; //搜索结果list
    private Marker preMarker; //上一次点击标记
    private PoiInfo recommendPoiInfo; //推荐停车信息
    /**
     * 自定义自身定位图标  null:默认图标 <br>
     * mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo); 获取资源图<br>
     * <p>accuracyCircleFillColor 填充颜色 accuracyCircleStrokeColor 边框颜色
     * MyLocationConfiguration locationConfiguration = new MyLocationConfiguration(mCurrentMode,
     * true, mCurrentMarker, accuracyCircleFillColor, accuracyCircleStrokeColor);</p>
     */
    private BitmapDescriptor mCurrentMarker = null;

    /**
     * 初始化全局 bitmap 信息
     */
    BitmapDescriptor bitmapSelector = null;
    BitmapDescriptor bitmapNormal = null;
    BitmapDescriptor bitmapAddMarker = null; //自选标记图标

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainAct = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        initData();
        initListener();
        return view;
    }

    private void initData() {
        /*--------------- 广告区域 -------------------*/
        List<Advertisement> advertisementList = new ArrayList<>();
        advertisementList.add(new Advertisement(R.drawable.ic_qd_vector, "每日签到", "每日签到抽奖送停车券！,连续签到7天立得8折停车券", 0));
        advertisementList.add(new Advertisement(R.drawable.ic_wash_vector, "洗车保养服务", null, R.mipmap.car_maintenance));
        advertisementList.add(new Advertisement(R.drawable.ic_car_market_vector, "车位市场", null, R.mipmap.car_marker));
        advertisementList.add(new Advertisement(R.drawable.ic_ep_mall_vector, "EP商场", null, R.mipmap.car_mall));
        lv_advertisement.setAdapter(new AdvertisementAdapter(context, advertisementList));
        /*--------------- 广告区域 end -------------------*/
        mBaiduMap = mMapView.getMap();
        mPoiSearch = PoiSearch.newInstance();
        mSuggestionSearch = SuggestionSearch.newInstance(); //建议查询
        bitmapSelector = BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_park_seletor);
        bitmapNormal = BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_park);
        bitmapAddMarker = BitmapDescriptorFactory.fromResource(R.mipmap.ic_add_marker);
        keyWorldsView = search_content.getAet_search();
        sugAdapter = new ArrayAdapter<>(mainAct, android.R.layout.simple_list_item_1);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1); //至少1字符后才提示
        keyWorldsView.setDropDownVerticalOffset(CalcUtil.dp2px(context, 3));
        keyWorldsView.setDropDownAnchor(R.id.search_content);//指定在某个视图下
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mainAct, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionToast();
            } else {
                ActivityCompat.requestPermissions(mainAct, new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION
                );
            }
        }
    }

    private void showPermissionToast() {
        showAlertDialog(getString(R.string.modal_dialog_tip), "获取定位权限失败,\n请授权后重试~", "去设置", v -> {
            Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.parse("package:" + context.getPackageName());
            settingIntent.setData(uri);
            startActivity(settingIntent);
            dismiss();
        });
    }

    private void initListener() {
        search_content.setSearchIconClickListener(searchTxt -> {
            InputMethodUtils.hide(context);
            if (suggestionInfos != null && suggestionInfos.size() > 0) {
                SuggestionResult.SuggestionInfo suggestionInfo = suggestionInfos.get(searchPosition);
                if (suggestionInfo != null &&
                        chooseLat != suggestionInfo.getPt().latitude &&
                        chooseLon != suggestionInfo.getPt().longitude) {
                    showSearchAddress(suggestionInfos.get(searchPosition));
                } else ToastUtil.showToast(mainAct, "已显示" + searchTxt + "周边停车位置~");
            } else {
                ToastUtil.showToast(mainAct, "获取候选词失败，请检查网络状态后重试~");
            }
        });
        search_content.setVoiceIconTouchListener(editText -> ToastUtil.showToast(mainAct, "录音中……"));
        mIVMainAdd.setOnClickListener(this);
        iv_main_map_refresh.setOnClickListener(this);
        mFloatBtn.setOnClickListener(this);
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        main_navigation.setOnClickListener(this);
        main_park_more.setOnClickListener(this);
        //滑动监听
        mNSVScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (view, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY > 30) { //上滑30
                        mFloatBtn.setVisibility(View.VISIBLE);
                    } else {
                        mFloatBtn.setVisibility(View.GONE);
                    }
                });
        //地图加载完成监听
        mBaiduMap.setOnMapLoadedCallback(() -> isMapLoaded = true);
        //地图单击
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                showCurAddressPark(point);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                showCurAddressPark(mapPoi.getPosition());
                return true;
            }
        });
        //地图滑动时改变定位模式
        mBaiduMap.setOnMapTouchListener(event -> {
            float startX = 0, startY = 0;
            float MoveDistance = 100;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float endX = event.getX();
                    float endY = event.getY();
                    if (Math.abs(startY - endY) > MoveDistance ||
                            Math.abs(startX - endX) > MoveDistance) {
                        setLocationMode(MyLocationConfiguration.LocationMode.NORMAL);
                    }
                    break;
            }
        });
        //文本输入提示
        search_content.setSetTxtOnChangedListener(searchTxt -> mSuggestionSearch.requestSuggestion(
                new SuggestionSearchOption().city(curCity).keyword(searchTxt))
        );
        //搜索提示选项点击
        keyWorldsView.setOnItemClickListener((parent, view, position, id) -> {
            InputMethodUtils.hide(context);
            showSearchAddress(suggestionInfos.get(position));
            searchPosition = position;
        });
        //地图图标地点击
        mBaiduMap.setOnMarkerClickListener(marker -> {
            Bundle info = marker.getExtraInfo();//获取marker信息
            if (preMarker == marker || info == null) {
                return false;
            }
            PoiInfo poiInfo = info.getParcelable("poiInfo");
            if (preMarker != null) { //设置marker图标
                preMarker.setIcon(bitmapNormal);
            }
            marker.setIcon(bitmapSelector);
            preMarker = marker;
            updateParkInfo(poiInfo, true);
            return false;
        });
        initLocation();//初始化定位
    }

    //地图定位至搜索地址（刷新地图）
    private void showSearchAddress(SuggestionResult.SuggestionInfo suggestionInfo) {
        if (mBaiduMap == null || suggestionInfo == null || mLocationClient == null) {
            return;
        }
        if (mLocationClient.isStarted()) mLocationClient.stop();
        isChooseAddress = true;
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(suggestionInfo.getPt()).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        showCurAddressPark(suggestionInfo.getPt());
    }

    //显示自选标记周边停车场
    private void showCurAddressPark(LatLng point) {
        if (mBaiduMap == null || point == null || mLocationClient == null) {
            return;
        }
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        isChooseAddress = true;
        chooseLat = point.latitude;
        chooseLon = point.longitude;
        mBaiduMap.clear();//移除初始标记并添加自选标志
        OverlayOptions options = new MarkerOptions()
                .position(point)
                .icon(bitmapAddMarker)
                .alpha(.9f)
                .animateType(MarkerOptions.MarkerAnimateType.jump);
        mBaiduMap.addOverlay(options);
        showNearbyPark(point);//显示停车位
    }

    /**
     * 更新停车场信息
     *
     * @param isClick 是否为点击的坐标
     * @param poiInfo 坐标信息
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateParkInfo(PoiInfo poiInfo, boolean isClick) {
        if (poiInfo == null || mBaiduMap == null) {
            return;
        }
        if (isClick) {
            if (recommendPoiInfo == poiInfo) {
                setParkInfoTitle(0);
            } else {
                setParkInfoTitle(1);
            }
        } else {
            TextView tv_recommend = new TextView(mainAct);
            tv_recommend.setWidth(CalcUtil.dp2px(mainAct, 72));
            tv_recommend.setHeight(CalcUtil.dp2px(mainAct, 32));
            tv_recommend.setBackgroundResource(R.mipmap.ic_park_marker_recommend);
            LatLng ll = poiInfo.getLocation();
            InfoWindow infoWindow = new InfoWindow(
                    BitmapDescriptorFactory.fromView(tv_recommend),
                    ll, -70, null);
            mBaiduMap.showInfoWindow(infoWindow);//显示标志提示框
            setParkInfoTitle(0);
        }
        toPoiInfo = poiInfo;
        main_park_name.setText(poiInfo.getName());
        main_park_pay.setText(Constant.PAYS[(int) (Math.random() * 3)]);
        main_park_num.setText(String.format(getString(R.string.main_park_num), String.valueOf((int) (Math.random() * 99 + 1))));
        main_park_price.setText(String.format(getString(R.string.main_park_price), String.valueOf((int) parkPrice)));
        chooseParkingDistance = DistanceUtil.getDistance(new LatLng(mCurrentLat, mCurrentLon), poiInfo.location);
        if (chooseParkingDistance / 1000 < 1) {
            main_navigation.setText(String.format("%1dm", (int) Math.ceil(chooseParkingDistance)));
        } else {

            main_navigation.setText(String.format("%1dkm", (int) Math.ceil(chooseParkingDistance / 1000)));
        }
    }

    /**
     * 设置停车场信息标题内容<br>
     * val: 0 推荐 <br>
     * ---- 1 选择 <br>
     * ---- 资源ID
     *
     * @param titleResId 标题资源
     */
    private void setParkInfoTitle(Integer titleResId) {
        if (titleResId == null) {
            return;
        }
        switch (titleResId) {
            case 0:
                titleResId = R.string.main_page_park_recommend;
                break;
            case 1:
                titleResId = R.string.main_page_park_choose;
                break;
        }
        tv_main_park_tip.setText(titleResId);
    }

    /**
     * 设置定位模式：<br>
     * LocationMode.NORMAL:普通 <br>
     * LocationMode.FOLLOWING:跟随 <br>
     * LocationMode.COMPASS:罗盘
     * <p>使用前必须确保MapView.getMap()不为空。即地图控制器不为空</p>
     *
     * @param locationMode 定位模式
     */
    private void setLocationMode(MyLocationConfiguration.LocationMode locationMode) {
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationConfiguration(
                    new MyLocationConfiguration(locationMode, true, mCurrentMarker));
        }
    }

    private void initView(View view) {
        search_content = view.findViewById(R.id.search_content);
        mIVMainAdd = view.findViewById(R.id.iv_main_add);
        iv_main_map_refresh = view.findViewById(R.id.iv_main_map_refresh);
        tv_main_park_address = view.findViewById(R.id.tv_main_park_address);
        tv_main_park_tip = view.findViewById(R.id.tv_main_park_tip);
        tv_main_park_count = view.findViewById(R.id.tv_main_park_count);
        main_park_num = view.findViewById(R.id.main_park_num);
        main_park_price = view.findViewById(R.id.main_park_price);
        main_park_pay = view.findViewById(R.id.main_park_pay);
        main_park_name = view.findViewById(R.id.main_park_name);
        main_navigation = view.findViewById(R.id.main_navigation);
        main_park_more = view.findViewById(R.id.main_park_more);
        mFloatBtn = view.findViewById(R.id.float_main_btn);
        mNSVScroll = view.findViewById(R.id.nsv_main_scroll);
        mMapView = view.findViewById(R.id.mv_main_map);
        ll_park_time = view.findViewById(R.id.ll_park_time);
        tv_park_time = view.findViewById(R.id.tv_park_time);
        ll_park_time.setOnClickListener(this);
        //解决地图滑动与NestedScrollView滑动冲突
        MapFrameLayout mapContainer = view.findViewById(R.id.main_map_container);
        mapContainer.setNestedScrollView(mNSVScroll);
        lv_advertisement = view.findViewById(R.id.clv_advertisement);
    }

    /**
     * 设置当前位置停车场数量
     */
    private void setLocationParkCount() {
        if (tv_main_park_count == null) {
            return;
        }
        String parkCountTip = String.format(getResources().getString(R.string.main_park_count), curParkCount);
        int start = parkCountTip.indexOf(" ");
        int end = parkCountTip.lastIndexOf(" ");
        SpannableString parkCountSpan = new SpannableString(parkCountTip);
        parkCountSpan.setSpan(new ForegroundColorSpan(
                        getResources().getColor(R.color.colorParkBlue)),
                start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        parkCountSpan.setSpan(new RelativeSizeSpan(1.3f),
                start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_main_park_count.setText(parkCountSpan);
    }

    private void initLocation() {
        //搜索数据监听
        mPoiSearch.setOnGetPoiSearchResultListener(new MyOnGetPoiSearchResultListener());
        //获取传感器管理服务
        mSensorManager = (SensorManager) mainAct.getSystemService(Context.SENSOR_SERVICE);
        mLocationClient = new LocationClient(mainAct.getApplicationContext());
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption locationOption = new LocationClientOption();
        locationOption.setOpenGps(true);            //打开GPS
        locationOption.setCoorType("bd09ll");       //坐标类型
        locationOption.setScanSpan(2000);           //2s重新获取位置
        locationOption.setIsNeedAddress(true);      //是否需要地址信息，默认false
        locationOption.setIsNeedLocationDescribe(true);//是否需要位置描述，默认false
        locationOption.setIsNeedLocationPoiList(true);//是否需要兴趣点，默认false
        mLocationClient.setLocOption(locationOption);
        mLocationClient.registerNotify(notifyLister);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        requestLocation(MyLocationConfiguration.LocationMode.FOLLOWING);
    }

    /**
     * @param locationMode 定位模式：<br>
     *                     LocationMode.NORMAL:普通 <br>
     *                     LocationMode.FOLLOWING:跟随 <br>
     *                     LocationMode.COMPASS:罗盘
     */
    private void requestLocation(MyLocationConfiguration.LocationMode locationMode) {
        setLocationMode(locationMode);//设置定位模式
        if (NetworkConnectUtil.isNetworkConnected(mainAct)) {
            mLocationClient.start();//开始定位
        } else {
            showToast("检测到网络已断开，请开启网络后重试~");
        }
    }

    /**
     * 获取在线建议搜索结果
     *
     * @param res 结果
     */
    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        suggestionInfos = res.getAllSuggestions();
        List<String> suggests = new ArrayList<>();//获取搜索结果数据
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggests.add(info.key);
            }
        }
        sugAdapter = new ArrayAdapter<>(mainAct, android.R.layout.simple_list_item_1, suggests);
        keyWorldsView.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }

    /**
     * 传感器发生变化
     *
     * @param event 传感器事件
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            if (mBaiduMap != null) {
                mBaiduMap.setMyLocationData(locData);
            }
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 周边搜索监听回调
     */
    class MyOnGetPoiSearchResultListener implements OnGetPoiSearchResultListener {

        @Override
        public void onGetPoiResult(final PoiResult result) {
            if (!isAdded()) return;
            if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                curParkCount = 0;
                toPoiInfo = null;
                ToastUtil.showToastInUIThread(mainAct, mainAct.getResources().getString(R.string.main_park_no_found_msg));
                locationRadius += 100;
                showNearbyPark(new LatLng(chooseLat, chooseLon));
            } else if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                locationRadius = Constant.SEARCH_RADIUS;
                curParkCount = result.getAllPoi().size();
                recommendPoiInfo = result.getAllPoi().get(0);//初始化推荐
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (PoiInfo poi : result.getAllPoi()) {
                    LatLng point = new LatLng(poi.location.latitude, poi.location.longitude);
                    addMarker(poi);
                    builder.include(point);
                }
                showAllMarkerInMap(builder);
                setLocationParkCount();
                updateParkInfo(recommendPoiInfo, false);
            }
        }

        /**
         * 调整缩放级别：将所有marker显示在屏幕内
         *
         * @param builder 范围
         */
        private void showAllMarkerInMap(LatLngBounds.Builder builder) {
            if (mBaiduMap != null) {
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomBy(-0.8f));
            }
        }

        /**
         * 添加地图标志物
         *
         * @param poiInfo 标记信息
         */
        private void addMarker(PoiInfo poiInfo) {
            if (mBaiduMap != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("poiInfo", poiInfo);
                //用于在地图上添加Marker
                OverlayOptions options = new MarkerOptions()
                        .position(new LatLng(poiInfo.location.latitude,
                                poiInfo.location.longitude))
                        .icon(bitmapNormal)
                        .alpha(.9f)
                        .animateType(MarkerOptions.MarkerAnimateType.grow)  //从地面生长动画
                        .extraInfo(bundle); //设置额外信息
                //添加并显示
                if (recommendPoiInfo.getUid().equals(poiInfo.getUid())) {
                    preMarker = (Marker) mBaiduMap.addOverlay(options);
                    preMarker.setIcon(bitmapSelector);
                } else {
                    mBaiduMap.addOverlay(options);
                }
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    }

    //自身定位监听
    class MyLocationListener extends BDAbstractLocationListener {

        private long loaderTime = 0;

        @Override
        public void onReceiveLocation(BDLocation location) {
            //MapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            if (location.getLocationWhere() == BDLocation.LOCATION_WHERE_OUT_CN
                    || location.getLocationWhere() == BDLocation.LOCATION_WHERE_UNKNOW) {
                ToastUtil.showToast(mainAct, "目前EP智慧停只支持国内定位，国外车位推荐敬请期待~");
                return;
            }
            if (location.getLocType() == BDLocation.TypeNetWorkException) {
                ToastUtil.showToast(mainAct, "网络不通会导致定位精准度不高，请保持网络通畅~");
            }
            if (!isMapLoaded) {
                if ((System.currentTimeMillis() - loaderTime) >= 5000) {
                    ToastUtil.showToast(mainAct, "当前网络较慢，请耐心等候...");
                }
                loaderTime += 1000;
            }
            //获取经纬度
            double curLat = location.getLatitude();
            double curLon = location.getLongitude();

            if ((mCurrentLat != curLat || mCurrentLon != curLon) && !isChooseAddress && mBaiduMap != null) {
                LatLng ll = new LatLng(curLat, curLon);
                //设置定位数据
                locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        .direction(mCurrentDirection).latitude(curLat)
                        .longitude(curLon).build();
                mBaiduMap.setMyLocationData(locData);
                mCurrentAccracy = location.getRadius();
                mBaiduMap.clear();
                //移动定位图标至定位点上
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                //显示附近车位
                showNearbyPark(ll);
                //设置当前位置地址
                chooseLat = mCurrentLat = curLat;
                chooseLon = mCurrentLon = curLon;
                String curAddress = location.getAddrStr();
                if (!curAddress.equals(mCurAddress) && mainAct != null) {
                    mCurAddress = curAddress;
                    showAddress(mCurAddress);
                    curCity = location.getCity().trim(); //获取城市
                    mainAct.currentDistrict = location.getDistrict();
                }
            }
        }
    }

    //到达指定位置监听
    class MyNotifyLister extends BDNotifyListener {
        @Override
        public void onNotify(BDLocation bdLocation, float distance) { //已到达设置监听位置附近
            arrivedPark();
            if (mLocationClient != null) {
                mLocationClient.removeNotifyEvent(notifyLister);
            }
        }
    }

    //已到达目的地
    private void arrivedPark() {
        showAlertDialog(getString(R.string.modal_dialog_tip), "您已到达目的地附近，是否立即停车？\n（注意：立即停车即开始计时）",
                "立即停车", v -> {
                    ShareUtil.newInstance().getShared(context).edit()
                            .putString("parkingStartTime", StringUtil.getCurrentTime())
                            .apply();
                    handler.postDelayed(countRunnable, 1000);
                    dismiss();
                }, "重选车位", v -> {
                    requestLocation(MyLocationConfiguration.LocationMode.COMPASS);
                    dismiss();
                }
        );
    }

    //设置到达附近监听
    private void setNearbyListener() {
        if (mLocationClient != null) {
            notifyLister.SetNotifyLocation(toPoiInfo.location.latitude, toPoiInfo.location.longitude,
                    50, mLocationClient.getLocOption().getCoorType());
            mLocationClient.start();
        }
    }

    /**
     * 显示附近停车信息 <br>
     *
     * @param ll 检索坐标点（内含经纬度）
     */
    private void showNearbyPark(LatLng ll) {
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .location(ll)
                .radius(locationRadius)
                .keyword("停车场")
                .scope(2) // 1/空: 返回基本信息 2:返回详细信息
                .pageCapacity(MAX_PAGE_PARK_INFO_NUMBER) //每页最大数据量
        );
    }

    /**
     * 显示地位地址 格式为：xxx省xxx市xxx区/县xxx地区附近
     *
     * @param curAddress 当前地址
     */
    private void showAddress(String curAddress) {
        if (!TextUtils.isEmpty(curAddress)) {
            tv_main_park_address.setText(curAddress);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_main_add:
                ToastUtil.showToast(mainAct, "添加功能还未完善，敬请期待~");
                break;
            case R.id.float_main_btn://缓慢回顶部
                mNSVScroll.fullScroll(View.FOCUS_UP);
                break;
            case R.id.iv_main_map_refresh://自身定位
                InputMethodUtils.hide(context);
                locationRadius = Constant.SEARCH_RADIUS;
                mCurrentLat = 0; //随意设置纬度，防止自选位置后刷新自身点没有标志
                isChooseAddress = false;
                ToastUtil.showToast(context, "定位中……");
                requestLocation(MyLocationConfiguration.LocationMode.COMPASS);
                break;
            case R.id.main_park_more://车位详情
                showToast("功能还未完善，敬请期待~");
                break;
            case R.id.main_navigation://导航
                if (curParkCount <= 0 || toPoiInfo == null) {
                    showToast("当前位置未找到停车位，\n请选择其他地点后尝试~");
                    return;
                }
                if (parkingSecond > 0) {
                    showAlertDialog("您当前有未完成订单，\n请先完成订单后重试~", confirm -> dismiss());
                } else if (chooseParkingDistance <= 100) {
                    showAlertDialog("目的地在附近，建议您预约车位~", "立即预约", confirm -> {
                        dismiss();
                        setNearbyListener();
                    }, cancel -> {
                        dismiss();
                        showChooseNavigation();
                    });
                } else {
                    showChooseNavigation();
                }
                break;
            case R.id.ll_park_time://暂停计时
                if (parkingSecond >= 3600) {
                    showAlertDialog("订单结算", "您当前停车时长\n" + StringUtil.formatTime(parkingSecond) + "\n确定要结算吗？", "结算", new stopListener(), v -> dismiss());
                } else if (parkingSecond < 3600) {
                    showAlertDialog("您当前停车时长小于1小时，\n按1小时计算，确定要结算吗？",
                            new stopListener(),
                            v -> dismiss()
                    );
                }
                break;
        }
    }

    //显示选择导航
    private void showChooseNavigation() {
        List<ModalBean> modalBeanList = new ArrayList<>();
        modalBeanList.add(new ModalBean("百度地图", getResources().getColor(R.color.modal_nav_baidu)));
        modalBeanList.add(new ModalBean("高德地图", getResources().getColor(R.color.modal_nav_autonavi)));
        modalBeanList.add(new ModalBean("腾讯地图", getResources().getColor(R.color.modal_nav_tencent)));
        modalBeanList.add(new ModalBean("在线导航", getResources().getColor(R.color.g333333)));
        View modalNavView = View.inflate(context, R.layout.layout_modal, null);
        modalNavView.findViewById(R.id.modal_nav_tip).setVisibility(View.VISIBLE);
        ListView modal_lv = modalNavView.findViewById(R.id.modal_content);
        modal_lv.setAdapter(new ModalAdapter(context, modalBeanList));
        modal(modalNavView).show();
        modal_lv.setOnItemClickListener((parent, v, position, id) -> {
            Intent it = null;
            int requestCode = -1;
            switch (position) {
                case 0:
                    if (StringUtil.isInstalled(context, ThirdPartyMapType.BAIDUMAP_PACKAGENAME)) {
                        it = new Intent();
                        it.setData(Uri.parse("baidumap://map/direction?destination=name:" + toPoiInfo.getName() +
                                "|latlng:" + toPoiInfo.location.latitude + "," + toPoiInfo.location.longitude +
                                "&coord_type=bd09ll&mode=driving&src=andr.bika.epark"
                        ));
                        requestCode = ThirdPartyMapType.BAIDUMAP_REQUSTCODE;
                        ToastUtil.showLongToast(context, "百度导航加载中……");
                        dismissModal();
                    } else {
                        showToast("您尚未安装百度地图，\n请选择其他地图导航~");
                    }
                    break;
                case 1:
                    if (StringUtil.isInstalled(context, ThirdPartyMapType.AUTONAVI_PACKAGENAME)) {
                        it = new Intent();
                        LatLng toLL = BdAndGcjUtil.BD2GCJ(toPoiInfo.getLocation());
                        it.setData(Uri.parse("androidamap://navi?sourceApplication=" + BuildConfig.APPLICATION_ID +
                                "&poiname=我的位置" +
                                "&lat=" + toLL.latitude +
                                "&lon=" + toLL.longitude +
                                "&dev=0"
                        ));
                        requestCode = ThirdPartyMapType.GDMAP_REQUSTCODE;
                        ToastUtil.showLongToast(context, "高德导航加载中……");
                        dismissModal();
                    } else {
                        showToast("您尚未安装高德地图，\n请选择其他地图导航~");
                    }
                    break;
                case 2:
                    if (StringUtil.isInstalled(context, ThirdPartyMapType.TENCENTMAP_PACKAGENAME)) {
                        it = new Intent();
                        LatLng fromLL = BdAndGcjUtil.BD2GCJ(new LatLng(mCurrentLat, mCurrentLon));
                        LatLng toLL = BdAndGcjUtil.BD2GCJ(toPoiInfo.getLocation());
                        it.setData(Uri.parse("qqmap://map/routeplan?type=drive&from=我的位置" +
                                "&fromcoord=" + fromLL.latitude + "," + fromLL.longitude +
                                "&to=" + toPoiInfo.getName() +
                                "&tocoord=" + toLL.latitude + "," + toLL.longitude +
                                "&referer=5QPBZ-7SQWP-FD5D6-LQJTX-WTLJZ-VGFNT"));//referer:腾讯key
                        requestCode = ThirdPartyMapType.TENCENTMAP_REQUSTCODE;
                        ToastUtil.showLongToast(context, "腾讯导航加载中……");
                        dismissModal();
                    } else {
                        showToast("您尚未安装腾讯地图，\n请选择其他地图导航~");
                    }
                    break;
                case 3:

                    dismissModal();
                    break;
            }
            if (it != null) {
                startActivityForResult(it, requestCode);
            }
        });
    }

    //暂停监听
    class stopListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dismiss();
            Intent it = new Intent(context, SafePaymentActivity.class);
            if (parkingSecond < 3600) { //小于1小时 按1小时算
                it.putExtra("orderPrice", parkPrice);
            } else {
                it.putExtra("orderPrice", parkingSecond / 60 / 60 * parkPrice);
            }
            handler.removeCallbacks(countRunnable);
            startActivity(it);
        }

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_TIME_COUNT:
                if (parkingSecond > 0) {
                    ll_park_time.setVisibility(View.VISIBLE);
                    tv_park_time.setText(StringUtil.formatTime(parkingSecond));
                } else {
                    ll_park_time.setVisibility(View.GONE);
                }
                break;
        }
        super.handleMessage(msg);
    }


    //计时线程
    private Runnable countRunnable = new Runnable() {
        @Override
        public void run() {
            parkingSecond++;
            handler.sendEmptyMessage(SHOW_TIME_COUNT);
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onPause() {
        mMapView.onPause();
        mBaiduMap = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        if (mBaiduMap == null) {
            mBaiduMap = mMapView.getMap();
        }
        String parkingStartTime = ShareUtil.newInstance().getShared(context).getString("parkingStartTime", null);
        parkingSecond = StringUtil.getSeconds(parkingStartTime);
        if (!TextUtils.isEmpty(parkingStartTime)) {
            handler.postDelayed(countRunnable, 1000);
        } else {
            handler.sendEmptyMessage(SHOW_TIME_COUNT);
        }
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    @Override
    public void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
        if (mPoiSearch != null) {
            mPoiSearch.destroy();
        }
        if (mSuggestionSearch != null) {
            mSuggestionSearch.destroy();
        }
        if (mLocationClient != null) {
            mLocationClient.stop(); // 退出时销毁定位
        }
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(false);  // 关闭定位图层
        }
        super.onDestroyView();
        if (bitmapNormal != null && bitmapSelector != null && bitmapAddMarker != null) {
            //回收 bitmap 资源
            bitmapNormal.recycle();
            bitmapSelector.recycle();
            bitmapAddMarker.recycle();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            //未授权
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showPermissionToast();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ThirdPartyMapType.TENCENTMAP_REQUSTCODE ||
                requestCode == ThirdPartyMapType.BAIDUMAP_REQUSTCODE ||
                requestCode == ThirdPartyMapType.GDMAP_REQUSTCODE) {
            arrivedPark();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
