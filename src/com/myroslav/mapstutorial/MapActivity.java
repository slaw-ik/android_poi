package com.myroslav.mapstutorial;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity {

	Context context = this;
	private GoogleMap googlemap;
	MarkerDataSource data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initMap();

		// addTwitterToMap();
		checkForGPSActive();
		displayMarkersFromDB();

		googlemap
				.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

					@Override
					public void onInfoWindowClick(Marker marker) {
						showFullDescription(marker);
						updateMarkersRating(marker, 2);
					}
				});
		googlemap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				updateMarkersRating(marker, 1);
				return false;
			}
		});

	}

	protected void showFullDescription(Marker marker) {
		data = new MarkerDataSource(context);
		data.open();

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		String position = marker.getTitle();
		String full_desc = data.getMarkerByPositon(position).getFullDesc();

		builder.setTitle("Info");
		builder.setMessage(full_desc);
		builder.setCancelable(false);
//		builder.setPositiveButton("Show more Info",
//				new DialogInterface.OnClickListener() {

//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//
//					}
//				});
		builder.setNegativeButton("Close",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
		data.close();
	}
	
	private void updateMarkersRating(Marker marker, int i) {
		data = new MarkerDataSource(context);
		data.open();		
		String position = marker.getTitle();
		MyMarkerObj my_marker = data.getMarkerByPositon(position);
		data.increaseRating(my_marker, i);
		data.close();		
	}

	private void checkForGPSActive() {
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		String provider = lm.getBestProvider(new Criteria(), true);
		List<String> priversList = lm.getProviders(new Criteria(), true);

		if (!priversList.contains("gps")) {
			locationListener.onProviderDisabled(provider);
		}
		;
	}

	private void displayMarkersFromDB() {
		data = new MarkerDataSource(context);
		data.open();

		// data.addMarker(new MyMarkerObj("ED office", "Essential Data s.r.o.",
		// "48.147459 17.117777"));
		// data.addMarker(new MyMarkerObj("Runa", "Polonyna Runa",
		// "48.804376 22.812252"));
		// data.addMarker(new MyMarkerObj("Robinzon", "Transcarpathia",
		// "48.760333 23.361685"));

		List<MyMarkerObj> m = data.getMyMarkers();

		for (int i = 0; i < m.size(); i++) {
			String[] slatlng = m.get(i).getPosition().split(" ");
			LatLng lat = new LatLng(Double.valueOf(slatlng[0]),
					Double.valueOf(slatlng[1]));
			googlemap.addMarker(new MarkerOptions()
					.title(m.get(i).getPosition())
					.snippet("Rating: " + m.get(i).getRating() + " " + m.get(i).getSnippet()).position(lat));
		}
		data.close();

		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(
				new LatLngBounds(new LatLng(47.96, 22.16), new LatLng(49.03,
						23.90)), 300, 300, 10);
		googlemap.animateCamera(cameraUpdate);
	}

	private void initMap() {

		SupportMapFragment mf = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		googlemap = mf.getMap();

		googlemap.setMyLocationEnabled(true);
		googlemap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

	}

	private void addTwitterToMap() {
		LatLng pos = new LatLng(37.776994, -122.4169725);

		googlemap.addMarker(new MarkerOptions()
				.title("Twitter")
				.snippet("Twitetr Inc")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
				.position(pos));
	}

	LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location latlong) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderDisabled(String provider) {

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("GPS is disabled");
			builder.setCancelable(false);
			builder.setPositiveButton("Enable GPS",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent startGps = new Intent(
									android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(startGps);

						}
					});
			builder.setNegativeButton("Leave GPS off",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

			AlertDialog alert = builder.create();
			alert.show();
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}

	};
}
