package com.unal.iun;

import java.util.Calendar;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

public class DetailsActivity extends Activity {
	double lat[];
	double lon[];
	String titulos[], descripciones[];
	TableLayout tl;
	boolean evento = false;
	String data[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_details);
		Bundle b = getIntent().getExtras();
		tl = (TableLayout) findViewById(R.id.tableLayoutDetalles);
		int ids[] = { R.id.textPhone, R.id.textEmail, R.id.textEdificios,
				R.id.textWebSite, R.id.textServicio, R.id.textTitle,
				R.id.textExtension };

		ScrollView sc = (ScrollView) findViewById(R.id.scrollViewDetalles);
		Space sp = (Space) findViewById(R.id.spaceDetalles);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		double factor = screenHeight / 2000.0 + 0.33;
		double factor2 = 3.0 * screenHeight / 20000.0 + 0.09;
		if (factor > 0.6) {
			factor = 0.6;
		}
		if (factor2 > 0.2) {
			factor2 = 0.2;
		}
		sp.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor2))));
		sc.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor))));
		try {
			data = b.getStringArray("datos");
			Typeface fuente = Typeface.createFromAsset(getAssets(),
					"Helvetica.ttf");
			evento = b.getBoolean("evento");
			for (int i = 0; i < ids.length; i++) {
				TextView tx = (TextView) findViewById(ids[i]);
				tx.setTypeface(fuente);
				if (data[i] != null) {
					if (i == 6) {
						if (evento) {
							Drawable image = new BitmapDrawable(
									BitmapFactory.decodeResource(
											getResources(),
											R.drawable.ic_calendario));
							ImageView im = (ImageView) findViewById(R.id.ImageView01);
							/*
							 * image = MainActivity.resizeImage(
							 * getApplicationContext(), R.drawable.calendario,
							 * 35, 35);
							 */
							im.setImageDrawable(image);
							tx.setText(data[i].trim());
						} else {
							tx.setText("Extension: " + data[i].trim());
						}
					} else {
						tx.setText(data[i].trim());
					}
				}
				// Log.e("los datos son: ", data[i]);
			}
			lat = new double[1];
			lon = new double[1];
			titulos = new String[1];
			descripciones = new String[1];
			titulos[0] = data[5];
			descripciones[0] = data[3];
			lat[0] = b.getDouble("lat");
			lon[0] = b.getDouble("lon");
			int id = R.drawable.fondo2;
			if (b.getInt("fondo") != 0) {
				id = b.getInt("fondo");
			}
			Drawable background = new BitmapDrawable(
					BitmapFactory.decodeResource(getResources(), id));
			tl.setBackgroundDrawable(background);
		} catch (Exception e) {
			Log.e("error de Detalles ", e.toString());
		}

	}

	public void addEventToCalendar(View v) {
		try {
			if (evento) {
				addEventToCalendar(getParent());
			} else {
				llamar("3165000;" + data[6]);

			}
		} catch (Exception e) {
			Log.e("Error Detalles", e.toString());
		}
	}

	public void addEventToCalendar(Activity activity) {
		Bundle b = getIntent().getExtras();
		String data[] = b.getStringArray("datos");
		Calendar cal = Calendar.getInstance();
		String cad[] = data[6].split("-");
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(cad[2]));
		cal.set(Calendar.MONTH, Integer.parseInt(cad[1]));
		cal.set(Calendar.YEAR, Integer.parseInt(cad[0]));

		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 45);

		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");

		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
				cal.getTimeInMillis());
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
				cal.getTimeInMillis() + 60 * 60 * 1000);

		intent.putExtra(Events.ALL_DAY, false);
		/*
		 * case DAILY: event.put("rrule", "FREQ=DAILY"); break; case MONTHLY:
		 * event.put("rrule", "FREQ=MONTHLY"); break; case WEEKLY:
		 * event.put("rrule", "FREQ=WEEKLY"); break; case FORTNIGHTLY:
		 * event.put("rrule", "FREQ=YEARLY"); //CODE for Fortnight to be
		 */
		intent.putExtra("rrule", "FREQ=DAILY;COUNT=1");
		intent.putExtra(Events.TITLE, data[5]);
		intent.putExtra(Events.DESCRIPTION, data[4]);
		intent.putExtra(Events.EVENT_LOCATION, data[2]);

		this.startActivity(intent);
	}

	public void irA(View v) {
		Intent deta = new Intent(this, WebActivity.class);
		TextView tx = (TextView) findViewById(R.id.textWebSite);
		if (tx.getText().toString().contains("Sitio Web")) {
			return;
		}
		deta.putExtra("paginaWeb", tx.getText());
		startActivity(deta);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			try {
				this.finish();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.finish();
			/*
			 * Toast.makeText(getApplicationContext(), "Estas intentando Salir",
			 * 1) .show();
			 */
		}
		return super.onKeyDown(keyCode, event);
	}

	public void llamar(View v) {
		TextView tx = (TextView) findViewById(R.id.textPhone);
		if (tx.getText().toString().contains("ele")) {
			return;
		}
		llamar(tx.getText().toString());
	}

	public void llamar(String number) {
		Uri numero = Uri.parse("tel: +571"+ number);
		Intent intent = new Intent(Intent.ACTION_CALL, numero);
		startActivity(intent);
	}

	public void correo(View v) {
		TextView tx = (TextView) findViewById(R.id.textEmail);
		if (tx.getText().toString().contains("Correo")) {
			return;
		}
		enviar(tx.getText().toString().split(" "), null, "", "");
	}

	private void enviar(String[] to, String[] cc, String asunto, String mensaje) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
		emailIntent.putExtra(Intent.EXTRA_CC, cc);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, asunto);
		emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
		emailIntent.setType("message/rfc822");
		startActivity(Intent.createChooser(emailIntent, "Email "));
	}

	public void home(View v) {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		this.finish();
	}

	public void ubicar(View v) {
		try {
			TextView tx = (TextView) findViewById(R.id.textEdificios);
			if (tx.getText().toString().equals("Edificio")) {
				return;
			}
			Intent mapa = new Intent(this, MapaActivity.class);
			mapa.putExtra("lat", lat);
			mapa.putExtra("lon", lon);
			mapa.putExtra("titulos", titulos);
			mapa.putExtra("descripciones", descripciones);
			mapa.putExtra("zoom", 18);
			mapa.putExtra("tipo", 1);
			startActivity(mapa);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} catch (Exception ex) {
		}
	}

}
