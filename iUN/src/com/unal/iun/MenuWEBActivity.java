package com.unal.iun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Space;
import android.widget.TextView;

public class MenuWEBActivity extends Activity {
	SearchView sv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_menu_web);
		ScrollView sc = (ScrollView) findViewById(R.id.scrollViewMenuWeb);
		Space sp = (Space) findViewById(R.id.spaceMenuWebGeneral);
		BitmapDrawable background2 = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.fondoinf));
		this.getActionBar().setBackgroundDrawable(background2);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		double factor = screenHeight / 2000.0 + 0.35;
		double factor2 = 3.0 * screenHeight / 20000.0 + 0.09;
		if (factor > 0.65) {
			factor = 0.65;
		}
		if (factor2 > 0.2) {
			factor2 = 0.2;
		}
		sp.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor2))));
		sc.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (screenHeight * (factor))));
		Log.e("factor 1", factor + " " + (int) (screenHeight * (factor)));
		Log.e("factor 2", factor2 + " " + (int) (screenHeight * (factor2)));
		int ids[] = new int[] { R.id.spaceMenuWeb01, R.id.spaceMenuWeb02,
				R.id.spaceMenuWeb03, R.id.spaceMenuWeb10, R.id.spaceMenuWeb11,
				R.id.spaceMenuWeb12, R.id.spaceMenuWeb20, R.id.spaceMenuWeb21,
				R.id.spaceMenuWeb22, R.id.spaceMenuWeb30, R.id.spaceMenuWeb31,
				R.id.spaceMenuWeb32, R.id.spaceMenuWeb40, R.id.spaceMenuWeb41,
				R.id.spaceMenuWeb42, R.id.spaceMenuWeb50, R.id.spaceMenuWeb51,
				R.id.spaceMenuWeb52, R.id.spaceMenuWeb60, R.id.spaceMenuWeb61,
				R.id.spaceMenuWeb62, R.id.spaceMenuWeb70, R.id.spaceMenuWeb71,
				R.id.spaceMenuWeb72, R.id.spaceMenuWeb110,
				R.id.spaceMenuWeb111, R.id.spaceMenuWeb112,
				R.id.spaceMenuWeb120, R.id.spaceMenuWeb121,
				R.id.spaceMenuWeb122, R.id.spaceMenuWeb130,
				R.id.spaceMenuWeb131, R.id.spaceMenuWeb132,
				R.id.spaceMenuWeb140, R.id.spaceMenuWeb141,
				R.id.spaceMenuWeb142, R.id.spaceMenuWeb150,
				R.id.spaceMenuWeb151, R.id.spaceMenuWeb160,
				R.id.spaceMenuWeb161 };
		Log.e("ancho de pantalla",screenWidth+"");
		for (int i = 0; i < ids.length; i++) {
			Space im = (Space) findViewById(ids[i]);
			ViewGroup.LayoutParams iv_params_b = im.getLayoutParams();
			iv_params_b.height = 0;			
			iv_params_b.width = (int) ((screenWidth) * (0.04+screenWidth*3/10000));
			im.setLayoutParams(iv_params_b);
		}
		int ids2[] = new int[] { R.id.textTituloMenuWeb, R.id.textMP,
				R.id.textUNradio, R.id.textPrisma, R.id.textLeon,
				R.id.textAnimales, R.id.textSicologia, R.id.textJuridico,
				R.id.textLibreria, R.id.textComunidad, R.id.textCorreo,
				R.id.textComedor, R.id.textMedico, R.id.textTituloMenuWeb,
				R.id.textUNIsalud, R.id.textBB, R.id.textServiciosACOM,
				R.id.textSIA, R.id.textSINAB, R.id.textHermes, R.id.textDRE,
				R.id.textSARA, R.id.textWD };
		Typeface fuente = Typeface
				.createFromAsset(getAssets(), "Helvetica.ttf");
		for (int i = 0; i < ids2.length; i++) {
			TextView tx = (TextView) findViewById(ids2[i]);
			tx.setTypeface(fuente);
		}
		
		

	}

	public void navegar(String cad) {
		Intent deta = new Intent(this, WebActivity.class);
		deta.putExtra("paginaWeb", cad);
		startActivity(deta);
	}

	@Override
	public void onBackPressed() {
		home();
		super.onBackPressed();
	}

	public void home(View v) {
		home();
	}

	public void home() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		this.finish();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_web, menu);
		MenuItem menuItem = menu.getItem(0);
		sv = (SearchView) menuItem.getActionView();
		sv.setQueryHint("Inicia una Busqueda...");
		sv.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				String cad = "http://unal.edu.co/resultados-de-la-busqueda/?q="
						+ arg0;
				navegar(cad);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		sv.setSoundEffectsEnabled(true);
		return super.onCreateOptionsMenu(menu);
	}

	public void irA(View v) {

		String cad = "";
		switch (v.getId()) {
		case R.id.imageSIA:
			cad = "http://www.sia.unal.edu.co/";
			break;
		case R.id.imageSINAB:
			cad = "http://www.sinab.unal.edu.co/";
			break;
		case R.id.imageDRE:
			cad = "http://www.ori.unal.edu.co/";
			break;
		case R.id.imageSARA:
			cad = "http://www.sara.unal.edu.co:8082/saraweb/";
			break;
		case R.id.imagePrisma:
			cad = "http://livestream.com/prisma_tv";
			break;
		case R.id.imageUNradio:
			cad = "http://www.unradio.unal.edu.co/nc/en-linea/web.html";
			break;
		case R.id.imageBB:
			cad = "http://www.bb.unal.edu.co/";
			break;
		case R.id.imageWD:
			cad = "http://www.docentes.unal.edu.co/";
			break;
		case R.id.imageHermes:
			cad = "http://www.hermes.unal.edu.co/";
			break;
		case R.id.imageUNPrincipal:
			cad = "http://www.unal.edu.co/";
			break;
		case R.id.imageCorreo:
			cad = "https://login.unal.edu.co/cloudkey/a/unal.edu.co/user/login?namespace=unal.edu.co";
			break;
		case R.id.imageAnimales:
			cad = "http://www.veterinaria.unal.edu.co/ex_clinica_peq.html";
			break;
		case R.id.imageSicologia:
			cad = "http://www.humanas.unal.edu.co/sap/";
			break;
		case R.id.imageJuridico:
			cad = "http://www.bogota.unal.edu.co/estructura/juridica/";
			break;
		case R.id.imageLeonG:
			cad = "http://www.bogota.unal.edu.co/estructura/juridica/";
			break;
		case R.id.imageLibreria:
			cad = "http://www.bogota.unal.edu.co/estructura/juridica/";
			break;
		case R.id.imageComedor:
			cad = "http://www.bogota.unal.edu.co/estructura/juridica/";
			break;
		case R.id.imageMedico:
			cad = "http://www.bogota.unal.edu.co/estructura/juridica/";
			break;
		case R.id.textUNIsalud:
			cad = "http://www.bogota.unal.edu.co/estructura/juridica/";
		default:
			break;
		}
		navegar(cad);

	}
}
