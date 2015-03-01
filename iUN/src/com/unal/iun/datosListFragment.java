package com.unal.iun;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.Toast;

import com.unal.iun.LN.LinnaeusDatabase;
import com.unal.iun.LN.MiAdaptador;
import com.unal.iun.LN.Util;

/**
 * A list fragment representing a list of datos. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link datosDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class datosListFragment extends ListFragment {

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
	String columnas[] = { "codigo", "NIVEL_ADMINISTRATIVO", "SEDE",
			"DEPENDENCIAS", "DIVISIONES", "DEPARTAMENTOS", "SECCIONES",
			"CORREO_ELECTRONICO", "EXTENSION", "FAX", "DIRECTO",
			"LUGAR_GEOGRAFICO", "EDIFICIO", "PISO_OFICINA", "CLASIFICACION" };
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	LinnaeusDatabase lb;
	private static boolean mTwoPane = false;
	public SQLiteDatabase db;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public datosListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = getActivity().openOrCreateDatabase(MainActivity.dataBaseName,
				Context.MODE_WORLD_READABLE, null);
		lb = new LinnaeusDatabase(getActivity().getApplicationContext());
		/*
		 * TODO: replace with a real list adapter. setListAdapter(new
		 * ArrayAdapter<DummyContent.DummyItem>(getActivity(),
		 * android.R.layout.simple_list_item_activated_1, android.R.id.text1,
		 * DummyContent.ITEMS));
		 */
	}

	@Override
	public void onDestroy() {
		db.close();
		super.onDestroy();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		sql = "select distinct sede from BaseM ";
		if (db != null) {
			Cursor c = db.rawQuery(sql, null);
			final String[][] mat = Util.imprimirLista(c);
			c.close();
			ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, Util.getcolumn(mat, 0));
			setListAdapter(adapter);
		}
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int posicion,
			long id) {
		if(current>=5){
			return;
		}
		Cursor c = db.rawQuery(sql, null);
		final String[][] mat = Util.imprimirLista(c);
		c.close();
		ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, Util.getcolumn(mat, 0));
		setListAdapter(adapter);
		seleccion = mat[posicion][0];
		if (path == "") {
			path = seleccion;
		} else {
			path = path + ">" + seleccion.toUpperCase().trim();
		}
		current++;
		int resta = 1;
		if (condicion.equals("")) {
			condicion = columnas[current - resta] + " = '" + seleccion + "'";

		} else {
			condicion += " and " + columnas[current - resta] + " = '"
					+ seleccion + "'";
		}
		sql = "select  distinct " + columnas[current] + ", " + columnas[2]
				+ " from " + tableName + "  where " + condicion;
		if (current == 5) {
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
						.replace(R.id.datos_detail_container, fragment)
						.commit();

			} else {
				// In single-pane mode, simply start the detail activity
				// for the selected item ID.
				Intent detailIntent = new Intent(getActivity(),
						datosDetailActivity.class);
				detailIntent.putStringArrayListExtra("datos", datosLinea);
				startActivity(detailIntent);
			}
		}else{
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		// mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
		c = db.rawQuery(sql, null);
		String[][] mat2 = Util.imprimirLista(c);
		c.close();
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, Util.getcolumn(mat2, 0));
		setListAdapter(adapter);
		}
		super.onListItemClick(listView, view, posicion, id);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	private ArrayList<String[]> getDatos(String criteria, boolean cond) {
		String consulta = "SELECT departamentos,secciones,directo,extension,correo_electronico,NOMBRE_EDIFICIO,url,piso_oficina, LATITUD,LONGITUD FROM "
				+ tableName
				+ " natural join "
				+ tableName2
				+ " natural join "
				+ tableName3 + " where ";
		return getDatos(consulta, criteria, cond);
	}

	private ArrayList<String[]> getDatos(String baseConsult, String criteria,
			boolean cond) {
		String consulta = baseConsult + criteria;
		ArrayList<String[]> datos = new ArrayList<String[]>();
		Log.e("SQL ORIGINAL", consulta);
		if (cond) {
			consulta = sql;
		}

		Log.e("consulta", consulta);

		Cursor c = db.rawQuery(consulta, null);
		String[][] mat = Util.imprimirLista(c);
		c.close();
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
				//Log.e("los datos " + i, Util.toString(arr));
			}
		} catch (Exception e) {
			Log.e("Error Datos", e.toString());
			Toast.makeText(getActivity(), "Aun no tenemos los Datos",
					Toast.LENGTH_LONG).show();
		}
		return datos;
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

	public void setTwoPane(boolean mTwoPane) {
		this.mTwoPane = mTwoPane;

	}

}
