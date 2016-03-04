package com.example.coopsysapp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.exception.NotFoundException;
import com.example.coopsysapp.util.Data;
import com.example.coopsysapp.util.MyExpandableAdapter;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;

public class ExpandableListMainActivity extends ExpandableListActivity
{
	ExpandableListView expandableList;
    // Create ArrayList to hold parent Items and Child Items
    private ArrayList<String> parentItems = new ArrayList<String>();
    private ArrayList<Object> childItems = new ArrayList<Object>();

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {

        super.onCreate(savedInstanceState);
        getActionBar().hide();
        // Create Expandable List and set it's properties
        expandableList = getExpandableListView(); 
        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);
        expandableList.setCacheColorHint(0);
        
        getExpandableListView().setCacheColorHint(0);
        
		new getEinkaufList().execute(null, null , null);
    }
    
	@Override
	public void onBackPressed() {
	 super.onBackPressed();
	 overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
	 finish();
	}
    
    private class getEinkaufList extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog pdia;

		public getEinkaufList() {

		}
		
		@Override
		protected void onPreExecute() {
            pdia = new ProgressDialog(ExpandableListMainActivity.this);
            pdia.setMessage("Lade Einkäufe ...");
            //pdia.show();  
		}

	    @Override
	    protected void onProgressUpdate(Integer... values) {
	      
	    }

		@Override
		protected Void doInBackground(Void... params) {
			
				try {
				
				Einkauf[] einkaufeOfMe = ServerConnector.getEinkaufe(ServerConnector.getUser().getId());
				
				for (int i = 0; i < einkaufeOfMe.length; i++) {
					Einkauf einkauf = einkaufeOfMe[i];
					EinkaufPart[] einkaufPart = ServerConnector.getPartsForEinkauf(einkauf.getId());
					
					float sum = 0;
					ArrayList<String> child = new ArrayList<String>();
					for (int j = 0; j < einkaufPart.length; j++) {
						child.add(Data.getInstance().getUserList()[einkaufPart[j].getGastId()-1].getName() + ": " + einkaufPart[j].getBetrag() 
								+ " € (" + einkaufPart[j].getNotiz() + ")");
						sum+=einkaufPart[j].getBetrag();
					}
					parentItems.add(einkauf.getDatum() + " - " + einkauf.getName() + " - Betrag: " + sum +" €\n" 
											+ "Einkäufer: Du" );
					childItems.add(child);	
				}

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
			
			try {
				
				EinkaufPart[] einkaufPartsWithMe = ServerConnector.getPartsForUser((ServerConnector.getUser().getId()));				
				
				for (int i = 0; i < einkaufPartsWithMe.length; i++) {
					Einkauf einkauf = ServerConnector.getEinkauf(einkaufPartsWithMe[i].getEinkaufId());
					EinkaufPart[] einkaufPartsEinkauf = ServerConnector.getPartsForEinkauf(einkauf.getId());
					
					float sum = 0;
					ArrayList<String> child = new ArrayList<String>();
					for (int j = 0; j < einkaufPartsEinkauf.length; j++) {
						child.add(Data.getInstance().getUserList()[einkaufPartsEinkauf[j].getGastId()-1].getName() + ": " + einkaufPartsEinkauf[j].getBetrag() 
								+ " € (" + einkaufPartsEinkauf[j].getNotiz() + ")");
						sum+=einkaufPartsEinkauf[j].getBetrag();
					}
					parentItems.add(einkauf.getDatum() + " - " + einkauf.getName() + " - Betrag: " + sum +" €\n" 
											+ "Einkäufer: " + Data.getInstance().getUserList()[einkauf.getEinkauefer()-1].getName() );
					childItems.add(child);	
				}
				
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
			pdia.dismiss();
			MyExpandableAdapter adapter = new MyExpandableAdapter(parentItems,
					childItems);

			adapter.setInflater(
					(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
					ExpandableListMainActivity.this);

			// Set the Adapter to expandableList
			expandableList.setAdapter(adapter);
			expandableList
					.setOnChildClickListener(ExpandableListMainActivity.this);

			super.onPostExecute(result);
		}

	}

}
