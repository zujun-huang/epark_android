package cn.epark.fragments;

import android.Manifest;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
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

import cn.epark.App;
import cn.epark.Constant;
import cn.epark.R;
import cn.epark.ThirdPartyMapType;
import cn.epark.activitys.MainActivity;
import cn.epark.utils.InputMethodUtils;
import cn.epark.utils.OnMultiClickListener;
import cn.epark.utils.ShareUtil;
import cn.epark.utils.StringUtil;
import cn.epark.utils.ToastUtil;


/**
 * Created by huangzujun on 2019/9/25.
 * Describe: 预约停车
 */
public class ShareParkFragment extends BaseFragment implements SensorEventListener {

    public static final int PERMISSIONS_REQUEST_LOCATION = 0x000720;

    private MapView mMapView;
    private android.widget.ImageView mAppointmentBtn;
    private FloatingActionButton mLocationBtn;
    private MainActivity mainAct;

    private BaiduMap mBaiduMap;
    private SensorManager mSensorManager;
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener = new MyLocationListener();
    private MyNotifyLister notifyLister = new MyNotifyLister();
    private boolean isMapLoaded = false;
    private double mCurrentLat;
    private double mCurrentLon;
    private double chooseLat;
    private double chooseLon;
    private boolean isChooseAddress = false; //是否自选地址
    private int mCurrentDirection = 0;
    private float mCurrentAccracy;
    private double lastX = 0;
    private MyLocationData locData;
    private String mCurAddress = "";
    private Integer parkingSecond = 0;
    private Integer locationRadius = Constant.SEARCH_RADIUS;
    private Marker preMarker;
    private boolean isAppointment = false;//预约

