package xyz.gooin.mymap;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import static com.baidu.location.LocationClientOption.LOC_SENSITIVITY_HIGHT;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    // 权限请求.
    private static final int ACCESS_COARSE_LOCATION = 1;

    // 自定义定位图标
    private BitmapDescriptor mIconLocation;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    private MyOrientationListener mMyOrientationListener;
    private float mCurrentX;
    private Context context;

    //定位相关参数
    private LocationClient mLocationClient = null;
    private MyLocationListener mLocationListenter;
    private double mLatitude;
    private double mLongitude;
    private boolean isFirstIn = true;
    private static final int UPDATE_TIME = 5000;
    private static final int MINI_DISTANCE = 1;
    private static int LOCATION_COUTNS = 0;

    //private BDLocation mBDLocation;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        this.context = this;

        initMapView();
        initLocation();
        initPermission();
        initToolBar();
    }

    private void initPermission() {

    }


    private void initLocation() {
        // 定位sdk
        mLocationClient = new LocationClient(this);
        mLocationListenter = new MyLocationListener();
        // 注册监听器
        mLocationClient.registerLocationListener(mLocationListenter);

        // 设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); //开启GPS
        option.setCoorType("bd09ll"); //坐标系统为百度坐标
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //定位精度为高精度 GPS+网络
        option.setOpenAutoNotifyMode
                (UPDATE_TIME, MINI_DISTANCE, LOC_SENSITIVITY_HIGHT); // 最短定位时间间隔, 最短定位距离间隔,定位变化敏感度

        mLocationClient.setLocOption(option);
        // 初始化图标
        mIconLocation = BitmapDescriptorFactory.fromResource(R.mipmap.ic_huaji);

        mMyOrientationListener = new MyOrientationListener(context);
        mMyOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
                // Log.d(TAG, "onSensorChanged: value is " + mCurrentX );
            }
        });

    }

    private void initMapView() {

        // 实例化地图
        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.showZoomControls(false);
        // 开启定位
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(18.0f);
        mBaiduMap.animateMapStatus(update);


    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            MyLocationData data = new MyLocationData.Builder()
                    .direction(mCurrentX)
                    .accuracy(bdLocation.getRadius())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(data);

            // 设置自定义图标
            MyLocationConfiguration config = new
                    MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.COMPASS, true, mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);


            // 更新经纬度
            mLatitude = bdLocation.getLatitude();
            mLongitude = bdLocation.getLongitude();

//            if (isFirstIn) {
            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
//                Log.d(TAG, "lat: " + bdLocation.getLatitude() + " lon: " +bdLocation.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(update);
//                isFirstIn = false;
//            }

        }
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 移动到我的位置
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(latLng, 19.5f);
                mBaiduMap.animateMapStatus(update);


//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Log.d(TAG, "onCreate: end");

        // 开启定位
        if (mBaiduMap.isMyLocationEnabled()) {
            mLocationClient.start();
        }
        // 开启方向传感器
        mMyOrientationListener.start();
        Log.d(TAG, "initToolBar: 定位传感器创建");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        Log.d(TAG, "onDestroy: 1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        // 开启定位
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.start();

        // 开启方向传感器
        mMyOrientationListener.start();
        Log.d(TAG, "onStart: ");
        Log.d(TAG, "onResume: ");

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 开启定位
        if (mBaiduMap.isMyLocationEnabled()) {
            mLocationClient.start();
        }
        // 开启方向传感器
        mMyOrientationListener.start();
        Log.d(TAG, "onStart: ");

    }

    @Override
    protected void onStop() {
        super.onStop();
        // 停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();

        // 关闭方向传感器
        mMyOrientationListener.stop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.map_common) {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            Log.d(TAG, "onOptionsItemSelected: 普通视图");
            return true;
        }
        if (id == R.id.map_satellite) {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            Log.d(TAG, "onOptionsItemSelected: 卫星视图");
            return true;
        }
        if (id == R.id.map_traffic) {
            if (mBaiduMap.isTrafficEnabled()) {
                mBaiduMap.setTrafficEnabled(false);
                Log.d(TAG, "onOptionsItemSelected: 实时交通关闭");
                item.setTitle(R.string.map_traffic_on);
            } else {
                mBaiduMap.setTrafficEnabled(true);
                Log.d(TAG, "onOptionsItemSelected: 实时交通开启");
                item.setTitle(R.string.map_traffic_off);
            }

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
