package com.unal.iun;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaActivity extends FragmentActivity {
	GoogleMap mapa;
	double lat[], lon[];
	String titulos[];
	String descripciones[];
	int tipo = 1, count = 0, zoom = 19;
	boolean traffic = true;
	String urlRutas = "http://maps.googleapis.com/maps/api/directions/json?origin=4.6382023,-74.0840434&destination=6.26261,-75.57775&sensor=true";
	String urlClima = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=4.53&lon=-74.07&units=metric&mode=JSON&cnt=7";
	Intent deta;
	Marker focus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa);
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		if (status == ConnectionResult.SUCCESS) {
			mapa = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
		} else {
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();
		}
		deta = new Intent(this, WebActivity.class);
		BitmapDrawable background2 = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.fondoinf));
		this.getActionBar().setBackgroundDrawable(background2);
		Bundle b = getIntent().getExtras();
		lat = b.getDoubleArray("lat");
		lon = b.getDoubleArray("lon");
		titulos = b.getStringArray("titulos");
		descripciones = b.getStringArray("descripciones");
		zoom = b.getInt("zoom");
		tipo = b.getInt("tipo");
		cambiar();
		LatLng position = new LatLng(lat[0], lon[0]);
		CameraPosition camPos = new CameraPosition.Builder().target(position)
				.zoom(zoom) // Establecemos el zoom en 19
				.bearing(0) // Establecemos la orientación con el noreste arriba
				.tilt(0) // Bajamos el punto de vista de la cámara 70 grados
				.build();
		CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);

		mapa.animateCamera(camUpd3);
		for (int i = 0; i < lat.length; i++) {
			mostrarMarcador(lat[i], lon[i], titulos[i], descripciones[i]);
		}
		mapa.setMyLocationEnabled(true);
		mapa.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(final Marker arg0) {
				final String[] items = { "Información", "Cómo llegar" };
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MapaActivity.this);

				builder.setTitle("Selección").setItems(items,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								if (item == 1) {
									ruta(arg0.getPosition().latitude,
											arg0.getPosition().longitude);
								} else {
									if (tipo == 0) {
										tipo = 1;
									}
									cambiar();
									CameraPosition camPos = new CameraPosition.Builder()
											.target(arg0.getPosition())
											.zoom(19) // Establecemos el zoom en
														// 19
											.bearing(0) // Establecemos la
														// orientación con el
														// noreste arriba
											.tilt(0) // Bajamos el punto de
														// vista de la cámara 70
														// grados
											.build();
									CameraUpdate camUpd3 = CameraUpdateFactory
											.newCameraPosition(camPos);
									mapa.animateCamera(camUpd3);
									String cad = "http://www.unal.edu.co";
									deta.putExtra("paginaWeb", cad);
									startActivity(deta);

								}
							}
						});
				builder.setNegativeButton("cancelar", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});
				builder.show();

				// mapa.clear();
			}
		});

		mapa.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(Marker marker) {
				focus=marker;
				return false;
			}
		});

		try {
			MapsInitializer.initialize(MapaActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ruta(double lat, double lon) {
		Intent deta = new Intent(this, WebActivity.class);
		double lat2 = 4.637275;// (float) MiLocationListener.lat;
		double lon2 = -74.082776; // (float) MiLocationListener.longi;
		if (lat2 != 0 && lon2 != 0) {
			lat2 = (double) MiLocationListener.lat;
			lon2 = (double) MiLocationListener.longi;
		}
		deta.putExtra("paginaWeb", "https://www.google.es/maps/dir/'" + lat2
				+ "," + lon2 + "'/'" + lat + "," + lon + "'");
		startActivity(deta);
		count++;
		count = count % 5;
	}

	private void mostrarMarcador(double lat, double lng, String title,
			String desc) {
		float a = 0;
		switch (count) {
		case 0:
			a = BitmapDescriptorFactory.HUE_BLUE;
			break;
		case 1:
			a = BitmapDescriptorFactory.HUE_ORANGE;
			break;
		case 2:
			a = BitmapDescriptorFactory.HUE_VIOLET;
			break;
		case 3:
			a = BitmapDescriptorFactory.HUE_YELLOW;
			break;
		case 4:
			a = BitmapDescriptorFactory.HUE_ORANGE;
			break;

		default:
			break;
		}
		mapa.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
				.title(title).snippet(desc)
				.icon(BitmapDescriptorFactory.defaultMarker(a)).alpha(0.7f));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mapa, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_tipo_mapa:
			cambiar();
			return true;
		case R.id.menu_trafico:
			mapa.setTrafficEnabled(traffic);
			if (traffic) {
				traffic = false;
			} else {
				traffic = true;
			}
			return true;
			/*
			 * case R.id.ItemRefresh: recargar(); return true; case
			 * R.id.ItemForward: adelante(); return true;
			 */
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	private void cambiar() {
		if (tipo == 0) {
			mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			tipo++;
		} else {
			mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			tipo = 0;
		}
	}

}
