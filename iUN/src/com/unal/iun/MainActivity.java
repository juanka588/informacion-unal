package com.unal.iun;

import com.unal.iun.LN.LinnaeusDatabase;
import com.unal.iun.LN.MiLocationListener;
import com.unal.iun.LN.Timer;
import com.unal.iun.LN.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static String dataBaseName = "datastore.sqlite", tbName = "BaseM";
	public Timer tim;
	public static String sede = "Bogotá";
	ImageView im;
	int screenWidth;
	int screenHeight;

	@Override
	public void onBackPressed() {
		this.finish();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		if (savedInstanceState != null) {
			sede = savedInstanceState.getString("sede");
		}
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		/*
		 * Space sp = (Space) findViewById(R.id.SpaceMain);
		 * 
		 * double factor = screenHeight / 2000.0 + 0.34; if (factor > 0.70) {
		 * factor = 0.70; }
		 * 
		 * sp.setLayoutParams(new LinearLayout.LayoutParams(
		 * LinearLayout.LayoutParams.MATCH_PARENT, (int) (screenHeight *
		 * (factor)))); tim = new Timer((Button)
		 * findViewById(R.id.nacionalButton), 30); Handler hand = tim.puente;
		 * Thread hilo = new Thread(tim); hilo.start();
		 */
		Typeface fuente = Typeface
				.createFromAsset(getAssets(), "Helvetica.ttf");
		int ids[] = { R.id.SOnlineButton, R.id.admisionesButton,
				R.id.sedesButton, R.id.textLatitud, R.id.textLongitud,
				R.id.textLugar };
		for (int i = 0; i < ids.length; i++) {
			TextView prueba = (TextView) findViewById(ids[i]);
			prueba.setTypeface(fuente);
			if (ids[i] == R.id.textSede) {
				prueba.setText(sede);
			}
		}
		iniciarLocalService();
		if (!Util.isOnline(this)) {
			Util.notificarRed(this);
		}
		// addShortcut();
	}

	protected void cambiarBD() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage("¿Que Base de Datos Desea usar?")
				.setTitle("Confirme:")
				.setPositiveButton("Modo Básico",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								tbName = "BaseM";
								dialog.cancel();
							}
						})
				.setNegativeButton("Modo Experto",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								tbName = "Base";
								dialog.cancel();
							}
						});
		builder.create().show();
	}

	public void comentar(View v) {
		Util.enviar(
				this,
				"mahiguerag@unal.edu.co",
				"",
				"Comentarios Aplicacion iUN Android",
				"iUN es una aplicacción de apoyo, "
						+ "en tal razón la información contenida "
						+ "en ella es solo de referencia. \n\n\n "
						+ "Envía tus comentarios acerca de la aplicación iUN"
						+ " respondiendo las siguientes preguntas.\n\n\n "
						+ "A. Nombres y Apellidos. \n\n\n B. Estudiante, Docente, "
						+ "Administrativo de la Universidad Nacional de Colombia o "
						+ "persona externa? \n\n\n 1 "
						+ "¿Cual fue la primera acción que ejecutó? \n\n\n 2 "
						+ "¿Identifica claramente las acciones de los botones e iconos?"
						+ " \n\n\n 3. Si hubo un bloqueo, ¿Cual fue, si recuerda, "
						+ "la acción que causo esto?  \n\n\n 4 "
						+ "¿ Impresión general sobre la aplicación? "
						+ "\n\n\n Gracias por su colaboración.\n "
						+ "Equipo de Desarrollo iUN.");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		sede = savedInstanceState.getString("sede");
		TextView tx = (TextView) findViewById(R.id.textSede);
		tx.setText(sede);
		im = (ImageView) findViewById(R.id.imageUNPrincipal);
		im.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				cambiarBD();
				return false;
			}
		});
		super.onRestoreInstanceState(savedInstanceState);
	}

	public void eventos(View v) {
		Util.irA("http://circular.unal.edu.co/nc/eventos-3.html", this);
	}

	public void admisiones(View v) {
		final String[] items = { "Instituciones", "Información" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final Activity act = this;
		builder.setTitle("Admisiones").setItems(items,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) {
							try {
								Intent ca = new Intent(act,
										InstitucionesActivity.class);
								startActivity(ca);
								overridePendingTransition(R.anim.fade_in,
										R.anim.fade_out);
							} catch (Exception e) {
								Toast.makeText(getApplicationContext(),
										"Disponible proximamente", 1).show();
							}
						} else {
							String cad = "http://admisiones.unal.edu.co/";
							Util.irA(cad, act);
						}
					}
				});

		builder.setNegativeButton("Cancelar", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}

	private void iniciarLocalService() {
		LocationManager milocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener milocListener = new MiLocationListener();
		MiLocationListener.appcont = this.getApplicationContext();
		MiLocationListener.textLat = (TextView) findViewById(R.id.textLatitud);
		MiLocationListener.textLon = (TextView) findViewById(R.id.textLongitud);
		MiLocationListener.textSede = (TextView) findViewById(R.id.textSede);
		LinnaeusDatabase lb = new LinnaeusDatabase(getApplicationContext());
		MiLocationListener.db = openOrCreateDatabase(dataBaseName,
				MODE_WORLD_READABLE, null);
		try {

			milocManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, milocListener);
		} catch (Exception e) {

		} finally {
			milocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					0, 0, milocListener);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("sede", sede);
		super.onSaveInstanceState(outState);
	}

	public void directorio(View v) {
		directorio(false);
	}

	public void irSede(View v) {
		directorio(true);
	}

	private void directorio(boolean cond) {
		try {
			Intent directorio = new Intent(getApplicationContext(),
					DirectorioActivity.class);
			if (cond) {
				directorio.putExtra("current", 3);
				directorio.putExtra("sede", sede);
			}
			directorio.putExtra("salto", cond);
			startActivity(directorio);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			// this.finish();
		} catch (Exception e) {
			Log.e("error", e.toString());
		}

	}

	public void servicios(View v) {
		try {
			Class cls;
			if (screenWidth > 600 && false) {
				cls = ServiciosActivity.class;
			} else {
				cls = MenuWEBActivity.class;
			}
			startActivity(new Intent(getApplicationContext(), cls));
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			// this.finish();
		} catch (Exception e) {
			Log.e("error", e.toString());
		}
	}

	private void addShortcut() {
		// Creamos el Intent y apuntamos a nuestra classe principal
		// al hacer click al acceso directo
		// En este caso de ejemplo se llama "Principal"
		Intent shortcutIntent = new Intent(getApplicationContext(),
				MainActivity.class);
		// Añadimos accion
		shortcutIntent.setAction(Intent.ACTION_MAIN);
		// Recogemos el texto des de nuestros Values
		CharSequence contentTitle = getString(R.string.app_name);
		// Creamos intent para crear acceso directo
		Intent addIntent = new Intent();
		// Añadimos los Extras necesarios como nombre del icono y icono
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, contentTitle.toString());
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(
						getApplicationContext(), R.drawable.icono_app));
		// IMPORTATE: si el icono ya esta creado que no cree otro
		addIntent.putExtra("duplicate", false);
		// Llamamos a la acción
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// Enviamos petición
		getApplicationContext().sendBroadcast(addIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
