package com.unal.iun;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.unal.iun.LN.MiAdaptador;
import com.unal.iun.LN.Util;

/**
 * An activity representing a list of datos. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link datosDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link datosListFragment} and the item details (if present) is a
 * {@link datosDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link datosListFragment.Callbacks} interface to listen for item selections.
 */
public class datosListActivity extends Activity implements
		datosListFragment.Callbacks {
	String seleccion = "";
	String condicion = "";
	String tableName = "BaseM";
	String tableName2 = "Edificios";
	String tableName3 = "ENLACE";
	String sql = "";
	String path = "";
	String auxCon;
	ListView lv;
	MenuItem item;
	SearchView sv;
	int current = 1;
	TableRow tr;
	int idFondo = R.drawable.fondo, idFondoTras = R.drawable.fondo;
	double lat[];
	double lon[];
	String titulos[], descripciones[];
	String columnas[] = { "codigo", "NIVEL_ADMINISTRATIVO", "SEDE",
			"DEPENDENCIAS", "DIVISIONES", "DEPARTAMENTOS", "SECCIONES",
			"CORREO_ELECTRONICO", "EXTENSION", "FAX", "DIRECTO",
			"LUGAR_GEOGRAFICO", "EDIFICIO", "PISO_OFICINA", "CLASIFICACION" };
	TableLayout tl;
	BitmapDrawable background;
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	public boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datos_list);
		BitmapDrawable background2 = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.fondoinf));
		this.getActionBar().setBackgroundDrawable(background2);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setHomeButtonEnabled(true);
		datosListFragment dlf = null;
		if (findViewById(R.id.datos_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			dlf = ((datosListFragment) getFragmentManager().findFragmentById(
					R.id.datos_list));
			dlf.setActivateOnItemClick(true);
			dlf.setTwoPane(mTwoPane);
		}
		

		// TODO: If exposing deep links into your app, handle intents here.
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		try {
			if (savedInstanceState != null) {
				current = savedInstanceState.getInt("current");
				sql = savedInstanceState.getString("sql");
				path = savedInstanceState.getString("path");
				idFondo = savedInstanceState.getInt("idFondo");
				idFondoTras = savedInstanceState.getInt("idFondoTras");
				condicion = savedInstanceState.getString("condicion");
				// Log.e("al restaurar", sql);
				animarFondo(path, false);
				recargar(sql, current == 5, false);
				// item.setTitleCondensed(path);
			}
		} catch (Exception e) {
			Log.e("Error de restauracion", e.toString());
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.directorio, menu);
		// item = menu.getItem(0);
		MenuItem menuItem = menu.getItem(0);
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
		case R.id.ItemMapa:
			ubicar();
			break;
		case R.id.ItemWEB:
			if (current == 2) {
				Util.irA("http://www.unal.edu.co", this);
			} else {
				String baseConsult = "select url from enlace natural join "
						+ tableName + " where ";
				ArrayList<String[]> datos = getDatos(baseConsult, condicion,
						false);
				Util.irA(datos.get(0)[0], this);

			}
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

	public void ubicar() {
		try {
			Intent mapa = new Intent(this, MapaActivity.class);
			SQLiteDatabase db = openOrCreateDatabase(MainActivity.dataBaseName,
					MODE_WORLD_READABLE, null);
			String query;
			int nivel = current - 1;
			if (nivel > 2) {
				nivel = 3;
			}
			if (condicion != "") {
				query = "select distinct edificio,nombre_edificio,latitud,longitud from edificios natural join "
						+ tableName + " where " + condicion;
				// chambonazo mapa
				if (path.contains("Bogotá")) {
					query = query + " and nivel=" + nivel;
				}
				Log.e("query mapa", query);
			} else {
				query = "select distinct edificio,nombre_edificio,latitud,longitud from edificios natural join "
						+ tableName + " where nivel=" + nivel;
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
			mapa.putExtra("zoom", current <= 2 ? current + 3 : current + 13);
			mapa.putExtra("tipo", 0);
			startActivity(mapa);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} catch (Exception ex) {
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("current", current);
		outState.putString("sql", sql);
		outState.putString("path", path);
		outState.putInt("idFondo", idFondo);
		outState.putInt("idFondoTras", idFondoTras);
		outState.putString("condicion", condicion);
		// recargar(current==6, false);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Callback method from {@link datosListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		ArrayList<String[]> datos = getDatos();
		ArrayList<String> datosLinea = Util.parseLine(datos);
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.

			Bundle arguments = new Bundle();
			arguments.putStringArrayList("datos", datosLinea);
			datosDetailFragment fragment = new datosDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.datos_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, datosDetailActivity.class);
			detailIntent.putExtra(datosDetailFragment.ARG_ITEM_ID, id);
			detailIntent.putStringArrayListExtra("datos", datosLinea);
			startActivity(detailIntent);
		}
	}

	public void animarFondo(String cad, boolean cond) {
		int id = R.drawable.fondo;
		Log.e("Seleccionado el fondo", cad);
		if (cad.contains("Bogo")) {
			id = R.drawable.ciudad_universitaria;
			idFondoTras = id;
		}
		if (cad.contains("Amaz")) {
			id = R.drawable.amazonas;
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
		//tr.setVisibility(View.INVISIBLE);
		recargar(sql, true, true);
		// animarFondo("", false);

	}

	public void recargar(String query, final boolean cond, final boolean cond2) {

		try {

			lv.setAdapter(null);
			SQLiteDatabase db = openOrCreateDatabase(MainActivity.dataBaseName,
					MODE_WORLD_READABLE, null);
			Cursor c = db.rawQuery(query, null);
			if (current == 3) {
				c = db.rawQuery(query
						+ " and NIVEL_ADMINISTRATIVO between 1 and 4", null);
			}

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
					if (saltar(seleccion)) {
						irDirecto(seleccion);
						return;
					}
					if (seleccion.contains("Programas")) {
						irDirecto();
						return;
					}
					if (current == 2) {
						animarFondo(mat[posicion][1], true);
					}
					if (path == "") {
						path = seleccion;
						// item.setTitleCondensed(path.toUpperCase().trim());
					} else {
						path = path + ">" + seleccion.toUpperCase().trim();
						// item.setTitleCondensed(path);
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
					recargar(sql, current == 5, false);
					boolean cont = path.contains("FACULTAD DE");
					if (cont) {
						tr.setVisibility(View.VISIBLE);
						refresh(findViewById(R.id.buttonDirectorio));
					}
				}
			});
		} catch (Exception e) {
			Log.e("error al recargar ", e.toString());
		}
	}

	protected boolean saltar(String seleccion2) {
		if (seleccion2.contains("Museo")) {
			return true;
		}
		if (seleccion2.contains("Roberto")) {
			return true;
		}
		return false;
	}

	public void irDirecto(String seleccion) {
		String query = columnas[current] + " = '" + seleccion + "'";
		if (condicion != "") {
			query = condicion + " and " + query;
		}
		Intent deta = new Intent(this, DetailsActivity.class);
		ArrayList<String[]> datos = getDatos(query, false);
		deta.putExtra("datos", datos);
		try {
			deta.putExtra("fondo", idFondoTras);
		} catch (Exception e) {

		}
		startActivity(deta);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	public void irDirecto() {
		String query = columnas[current] + " = '" + seleccion + "' ";
		String baseConsult = "select url from enlace natural join " + tableName
				+ " where ";
		ArrayList<String[]> datos = getDatos(baseConsult, condicion + " and "
				+ query, false);
		Util.irA(datos.get(0)[0], this);
	}

	private ArrayList<String[]> getDatos(String baseConsult, String criteria,
			boolean cond) {
		String consulta = baseConsult + criteria;
		ArrayList<String[]> datos = new ArrayList<String[]>();
		SQLiteDatabase db = openOrCreateDatabase(MainActivity.dataBaseName,
				MODE_WORLD_READABLE, null);

		Log.e("SQL ORIGINAL", consulta);
		if (cond) {
			consulta = sql;
		}

		Log.e("consulta", consulta);

		Cursor c = db.rawQuery(consulta, null);
		String[][] mat = Util.imprimirLista(c);
		c.close();
		db.close();
		Log.e("datos", Util.toString(mat));
		try {
			for (int i = 0; i < mat.length; i++) {
				String arr[];
				if (mat[i].length == 1) {
					arr = new String[] { mat[i][0] };
				} else {
					arr = new String[mat[i].length - 1];
					for (int j = 0; j < mat[i].length - 1; j++) {
						if (j == mat[i].length - 2) {
							arr[j] = mat[i][j] + " " + mat[i][j + 1];
						} else {
							arr[j] = mat[i][j];
						}
					}
				}
				datos.add(arr);
				Log.e("los datos " + i, Util.toString(arr));
			}
		} catch (Exception e) {
			Log.e("Error Datos", e.toString());
			Toast.makeText(getApplication(), "Aun no tenemos los Datos",
					Toast.LENGTH_LONG).show();
		}
		return datos;
	}

	private ArrayList<String[]> getDatos(String criteria, boolean cond) {
		String consulta = "SELECT departamentos,secciones,directo,extension,correo_electronico,NOMBRE_EDIFICIO,url,DESCRIPCION, LATITUD,LONGITUD FROM "
				+ tableName
				+ " natural join "
				+ tableName2
				+ " natural join "
				+ tableName3 + " where ";
		return getDatos(consulta, criteria, cond);
	}

	public void refresh(View v) {
		CheckedTextView b = null;
		CheckedTextView b2;
		switch (v.getId()) {
		case R.id.buttonDirectorio:
			b = (CheckedTextView) findViewById(v.getId());
			b.setChecked(true);
			b2 = (CheckedTextView) findViewById(R.id.buttonDepartamentos);
			b2.setChecked(false);
			break;
		case R.id.buttonDepartamentos:
			b = (CheckedTextView) findViewById(v.getId());
			b.setChecked(true);
			b2 = (CheckedTextView) findViewById(R.id.buttonDirectorio);
			b2.setChecked(false);
			break;
		default:
			break;
		}
		refresh(v.getId() == R.id.buttonDirectorio);
	}

	private void refresh(boolean cond) {
		String auxCond = "";
		if (cond) {
			auxCond = " and departamentos not like('%Departamento%')";
		} else {
			auxCond = " and departamentos like('%Departamento%')";
		}
		try {
			String query = "select distinct departamentos,sede from "
					+ tableName + " where " + condicion;
			SQLiteDatabase db = openOrCreateDatabase(MainActivity.dataBaseName,
					MODE_WORLD_READABLE, null);
			Cursor c = db.rawQuery(query + auxCond, null);
			Log.e("consulta recarga", query + auxCond);
			final String[][] mat = Util.imprimirLista(c);
			if (mat.length == 0) {
				Toast.makeText(getApplicationContext(),
						"No hay " + (cond ? "Directorio" : "Departamentos"), 1)
						.show();
				c.close();
				db.close();
				return;
			}
			c.close();
			db.close();
			lv.setAdapter(null);
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
					condicion += " and " + columnas[5] + " = '" + seleccion
							+ "'";
					sql = "select  distinct " + columnas[5] + ", "
							+ columnas[2] + " from " + tableName + "  where "
							+ condicion;
					detalles();
					return;
				}
			});
		} catch (Exception e) {
			Log.e("error al recargar ", e.toString());
		}
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
		/*
		 * erase(condicion, true); recargar(sql, false, false);
		 */
	}

	private void erase(String condicion2, boolean cond) {
		String conds[] = condicion2.split("and");
		String textos[] = path.split(">");

		if (conds.length == 1) {
			// item.setTitleCondensed("");
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
		// item.setTitleCondensed(cad2);
		path = cad2;
		boolean cont = path.contains("FACULTAD DE");
		if (!cont) {
			tr.setVisibility(View.INVISIBLE);
			CheckedTextView b = (CheckedTextView) findViewById(R.id.buttonDepartamentos);
			b.setChecked(false);
			b = (CheckedTextView) findViewById(R.id.buttonDirectorio);
			b.setChecked(false);
		}
		condicion = cad;
		sql = "select distinct " + columnas[current - 1] + ", " + columnas[2]
				+ " from " + tableName + " where " + condicion;
		if (cond) {
			current--;
		}
		// Log.e("la cond", sql);
		// Log.e("current ", current + "");
	}

	private ArrayList<String[]> getDatos() {
		boolean cond = false;
		if (!sql.contains("ASC")) {
			if (current != 5) {
				cond = true;
			}
		}
		return getDatos(condicion, cond);
	}

	public void detalles() {
		ArrayList<String[]> datos = getDatos();
		ArrayList<String> datosLinea = Util.parseLine(datos);
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putStringArrayList("datos", datosLinea);
			arguments.putInt("fondo", idFondoTras);
			datosDetailFragment fragment = new datosDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.datos_detail_container, fragment).commit();
		} else {
			Intent deta = new Intent(this, DetailsActivity.class);
			deta.putExtra("datos", datos);
			try {
				deta.putExtra("fondo", idFondoTras);
			} catch (Exception e) {

			}
			startActivity(deta);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			if (current == 5) {
				erase(condicion, false);
			}
		}
	}
}
