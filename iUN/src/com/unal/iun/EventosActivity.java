package com.unal.iun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.unal.iun.LN.MiAdaptadorEventos;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.CursorJoiner.Result;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.Toast;

public class EventosActivity extends Activity {
	NotificationManager nm;
	Notification notif;
	static String ns = Context.NOTIFICATION_SERVICE;
	Intent deta;
	JSONArray jsonArray;
	String readTwitterFeed;
	ListView lv;
	Space sp;
	Activity act;
	boolean recargable = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_eventos);
		BitmapDrawable background2 = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.fondoinf));
		this.getActionBar().setBackgroundDrawable(background2);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		lv = (ListView) findViewById(R.id.listView2);
		sp = (Space) findViewById(R.id.spaceEventos);
		act = this;
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		double factor = screenHeight / 2000.0 + 0.35;
		double factor2 = 3.0 * screenHeight / 20000.0 + 0.09;
		if (factor > 0.58) {
			factor = 0.58;
		}
		if (factor2 > 0.2) {
			factor2 = 0.2;
		}
		sp.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor2))));
		lv.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor))));

		deta = new Intent(this, DetailsActivity.class);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		URL url;
		try {
			url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=4.6382023,-74.0840434&destination=6.26261,-75.57775&sensor=true");
			new EventFetch().execute(url);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void abrirDetalles() {
		startActivity(deta);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		// this.finish();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			home();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (recargable) {
			recargarLista();
		} else {
			Toast.makeText(getApplicationContext(), "Aun no se puede recargar",
					1).show();
		}
		return super.onTouchEvent(event);
	}

	public String[] obtenerDatos(int pos) throws JSONException {
		JSONObject jsonObject = jsonArray.getJSONObject(pos);
		String data[] = new String[7];
		data[0] = jsonObject.getString("telefono");// telefono
		data[1] = jsonObject.getString("email");// email
		data[2] = jsonObject.getString("lugar");// ubicacion-Edificio
		data[3] = jsonObject.getString("enlace");// sitio web
		data[4] = jsonObject.getString("descripcion");// descripcion
		data[5] = jsonObject.getString("titulo");// titulo
		data[6] = jsonObject.getString("fecha");// fecha

		return data;
	}

	public void recargarLista() {
		ArrayList<String[]> cad2 = new ArrayList<String[]>();
		try {
			jsonArray = new JSONArray(readTwitterFeed);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String arr[] = new String[] { jsonObject.getString("titulo"),
						jsonObject.getString("descripcion"),
						jsonObject.getString("lugar"),
						jsonObject.getString("fecha"),
						jsonObject.getString("email"),
						jsonObject.getString("enlace"),
						jsonObject.getString("telefono") };
				nm = (NotificationManager) getSystemService(ns);
				notificacion(
						R.drawable.icono_app,
						jsonObject.getString("titulo"),
						jsonObject.getString("titulo"),
						jsonObject.getString("lugar")
								+ jsonObject.getString("fecha"), i);
				cad2.add(arr);
			}
			MiAdaptadorEventos adapter = new MiAdaptadorEventos(act, cad2);
			adapter.fuente = Typeface.createFromAsset(getAssets(),
					"Helvetica.ttf");
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int posicion, long arg3) {
					try {
						JSONObject jsonObject = jsonArray
								.getJSONObject(posicion);
						deta.putExtra("datos", obtenerDatos(posicion));
						deta.putExtra("lat", Double.parseDouble(jsonObject
								.getString("lugarLatitud")));
						deta.putExtra("lon", Double.parseDouble(jsonObject
								.getString("lugarLongitud")));
						deta.putExtra("evento", true);
						abrirDetalles();
					} catch (Exception e) {
						Log.e("Error Eventos", e.toString());
					}

				}
			});
		} catch (Exception e) {
			Log.e("Error Eventos Tarea Alterna", e.toString());
		}
	}

	private class EventFetch extends AsyncTask<URL, Integer, String> {

		@Override
		protected String doInBackground(URL... params) {
			// TODO Auto-generated method stub
			readTwitterFeed = readJSONFeed(params[0]);
			recargable = true;

			return readTwitterFeed;
		}

		protected void onPostExecute(Long result) {
			recargarLista();
		}

		public String readJSONFeed(URL params) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(params.toString());
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"No se ha accedido al servidor de Eventos", 1)
							.show();
					Log.e(MainActivity.class.toString(),
							"Failed to download file");
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return builder.toString();
		}
	}

	public void notificacion(int icon, CharSequence textoEstado,
			CharSequence titulo, CharSequence texto, int id)
			throws NumberFormatException, JSONException {

		long hora = System.currentTimeMillis();

		// Definimos la accion de la pulsacion sobre la notificacion (esto es
		// opcional)
		Context context = getApplicationContext();
		Intent notificationIntent = new Intent(this, DetailsActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		JSONObject jsonObject = jsonArray.getJSONObject(id);
		notificationIntent.putExtra("datos", obtenerDatos(id));
		notificationIntent.putExtra("lat",
				Double.parseDouble(jsonObject.getString("lugarLatitud")));
		notificationIntent.putExtra("lon",
				Double.parseDouble(jsonObject.getString("lugarLongitud")));

		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		notif = new Notification(icon, textoEstado, hora);
		notif.setLatestEventInfo(context, titulo, texto, contentIntent);
		// Defino que la notificacion sea permamente
		notif.flags = Notification.FLAG_AUTO_CANCEL;
		nm.notify(id, notif);
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		super.onBackPressed();
	}

	public void home() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		this.finish();
	}
}
