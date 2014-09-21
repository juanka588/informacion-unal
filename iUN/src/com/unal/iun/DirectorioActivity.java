package com.unal.iun;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActionBar;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Space;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class DirectorioActivity extends Activity {
	boolean nacional = false;
	String seleccion = "";
	String condicion = "";
	String tableName = "Base";
	String tableName2 = "Edificios";
	String tableName3 = "ENLACE";
	String sql = "";
	String path = "";
	String auxCon;
	MenuItem item;
	SearchView sv;
	static int current = 2;
	int idFondo = R.drawable.fondo2, idFondoTras = R.drawable.fondo2;
	double lat[];
	double lon[];
	String titulos[], descripciones[];
	String columnas[] = { "codigo", "NIVEL_ADMINISTRATIVO", "SEDE",
			"DEPENDENCIAS", "DIVISIONES", "DEPARTAMENTOS", "SECCIONES",
			"CORREO_ELECTRONICO", "EXTENSION", "FAX", "DIRECTO",
			"LUGAR_GEOGRAFICO", "EDIFICIO", "PISO_OFICINA", "CLASIFICACION" };
	TableLayout tl;
	BitmapDrawable background;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/*
		 * EditText texto = (EditText) findViewById(R.id.editText1);
		 * texto.setVisibility(0); TranslateAnimation an = new
		 * TranslateAnimation( TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
		 * TranslateAnimation.RELATIVE_TO_PARENT, -1f,
		 * TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
		 * TranslateAnimation.RELATIVE_TO_PARENT, 0.0f); an.setDuration(1000);
		 * texto.startAnimation(an);
		 */
		return super.onTouchEvent(event);
	}

	public void home() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		this.finish();
	}

	public void ubicar(View v) {
		try {
			Intent mapa = new Intent(this, MapaActivity.class);
			LinnaeusDatabase lb = new LinnaeusDatabase(getApplicationContext());
			SQLiteDatabase db = openOrCreateDatabase("DataStore.sqlite",
					MODE_WORLD_READABLE, null);
			String query;
			int nivel = current - 1;
			if (nivel > 1) {
				nivel = 3;
			}
			if (condicion != "") {
				query = "select distinct edificio,nombre_edificio,latitud,longitud from edificios natural join base where "
						+ condicion;// + " nivel="+ nivel;
				Log.e("query mapa", query);
			} else {
				query = "select distinct edificio,nombre_edificio,latitud,longitud from edificios natural join base where nivel="
						+ nivel;
			}
			Cursor c = db.rawQuery(query, null);
			String[][] mat = Util.imprimirLista(c);
			c.close();
			db.close();
			lat = Util.toDouble(Util.getcolumn(mat, 2));
			lon = Util.toDouble(Util.getcolumn(mat, 3));
			titulos = Util.getcolumn(mat, 1);
			descripciones = Util.getcolumn(mat, 0);
			mapa.putExtra("lat", lat);
			mapa.putExtra("lon", lon);
			mapa.putExtra("titulos", titulos);
			mapa.putExtra("descripciones", descripciones);
			mapa.putExtra("nivel", current - 1);
			mapa.putExtra("zoom", current <= 2 ? current + 3 : current + 10);
			mapa.putExtra("tipo", 0);
			startActivity(mapa);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} catch (Exception ex) {
		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.directorio, menu);
		item = menu.getItem(0);
		MenuItem menuItem = menu.getItem(1);
		sv = (SearchView) menuItem.getActionView();
		sv.setQueryHint("Inicia una Busqueda...");
		sv.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				recargar(query);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (newText.length() > 3) {
					recargar(newText);
				}
				return false;
			}
		});
		/*
		 * c.close(); db.close();
		 */
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



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_directorio);
		BitmapDrawable background2 = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.fondoinf));
		this.getActionBar().setBackgroundDrawable(background2);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setHomeButtonEnabled(true);
		tl = (TableLayout) findViewById(R.id.TableLayoutDirectorio);
		ListView lv = (ListView) findViewById(R.id.listViewDirectorio);
		Space sp = (Space) findViewById(R.id.SpaceDirectorio);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		double factor = screenHeight / 2000.0 + 0.25;
		double factor2 = 3.0 * screenHeight / 20000.0 + 0.09;
		if (factor > 0.5) {
			factor = 0.5;
		}
		if (factor2 > 0.2) {
			factor2 = 0.2;
		}
		sp.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor2))));
		lv.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				(int) (screenHeight * (0.47))));

		Bundle b = getIntent().getExtras();
		try {
			LinnaeusDatabase lb = new LinnaeusDatabase(getApplicationContext());
			SQLiteDatabase db = openOrCreateDatabase("DataStore.sqlite",
					MODE_WORLD_READABLE, null);
			if (b.getBoolean("salto")) {
				current = b.getInt("current");
				String sede = b.getString("sede");
				condicion = "sede='" + sede + "'";
				sql = "select  distinct " + columnas[current] + " from "
						+ tableName + " where " + condicion;
				path = sede;
				animarFondo(sede, false);
			} else {
				sql = "select  distinct " + columnas[current] + " from "
						+ tableName;
			}
			Cursor c = db.rawQuery(sql, null);
			final String[][] mat = Util.imprimirLista(c);
			c.close();
			db.close();
			MiAdaptador adapter = new MiAdaptador(this, Util.getcolumn(mat, 0),
					Util.getcolumn(mat, 0));
			adapter.fuente = Typeface.createFromAsset(getAssets(),
					"Helvetica.ttf");
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View vista,
						int posicion, long arg3) {
					try {
						seleccion = mat[posicion][0];
						if (path == "") {
							path = seleccion;
							item.setTitleCondensed(path.toUpperCase().trim());
						} else {
							path = path + ">" + seleccion.toUpperCase().trim();
							item.setTitleCondensed(path);
						}
						item.setTitleCondensed(path);
						current++;
						if (current == 3) {
							animarFondo(mat[posicion][0], true);
						}
						if (condicion.equals("")) {
							condicion = columnas[current - 1] + " = '"
									+ seleccion + "'";
						} else {
							condicion += " and " + columnas[current - 1]
									+ " = '" + seleccion + "'";
						}

						sql = "select  distinct " + columnas[current] + ", "
								+ columnas[2] + " from " + tableName
								+ "  where " + condicion;
						recargar(false, false);

					} catch (Exception e) {
						Toast.makeText(getApplication(), e.toString(),
								Toast.LENGTH_LONG).show();
					}

				}
			});

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}

	}

	public void animarFondo(String cad, boolean cond) {
		int id = R.drawable.fondo2;
		Log.e("Seleccionado el fondo", cad);
		if (cad.contains("Bogo")) {
			id = R.drawable.ciudad_universitaria;
			idFondoTras = id;
		}
		if (cad.contains("Amaz")) {
			id = R.drawable.amazonia;
			idFondoTras = id;
		}
		if (cad.contains("Caribe")) {
			id = R.drawable.caribe;
			idFondoTras = id;
		}
		if (cad.contains("Mani")) {
			id = R.drawable.manizales;
			idFondoTras = id;
		}
		if (cad.contains("Mede")) {
			id = R.drawable.medellin;
			idFondoTras = id;
		}
		if (cad.contains("Tumac")) {
			id = R.drawable.tumaco;
			idFondoTras = id;
		}
		if (cad.contains("Palmira")) {
			id = R.drawable.palmira;
			idFondoTras = id;
		}
		if (cad.contains("Orino")) {
			id = R.drawable.orinoquia;
			idFondoTras = id;
		}
		if (cad.contains("Franco")) {
			id = R.drawable.ciudad_universitaria;
			idFondoTras = id;
		}
		if (cad.contains("Museo")) {
			id = R.drawable.ciudad_universitaria;
			idFondoTras = id;
		}
		idFondo = id;
		background = new BitmapDrawable(BitmapFactory.decodeResource(
				getResources(), id));
		Animation aparecer = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.fade_in2);
		aparecer.reset();
		tl.setAnimation(aparecer);
		tl.setBackgroundDrawable(background);
		if (cond) {
			tl.startAnimation(aparecer);
		}
	}

	public void detalles() {
		Intent deta = new Intent(this, DetailsActivity.class);
		ArrayList<String[]> datos = getDatos();
		deta.putExtra("datos", datos);
		try {
			deta.putExtra("fondo", idFondoTras);
		} catch (Exception e) {

		}
		startActivity(deta);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		// this.finish();
		if (current == 5) {
			erase(condicion, false);
		}
	}

	private ArrayList<String[]> getDatos() {
		ArrayList<String[]> datos = new ArrayList<String[]>();
		SQLiteDatabase db = openOrCreateDatabase("DataStore.sqlite",
				MODE_WORLD_READABLE, null);
		String consulta = "SELECT departamentos,secciones,directo,extension,correo_electronico,NOMBRE_EDIFICIO,url,DESCRIPCION, LATITUD,LONGITUD FROM "
				+ tableName
				+ " natural join "
				+ tableName2
				+ " natural join "
				+ tableName3 + " where " + condicion;
		Log.e("SQL ORIGINAL", consulta);
		if (!sql.contains("ASC")) {
			if (current != 5) {
				consulta = sql;
			}
		}

		Log.e("consulta", consulta);

		Cursor c = db.rawQuery(consulta, null);
		String[][] mat = Util.imprimirLista(c);
		c.close();
		db.close();
		//Log.e("datos",Util.toString(mat));
		try {
			for (int i = 0; i < mat.length; i++) {
				String arr[] = new String[mat[i].length-1];
				for (int j = 0; j < mat[i].length-1; j++) {
					if (j ==mat[i].length-2) {
						arr[j] = mat[i][j] + " " + mat[i][j + 1];
					} else {
						arr[j] = mat[i][j];
					}
				}
				datos.add(arr);
				Log.e("los datos "+i,Util.toString(arr));
			}
		/*	datos[0] = mat[0][0];// departamento
			datos[1] = mat[0][1];// titulo
			datos[2] = mat[0][2];// telefono
			datos[3] = mat[0][3];// extension
			datos[4] = mat[0][4];// email
			datos[5] = mat[0][5];// edificio
			datos[6] = mat[0][6];// enlace
			datos[7] = mat[0][7];// descripcion
			datos[8] = mat[0][8] + " " + mat[0][9];// posiciones*/
		} catch (Exception e) {
			Log.e("Error Datos",e.toString());
			Toast.makeText(getApplication(), "Aun no tenemos los Datos",
					Toast.LENGTH_LONG).show();
		}
		return datos;
	}



	@Override
	public void onBackPressed() {
		if (current <= 2) {
			startActivity(new Intent(getApplicationContext(),
					MainActivity.class));
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			this.finish();
			super.onBackPressed();
		}
		erase(condicion, true);
		recargar(false, false);
	}

	private void erase(String condicion2, boolean cond) {
		String conds[] = condicion2.split("and");
		String textos[] = path.split(">");

		if (conds.length == 1) {
			item.setTitleCondensed("");
			path = "";
			condicion = "";
			sql = "select  distinct " + columnas[2] + ", " + columnas[2]
					+ " from " + tableName;
			current = 2;
			animarFondo("", true);
			return;
		}
		String cad = "", cad2 = "";
		for (int i = 0; i < conds.length - 1; i++) {
			if (i != conds.length - 2) {
				cad += conds[i] + "and";
				cad2 += textos[i] + ">";
			} else {
				cad += conds[i];
				cad2 += textos[i];
			}
		}
		item.setTitleCondensed(cad2);
		path = cad2;
		condicion = cad;
		sql = "select distinct " + columnas[current - 1] + ", " + columnas[2]
				+ " from " + tableName + " where " + condicion;
		if (cond) {
			current--;
		}
		// Log.e("la cond", sql);
		// Log.e("current ", current + "");
	}

	public void recargar(String cad2) {

		String cad = cad2.replaceAll("i", "_");
		cad = cad.replaceAll("a", "_");
		cad = cad.replaceAll("e", "_");
		cad = cad.replaceAll("o", "_");
		cad = cad.replaceAll("u", "_");
		Log.e("cadena", cad);
		sql = "select secciones, " + columnas[2] + " from " + tableName
				+ " where secciones like('%" + cad
				+ "%') order by NIVEL_ADMINISTRATIVO ASC";
		Log.e("buscado", sql);
		current = 1;
		recargar(true, true);
		// animarFondo("", false);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("current", current);
		outState.putBoolean("nacional", nacional);
		outState.putString("sql", sql);
		outState.putString("path", path);
		outState.putInt("idFondo", idFondo);
		outState.putInt("idFondoTras", idFondoTras);
		outState.putString("condicion", condicion);
		// recargar(current==6, false);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		try {
			if (savedInstanceState != null) {
				current = savedInstanceState.getInt("current");
				nacional = savedInstanceState.getBoolean("nacional");
				sql = savedInstanceState.getString("sql");
				path = savedInstanceState.getString("path");
				idFondo = savedInstanceState.getInt("idFondo");
				idFondoTras = savedInstanceState.getInt("idFondoTras");
				condicion = savedInstanceState.getString("condicion");
				Log.e("al restaurar", sql);
				animarFondo(path, false);
				recargar(current == 5, false);
				// item.setTitleCondensed(path);
			}
		} catch (Exception e) {
			Log.e("Error de restauracion", e.toString());
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	public void recargar(final boolean cond, final boolean cond2) {

		try {
			ListView lv = (ListView) findViewById(R.id.listViewDirectorio);
			lv.setAdapter(null);
			SQLiteDatabase db = openOrCreateDatabase("DataStore.sqlite",
					MODE_WORLD_READABLE, null);
			Cursor c = db.rawQuery(sql, null);
			final String[][] mat = Util.imprimirLista(c);
			c.close();
			db.close();
			MiAdaptador adapter = new MiAdaptador(this, Util.getcolumn(mat, 0),
					Util.getcolumn(mat, 1));
			adapter.fuente = Typeface.createFromAsset(getAssets(),
					"Helvetica.ttf");
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View vista,
						int posicion, long arg3) {
					seleccion = mat[posicion][0];
					if (current == 2) {
						animarFondo(mat[posicion][1], true);
					}
					if (path == "") {
						path = seleccion;
						item.setTitleCondensed(path.toUpperCase().trim());
					} else {
						path = path + ">" + seleccion.toUpperCase().trim();
						item.setTitleCondensed(path);
					}
					if (cond) {
						if (cond2) {
							condicion = "secciones like('" + seleccion + "')";
							animarFondo(mat[posicion][1], false);
							detalles();
							return;
						} else {
							condicion += " and " + columnas[current] + " = '"
									+ seleccion + "'";
							detalles();
							return;
						}
					}
					current++;
					int resta = 1;
					if (condicion.equals("")) {
						condicion = columnas[current - resta] + " = '"
								+ seleccion + "'";

					} else {
						condicion += " and " + columnas[current - resta]
								+ " = '" + seleccion + "'";
					}
					sql = "select  distinct " + columnas[current] + ", "
							+ columnas[2] + " from " + tableName + "  where "
							+ condicion;
					// Toast.makeText(getApplication(), sql, Toast.LENGTH_LONG)
					// .show();
					recargar(current == 5, false);

				}
			});
		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}
	}



}
