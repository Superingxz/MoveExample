package com.amap.api.example;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

public class MainActivity extends Activity {

	private MapView mMapView;
	private AMap mAmap;
	private Polyline mVirtureRoad;
	private Marker mMoveMarker;

	// 通过设置间隔时间和距离可以控制速度和图标移动的距离
	private static final int TIME_INTERVAL = 80;
	private static final double DISTANCE = 0.0001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mMapView = (MapView) findViewById(R.id.map);

		mMapView.onCreate(savedInstanceState);
		mAmap = mMapView.getMap();
		initRoadData();
		moveLooper();
	}

	private void initRoadData() {
		// 116.504505 39.931057
		double centerLatitude = 39.916049;
		double centerLontitude = 116.399792;
		double deltaAngle = Math.PI / 180 * 5;
		double radius = 0.02;
		PolylineOptions polylineOptions = new PolylineOptions();
		for (double i = 0; i < Math.PI * 2; i = i + deltaAngle) {
			float latitude = (float) (-Math.cos(i) * radius + centerLatitude);
			float longtitude = (float) (Math.sin(i) * radius + centerLontitude);
			polylineOptions.add(new LatLng(latitude, longtitude));
			if (i > Math.PI) {
				deltaAngle = Math.PI / 180 * 30;
			}
		}
		float latitude = (float) (-Math.cos(0) * radius + centerLatitude);
		float longtitude = (float) (Math.sin(0) * radius + centerLontitude);
		polylineOptions.add(new LatLng(latitude, longtitude));
	
		// polylineOptions.add(new LatLng(39.954368, 116.478038));
		// polylineOptions.add(new LatLng(39.92515, 116.510997));
		// polylineOptions.add(new LatLng(39.892198, 116.439449));
		// polylineOptions.add(new LatLng(39.919324, 116.379367));
		// polylineOptions.add(new LatLng(39.951704, 116.356708));
		//
		//
		//
		//
		// polylineOptions.add(new LatLng(39.970914, 116.408206));
		//
		// polylineOptions.add(new LatLng(40.005899, 116.467601));
		// polylineOptions.add(new LatLng(39.954368, 116.478038));
		polylineOptions.width(10);
		polylineOptions.color(Color.RED);
		mVirtureRoad = mAmap.addPolyline(polylineOptions);
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.setFlat(true);
		markerOptions.anchor(0.5f, 0.5f);
		markerOptions.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.marker));
		markerOptions.position(polylineOptions.getPoints().get(0));
		mMoveMarker = mAmap.addMarker(markerOptions);
		mMoveMarker.setRotateAngle((float) MoveUtils.getAngle(0,mVirtureRoad));
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}
	
	/**
	 * 循环进行移动逻辑
	 */
	public void moveLooper() {
		new Thread() {
			public void run() {
				while (true) {
					for (int i = 0; i < mVirtureRoad.getPoints().size() - 1; i++) {
						LatLng startPoint = mVirtureRoad.getPoints().get(i);
						LatLng endPoint = mVirtureRoad.getPoints().get(i + 1);
						MoveUtils.move(mMoveMarker,startPoint, endPoint);
					}
				}
			}

		}.start();
	}	
}
