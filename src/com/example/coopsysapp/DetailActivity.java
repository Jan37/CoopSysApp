package com.example.coopsysapp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.exception.NotFoundException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DetailActivity extends Activity {
	
	ListView listview ;
	ArrayList<String> list;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		listview = (ListView) findViewById(R.id.listview);
	    
//	    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//	        @Override
//	        public void onItemClick(AdapterView<?> parent, final View view,
//	            int position, long id) {
//	          final String item = (String) parent.getItemAtPosition(position);
//	          view.animate().setDuration(2000).alpha(0)
//	              .withEndAction(new Runnable() {
//	                @Override
//	                public void run() {
//	                  list.remove(item);
//	                  adapter.notifyDataSetChanged();
//	                  view.setAlpha(1);
//	                }
//	              });
//	        }
//
//	      });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
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
	
	private class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }

	    @Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }

	  }
	
	private class getEinkaufList extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog progress ;

		public getEinkaufList(DetailActivity activity) {
			if(progress !=null)
			{
			    progress= null;
			}
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
				
				EinkaufPart[] einkaufPartsWithMe = ServerConnector.getPartsForUser((ServerConnector.getUser().getId()));
				
				String[] values = new String[einkaufPartsWithMe.length];
				
				
				for (int i = 0; i < einkaufPartsWithMe.length; i++) {
					StringBuilder item = new StringBuilder();
					Einkauf einkauf = ServerConnector.getEinkauf(einkaufPartsWithMe[i].getEinkaufId());
					EinkaufPart[] einkaufPartsEinkauf = ServerConnector.getPartsForEinkauf(einkauf.getId());
					
					item.append(einkauf.getDatum() + " - " + einkauf.getName() + " - " + einkauf.getEinkauefer() + "\n");
					for (int j = 0; j < einkaufPartsEinkauf.length; j++) {
						item.append(" - " + einkaufPartsEinkauf[j].getGastId() + ": " + einkaufPartsEinkauf[j].getBetrag() 
								+ " € (" + einkaufPartsEinkauf[j].getNotiz() +")\n");
					}
					
					values[i]= item.toString();					
				}
				
//				String[] values = new String[] { "19.02.16 - Marktkauf - Yanick - 11,10 €\n - Du: 5,55 € (+6er Wasser)\n - Richard: 5,55 €\n", 
//			    		"18.02.16 - Netto - Du - 11,10 €\n - Emme: 5,55 € (+6er Wasser)\n - Richard: 5,55 €\n",
//			    		"17.02.16 - Marktkauf - Emme - 11,10 €\n - Du: 5,55 € (+6er Wasser)\n - Richard: 5,55 €\n"};
//			    

			    list = new ArrayList<String>();
			    for (int i = 0; i < values.length; ++i) {
			      list.add(values[i]);
			    }
			    final StableArrayAdapter adapter = new StableArrayAdapter(getApplicationContext(),
			        android.R.layout.simple_list_item_1, list);
			    listview.setAdapter(adapter);

				
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
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
	        	progress.dismiss();

			super.onPostExecute(result);
		}

	}
}
