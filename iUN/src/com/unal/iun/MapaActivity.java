package com.unal.iun;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData.Item;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.d;
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
import com.google.android.gms.maps.model.internal.f;
import com.unal.iun.LN.LinnaeusDatabase;
import com.unal.iun.LN.MiLocationListener;
import com.unal.iun.LN.Util;

public class MapaActivity extends FragmentActivity {
	GoogleMap mapa;
	double lat[], lon[];
	ArrayList<LatLng> marcadores = new ArrayList<LatLng>();
	String titulos[];
	String descripciones[];
	static int tipo = 1, count = 0, zoom = 19;
	boolean traffic = true;
	String urlRutas = "http://maps.googleapis.com/maps/api/directions/json?origin=4.6382023,-74.0840434&destination=6.26261,-75.57775&sensor=true";
	String urlClima = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=4.53&lon=-74.07&units=metric&mode=JSON&cnt=7";
	Intent deta;
	Marker focus;
	String tableName = "BaseM";
	int nivel = 1;
	MenuItem item;
	static String cond = "";

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
		nivel = b.getInt("nivel");
		cambiar();
		animarCamara(lat[0], lon[0], zoom);
		addNuevos(false);
		tableName = MainActivity.tbName;
		try {
			MapsInitializer.initialize(MapaActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void animarCamara(double d, double e, int zoom2) {
		LatLng position = new LatLng(d, e);
		CameraPosition camPos = new CameraPosition.Builder().target(position)
				.zoom(zoom2) // Establecemos el zoom en 19
				.bearing(0) // Establecemos la orientación con el noreste arriba
				.tilt(0) // Bajamos el punto de vista de la cámara 70 grados
				.build();
		CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);

		mapa.animateCamera(camUpd3);

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
	}

	private void mostrarMarcador(double lat, double lng, String title,
			String desc, int tipo) {
		/*
		 * float a = 0; switch (count) { case 0: a =
		 * BitmapDescriptorFactory.HUE_CYAN; break; case 1: a =
		 * BitmapDescriptorFactory.HUE_ORANGE; break; case 2: a =
		 * BitmapDescriptorFactory.HUE_VIOLET; break; case 3: a =
		 * BitmapDescriptorFactory.HUE_YELLOW; break; case 4: a =
		 * BitmapDescriptorFactory.HUE_ORANGE; break;
		 * 
		 * default: break; }
		 */
		if (!marcadores.contains(new LatLng(lat, lng))) {
			marcadores.add(new LatLng(lat, lng));
			MarkerOptions k = null;
			if (tipo == 0) {
				k = new MarkerOptions()
						.position(new LatLng(lat, lng))
						.title(title)
						.snippet(desc)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.edificiop));

			} else {
				k = new MarkerOptions()
						.position(new LatLng(lat, lng))
						.title(title)
						.snippet(desc)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.edificiop));
			}
			mapa.addMarker(k);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mapa, menu);
		item = menu.getItem(0);
		item.setTitle("Cobertura Nacional");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
		case R.id.menu_otros_sitios:
			try {
				acercar();
			} catch (Exception e) {
				// TODO: handle exception
			}
			return true;
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

	public void acercar(Marker arg0) {
		LinnaeusDatabase lb = new LinnaeusDatabase(getApplicationContext());
		SQLiteDatabase db = openOrCreateDatabase("DataStore.sqlite",
				MODE_WORLD_READABLE, null);
		double lat = arg0.getPosition().latitude;
		double lon = arg0.getPosition().longitude;
		String query = "select sede_edificio from edificios where latitud between "
				+ (lat - 0.0001)
				+ " and "
				+ (lat + 0.0001)
				+ " and longitud between"
				+ (lon - 0.0001)
				+ " and "
				+ (lon + 0.0001);
		Cursor c = db.rawQuery(query, null);
		String[][] mat = Util.imprimirLista(c);
		cond = Util.getcolumn(mat, 0)[0].trim();
		Log.e("sede", cond);
		nivel++;
		String query2 = "select latitud, longitud,nombre_edificio, url from edificios "
				+ "natural join "
				+ tableName
				+ " natural join enlace where sede_edificio='"
				+ cond
				+ "' and nivel =" + nivel + " group by nombre_edificio";
		c = db.rawQuery(query2, null);
		mat = Util.imprimirLista(c);
		this.lat = Util.toDouble(Util.getcolumn(mat, 0));
		this.lon = Util.toDouble(Util.getcolumn(mat, 1));
		this.titulos = Util.getcolumn(mat, 2);
		this.descripciones = Util.getcolumn(mat, 3);
		addNuevos(false);
		c.close();
		db.close();
	}

	public void acercar() {
		LinnaeusDatabase lb = new LinnaeusDatabase(getApplicationContext());
		SQLiteDatabase db = openOrCreateDatabase("DataStore.sqlite",
				MODE_WORLD_READABLE, null);
		double lat = focus.getPosition().latitude;
		double lon = focus.getPosition().longitude;
		String query = "select latitud, longitud,nombre_edificio, edificio  from edificios where latitud between "
				+ (lat - 0.001)
				+ " and "
				+ (lat + 0.001)
				+ " and longitud between"
				+ (lon - 0.001)
				+ " and "
				+ (lon + 0.001);// + " and nivel=4";
		Cursor c = db.rawQuery(query, null);
		String[][] mat = Util.imprimirLista(c);
		this.lat = Util.toDouble(Util.getcolumn(mat, 0));
		this.lon = Util.toDouble(Util.getcolumn(mat, 1));
		this.titulos = Util.getcolumn(mat, 2);
		this.descripciones = Util.getcolumn(mat, 3);
		addNuevos(true);
		c.close();
		db.close();
	}

	public void addNuevos(final boolean b) {
		if (nivel < 3&&!b) {
			mapa.clear();
			marcadores.removeAll(marcadores);
		}
		int type = 0;
		if (b) {
			type = 1;
		}
		count++;
		count = count % 5;
		for (int i = 0; i < lat.length; i++) {
			mostrarMarcador(lat[i], lon[i], titulos[i], descripciones[i], type);
		}
		mapa.setMyLocationEnabled(true);
		mapa.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(final Marker arg0) {

				final String[] items = { "¿Cómo llegar?", "Información" };
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MapaActivity.this);

				builder.setTitle("Selección").setItems(items,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								if (item == 0) {
									ruta(arg0.getPosition().latitude,
											arg0.getPosition().longitude);
								} else {
									if (tipo == 0) {
										tipo = 1;
									}
									switch (nivel) {
									case 1:
										zoom = 12;
										break;
									case 2:
										zoom = 17;
										break;
									case 3:
										zoom = 19;
										break;

									default:
										break;
									}
									animarCamara(arg0.getPosition().latitude,
											arg0.getPosition().longitude, zoom);
									if (nivel != 3 && nivel < 3) {
										acercar(arg0);
									} else {
										String cad = arg0.getSnippet();
										if (cad.contains("www.")) {
											cambiar();
											deta.putExtra("paginaWeb", cad);
											startActivity(deta);
										}
									}
								}
							}
						});

				builder.setNegativeButton("Cancelar", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});
				builder.show();

			}
		});

		mapa.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(Marker marker) {
				focus = marker;
				item.setTitle(marker.getTitle());
				return false;
			}
		});

	}

}
