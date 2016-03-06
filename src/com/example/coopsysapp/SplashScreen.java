package com.example.coopsysapp;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.util.Data;
import com.example.coopsysapp.util.Dialogs;

import android.text.Spanned;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
 
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
        
        requestIP task = new requestIP();
        task.execute();
        
//        getNameList task = new getNameList();
//        task.execute();
        
//        connectWifi task = new connectWifi();
//        task.execute();
        
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
                Thread.sleep(2000);
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
    
    private class connectWifi extends AsyncTask<Void, Integer, Void> {
    	private String errorMessage = "";
    	private boolean wlanAvailable = false;
    	
    	public connectWifi() {
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		tvStatus.setText("Suche WLAN ...");
    	}

        @Override
        protected void onProgressUpdate(Integer... values) {
        	tvStatus.setText("Verbinde WLAN ...");
        }

    	@Override
    	protected Void doInBackground(Void... params) {
    				
                tvStatus.setVisibility(View.VISIBLE);
                
                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + ServerConnector.networkSSID + "\"";
                
                conf.preSharedKey = "\""+ ServerConnector.networkPass +"\"";
                
                WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE); 
                wifiManager.addNetwork(conf);
                
                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                for( WifiConfiguration i : list ) {
                    if(i.SSID != null && i.SSID.equals("\"" + ServerConnector.networkSSID + "\"")) {
                         wifiManager.disconnect();
                         wifiManager.enableNetwork(i.networkId, true);
                         wifiManager.reconnect();    
                         
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                             	tvStatus.setText("Verbinde WLAN ...");
                             }
                          });

                         
                         wlanAvailable=true;
                         
                         break;
                    }           
                
                }
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ssid ="";

                while (wlanAvailable && !ssid.matches("\"" + ServerConnector.networkSSID + "\"") && !mWifi.isRoaming()) {
                	wifiInfo = wifiManager.getConnectionInfo();
                	Log.d("Matches?", String.valueOf(ssid.matches("\"" + ServerConnector.networkSSID + "\""))); 
                	Log.d("not Connected", wifiInfo.getSupplicantState().toString()); 
                	if (wifiInfo.getSupplicantState()== SupplicantState.COMPLETED) {
                        ssid = wifiInfo.getSSID();
                        Log.d("Connected", ssid); 
                    }
                	try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
                
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		super.onPostExecute(result);
    		if (wlanAvailable) {
				ServerConnector.offline=false;
			}
    		
            getNameList task = new getNameList();
            task.execute();
    	}

    }
    
    private class requestIP extends AsyncTask<Void, Integer, Void> {
    	private String errorMessage = "";
    	private AlertDialog.Builder builder;
    	public requestIP() {
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		tvStatus.setText("Erfrage Server-IP ...");
    		builder = new AlertDialog.Builder(SplashScreen.this);
    	}

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

    	@Override
    	protected Void doInBackground(Void... params) {
    				
                tvStatus.setVisibility(View.VISIBLE);
                
                
                
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		
    		builder.setTitle("IP Adresse des Servers");

            // Set up the input
            final EditText input = new EditText(SplashScreen.this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_PHONE);
            
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter() {
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    if (end > start) {
                        String destTxt = dest.toString();
                        String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                        if (!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) { 
                            return "";
                        } else {
                            String[] splits = resultingTxt.split("\\.");
                            for (int i=0; i<splits.length; i++) {
                                if (Integer.valueOf(splits[i]) > 255) {
                                    return "";
                                }
                            }
                        }
                    }
                return null;
                }
            };
            input.setFilters(filters);
            
            SharedPreferences prefs = getSharedPreferences("RadS", Context.MODE_PRIVATE);
            String restoredIp = prefs.getString("ip", null);
            if (restoredIp != null) 
            {
              input.setText(restoredIp);
            }
            
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	SharedPreferences sharedpreferences = getSharedPreferences("RadS", Context.MODE_PRIVATE);	
                	Editor editor = sharedpreferences.edit();
                	editor.putString("ip", input.getText().toString());
                	editor.commit();
                    ServerConnector.ip = input.getText().toString();
                    ServerConnector.offline=false;
                    getNameList task = new getNameList();
                    task.execute();
                }
            });
            builder.setNegativeButton("Offline-Modus", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	ServerConnector.offline=true;
                    dialog.cancel();
                    getNameList task = new getNameList();
                    task.execute();
                }
            });

            builder.show();

    	}

    }


 
}