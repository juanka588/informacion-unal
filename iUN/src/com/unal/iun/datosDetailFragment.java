package com.unal.iun;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import com.unal.iun.LN.MiAdaptadorExpandible;
import com.unal.iun.LN.Util;

/**
 * A fragment representing a single datos detail screen. This fragment is either
 * contained in a {@link datosListActivity} in two-pane mode (on tablets) or a
 * {@link datosDetailActivity} on handsets.
 */
public class datosDetailFragment extends Fragment {
	public static double lat[];
	public static double lon[];
	public static String titulos[], descripciones[];
	LinearLayout tl;
	boolean evento = false;
	static Display display;
	ArrayList<String[]> data = new ArrayList<String[]>();

	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private ArrayList<String> datosLinea;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public datosDetailFragment() {
	}

	public void setDisplay(Activity act) {
		display = ((WindowManager) act.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
	}

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		datosLinea = getArguments().getStringArrayList("datos");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle b) {
		View rootView = inflater.inflate(R.layout.fragment_datos_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		tl = (LinearLayout) rootView
				.findViewById(R.id.linearLayoutDatosDetalles);

		ExpandableListView sc = (ExpandableListView) rootView
				.findViewById(R.id.expandableListDatosDestails);
		Space sp = (Space) rootView.findViewById(R.id.spaceDatosDetalles1);
		Space sp2 = (Space) rootView.findViewById(R.id.spaceDatosDetalles2);

		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		double factor = screenHeight / 2000.0 + 0.25;
		double factor2 = 3.0 * screenHeight / 20000.0 + 0.09;
		if (factor > 0.65) {
			factor = 0.65;
		}
		if (factor2 > 0.2) {
			factor2 = 0.2;
		}
		sp.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor2 / 2.0))));
		sp2.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor2 / 2.0))));
		sc.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor))));
		try {
			data = Util.toArray(datosLinea);
			TextView tx = (TextView) rootView
					.findViewById(R.id.tituloDatosDetallesDtos);
			tx.setText(data.get(0)[0].trim());

			int id = R.drawable.fondo2;
			if (b.getInt("fondo") != 0) {
				id = b.getInt("fondo");
			}
			Drawable background = new BitmapDrawable(
					BitmapFactory.decodeResource(getResources(), id));
			tl.setBackgroundDrawable(background);
			sc.setDividerHeight(2);
			// sc.setGroupIndicator(null);
			sc.setClickable(true);
			ArrayList<String> parentItems = new ArrayList<String>();
			ArrayList<Object> childItems = new ArrayList<Object>();
			for (int i = 0; i < data.size(); i++) {
				parentItems.add(data.get(i)[1].trim());

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
						// child.add("");
					} else {
						if (i == datos.length - 1) {
							titulos[k] = datos[5];
							descripciones[k] = datos[6];
							lat[k] = Double.parseDouble(datos[8].split(" ")[0]);
							lon[k] = Double.parseDouble(datos[8].split(" ")[1]);
						} else {
							if (datos[i].trim().length() > 4) {
								child.add(datos[i]);
							}
						}
					}
				}
				childItems.add(child);
			}

			MiAdaptadorExpandible adapter = new MiAdaptadorExpandible(
					parentItems, childItems);
			adapter.setInflater(inflater, new datosDetailActivity());
			sc.setAdapter(adapter);
			sc.expandGroup(0);
			// sc.setOnChildClickListener(this);
		} catch (Exception e) {
			Log.e("error de Detalles ", e.toString());
		}

		return rootView;
	}
}
