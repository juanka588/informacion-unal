package com.unal.iun;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class LicenseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_license);
		BitmapDrawable background2 = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.fondoinf));
		this.getActionBar().setBackgroundDrawable(background2);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setHomeButtonEnabled(true);
		TextView tx1 = (TextView) findViewById(R.id.textDevelop);
		TextView tx2 = (TextView) findViewById(R.id.textLicense);
		tx1.setText("DESAROLLADORES:\n\nmahiguerag\nafgranadosc\nmechaconc\n"
				+ "jcrodriguezd\ngromerop\nmrinconl\naframosp\n"
				+ "gmcabanah\njajaramilloa" + "\nmjangaritar"
				+ "\n\n @unal.edu.co");
		tx2.setText("LICENCIA DE USO"
				+ "\naplicaci�n m�vil iUN"
				+ "\n\nA- Reglas de uso:\n\n"
				+ "A1- Usted estar� autorizado a utilizar la aplicaci�n"
				+ " m�vil iUN s�lo para uso personal, no comercial.\n\n"
				+ "A2- Puede utilizar iUN para explorar, buscar y/o navegar "
				+ "contenidos en su dispositivo m�vil. Tenga en cuenta que la "
				+ "disponibilidad del contenido puede variar con base en "
				+ "actualizaciones de los datos del directorio o enlaces de los sitios web.\n\n"
				+ "B- Limitaci�n de responsabilidad\n\n"
				+ "B1-. iUN, suministrar� servicios con el cuidado y "
				+ "profesionalismo razonable. iUN no asume responsabilidades y garant�as"
				+ " adicionales en relaci�n con el Servicio, "
				+ "en particular, no garantiza que:\n\n"
				+ "-  El uso que usted haga del Servicio  se mantenga ininterrumpido o"
				+ " libre de errores. Usted acepta que ocasionalmente, iUN pueda "
				+ "dejar de funcionar por limitaciones de las redes Wi-Fi, o de datos.\n\n"
				+ "- El Servicio estar� exento de p�rdidas, corrupci�n, ataques, "
				+ "virus, interferencia o cualquier intromisi�n en la seguridad, "
				+ "los cuales ser�n considerados como eventos de Fuerza Mayor, "
				+ "y sobre los cuales iUN no ser� responsable. \n\n"
				+ "B2- El equipo de desarrollo iUNDev har� esfuerzos "
				+ "razonables por validar, actualizar y proteger la "
				+ "informaci�n registrada en la aplicaci�n iUN, pero usted "
				+ "reconoce y acepta que dicha informaci�n es de referencia"
				+ " y debe ser siempre validada por usted.");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.license, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			home();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void home() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		this.finish();
	}
}
