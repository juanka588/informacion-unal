package com.example.visorsqlite;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MiAdaptador extends BaseAdapter {
	private final Activity actividad;
	private final String[] lista;
	private final String[] lista2;

	public MiAdaptador(Activity actividad, ArrayList<String> titulos,ArrayList<String> subtitulos) {
		super();
		this.actividad = actividad;
		this.lista =(String[]) titulos.toArray();
		this.lista2=(String[])subtitulos.toArray();
	}
	public MiAdaptador(Activity actividad, String[] titulos,String[] subtitulos) {
		super();
		this.actividad = actividad;
		this.lista = titulos;
		this.lista2=subtitulos;
	}

	public View getView(int position, View convertView, 
                                     ViewGroup parent) {
          LayoutInflater inflater = actividad.getLayoutInflater();
          View view = inflater.inflate(R.layout.elemento_lista, null,true);
          TextView textView =(TextView)view.findViewById(R.id.titulo);
          textView.setText(lista[position]);
          TextView textView2 =(TextView)view.findViewById(R.id.subtitulo);
          textView2.setText(lista2[position]);
          ImageView imageView=(ImageView)view.findViewById(R.id.icono);
          switch (Math.round((float)Math.random()*3)){
          case 0:
                 imageView.setImageResource(R.drawable.ic_launcher);
                 break;
          case 1:
                 imageView.setImageResource(R.drawable.ic_launcher);
                 break;
          default:
                 imageView.setImageResource(R.drawable.ic_launcher);
                 break;
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