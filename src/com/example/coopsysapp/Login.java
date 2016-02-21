package com.example.coopsysapp;

import java.io.IOException;

import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.exception.NotFoundException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class Login extends Activity {

	public Button btnLogin;
	public Spinner spnDropDown;
	public String[] adapterItems= {};
	public User[] items ={};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		spnDropDown = (Spinner) findViewById(R.id.spinner1);

		btnLogin = (Button) findViewById(R.id.buttonLogin);
		
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ServerConnector.setUser(new User((spnDropDown.getSelectedItemPosition()+1), 
						spnDropDown.getSelectedItem().toString()));
				Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
	            startActivity(intent);      
	            //finish();
			}
		});
	
	}
	
	@Override
	protected void onStart() {
		
		  getNameList task = new getNameList(Login.this);
          task.execute();
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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


private class getNameList extends AsyncTask<Void, Integer, Void> {
	private ProgressDialog progress ;

	public getNameList(Login activity) {
		progress= new ProgressDialog(activity);
	}
	
	@Override
	protected void onPreExecute() {
		progress.setMessage("Lade Benutzer, bitte warten ...");
        progress.show();
	}

    @Override
    protected void onProgressUpdate(Integer... values) {
      
    }

	@Override
	protected Void doInBackground(Void... params) {
				
		try {
			items = ServerConnector.getNameList();
			Thread.sleep(500);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FunctionNotDefinedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		       		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		if (items.length==0) {
			items= new User[] {new User(1, "Testuser1"), new User(2, "Testuser2"), new User(3, "Testuser3")};
		}
		
		adapterItems = new String[items.length];
		for (int i = 0; i < adapterItems.length; i++) {
			adapterItems[i]=items[i].getName();
		}		 		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_spinner_item, adapterItems);
		spnDropDown.setAdapter(adapter);
        
		if (progress.isShowing()) {
        	progress.dismiss();
        }
		super.onPostExecute(result);
	}

}

}
