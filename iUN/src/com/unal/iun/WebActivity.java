package com.unal.iun;

import com.unal.iun.LN.Util;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData.Item;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class WebActivity extends Activity {
	String URL = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_web);
		WebView wb1 = (WebView) findViewById(R.id.webView1);
		wb1.getSettings().setJavaScriptEnabled(true);
		wb1.getSettings().setBuiltInZoomControls(true);
		wb1.setWebViewClient(new WebViewClient() {
			// evita que los enlaces se abran fuera nuestra app en el navegador
			// de android
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

		});
		Bundle b = getIntent().getExtras();
		wb1.loadUrl(b.getString("paginaWeb"));
		URL = b.getString("paginaWeb");
		BitmapDrawable background = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.fondoinf));
		this.getActionBar().setBackgroundDrawable(background);
		if (!Util.isOnline(this)) {
			Util.notificarRed(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.web, menu);
		menu.getItem(3).setTitle(URL);
		menu.getItem(3).setTitleCondensed(URL);
		return super.onCreateOptionsMenu(menu);
	}

	public void volver(View v) {
		volver();
	}

	public void volver() {
		WebView browser = (WebView) findViewById(R.id.webView1);
		if (browser.canGoBack()) {
			browser.goBack();
		}
	}

	public void adelante() {
		WebView browser = (WebView) findViewById(R.id.webView1);
		if (browser.canGoForward()) {
			browser.goForward();
		}
	}

	public void recargar() {
		WebView browser = (WebView) findViewById(R.id.webView1);
		browser.reload();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ItemVolver:
			volver();
			return true;

		case R.id.ItemRefresh:
			recargar();
			return true;
		case R.id.ItemForward:
			adelante();
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

}
