package com.unal.iun;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TableRow;

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
	int current = 1;
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
	SQLiteDatabase db;

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

	public void setDataBase(LinnaeusDatabase lb, SQLiteDatabase db) {
		this.lb = lb;
		this.db = db;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		Cursor c = db.rawQuery(sql, null);
		final String[][] mat = Util.imprimirLista(c);
		c.close();
		ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, Util.getcolumn(mat, 0));
		setListAdapter(adapter);
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
		
		sql = "select distinct dependencias from BaseM where sede='Bogotá' ";
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
			// item.setTitleCondensed(path.toUpperCase().trim());
		} else {
			path = path + ">" + seleccion.toUpperCase().trim();
			// item.setTitleCondensed(path);
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
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		// mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
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
}
