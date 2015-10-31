package com.dee.sean.baidumapdemo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private Context context;

    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private boolean isFirstIn = true;

    private double mLatitude;
    private double mLongtitude;

    //自定义定位图标
    private BitmapDescriptor mIconLocation;
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;

    private MyLocationConfiguration.LocationMode mLocationMode;


    //覆盖物相关
    private BitmapDescriptor mMarker;
    private RelativeLayout mMarkerLy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        this.context = this;

        initView();

        initLocation();

        initMarker();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle extraInfo = marker.getExtraInfo();
                Info info = (Info) extraInfo.getSerializable("info");
                ImageView iv = (ImageView) mMarkerLy.findViewById(R.id.id_info_img);
                TextView distance = (TextView) mMarkerLy.findViewById(R.id.id_info_distance);
                TextView name = (TextView) mMarkerLy.findViewById(R.id.id_info_name);
                TextView zan = (TextView) mMarkerLy.findViewById(R.id.id_info_zan);
                iv.setImageResource(info.getImgId());
                distance.setText(info.getDistance());
                name.setText(info.getName());
                zan.setText(info.getZan() + "");

                //点击覆盖物显示文本
                InfoWindow infoWindow;
                TextView tv = new TextView(context);
                tv.setBackgroundResource(R.drawable.location_tips);
                tv.setPadding(30, 20, 30, 50);
                tv.setText(info.getName());
                tv.setTextColor(Color.parseColor("#ffffff"));

                final LatLng latLng = marker.getPosition();
                Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
                p.y -= 47;
                LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p);

                //为弹出的InfoWindow添加点击事件
                //
                //
                infoWindow = new InfoWindow(tv, ll, 0);


                mBaiduMap.showInfoWindow(infoWindow);
                mMarkerLy.setVisibility(View.VISIBLE);
                return true;

            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                mMarkerLy.setVisibility(View.GONE);
                mBaiduMap.hideInfoWindow();
            }
        });
    }

    private void initMarker() {
        mMarker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
        mMarkerLy = (RelativeLayout) findViewById(R.id.id_maker_ly);


    }

    private void initLocation() {
        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(3000);

        mLocationClient.setLocOption(option);

        //初始化图标
        mIconLocation = BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked);

        myOrientationListener = new MyOrientationListener(context);

        myOrientationListener.setmOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationCHanged(float x) {
                mCurrentX = x;
            }
        });
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.id_bmapView);
        mBaiduMap = mMapView.getMap();

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.id_map_common:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.id_map_site:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.id_map_traffic:
                if (mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(false);
                    item.setTitle("实时交通(On)");
                } else {
                    mBaiduMap.setTrafficEnabled(true);
                    item.setTitle("实时交通(Off)");
                }
                break;
            case R.id.id_map_location:
                centerToMyLocation();
                break;
            case R.id.id_map_mode_common:
                mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
                break;
            case R.id.id_map_mode_following:
                mLocationMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.id_map_mode_compass:
                mLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
                break;
            case R.id.id_add_overlay:
                addOverlays(Info.infos);
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 添加覆盖物
     *
     * @param infos
     */
    private void addOverlays(List<Info> infos) {
        mBaiduMap.clear();
        LatLng latLng = null;
        Marker marker = null;
        OverlayOptions options;
        for (Info info : infos) {
            //经纬度
            latLng = new LatLng(info.getLatitude(), info.getLongtitude());
            //图标
            options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
        }

        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);
    }

    /**
     * 定位到我的位置
     */
    private void centerToMyLocation() {
        LatLng latLng = new LatLng(mLatitude, mLongtitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        //开启方向传感器
        myOrientationListener.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        //停止方向传感器
        myOrientationListener.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            MyLocationData data = new MyLocationData.Builder()
                    .direction(mCurrentX)
                    .accuracy(bdLocation.getRadius())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();

            mBaiduMap.setMyLocationData(data);

            //设置自定义图标
            MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode, true, mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);

            //更新经纬度
            mLatitude = bdLocation.getLatitude();
            mLongtitude = bdLocation.getLongitude();

            if (isFirstIn) {

                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);

                isFirstIn = false;
            }

            Toast.makeText(context, bdLocation.getAddrStr(), Toast.LENGTH_SHORT).show();
        }
    }
}
