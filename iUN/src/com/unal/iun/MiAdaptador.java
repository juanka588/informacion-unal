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

public class MiAdaptador extends BaseAdapter {
	private final Activity actividad;
	private final String[] lista;
	private final String[] lista2;
	public Typeface fuente;

	public MiAdaptador(Activity actividad, ArrayList<String> titulos,
			ArrayList<String> subtitulos) {
		super();
		this.actividad = actividad;
		lista = new String[titulos.size()];
		titulos.toArray(lista);
		lista2 = new String[subtitulos.size()];
		subtitulos.toArray(lista2);
	}

	public MiAdaptador(Activity actividad, String[] titulos, String[] subtitulos) {
		super();
		this.actividad = actividad;
		this.lista = titulos;
		this.lista2 = subtitulos;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = actividad.getLayoutInflater();
		View view = inflater.inflate(R.layout.elemento_lista, null, true);
		TextView textView = (TextView) view.findViewById(R.id.titulo);
		if (lista[position] != null) {
			textView.setText(toCammelCase(lista[position].toLowerCase()));
			textView.setHint(toCammelCase(lista[position].toLowerCase()));
			if (false && lista[position].length() > 20) {
				if (lista[position].length() > 35) {
					textView.setTextSize(12);
				} else {
					textView.setTextSize(15);
				}
			} else {
				textView.setTextSize(18);
			}
		}
		TextView textView2 = (TextView) view.findViewById(R.id.subtitulo);

		ImageView imageView = (ImageView) view.findViewById(R.id.icono);
		imageView.setImageResource(R.drawable.ic_launcher);
		int ids[] = { R.id.titulo, R.id.subtitulo };
		for (int i = 0; i < ids.length; i++) {
			TextView prueba = (TextView) view.findViewById(ids[i]);
			prueba.setTypeface(fuente);
		}
		if (lista2[position] != null) {
			textView2.setText(toCammelCase(lista2[position].toLowerCase()));
			if (lista2[position].contains("Bogo")) {
				imageView.setImageResource(R.drawable.ic_bogota);
			}
			if (lista2[position].contains("Amaz")) {
				imageView.setImageResource(R.drawable.ic_amazonas);
			}
			if (lista2[position].contains("Caribe")) {
				imageView.setImageResource(R.drawable.ic_caribe);
			}
			if (lista2[position].contains("Mani")) {
				imageView.setImageResource(R.drawable.ic_manizales);
			}
			if (lista2[position].contains("Mede")) {
				imageView.setImageResource(R.drawable.ic_medellin);
			}
			if (lista2[position].contains("Tumac")) {
				imageView.setImageResource(R.drawable.ic_tumaco);
			}
			if (lista2[position].contains("Palmira")) {
				imageView.setImageResource(R.drawable.ic_palmira);
			}
			if (lista2[position].contains("Orino")) {
				imageView.setImageResource(R.drawable.ic_orinoquia);
			}
			if (lista2[position].contains("Nacio")) {
				imageView.setImageResource(R.drawable.ic_nacional);
			}
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