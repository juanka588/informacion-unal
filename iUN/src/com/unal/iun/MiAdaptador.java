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
		TextView textView2 = (TextView) view.findViewById(R.id.subtitulo);
		ImageView imageView = (ImageView) view.findViewById(R.id.icono);

		if (lista[position] != null) {
			textView.setText(Util.toCammelCase(lista[position].toLowerCase()));
			textView.setHint(Util.toCammelCase(lista[position].toLowerCase()));
		/*	if (lista[position].contains("Museo")) {
				textView.setText("\t Museo Paleontologico");

			}
			if (lista[position].contains("Franco")) {
				textView.setText("\t Estacion Tropical Roberto Franco");

			}*/
			textView.setTextSize(18);

		}
		imageView.setImageResource(R.drawable.ic_launcher);
		int ids[] = { R.id.titulo, R.id.subtitulo };
		for (int i = 0; i < ids.length; i++) {
			TextView prueba = (TextView) view.findViewById(ids[i]);
			prueba.setTypeface(fuente);
		}
		if (lista2[position] != null) {
			textView2.setText(Util.toCammelCase(lista2[position].toLowerCase()));
			if (lista2[position].contains("useo")
					|| lista2[position].contains("ranco")
					|| lista[position].contains("Bogotá")
					|| lista[position].contains("Amaz")
					|| lista[position].contains("Caribe")
					|| lista[position].contains("Mani")
					|| lista[position].contains("Mede")
					|| lista[position].contains("Tumac")
					|| lista[position].contains("Palmira")
					|| lista[position].contains("Orino")) {
				textView2.setText("");

			}
			if (lista2[position].contains("Bogo")) {
				imageView.setImageResource(R.drawable.ic_bogota);
			}
			if (lista2[position].contains("Amaz")) {
				imageView.setImageResource(R.drawable.ic_amazonia);
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
				imageView.setImageResource(R.drawable.ic_oriniquia);
			}
			imageView.setImageResource(R.drawable.contact);
			textView2.setText("");
		}
		return view;
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