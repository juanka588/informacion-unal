package com.unal.iun;

import java.util.ArrayList;
import java.util.List;

import com.unal.iun.LN.LinnaeusDatabase;
import com.unal.iun.LN.MiAdaptador;
import com.unal.iun.LN.MiAdaptadorExpandibleDetalles;
import com.unal.iun.LN.MiAdaptadorExpandibleInstituciones;
import com.unal.iun.LN.MiLocationListener;
import com.unal.iun.LN.Util;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Space;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

public class InstitucionesActivity extends Activity {
	private ExpandableListView lv;
	private Activity act;
	private SearchView sv;
	private boolean mode;
	private String table = "colegios";
	private ActionBar barra;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instituciones);
		act = this;
		BitmapDrawable background2 = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.fondoinf));
		barra=this.getActionBar();
		barra.setBackgroundDrawable(background2);
		barra.setDisplayHomeAsUpEnabled(true);
		barra.setHomeButtonEnabled(true);
		lv = (ExpandableListView) findViewById(R.id.listViewInstituciones);
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
			SQLiteDatabase db = openOrCreateDatabase(MainActivity.dataBaseName,
					MODE_WORLD_READABLE, null);
			Bundle b = getIntent().getExtras();
			String query = "";
			mode = b.getBoolean("modo");
			if (mode) {
				table = "colegios";
				query = "select nombre_edificio,direccion_edificio,latitud,longitud,departamento from "
						+ table;
			} else {
				table = "edificios";
				query = "select distinct nombre_edificio,edificio,latitud,longitud,sede_edificio from "
						+ table;
				barra.setTitle("Edificios");
			}
			Log.e("buscado", query);
			Cursor c = db.rawQuery(query, null);
			final String[][] mat = Util.imprimirLista(c);
			c.close();
			ArrayList<String> parentItems = new ArrayList<String>();
			ArrayList<List> childItems = new ArrayList<List>();
			String ciudades[] = Util.getcolumn(mat, 4);
			for (int i = 0; i < ciudades.length; i++) {
				String current = ciudades[i];
				if (!parentItems.contains(current)) {
					parentItems.add(current);
				}
			}
			for (int i = 0; i < parentItems.size(); i++) {
				ArrayList<String> data = new ArrayList<String>();
				childItems.add(data);
			}
			String instituciones[] = Util.getcolumn(mat, 0);
			for (int i = 0; i < instituciones.length; i++) {
				childItems.get(parentItems.indexOf(ciudades[i])).add(
						instituciones[i]);
			}
			MiAdaptadorExpandibleInstituciones adapter = new MiAdaptadorExpandibleInstituciones(
					parentItems, childItems, mat, mode);
			adapter.fuente = Typeface.createFromAsset(getAssets(),
					"Helvetica.ttf");
			adapter.setInflater(
					(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
					this);
			lv.setAdapter(adapter);
			db.close();

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.instituciones, menu);
		MenuItem menuItem = menu.getItem(0);
		sv = (SearchView) menuItem.getActionView();
		sv.setQueryHint("Inicia una Busqueda...");
		sv.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				if (query.length() > 0) {
					recargar(query);
				} else {
					recargar("");
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (newText.length() > 0) {
					recargar(newText);
				} else {
					recargar("");
				}
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
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

	public void recargar(String query) {
		String cad = query.replaceAll("i", "_");
		cad = cad.replaceAll("a", "_");
		cad = cad.replaceAll("e", "_");
		cad = cad.replaceAll("o", "_");
		cad = cad.replaceAll("u", "_");
		cad = cad.replaceAll(" ", "_");
		String cad2 = "";
		if (mode) {
			table = "colegios";
			cad2 = "select nombre_edificio,direccion_edificio,latitud,longitud,departamento from "
					+ table + " where nombre_edificio like('%" + cad + "%')";
		} else {
			table = "edificios";
			cad2 = "select nombre_edificio,edificio,latitud,longitud,sede_edificio,"
					+ "nombre_edificio||edificio as busqueda from "
					+ table
					+ " where busqueda like('%" + cad + "%')";
		}
		Log.e("busqueda", cad2);
		SQLiteDatabase db = openOrCreateDatabase(MainActivity.dataBaseName,
				MODE_WORLD_READABLE, null);
		Cursor c = db.rawQuery(cad2, null);
		final String[][] mat = Util.imprimirLista(c);
		c.close();
		ArrayList<String> parentItems = new ArrayList<String>();
		ArrayList<List> childItems = new ArrayList<List>();
		String ciudades[] = Util.getcolumn(mat, 4);
		for (int i = 0; i < ciudades.length; i++) {
			String current = ciudades[i];
			if (!parentItems.contains(current)) {
				parentItems.add(current);
			}
		}
		for (int i = 0; i < parentItems.size(); i++) {
			ArrayList<String> data = new ArrayList<String>();
			childItems.add(data);
		}
		String instituciones[] = Util.getcolumn(mat, 0);
		for (int i = 0; i < instituciones.length; i++) {
			childItems.get(parentItems.indexOf(ciudades[i])).add(
					instituciones[i]);
		}
		MiAdaptadorExpandibleInstituciones adapter = new MiAdaptadorExpandibleInstituciones(
				parentItems, childItems, mat, mode);
		adapter.fuente = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		adapter.setInflater(
				(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
				this);
		lv.setAdapter(adapter);
		try {
			lv.expandGroup(0);
		} catch (Exception e) {

		}
		db.close();
	}

}
