package com.unal.iun;

import com.unal.iun.LN.MiAdaptador;
import com.unal.iun.LN.Util;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class ServiciosActivity extends Activity {
	ListView lv;
	SearchView sv;
	WebView browser;
	Activity act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servicios);
		BitmapDrawable background2 = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.fondoinf));
		this.getActionBar().setBackgroundDrawable(background2);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		lv = (ListView) findViewById(R.id.listaServicios);
		browser = (WebView) findViewById(R.id.navegadorWebServicios);
		browser.getSettings().setJavaScriptEnabled(true);
		browser.getSettings().setBuiltInZoomControls(true);
		browser.setWebViewClient(new WebViewClient() {
			// evita que los enlaces se abran fuera nuestra app en el navegador
			// de android
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

		});
		browser.setVisibility(View.INVISIBLE);
		iniciarComponentes();
		act = this;

	}

	public void iniciarComponentes() {
		SQLiteDatabase db = openOrCreateDatabase(MainActivity.dataBaseName,
				MODE_WORLD_READABLE, null);
		Cursor c = db.rawQuery("SELECT descripcion_enlace,nombre_icono, url, "
				+ "servComunidad FROM ENLACE where orden_enlace !='' "
				+ "order by servComunidad ASC ", null);
		final String[][] mat = Util.imprimirLista(c);
		c.close();
		db.close();
		lv.setAdapter(null);
		MiAdaptador adapter = new MiAdaptador(this, Util.getcolumn(mat, 0),
				Util.getcolumn(mat, 1), 2);
		adapter.fuente = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// Log.e("Url Cargada", mat[position][2]);
				if (mat[position][2].equals("") || mat[position][2] == null) {
					Intent radio = new Intent(act, RadioActivity.class);
					act.startActivity(radio);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
				irA(mat[position][2]);
				browser.setVisibility(View.VISIBLE);
			}
		});
	}

	public void irA(String url) {
		browser.loadUrl(url);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("url", browser.getUrl());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		irA(savedInstanceState.getString("url"));
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.servicios, menu);
		MenuItem menuItem = menu.getItem(0);
		sv = (SearchView) menuItem.getActionView();
		sv.setQueryHint("Inicia una Busqueda...");
		sv.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				String cad = "http://unal.edu.co/resultados-de-la-busqueda/?q="
						+ arg0;
				navegar(cad);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		sv.setSoundEffectsEnabled(true);
		return true;
	}

	public void navegar(String cad) {
		browser.loadUrl(cad);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			home();
			break;
		case R.id.ItemVolver:
			volver();
			return true;
		case R.id.ItemRefresh:
			recargar();
			return true;
		case R.id.ItemForward:
			adelante();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void volver() {
		if (browser.canGoBack()) {
			browser.goBack();
		}
	}

	public void adelante() {
		if (browser.canGoForward()) {
			browser.goForward();
		}
	}

	public void recargar() {
		browser.reload();
	}

	public void home() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		this.finish();
	}
}
