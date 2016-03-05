package com.example.coopsysapp;

import java.io.IOException;

import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.exception.UserAlreadyExistsException;
import com.example.coopsysapp.util.Dialogs;

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
		getActionBar().hide();
		btnRegister = (Button) findViewById(R.id.button1);
		etRegister = (EditText) findViewById(R.id.editText1);
		
		btnRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (etRegister.getText().toString().matches("")) {
					etRegister.setError("Plichtfeld");
					return;
				}
				new register().execute(null, null , null);
			
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
	
	@Override
	public void onBackPressed() {
	 super.onBackPressed();
	 overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
	}

	private class register extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog pdia;
		int index=9999;
		private String error = null;

		public register() {
			
		}
		
		@Override
		protected void onPreExecute() {
			if (pdia != null) {
				//pdia.dismiss();
				pdia=null;
			}
			pdia = new ProgressDialog(RegisterActivity.this);
            pdia.setMessage("Registriere Benutzer ... ");
            pdia.show();
		}

	    @Override
	    protected void onProgressUpdate(Integer... values) {
	      
	    }

		@Override
		protected Void doInBackground(Void... params) {
			User newUser = null;
			try {
				index = ServerConnector.register(etRegister.getText().toString());
				newUser = new User(index, etRegister.getText().toString());
				Thread.sleep(500);
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				error = e.getMessage();
				return null;
			} catch (FunctionNotDefinedException e) {
				// TODO Auto-generated catch block
				error = e.getMessage();
				return null;

			} catch (UserAlreadyExistsException e) {
				// TODO Auto-generated catch block
				error = e.getMessage();
				return null;

			}
			
			ServerConnector.setUser(newUser);
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
		
			if (pdia.isShowing()) {
	        	pdia.dismiss();
	        }
			
			if (error!=null) {
				Dialogs.showError(RegisterActivity.this, getApplicationContext(), null, 
						"Fehler bei der Registrierung" 
						, error, null);
			}else{
			Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
            startActivity(intent); 
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            finish();
			}
		}

	}



}
