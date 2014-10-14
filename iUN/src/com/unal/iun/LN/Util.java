package com.unal.iun.LN;

import java.util.ArrayList;
import java.util.Calendar;

import com.unal.iun.MainActivity;
import com.unal.iun.WebActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;

public class Util {
	public static Drawable resizeImage(Context ctx, int resId, int w, int h) {

		// cargamos la imagen de origen
		Bitmap BitmapOrg = BitmapFactory.decodeResource(ctx.getResources(),
				resId);

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		// calculamos el escalado de la imagen destino
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// para poder manipular la imagen
		// debemos crear una matriz

		Matrix matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);

		// volvemos a crear la imagen con los nuevos valores
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);

		// si queremos poder mostrar nuestra imagen tenemos que crear un
		// objeto drawable y as� asignarlo a un bot�n, imageview...
		return new BitmapDrawable(resizedBitmap);
	}

	public static boolean isOnline(Activity acc) {
		ConnectivityManager cm = (ConnectivityManager) acc
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		if (netInfo != null) {
			if (netInfo.isConnectedOrConnecting()) {
				return true;
			}
			return false;
		}

		return false;
	}

	public static double[] toDouble(String[] getcolumn) {
		double conv[] = new double[getcolumn.length];
		try {
			for (int i = 0; i < conv.length; i++) {
				conv[i] = Double.parseDouble(getcolumn[i]);
			}
		} catch (Exception e) {
			Log.e("Error Directorio", e.toString());
		}
		return conv;
	}

	public static String[] getcolumn(String[][] mat, int i) {
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

	public static String[][] imprimirLista(Cursor cursor) {

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

	public static String toString(String[][] mat) {
		String cad = "";
		for (int i = 0; i < mat.length; i++) {
			cad += toString(mat[i]) + "\n";
		}
		return cad;
	}

	public static String toString(String[] getcolumn) {
		String cad = "";
		for (int i = 0; i < getcolumn.length; i++) {
			cad += getcolumn[i] + " ";
		}
		return cad;
	}

	public static String toString(double[] getcolumn) {
		String cad = "";
		for (int i = 0; i < getcolumn.length; i++) {
			cad += getcolumn[i] + " ";
		}
		return cad;
	}

	public static void notificarRed(final Activity act) {
		AlertDialog.Builder builder = new AlertDialog.Builder(act);
		builder.setMessage("No tiene una conexion a la red")
				.setTitle("Informaci�n UNAL")
				.setPositiveButton("Aceptar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		builder.setNegativeButton("Cancelar", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (act.getClass() != MainActivity.class) {
					act.finish();
				}

			}
		});
		builder.create().show();
		/*
		 * AlertDialog.Builder dialog = new AlertDialog.Builder(
		 * getApplicationContext()); LayoutInflater inflater = (LayoutInflater)
		 * getApplicationContext
		 * ().getSystemService(Context.LAYOUT_INFLATER_SERVICE); LayoutInflater
		 * inflater = this.getLayoutInflater();
		 * dialog.setView(inflater.inflate(R.layout.dialogo_mapa, null))
		 * .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		 * public void onClick(DialogInterface dialog, int id) {
		 * dialog.cancel(); } }); dialog.create().show();
		 */
	}

	public static void addEventToCalendar(Activity activity, String date,
			String title, String description, String location) {
		Calendar cal = Calendar.getInstance();
		String cad[] = date.split("-");
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(cad[2]));
		cal.set(Calendar.MONTH, Integer.parseInt(cad[1]));
		cal.set(Calendar.YEAR, Integer.parseInt(cad[0]));

		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 45);

		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");

		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
				cal.getTimeInMillis());
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
				cal.getTimeInMillis() + 60 * 60 * 1000);

		intent.putExtra(Events.ALL_DAY, false);
		/*
		 * case DAILY: event.put("rrule", "FREQ=DAILY"); break; case MONTHLY:
		 * event.put("rrule", "FREQ=MONTHLY"); break; case WEEKLY:
		 * event.put("rrule", "FREQ=WEEKLY"); break; case FORTNIGHTLY:
		 * event.put("rrule", "FREQ=YEARLY"); //CODE for Fortnight to be
		 */
		intent.putExtra("rrule", "FREQ=DAILY;COUNT=1");
		intent.putExtra(Events.TITLE, title);
		intent.putExtra(Events.DESCRIPTION, description);
		intent.putExtra(Events.EVENT_LOCATION, location);

		activity.startActivity(intent);
	}

	public static void enviar(Activity act, String[] to, String[] cc,
			String asunto, String mensaje) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
		emailIntent.putExtra(Intent.EXTRA_CC, cc);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, asunto);
		emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
		emailIntent.setType("message/rfc822");
		act.startActivity(Intent.createChooser(emailIntent, "Email "));
	}

	public static void enviar(Activity act, String to, String cc,
			String asunto, String mensaje) {
		enviar(act, new String[] { to }, new String[] { cc }, asunto, mensaje);
	}

	public static void irA(String url, Activity act) {
		Intent deta = new Intent(act, WebActivity.class);
		if (url.contains("Sitio Web")) {
			return;
		}
		deta.putExtra("paginaWeb", url);
		act.startActivity(deta);
	}

	public static CharSequence toCammelCase(String lowerCase) {
		String cad = "";
		String[] palabras = lowerCase.split(" ");
		for (int i = 0; i < palabras.length; i++) {
			if (palabras[i].length() > 3) {
				palabras[i] = (palabras[i].charAt(0) + "").toUpperCase()
						+ palabras[i].substring(1, palabras[i].length());
			}
			if(palabras[i].contains("un")){
				palabras[i]="UN";
			}
			cad += palabras[i] + " ";
		}
		return cad.trim();
	}

	public static ArrayList<String> parseLine(ArrayList<String[]> datos) {
		ArrayList<String> salida = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		for (String[] arr : datos) {
			for (int i = 0; i < arr.length - 1; i++) {
				sb.append(arr[i]);
				sb.append(' ');
			}
			sb.append(arr[arr.length - 1]);
			salida.add(sb.toString());
			sb = new StringBuilder();
		}
		return salida;
	}

	public static ArrayList<String[]> toArray(ArrayList<String> datosLinea) {
		ArrayList<String[]> salida=new ArrayList<String[]>();
		for (String string : datosLinea) {
			String arr[]=string.split(" ");
			salida.add(arr);
		}
		return salida;
	}
}
