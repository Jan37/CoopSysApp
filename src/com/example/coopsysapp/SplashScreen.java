package com.example.coopsysapp;

import java.io.IOException;
import java.net.ConnectException;

import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.util.Data;
import com.example.coopsysapp.util.Dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
 
public class SplashScreen extends Activity {
	public TextView tvStatus;
	
    public void onAttachedToWindow() {
            super.onAttachedToWindow();
            Window window = getWindow();
            window.setFormat(PixelFormat.RGBA_8888);
        }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        tvStatus = (TextView) findViewById(R.id.textViewStatus);
        tvStatus.setVisibility(View.INVISIBLE);
        
        ImageView iv = (ImageView) findViewById(R.id.logo);
        iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (ServerConnector.offline) {
					ServerConnector.offline=false;
					Toast.makeText(getApplicationContext(), "Online-Modus aktiviert", Toast.LENGTH_SHORT).show();
				}else{
					ServerConnector.offline =true;
					Toast.makeText(getApplicationContext(), "Offline-Modus aktiviert", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        StartAnimations();
        
        getNameList task = new getNameList();
        task.execute();
        
    }
    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l=(RelativeLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);
 
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);
    }
    
    private class getNameList extends AsyncTask<Void, Integer, Void> {
    	private String errorMessage = "";
    	public getNameList() {
    	}
    	
    	@Override
    	protected void onPreExecute() {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				errorMessage = e.getMessage();
			}
    		tvStatus.setText("Lade Benutzer ...");
    	}

        @Override
        protected void onProgressUpdate(Integer... values) {
          
        }

    	@Override
    	protected Void doInBackground(Void... params) {
    				
    		try {
                tvStatus.setVisibility(View.VISIBLE);
                Thread.sleep(2500);
    			User[] userlist = ServerConnector.getNameList();
    			Data.getInstance().setUserList(userlist);
    			
    		} catch (ConnectException e) {
    			errorMessage = e.getMessage();
    		} catch (IOException e) {
    			errorMessage = e.getMessage();
    		} catch (InterruptedException e) {
    			errorMessage = e.getMessage();
    		} catch (FunctionNotDefinedException e) {
    			errorMessage = e.getMessage();
    		}
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

    		if (!errorMessage.matches("")) {
                intent.putExtra("ErrorTitle", "Fehler beim Laden der Benutzerliste");
                intent.putExtra("ErrorMessage", errorMessage);
			}

            startActivity(intent);
            //overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

            finish();
    		super.onPostExecute(result);
    	}

    }

 
}