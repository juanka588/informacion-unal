package com.unal.iun;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

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
import android.widget.ExpandableListView;
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
	public static double lat[];
	public static double lon[];
	public static String titulos[], descripciones[];
	LinearLayout tl;
	boolean evento = false;
	ArrayList<String[]> data = new ArrayList<String[]>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_details);
		BitmapDrawable background2 = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.fondoinf));
		this.getActionBar().setBackgroundDrawable(background2);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		Bundle b = getIntent().getExtras();
		tl = (LinearLayout) findViewById(R.id.linearLayoutDetalles);

		ExpandableListView sc = (ExpandableListView) findViewById(R.id.expandableListDestails);
		Space sp = (Space) findViewById(R.id.spaceDetalles);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		double factor = screenHeight / 2000.0 + 0.35;
		double factor2 = 3.0 * screenHeight / 20000.0 + 0.09;
		if (factor > 0.65) {
			factor = 0.65;
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
		//try {
			data = (ArrayList<String[]>) b.get("datos");
			TextView tx = (TextView) findViewById(R.id.tituloDetallesDtos);
			tx.setText(data.get(0)[0]);

			int id = R.drawable.fondo2;
			if (b.getInt("fondo") != 0) {
				id = b.getInt("fondo");
			}
			Drawable background = new BitmapDrawable(
					BitmapFactory.decodeResource(getResources(), id));
			tl.setBackgroundDrawable(background);
			// sc.setDividerHeight(2);
			// sc.setGroupIndicator(null);
			sc.setClickable(true);
			ArrayList<String> parentItems = new ArrayList<String>();
			ArrayList<Object> childItems = new ArrayList<Object>();
			for (int i = 0; i < data.size(); i++) {
				parentItems.add(data.get(i)[1]);

			}

			lat = new double[data.size()];
			lon = new double[data.size()];
			titulos = new String[data.size()];
			descripciones = new String[data.size()];

		
			for (int k = 0; k < data.size(); k++) {
				ArrayList<String> child = new ArrayList<String>();
				String[] datos = data.get(k);
				for (int i = 2; i < datos.length; i++) {
					if (datos[i] == null) {
						child.add("");
					} else {
						if (i == datos.length - 1) {
							titulos[k] = datos[5];
							descripciones[k] = datos[6];
							lat[k] = Double.parseDouble(datos[8].split(" ")[0]);
							lon[k] = Double.parseDouble(datos[8].split(" ")[1]);
						} else {
							child.add(datos[i]);
						}
					}
				}
				childItems.add(child);
			}

			MiAdaptadorExpandible adapter = new MiAdaptadorExpandible(
					parentItems, childItems);
			adapter.setInflater(
					(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
					this);
			sc.setAdapter(adapter);
			// sc.setOnChildClickListener(this);
		//} catch (Exception e) {
		//	Log.e("error de Detalles ", e.toString());
		//}

	}

	public void addEventToCalendar(View v) {
		try {
			if (evento) {
				addEventToCalendar(getParent());
			} else {
				// llamar("3165000;" + data[6]);

			}
		} catch (Exception e) {
			Log.e("Error Detalles", e.toString());
		}
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

		}
		return super.onKeyDown(keyCode, event);
	}

	public void home() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		this.finish();
	}

}
