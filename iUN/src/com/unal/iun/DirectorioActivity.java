package com.unal.iun;

import java.util.ArrayList;

import com.unal.iun.LN.LinnaeusDatabase;
import com.unal.iun.LN.MiAdaptador;
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
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class DirectorioActivity extends Activity {
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
	int current = 2;
	TableRow tr;
	int idFondo = R.drawable.fondo,
			idFondoTras = R.drawable.ciudad_universitaria;
	double lat[];
	double lon[];
	String titulos[], descripciones[];
	String columnas[] = { "codigo", "NIVEL_ADMINISTRATIVO", "SEDE",
			"DEPENDENCIAS", "DIVISIONES", "DEPARTAMENTOS", "SECCIONES",
			"CORREO_ELECTRONICO", "EXTENSION", "FAX", "DIRECTO",
			"LUGAR_GEOGRAFICO", "EDIFICIO", "PISO_OFICINA", "CLASIFICACION" };
	TableLayout tl;
	BitmapDrawable background;
	boolean buscando = false;

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
				if (path.contains("Bogot�")) {
					query = query + " and nivel=" + nivel;
				}
			} else {
				query = "select distinct edificio,nombre_edificio,latitud,longitud from edificios ";
				query += " where nivel=" + nivel;
			}
			Log.e("query mapa", query);
			Log.e("PATH", path);
			Cursor c = db.rawQuery(query, null);
			String[][] mat = Util.imprimirLista(c);
			c.close();
			db.close();
			if (mat.length == 0) {
				Toast.makeText(getApplicationContext(),
						"No hemos podido ubicar los edificios", 1).show();
				return;
			}
			lat = Util.toDouble(Util.getcolumn(mat, 2));
			lon = Util.toDouble(Util.getcolumn(mat, 3));
			titulos = Util.getcolumn(mat, 1);
			descripciones = Util.getcolumn(mat, 0);
			mapa.putExtra("lat", lat);
			mapa.putExtra("lon", lon);
			mapa.putExtra("titulos", titulos);
			mapa.putExtra("descripciones", descripciones);
			mapa.putExtra("nivel", current - 1);
			mapa.putExtra("zoom", current <= 2 ? current + 3 : current + 9);
			mapa.putExtra("tipo", 0);
			startActivity(mapa);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} catch (Exception ex) {
			Toast.makeText(getApplicationContext(), ex.toString(), 1).show();
		}
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
				if (query.length() > 3) {
					recargar(query);
				} else {
					/*
					current = 2;
					recargar("select  distinct " + columnas[2] + ", "
							+ columnas[2] + " from " + tableName
							+ " natural join edificios order by (orden)",
							false, false);
					*/
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (newText.length() > 3) {
					recargar(newText);
				} else {
					/*
					current = 2;
					recargar("select  distinct " + columnas[2] + ", "
							+ columnas[2] + " from " + tableName
							+ " natural join edificios order by (orden)",
							false, false);
					*/
				}
				return false;
			}
		});
		/*
		 * c.close(); db.close();
		 */
		sv.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public boolean onClose() {
				home();
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
		case R.id.ItemMapa:
			if (!buscando) {

				ubicar(null);
			}
			break;
		case R.id.ItemWEB:
			if (buscando) {
				String cad = "http://unal.edu.co/resultados-de-la-busqueda/?q="
						+ sv.getQuery();
				Util.irA(cad, this);
				break;
			}
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
		lv = (ListView) findViewById(R.id.listViewDirectorio);
		Space sp = (Space) findViewById(R.id.SpaceDirectorio);
		tr = (TableRow) findViewById(R.id.tableRowDirectorio);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		double factor = screenHeight / 2000.0 + 0.25;
		double factor2 = screenHeight / 2000.0 + 0.25;
		if (factor > 0.35) {
			factor = 0.35;
		}
		if (factor2 > 0.35) {
			factor2 = 0.35;
		}
		sp.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor))));
		lv.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				(int) (screenHeight * (factor2))));

		Bundle b = getIntent().getExtras();
		try {
			LinnaeusDatabase lb = new LinnaeusDatabase(getApplicationContext());
			SQLiteDatabase db = openOrCreateDatabase(MainActivity.dataBaseName,
					MODE_WORLD_READABLE, null);
			if (b.getBoolean("salto")) {
				current = b.getInt("current");
				String sede = b.getString("sede");
				condicion = "sede='" + sede + "'";
				sql = "select  distinct " + columnas[current] + " from "
						+ tableName + " where " + condicion
						+ " and NIVEL_ADMINISTRATIVO between 1 and 4";
				path = sede;
				animarFondo(sede, false);
			} else {
				sql = "select  distinct " + columnas[current] + " from "
						+ tableName;
			}
			Cursor c;
			if (b.getBoolean("salto")) {
				c = db.rawQuery(sql, null);
			} else {
				c = db.rawQuery(sql
						+ " natural join edificios order by (orden)", null);
			}
			final String[][] mat = Util.imprimirLista(c);
			c.close();
			db.close();
			MiAdaptador.tipo = 1;
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
						if (saltar(seleccion)) {
							irDirecto(seleccion);
							return;
						}
						if (seleccion.contains("Programas")) {
							irDirecto();
							return;
						}
						if (path == "") {
							path = seleccion;
							// item.setTitleCondensed(path.toUpperCase().trim());
						} else {
							path = path + ">" + seleccion.toUpperCase().trim();
							// item.setTitleCondensed(path);
						}
						// item.setTitleCondensed(path);
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
						recargar(sql, false, false);

					} catch (Exception e) {
						Toast.makeText(getApplication(), e.toString(),
								Toast.LENGTH_LONG).show();
					}

				}
			});
			tableName = MainActivity.tbName;
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
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
		if (cad.contains("Observatorio")) {
			id = R.drawable.oan_sede_h;
			idFondoTras = id;
		}
		if (cad.contains("Direcci�n Nacional")) {
			id = R.drawable.uriel;
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
		try {
			Intent deta = new Intent(this, DetailsActivity.class);
			ArrayList<String[]> datos = getDatos();
			deta.putExtra("datos", datos);
			deta.putExtra("fondo", idFondoTras);
			startActivity(deta);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			// this.finish();
			if (current == 5) {
				erase(condicion, false);
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}
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
		recargar(sql, false, false);
	}

	private void erase(String condicion2, boolean cond) {
		String conds[] = condicion2.split("and");
		String textos[] = path.split(">");

		if (conds.length == 1) {
			// item.setTitleCondensed("");
			path = "";
			condicion = "";
			sql = "select  distinct " + columnas[2] + ", " + columnas[2]
					+ " from " + tableName
					+ " natural join edificios order by (orden)";
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

	public void recargar(String cad2) {

		String cad = cad2.replaceAll("i", "_");
		cad = cad.replaceAll("a", "_");
		cad = cad.replaceAll("e", "_");
		cad = cad.replaceAll("o", "_");
		cad = cad.replaceAll("u", "_");
		Log.e("cadena", cad);
		sql = "select secciones, " + columnas[2] 
				+ ", secciones||extension as consulta from " + tableName
				+ " where consulta like('%" + cad
				+ "%') order by NIVEL_ADMINISTRATIVO ASC";
		Log.e("buscado", sql);
		current = 1;
		tr.setVisibility(View.INVISIBLE);
		recargar(sql, true, true);
		buscando = true;
		// animarFondo("", false);

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

	public void recargar(String query, final boolean cond, final boolean cond2) {

		try {

			lv.setAdapter(null);
			SQLiteDatabase db = openOrCreateDatabase(MainActivity.dataBaseName,
					MODE_WORLD_READABLE, null);
			Cursor c = db.rawQuery(query, null);
			if (current == 3) {
				c = db.rawQuery(query
						+ " and NIVEL_ADMINISTRATIVO between 1 and 4", null);
				if (tableName.equals("Base")) {
					c = db.rawQuery(query, null);
				}
			}

			final String[][] mat = Util.imprimirLista(c);
			c.close();
			db.close();
			MiAdaptador.tipo = 1;
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
					if (path.contains("INSTITUTOS ")) {
						irDirecto(seleccion);
						return;
					}
					if (current == 2
							|| seleccion.contains("Direcci�n Nacional")) {
						animarFondo(seleccion, true);
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
					// Toast.makeText(getApplication(), sql, Toast.LENGTH_LONG)
					// .show();
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
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
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
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
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

	public ArrayList<String[]> getDatos(String baseConsult, String criteria,
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
			/*
			 * datos[0] = mat[0][0];// departamento datos[1] = mat[0][1];//
			 * titulo datos[2] = mat[0][2];// telefono datos[3] = mat[0][3];//
			 * extension datos[4] = mat[0][4];// email datos[5] = mat[0][5];//
			 * edificio datos[6] = mat[0][6];// enlace datos[7] = mat[0][7];//
			 * descripcion datos[8] = mat[0][8] + " " + mat[0][9];// posiciones
			 */
		} catch (Exception e) {
			Log.e("Error Datos", e.toString());
			Toast.makeText(getApplication(), "Aun no tenemos los Datos",
					Toast.LENGTH_LONG).show();
		}
		return datos;
	}

	public ArrayList<String[]> getDatos(String criteria, boolean cond) {
		String consulta = "SELECT departamentos,secciones,directo,extension,correo_electronico,NOMBRE_EDIFICIO,url,piso_oficina, LATITUD,LONGITUD FROM "
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
			MiAdaptador.tipo = 1;
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
					if (seleccion.contains("Observatorio")) {
						animarFondo(seleccion, true);
					}
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
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}
	}
}
