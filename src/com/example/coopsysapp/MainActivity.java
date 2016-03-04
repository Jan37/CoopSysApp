package com.example.coopsysapp;

import java.io.IOException;

import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.util.Data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public Button login, register;
	public Spinner spnDropDown;
	public ImageView ivLogo;
	public TextView tvRegisterText;
	public String[] adapterItems= {};
	public User[] items ={};
	boolean doubleBackToExitPressedOnce = false;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_new);
		getActionBar().hide();
        this.overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

		initialize();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	private void initialize() {
		login = (Button) findViewById(R.id.button1);
		register = (Button) findViewById(R.id.button2);
		spnDropDown = (Spinner) findViewById(R.id.spinner1);
		ivLogo = (ImageView) findViewById(R.id.imageView1);
		tvRegisterText = (TextView) findViewById(R.id.textView1);
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ServerConnector.setUser(new User((spnDropDown.getSelectedItemPosition()+1), 
						spnDropDown.getSelectedItem().toString()));
				Log.d("MainActivity", String.valueOf(spnDropDown.getSelectedItemPosition()+1));
				Log.d("MainActivity", String.valueOf(spnDropDown.getSelectedItem().toString()));
				Log.d("MainActivity", ServerConnector.getUser().getName());
				Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
	            startActivity(intent);  
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

	            //finish();
			}
		});
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
	                startActivity(intent);
	                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
	                //finish();
			}
		});
		
		
		adapterItems = new String[Data.getInstance().getUserList().length];
		for (int i = 0; i < adapterItems.length; i++) {
			adapterItems[i]=Data.getInstance().getUserList()[i].getName();
		}		 		
		spnDropDown.setAdapter(new MyAdapter(this, R.layout.user_spinner,adapterItems));
	}
	
	@Override
	protected void onResume() {
		if (!Data.getInstance().isFirstStart()) {
			new getNameList().execute(null, null , null);
		}
		
		Data.getInstance().setFirstStart(false);
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
	    if (doubleBackToExitPressedOnce) {
	        super.onBackPressed();
	        return;
	    }

	    this.doubleBackToExitPressedOnce = true;
	    Toast.makeText(this, "Klicke erneut, um die App zu schließen", Toast.LENGTH_SHORT).show();

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	            doubleBackToExitPressedOnce=false;                       
	        }
	    }, 2000);
	} 
	
	public class MyAdapter extends ArrayAdapter<String> {
		public MyAdapter(Context ctx, int txtViewResourceId, String[] objects) {
			super(ctx, txtViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}

		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View mySpinner = inflater.inflate(R.layout.user_spinner, parent,
					false);
			TextView main_text = (TextView) mySpinner
					.findViewById(R.id.text_main_seen);
			main_text.setText(adapterItems[position]);

			return mySpinner;
		}
	}
	
	private class getNameList extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog pdia;

    	public getNameList() {
    	}
    	
    	@Override
    	protected void onPreExecute() {
            pdia = new ProgressDialog(MainActivity.this);
            pdia.setMessage("Lade Benutzerliste ...");
            pdia.show();  
    	}

        @Override
        protected void onProgressUpdate(Integer... values) {
          
        }

    	@Override
    	protected Void doInBackground(Void... params) {
    				
    		try {
    			User[] userlist = ServerConnector.getNameList();
    			Data.getInstance().setUserList(userlist);
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
    		}
    		
    		

    		       		return null;
    	}
    	@Override
    	protected void onPostExecute(Void result) {
    		super.onPostExecute(result);
    		adapterItems = new String[Data.getInstance().getUserList().length];
    		for (int i = 0; i < adapterItems.length; i++) {
    			adapterItems[i]=Data.getInstance().getUserList()[i].getName();
    		}	
    		spnDropDown.setAdapter(null);
    		spnDropDown.setAdapter(new MyAdapter(MainActivity.this, R.layout.user_spinner,adapterItems));
    		pdia.dismiss();
    	}

    }

}

