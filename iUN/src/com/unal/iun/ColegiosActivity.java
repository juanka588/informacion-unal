package com.unal.iun;

import com.unal.iun.LN.LinnaeusDatabase;
import com.unal.iun.LN.MiAdaptador;
import com.unal.iun.LN.MiLocationListener;
import com.unal.iun.LN.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.Toast;

public class ColegiosActivity extends Activity {
	ListView lv;
	Activity act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_colegios);
		act = this;
		BitmapDrawable background2 = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.fondoinf));
		this.getActionBar().setBackgroundDrawable(background2);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setHomeButtonEnabled(true);
		lv = (ListView) findViewById(R.id.listViewColegios);
		Space sp = (Space) findViewById(R.id.SpaceColegios);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		double factor = screenHeight / 2000.0 + 0.25;
		double factor2 = screenHeight / 2000.0 + 0.25;
		if (factor > 0.15) {
			factor = 0.15;
		}
		if (factor2 > 0.65) {
			factor2 = 0.65;
		}
		sp.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor))));
		lv.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				(int) (screenHeight * (factor2))));
		try {
			LinnaeusDatabase lb = new LinnaeusDatabase(getApplicationContext());
			SQLiteDatabase db = openOrCreateDatabase("DataStore.sqlite",
					MODE_WORLD_READABLE, null);
			Cursor c = db.rawQuery(
					"select nombre_edificio,direccion_edificio from Coleg5A",
					null);
			final String[][] mat = Util.imprimirLista(c);
			c.close();
			db.close();
			MiAdaptador.tipo = 3;
			MiAdaptador adapter = new MiAdaptador(this, Util.getcolumn(mat, 0),
					Util.getcolumn(mat, 1));
			adapter.fuente = Typeface.createFromAsset(getAssets(),
					"Helvetica.ttf");
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View vista,
						int posicion, long arg3) {
					double lat2 = 4.637275;// (float) MiLocationListener.lat;
					double lon2 = -74.082776; // (float) MiLocationListener.longi;
					if (lat2 != 0 && lon2 != 0) {
						lat2 = (double) MiLocationListener.lat;
						lon2 = (double) MiLocationListener.longi;
					}
					Util.irA("https://www.google.es/maps/dir/'" + lat2 + ","
							+ lon2 + "'/'" +mat[posicion][1]+ "'", act);
				}
			});
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.colegios, menu);
		return true;
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

	public void home() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		this.finish();
	}
}