    /**
     * 初始化全局 bitmap 信息
     */
    BitmapDescriptor bitmapSelector = null;
    BitmapDescriptor bitmapNormal = null;
    BitmapDescriptor bitmapAddMarker = null;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_park, container, false);
        initView(view);
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        mLocationBtn.setOnClickListener(clickListener);
        mAppointmentBtn.setOnClickListener(clickListener);
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
        mBaiduMap.setOnMapLoadedCallback(() -> isMapLoaded = true);
        initLocation();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainAct = (MainActivity) getActivity();
    }

    private void initData() {
        bitmapSelector = BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_park_seletor);
        bitmapNormal = BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_park);
        bitmapAddMarker = BitmapDescriptorFactory.fromResource(R.mipmap.ic_add_marker);
        mBaiduMap = mMapView.getMap();
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

    private void initLocation() {
        //获取传感器管理服务
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mLocationClient = new LocationClient(context.getApplicationContext());
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
        mBaiduMap.setMyLocationEnabled(true);
        requestLocation(MyLocationConfiguration.LocationMode.FOLLOWING);
    }
    
    private void initView(View view) {
        mMapView = view.findViewById(R.id.mv_share_map);
        mLocationBtn = view.findViewById(R.id.share_park_location);
        mAppointmentBtn = view.findViewById(R.id.share_park_appointment);
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
     * @param locationMode 定位模式：<br>
     *                     LocationMode.NORMAL:普通 <br>
     *                     LocationMode.FOLLOWING:跟随 <br>
     *                     LocationMode.COMPASS:罗盘
     */
    private void requestLocation(MyLocationConfiguration.LocationMode locationMode) {
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(locationMode, true, null));
        }
        if (App.hasNetwork) {
            mLocationClient.start();
        } else {
            showToast("检测到网络已断开，请开启网络后重试~");
        }
    }

    //到达指定位置监听
    class MyNotifyLister extends BDNotifyListener {
        @Override
        public void onNotify(BDLocation bdLocation, float distance) {
            arrivedPark();
            if (mLocationClient != null) {
                mLocationClient.removeNotifyEvent(notifyLister);
            }
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
            if (location.getLocationWhere() == BDLocation.LOCATION_WHERE_OUT_CN) {
                ToastUtil.showToast(context, "目前EP智慧停只支持国内定位，国外车位推荐敬请期待~");
                return;
            }
            if (location.getLocationWhere() == BDLocation.LOCATION_WHERE_UNKNOW){
                ToastUtil.showToast(context, "获取定位失败，请开启定位权限后重试~");
                return;
            }
            if (location.getLocType() == BDLocation.TypeNetWorkException) {
                ToastUtil.showToast(context, "网络不通会导致定位精准度不高，请保持网络通畅~");
            }
            if (!isMapLoaded) {
                if ((System.currentTimeMillis() - loaderTime) >= 5000) {
                    ToastUtil.showToast(context, "当前网络较慢，请耐心等候...");
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
                if (!curAddress.equals(mCurAddress) && context != null) {
                    mCurAddress = curAddress;
                    showAddress(mCurAddress);
//                    curCity = location.getCity().trim(); //获取城市
                    mainAct.currentDistrict = location.getDistrict();
                }
            }
        }
    }

    /**
     * 显示附近停车信息 <br>
     *
     * @param ll 检索坐标点（内含经纬度）
     */
    private void showNearbyPark(LatLng ll) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (PoiInfo poi : result.getAllPoi()) {
//        LatLng latLng1 = new LatLng(29.35996063231551, 105.94210944010091);
//            addMarker(latLng1, true);
//            builder.include(latLng1);
//
//        LatLng latLng2 = new LatLng(29.358827379029663, 105.94228011814774);
//        addMarker(latLng2, false);
//        builder.include(latLng2);
//
//        LatLng latLng3 = new LatLng(29.359275959978568, 105.94174113484193);
//        addMarker(latLng3, false);
//        builder.include(latLng3);
//        }
//        updateParkInfo(recommendPoiInfo, false);
//        setLocationParkCount();
//        mPoiSearch.searchNearby(new PoiNearbySearchOption()
//                .location(ll)
//                .radius(locationRadius)
//                .keyword("停车场")
//                .scope(2) // 1/空: 返回基本信息 2:返回详细信息
//                .pageCapacity(MAX_PAGE_PARK_INFO_NUMBER) //每页最大数据量
//        );
    }

    private void updateParkInfo(boolean isClick){

    }

    /**
     * 添加地图标志物
     *
     * @param latLng 坐标
     * @param isRecommend 是否为推荐
     */
    private void addMarker(LatLng latLng, boolean isRecommend) {
        if (mBaiduMap != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("poiInfo", latLng);
            OverlayOptions options = new MarkerOptions()
                    .position(new LatLng(latLng.latitude,
                            latLng.longitude))
                    .icon(bitmapNormal)
                    .alpha(.9f)
                    .animateType(MarkerOptions.MarkerAnimateType.grow)  //从地面生长动画
                    .extraInfo(bundle); //设置额外信息
            //添加并显示
            if (isRecommend) {
                preMarker = (Marker) mBaiduMap.addOverlay(options);
                preMarker.setIcon(bitmapSelector);
            } else {
                mBaiduMap.addOverlay(options);
            }
        }
    }

    /**
     * 显示地位地址 格式为：xxx省xxx市xxx区/县xxx地区附近
     *
     * @param curAddress 当前地址
     */
    private void showAddress(String curAddress) {
        if (!TextUtils.isEmpty(curAddress)) {
//            tv_main_park_address.setText(curAddress);
        }
    }

    OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.share_park_location: //自身点
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
                    } else {
                        InputMethodUtils.hide(context);
                        locationRadius = Constant.SEARCH_RADIUS;
                        mCurrentLat = 0;
                        isChooseAddress = false;
                        ToastUtil.showToast(context, "定位中……");
                        requestLocation(MyLocationConfiguration.LocationMode.COMPASS);
                    }
                    break;
                case R.id.share_park_appointment://预约停
                    if (isAppointment){
                        isAppointment = false;
                        mAppointmentBtn.setBackgroundResource(R.mipmap.ic_share_park_appointment);
                    } else {
                        isAppointment = true;
                        mAppointmentBtn.setBackgroundResource(R.mipmap.ic_share_park_navigation);
                        showAlertDialog("预约有效期仅剩10分钟，是否延长到达时间？\n注：延长时间将为您保留车位，您需支付延时费用。",
                          v1 ->{
                            dismiss();
                            ToastUtil.showToast(context, "您已成功延时1小时~");
                        }, v1 ->{
                            dismiss();
                            mAppointmentBtn.setBackgroundResource(R.mipmap.ic_share_park_appointment);
                            ToastUtil.showToast(context, "您已取消当前停车点预约~");
                        });
                    }

                    break;
                default: break;
            }
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
        String parkingStartTime = ShareUtil.newInstance().getShared(context).getString("parkingStartTime", "");
        parkingSecond = StringUtil.getSeconds(parkingStartTime);
        if (!TextUtils.isEmpty(parkingStartTime)) {
            handler.postDelayed(countRunnable, 1000);
        } else {
//            ll_park_time.setVisibility(View.GONE);
        }
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
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
    public void onDestroyView() {
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
        if (mLocationClient != null) {
            mLocationClient.stop(); // 退出时销毁定位
        }
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(false);  // 关闭定位图层
        }
        if (bitmapNormal != null && bitmapSelector != null && bitmapAddMarker != null) {
            //回收 bitmap 资源
            bitmapNormal.recycle();
            bitmapSelector.recycle();
            bitmapAddMarker.recycle();
        }
        super.onDestroyView();
        handler.removeCallbacks(countRunnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
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

    //已到达目的地
    private void arrivedPark() {
        showAlertDialog(getString(R.string.modal_dialog_tip), "您已到达目的地附近，是否立即停车？\n（注：立即停车即开始计时）",
                "立即停车", v -> {
                    ShareUtil.newInstance().getShared(context).edit()
                            .putString("parkingStartTime", StringUtil.getCurrentTime())
                            .apply();
                    handler.postDelayed(countRunnable, 1000);
                    dismiss();
                }
        );
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                break;
        }
        super.handleMessage(msg);
    }

    //计时线程
    private Runnable countRunnable = new Runnable() {
        @Override
        public void run() {
            parkingSecond ++;
//            ll_park_time.setVisibility(View.VISIBLE);
//            tv_park_time.setText(StringUtil.formatTime(parkingSecond));
            handler.postDelayed(this, 1000);
        }
    };

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

}
