package com.unal.iun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static String dataBaseName = "DataStore.sqlite";
	public Timer tim;
	public static String sede = "Bogotá";
	public boolean pausado = false;

	@Override
	public void onBackPressed() {
		this.finish();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addShortcut();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		if (savedInstanceState != null) {
			sede = savedInstanceState.getString("sede");
		}
		Space sp = (Space) findViewById(R.id.SpaceMain);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		double factor = screenHeight / 2000.0 + 0.30;
		if (factor > 0.75) {
			factor = 0.75;
		}

		sp.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor))));
		/*
		 * tim = new Timer((Button) findViewById(R.id.nacionalButton), 30);
		 * Handler hand = tim.puente; Thread hilo = new Thread(tim);
		 * hilo.start();
		 */
		Typeface fuente = Typeface
				.createFromAsset(getAssets(), "Helvetica.ttf");
		int ids[] = { R.id.SOnlineButton, R.id.eventosButton, R.id.sedesButton,
				R.id.textLatitud, R.id.textLongitud, R.id.textLugar };
		for (int i = 0; i < ids.length; i++) {
			TextView prueba = (TextView) findViewById(ids[i]);
			prueba.setTypeface(fuente);
			if (ids[i] == R.id.textSede) {
				prueba.setText(sede);
			}
		}
		iniciarLocalService();
		if (!Util.isOnline(this)) {
			Toast.makeText(getApplicationContext(),
					"No estas conectado a internet", 1).show();
			// notificarRed();
		}

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		sede = savedInstanceState.getString("sede");
		TextView tx = (TextView) findViewById(R.id.textSede);
		tx.setText(sede);
		super.onRestoreInstanceState(savedInstanceState);
	}

	public void notificarRed() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				getApplicationContext());
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		dialog.setView(inflater.inflate(R.layout.dialogo_mapa, null))
				.setPositiveButton("Aceptar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		dialog.show();
	}

	public void eventos(View v) {
		startActivity(new Intent(getApplicationContext(), EventosActivity.class));
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	private void iniciarLocalService() {
		LocationManager milocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener milocListener = new MiLocationListener();
		MiLocationListener.appcont = this.getApplicationContext();
		MiLocationListener.textLat = (TextView) findViewById(R.id.textLatitud);
		MiLocationListener.textLon = (TextView) findViewById(R.id.textLongitud);
		MiLocationListener.textSede = (TextView) findViewById(R.id.textSede);
		LinnaeusDatabase lb = new LinnaeusDatabase(getApplicationContext());
		MiLocationListener.db = openOrCreateDatabase("DataStore.sqlite",
				MODE_WORLD_READABLE, null);
		try {

			milocManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, milocListener);
		} catch (Exception e) {

		} finally {
			milocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					0, 0, milocListener);
		}

	}

	@Override
	protected void onPause() {
		pausado = true;
		super.onPause();
	}

	@Override
	protected void onResume() {
		pausado = false;
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("sede", sede);
		super.onSaveInstanceState(outState);
	}

	public void directorio(View v) {
		directorio(false);
	}

	public void irSede(View v) {
		directorio(true);
	}

	private void directorio(boolean cond) {
		try {
			Intent directorio = new Intent(getApplicationContext(),
					DirectorioActivity.class);
			if (cond) {
				directorio.putExtra("current", 3);
				directorio.putExtra("sede", sede);
			}
			directorio.putExtra("salto", cond);
			startActivity(directorio);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			// this.finish();
		} catch (Exception e) {
			Log.e("error", e.toString());
		}

	}

	public void servicios(View v) {
		try {
			startActivity(new Intent(getApplicationContext(),
					MenuWEBActivity.class));
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			// this.finish();
		} catch (Exception e) {
			Log.e("error", e.toString());
		}
	}

	private void addShortcut() {
		// Creamos el Intent y apuntamos a nuestra classe principal
		// al hacer click al acceso directo
		// En este caso de ejemplo se llama "Principal"
		Intent shortcutIntent = new Intent(getApplicationContext(),
				MainActivity.class);
		// Añadimos accion
		shortcutIntent.setAction(Intent.ACTION_MAIN);
		// Recogemos el texto des de nuestros Values
		CharSequence contentTitle = getString(R.string.app_name);
		// Creamos intent para crear acceso directo
		Intent addIntent = new Intent();
		// Añadimos los Extras necesarios como nombre del icono y icono
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, contentTitle.toString());
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(
						getApplicationContext(), R.drawable.ic_launcher));
		// IMPORTATE: si el icono ya esta creado que no cree otro
		addIntent.putExtra("duplicate", false);
		// Llamamos a la acción
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// Enviamos petición
		getApplicationContext().sendBroadcast(addIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
