/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to show manu list of CIT.

===========================================================================*/

package com.android.ServiceMenu;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.bluetooth.BluetoothAdapter;
import android.os.IBinder;
import android.os.ServiceManager;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.util.ArrayList; 
import java.util.HashMap; 
import android.app.Activity; 
import android.os.Bundle; 
import android.view.View; 
import android.widget.AdapterView; 
import android.widget.AdapterView.OnItemClickListener; 
import android.widget.ListView; 
import android.widget.SimpleAdapter; 
import android.widget.Toast; 
import android.content.ComponentName;
import android.content.Intent;

public class ManuTest extends AutoItemActivity { 
	private static final String TAG = "ManuTest";
    ListView myListView; 
    ArrayList<HashMap<String,Object>> myArrayList=new ArrayList<HashMap<String,Object>>(); 
    SimpleAdapter mySimpleAdapter;

    @Override 
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        bFlagAutoTest = false;
        bWaitInitTime = false;
        
        setContentView(R.layout.manu_test); 
        myListView=(ListView)findViewById(R.id.myListView); 

        myListView.setOnItemClickListener(new OnItemClickListener(){ 
            @Override 
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) { 
                HashMap<String,String> map=(HashMap<String,String>)myListView.getItemAtPosition(arg2); 
                String nTestItem=map.get("TestItem");
                openActivity(Integer.valueOf(nTestItem).intValue());
            } 
        }); 

		initTestFailed();

		for(int i=TEST_START; i<getTestItemActivityCount() ;i++)
			addItemToList(i); 
		
		mySimpleAdapter=new SimpleAdapter(this, 
			myArrayList,
			R.layout.list_item,
			new String[]{"ItemImage", "Title", "TestItem"},
			new int[]{R.id.ItemImage, R.id.ItemTitle});
		myListView.setAdapter(mySimpleAdapter); 
    } 

  @Override
	public void onResume() {
		super.onResume();
    	//add your code
    	Log.e(TAG,"onResume");

		for(int i=TEST_START; i<getTestItemActivityCount() ;i++) {
			HashMap<String,Object> map=(HashMap<String,Object>)myListView.getItemAtPosition(i); 
        	if(autoMask[i] == 1) {   
				map.put("ItemImage", R.drawable.ok);
			}
			else {
				map.put("ItemImage", R.drawable.ng);
			}
		}
		mySimpleAdapter.notifyDataSetChanged();
	}
  
	@Override
	protected void onPause() {
		//add your code
		super.onPause();
	}
  
	public void addItemToList(int nTestItem)
    {
        HashMap<String, Object> map = new HashMap<String, Object>(); 
        String Title = getTestItemTitle(nTestItem);
        if(autoMask[nTestItem] == 1)
           map.put("ItemImage", R.drawable.ok);
        else
           map.put("ItemImage", R.drawable.ng);
        map.put("Title", Integer.toString(nTestItem+1) + ". " + Title); 
        map.put("TestItem", Integer.toString(nTestItem));
        myArrayList.add(map);
    }



    
} 
