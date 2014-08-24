package com.example.visorsqlite;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class DetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			Toast.makeText(getApplicationContext(),"Estas intentando Salir",1).show();
		}
		return super.onKeyDown(keyCode, event);
	}
	public void llamar(View v){
	    Uri numero = Uri.parse( "tel: 3142358852"  );
	    Intent intent = new Intent(Intent.ACTION_CALL, numero);
	    startActivity(intent);   
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}

}
