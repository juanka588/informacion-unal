package com.example.visorsqlite;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.security.KeyChain;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	String seleccion = "";
	String condicion = "";
	String tableName = "Base";
	String sql = "";
	int current = 1;
	String columnas[] = { "codigo", "SEDE", "DEPENDENCIAS", "DIVISIONES",
			"NIVEL_ADMINISTRATIVO", "DEPARTAMENTOS", "SECCIONES",
			"CORREO_ELECTRONICO", "EXTENSION", "FAX", "DIRECTO",
			"LUGAR_GEOGRAFICO", "EDIFICIO", "PISO_OFICINA", "CLASIFICACION" };
	public static String dataBaseName = "DataStore.sqlite";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			/*
			 * final ArrayList<String> textos = new ArrayList<String>();
			 * textos.add("Hola"); textos.add("soy"); textos.add("juan");
			 * textos.add("camilo");
			 */
			ListView lv = (ListView) findViewById(R.id.listView1);
			LinnaeusDatabase lb = new LinnaeusDatabase(getApplicationContext());
			SQLiteDatabase db = openOrCreateDatabase("DataStore.sqlite",
					MODE_WORLD_READABLE, null);
			Cursor c = db.rawQuery("select  distinct " + columnas[current]
					+ " from " + tableName + " where " + columnas[current]
					+ " !='' ", null);
			final String[][] mat = imprimirLista(c);
			c.close();
		/*	Toast.makeText(getApplicationContext(), c.getCount() + "", 1)
					.show();*/
			db.close();
			lv.setAdapter(new MiAdaptador(this, getcolumn(mat, 0), getcolumn(
					mat, 0)));

			// lv.setAdapter(new
			// ArrayAdapter<String>(this,android.R.layout.expandable_list_content,
			// textos));
			sql = "select  distinct " + columnas[current] + " from "
					+ tableName + " where" + columnas[current] + " !='' ";
			lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View vista,
						int posicion, long arg3) {
					try {
						/*Toast.makeText(
								getApplication(),
								"Click en: " + (posicion + 1) + " - "
										+ mat[posicion][0], Toast.LENGTH_LONG)
								.show();*/

						seleccion = mat[posicion][0];
						current++;
						if (condicion.equals("")) {
							condicion = columnas[current - 1] + " = '"
									+ seleccion + "'";
						} else {
							condicion += " and " + columnas[current - 1]
									+ " = '" + seleccion + "'";
						}

						sql = "select  distinct " + columnas[current]
								+ " from " + tableName + "  where " + condicion;
						recargar();
						Toast.makeText(getApplication(), sql, Toast.LENGTH_LONG)
								.show();

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

	public void detalles(View v) {
		startActivity(new Intent(this, DetailsActivity.class));
	}

	@Override
	public void onBackPressed() {
		Toast.makeText(getApplicationContext(), "DOBLE", 1).show();
		current--;
		condicion = "";
		recargar();
		if (current < 1) {
			super.onBackPressed();
		}
	}

	public void recargar(View v) {
		EditText texto = (EditText) findViewById(R.id.editText1);
		sql = "select secciones from " + tableName + " where secciones like('%"
				+ texto.getText().toString() + "%')";
		recargar();
	}

	public void recargar() {
		try {
			ListView lv = (ListView) findViewById(R.id.listView1);
			lv.setAdapter(null);
			SQLiteDatabase db = openOrCreateDatabase("DataStore.sqlite",
					MODE_WORLD_READABLE, null);
			Cursor c = db.rawQuery(sql, null);
			final String[][] mat = imprimirLista(c);
			c.close();
			db.close();
			lv.setAdapter(new MiAdaptador(this, getcolumn(mat, 0), getcolumn(
					mat, 0)));
			lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View vista,
						int posicion, long arg3) {
					try {
					/*	Toast.makeText(
								getApplication(),
								"Click en: " + (posicion + 1) + " - "
										+ mat[posicion][0], Toast.LENGTH_LONG)
								.show();*/
					} catch (Exception e) {
						Toast.makeText(getApplication(), e.toString(),
								Toast.LENGTH_LONG).show();
					}
					seleccion = mat[posicion][0];
					if(current==3){
						current=5;
					}else if(current==6){
						//startActivity(new Intent(this, DetailsActivity.class));
						return;
					}else{
					current++;
					if (condicion.equals("")) {
						condicion = columnas[current - 1] + " = '" + seleccion
								+ "'";
					} else {
						condicion += " and " + columnas[current - 1] + " = '"
								+ seleccion + "'";
					}
					sql = "select  distinct " + columnas[current] + " from "
							+ tableName + "  where " + condicion;
					Toast.makeText(getApplication(), sql, Toast.LENGTH_LONG)
							.show();
					recargar();
				}
					}
			});

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}
	}

	private String[] getcolumn(String[][] mat, int i) {
		String cad[] = new String[mat.length];
		int k = 0;
		for (int j = 0; j < mat.length; j++) {
			for (int j2 = 0; j2 < mat[j].length; j2++) {
				if (j2 == i) {
					cad[k] = mat[j][j2];
					k++;
				}
			}
		}
		return cad;

	}

	public String[][] imprimirLista(Cursor cursor) {

		String[][] lista = new String[cursor.getCount()][cursor
				.getColumnCount()];
		if (cursor.moveToFirst()) {
			for (int i = 0; i < cursor.getCount(); i++) {
				for (int j = 0; j < cursor.getColumnCount(); j++) {
					lista[i][j] = cursor.getString(j);
				}
				cursor.moveToNext();
			}
		}
		return lista;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
