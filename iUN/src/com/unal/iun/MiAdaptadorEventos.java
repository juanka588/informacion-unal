package com.unal.iun;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MiAdaptadorEventos extends BaseAdapter {
	private final Activity actividad;
	private final String[][] lista;
	public Typeface fuente;

	public MiAdaptadorEventos(Activity actividad, String[][] titulos) {
		super();
		this.actividad = actividad;
		this.lista = titulos;
	}

	public MiAdaptadorEventos(Activity actividad, ArrayList<String[]> titulos) {
		super();
		this.actividad = actividad;
		lista = new String[titulos.size()][titulos.get(0).length];
		for (int i = 0; i < lista.length; i++) {
			for (int j = 0; j < lista[i].length; j++) {
				lista[i][j]=titulos.get(i)[j];
			}
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = actividad.getLayoutInflater();
		View view = inflater.inflate(R.layout.elemento_lista_eventos, null,
				true);
		TextView textView = (TextView) view.findViewById(R.id.tituloEvento);
		if (lista[position][0] != null) {
			textView.setText(toCammelCase(lista[position][0].toLowerCase()));
			textView.setHint(toCammelCase(lista[position][0].toLowerCase()));
		}
		TextView textView2 = (TextView) view.findViewById(R.id.descripcionEvento);
		if (lista[position][1] != null) {
			textView2.setText(toCammelCase(lista[position][1].toLowerCase()));
			textView2.setHint(toCammelCase(lista[position][1].toLowerCase()));
		}
		TextView textView3 = (TextView) view.findViewById(R.id.lugarEvento);
		if (lista[position][2] != null) {
			textView3.setText(toCammelCase(lista[position][2].toLowerCase()));
			textView3.setHint(toCammelCase(lista[position][2].toLowerCase()));
		}
		TextView textView4 = (TextView) view.findViewById(R.id.fechaEvento);
		if (lista[position][3] != null) {
			textView4.setText(toCammelCase(lista[position][3].toLowerCase()));
			textView4.setHint(toCammelCase(lista[position][3].toLowerCase()));
		}
		TextView textView5 = (TextView) view.findViewById(R.id.emailEvento);
		if (lista[position][4] != null) {
			textView5.setText(toCammelCase(lista[position][4].toLowerCase()));
			textView5.setHint(toCammelCase(lista[position][4].toLowerCase()));
		}
		TextView textView6 = (TextView) view.findViewById(R.id.sitioWEBEvento);
		if (lista[position][5] != null) {
			textView6.setText(toCammelCase(lista[position][5].toLowerCase()));
			textView6.setHint(toCammelCase(lista[position][5].toLowerCase()));
		}
		TextView textView7 = (TextView) view.findViewById(R.id.telefonoEvento);
		if (lista[position][6] != null) {
			textView7.setText(toCammelCase(lista[position][6].toLowerCase()));
			textView7.setHint(toCammelCase(lista[position][6].toLowerCase()));
		}
		
		int ids[] = { R.id.tituloEvento, R.id.lugarEvento, R.id.fechaEvento,
				R.id.descripcionEvento, R.id.emailEvento, R.id.sitioWEBEvento,
				R.id.telefonoEvento };
		for (int i = 0; i < ids.length; i++) {
			TextView prueba = (TextView) view.findViewById(ids[i]);
			prueba.setTypeface(fuente);
		}
		return view;
	}

	private CharSequence toCammelCase(String lowerCase) {
		String cad = "";
		String[] palabras = lowerCase.split(" ");
		for (int i = 0; i < palabras.length; i++) {
			if (palabras[i].length() > 3) {
				palabras[i] = (palabras[i].charAt(0) + "").toUpperCase()
						+ palabras[i].substring(1, palabras[i].length());
			}
			cad += palabras[i] + " ";
		}
		return cad.trim();
	}

	public int getCount() {
		return lista.length;
	}

	public Object getItem(int arg0) {
		return lista[arg0];
	}

	public long getItemId(int position) {
		return position;
	}
}