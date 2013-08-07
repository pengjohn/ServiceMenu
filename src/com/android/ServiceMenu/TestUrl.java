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
import android.net.Uri;

public class TestUrl extends AutoItemActivity { 
	  private static final String TAG = "TestUrl";
    ListView myListView; 
    ArrayList<HashMap<String,Object>> myArrayList=new ArrayList<HashMap<String,Object>>(); 

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
                String strUrl=map.get("Url");
                Uri uri = Uri.parse(strUrl);  
                startActivity(new Intent(Intent.ACTION_VIEW,uri)); 
            } 
        }); 
    } 

  @Override
  public void onResume() {
    super.onResume();
    //add your code
    Log.e(TAG,"onResume");
    myArrayList.clear();
    
    addItemToList("UA", "http://www.966266.com/ua-test/ua.asp");
    addItemToList("wap.3g.net.cn", "http://wap.3g.net.cn");
    addItemToList("3g.youku.com", "http://3g.youku.com");
    addItemToList("www.wo.com.cn", "http://www.wo.com.cn");
    addItemToList("www.taobao.com", "http://www.taobao.com");
    addItemToList("www.baidu.com", "http://www.baidu.com");
    addItemToList("3g.sina.com.cn", "http://3g.sina.com.cn");
    addItemToList("qzone.qq.com", "http://qzone.qq.com");
    addItemToList("t.qq.com", "http://t.qq.com");
    addItemToList("weibo.com", "http://weibo.com");

    SimpleAdapter mySimpleAdapter=new SimpleAdapter(this, 
            myArrayList,
            R.layout.list_item,
            new String[]{"Title", "TestItem"},
            new int[]{R.id.ItemTitle});
    myListView.setAdapter(mySimpleAdapter); 
  }
  @Override
  protected void onPause() {
    //add your code
    
    super.onPause();
  }
  
    public void addItemToList(String strTitle, String strUrl)
    {
        HashMap<String, Object> map = new HashMap<String, Object>(); 
        map.put("Title", strTitle); 
        map.put("Url", strUrl);
        myArrayList.add(map);
    }



    
} 
