package com.amap.api.example;

import java.util.List;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;

public class MoveUtils {
	// ͨ�����ü��ʱ��;�����Կ����ٶȺ�ͼ���ƶ��ľ���
		private static final int TIME_INTERVAL = 80;
		private static final double DISTANCE = 0.0001;
	/**
	 * ���ݵ��ȡͼ��ת�ĽǶ�
	 */
	public static double getAngle(int startIndex,Polyline mVirtureRoad) {
		if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
			throw new RuntimeException("index out of bonds");
		}
		LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
		LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
		return getAngle(startPoint, endPoint);
	}

	/**
	 * ����������ȡͼ��ת�ĽǶ�
	 */
	public static  double getAngle(LatLng fromPoint, LatLng toPoint) {
		double slope = getSlope(fromPoint, toPoint);
		if (slope == Double.MAX_VALUE) {
			if (toPoint.latitude > fromPoint.latitude) {
				return 0;
			} else {
				return 180;
			}
		}
		float deltAngle = 0;
		if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
			deltAngle = 180;
		}
		double radio = Math.atan(slope);
		double angle = 180 * (radio / Math.PI) + deltAngle - 90;
		return angle;
	}

	/**
	 * ���ݵ��б����ȡ�ؾ�
	 */
	public static  double getInterception(double slope, LatLng point) {

		double interception = point.latitude - slope * point.longitude;
		return interception;
	}

	/**
	 * ��ȡб��
	 */
	public static  double getSlope(int startIndex,Polyline mVirtureRoad) {
		if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
			throw new RuntimeException("index out of bonds");
		}
		LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
		LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
		return getSlope(startPoint, endPoint);
	}

	/**
	 * ��б��
	 */
	public static  double getSlope(LatLng fromPoint, LatLng toPoint) {
		if (toPoint.longitude == fromPoint.longitude) {
			return Double.MAX_VALUE;
		}
		double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
		return slope;

	}
	
	/**
	 * ����ÿ���ƶ��ľ���
	 */
	public static double getMoveDistance(double slope) {
		if (slope == Double.MAX_VALUE||slope==0) {
			return DISTANCE;
		}
		return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
	}

	/**
	 * �ж��Ƿ�Ϊ����
	 * */
	public static boolean isReverse(LatLng startPoint,LatLng endPoint,double slope){
		if(slope==0){
		return	startPoint.longitude>endPoint.longitude;
		}
		return (startPoint.latitude > endPoint.latitude);
		 
	}

	/**
	 * ��ȡѭ����ʼֵ��С
	 * */
	public static double getStart(LatLng startPoint,double slope){
		if(slope==0){
			return	startPoint.longitude;
			}
			return  startPoint.latitude;
	}
	
	/**
	 * ��ȡѭ��������С
	 * */
	public static double getEnd(LatLng endPoint,double slope){
		if(slope==0){
			return	endPoint.longitude;
			}
			return  endPoint.latitude;
	}
	 
	
	/**
	 * �ƶ�
	 * @param mMoveMarker  ������
	 * @param startPoint   ��ʼλ��
	 * @param endPoint	        ����λ��
	 */
	public static void move(Marker mMoveMarker,LatLng startPoint,LatLng endPoint){
		mMoveMarker
		.setPosition(startPoint);
		mMoveMarker.setRotateAngle((float) getAngle(startPoint,
				endPoint));
		double slope = getSlope(startPoint, endPoint);
		boolean isReverse =isReverse(startPoint, endPoint, slope);	
		double moveDistance = isReverse ? getMoveDistance(slope) : -1 * getMoveDistance(slope);
		double intercept = getInterception(slope, startPoint);
		for(double j=getStart(startPoint, slope); (j > getEnd(endPoint, slope))==isReverse;j = j
				- moveDistance){
			LatLng latLng = null;
			if(slope==0){
				latLng = new LatLng(startPoint.latitude, j);
			}
			else if (slope == Double.MAX_VALUE) {
				latLng = new LatLng(j, startPoint.longitude);
			}
			else {			
				latLng = new LatLng(j, (j - intercept) / slope);
			}
			mMoveMarker.setPosition(latLng);
			try {
				Thread.sleep(TIME_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * �ı�ÿ��Ա����ǰλ��
	 * @param aMap  ��ͼ
	 * @param data	����Ա��
	 */
	public static void staffMove(AMap aMap,List<StaffLocationBean> data){
		aMap.clear();//���������
		if (data != null && data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				StaffLocationBean staffLocationBean = data.get(i);
				Marker mMoveMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
						.draggable(true));
				List<StaffPointBean> staffPointBeans = staffLocationBean.getuMaplist();
				if (staffPointBeans != null && staffPointBeans.size() > 1) {					
					LatLng startPoint = new LatLng(staffPointBeans.get(staffPointBeans.size() -2).getLatItude(), staffPointBeans.get(staffPointBeans.size() -2).getLongItude());
					LatLng endPoint = new LatLng(staffPointBeans.get(staffPointBeans.size() -1).getLatItude(), staffPointBeans.get(staffPointBeans.size() -1).getLongItude());					
					//move(mMoveMarker, startPoint, endPoint);
					mMoveMarker.setPosition(endPoint);
					modifyMarker(mMoveMarker, staffLocationBean, staffPointBeans.size() - 1);
				}
			}
		}	
	}
	
	/**
	 * �޸ĸ���������
	 * @param nowMaker       ������
	 * @param staffPointBean ��ǰԱ��
	 * @param position 		   Ա����ǰ��λ��
	 */
	public static void modifyMarker(Marker nowMaker,StaffLocationBean staffLocationBean,int position){
		List<StaffPointBean> staffPointBeans = staffLocationBean.getuMaplist();
		String title = "";
		String desc = staffLocationBean.getUserName() + "��"
				+ staffPointBeans.get(position).getMonitorDate();				
		if (!KingTellerJudgeUtils.isEmpty(staffPointBeans.get(position).getOrderNo()) && !staffPointBeans.get(position).getOrderNo().equals("orderNo")) {
			desc += "����ά������" + ":" + staffPointBeans.get(position).getJqbh() + "\nά��������:"
					+ staffPointBeans.get(position).getOrderNo();
		}
		desc += "\n��ַ�����ڻ�ȡ��...";
		nowMaker.setAnchor(0.5f, 0.5f);
		nowMaker.setTitle(title);
		nowMaker.setSnippet(desc);
	}
}
