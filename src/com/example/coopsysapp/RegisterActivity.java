package com.example.coopsysapp;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	public Button btnRegister;
	public EditText etRegister;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		btnRegister = (Button) findViewById(R.id.button1);
		etRegister = (EditText) findViewById(R.id.editText1);
		
		btnRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				  register task = new register(RegisterActivity.this, etRegister.getText().toString());
		          task.execute();				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class register extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog progress ;
		String name;
		int index=9999;

		public register(RegisterActivity activity, String name) {
			progress= new ProgressDialog(activity);
			this.name=name;
		}
		
		@Override
		protected void onPreExecute() {
			progress.setMessage("Registriere Benutzer " + name + ", bitte warten ...");
	        progress.show();
		}

	    @Override
	    protected void onProgressUpdate(Integer... values) {
	      
	    }

		@Override
		protected Void doInBackground(Void... params) {
					
			try {
				index = ServerConnector.register(name);
				Thread.sleep(2000);
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			       		return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			
	        
			if (progress.isShowing()) {
	        	progress.dismiss();
	        }
			
			Toast.makeText(getApplicationContext(), "index:" + index, Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
            startActivity(intent);      
            //finish();
			super.onPostExecute(result);
		}

	}



}
