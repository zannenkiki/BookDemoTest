package com.zq.callouttest;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private Callout mCallout;
    Point mapPoint,wgs84Point;
    Callout.Style calloutStyle;
    View calloutContent;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 把layout填充到MapView
        mMapView = (MapView) findViewById(R.id.mapView);
        // 创建一个是OPEN_STREET_MAP类型底图的地图，纬度，经度，缩放等级
        final ArcGISMap mMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, 36.000410, 120.116501, 15);
        // 设置地图显示到视图中set the map to be displayed in this view
        mMapView.setMap(mMap);


        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        // 把overlay添加到地图视图
        mMapView.getGraphicsOverlays().add(graphicsOverlay);
        // 定义一个点的Geometry，参数为 经度 纬度 空间参考
        final Point point1 = new Point(120.116501, 36.000410,SpatialReferences.getWgs84());
        SimpleMarkerSymbol pointStyle1 = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,Color.RED, 10);
        Graphic pointGraphic1 = new Graphic(point1, pointStyle1);
        // 把graphic添加到图形覆盖物
        graphicsOverlay.getGraphics().add(pointGraphic1);
        // 定义一个布局填充器
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        // 将布局填充callout
        calloutContent = layoutInflater.inflate(R.layout.calloutdisplay,null);
        // 设置callout的style
        calloutStyle = new Callout.Style(this,R.xml.calloutstyle);

        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this,mMapView){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                // 获取点击的点，把它转化为地图坐标系的点
                android.graphics.Point screenPoint = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
                // 从屏幕点创建一个地图点
                mapPoint = mMapView.screenToLocation(screenPoint);
                // 转化为WGS84经纬度格式的
                wgs84Point = (Point)GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());
                // 设置容差
                int tolerance = 10;
                double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
                // 创建一个需要做查询选取的对象
                Point mapShowScreen = (Point) GeometryEngine.project(point1,mapPoint.getSpatialReference());
                // 获取callout, 设置内容并显示
                mCallout = mMapView.getCallout();
                // 取消显示所有callout
                mCallout.dismiss();
                // 判断点击的点是否在显示的点容差范围内
                if (mapPoint.equals(mapShowScreen,mapTolerance)){
                    // 设置位置
                    mCallout.setLocation(mapPoint);
                    // 设置样式
                    mCallout.setStyle(calloutStyle);
                    // 设置内容
                    mCallout.setContent(calloutContent);
                    // 以点击的点居中
                    mMapView.setViewpointCenterAsync(mapPoint);
                    mCallout.show();
                }

                return true;
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }

}
