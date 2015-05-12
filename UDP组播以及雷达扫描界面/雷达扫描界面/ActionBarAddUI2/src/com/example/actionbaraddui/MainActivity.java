package com.example.actionbaraddui;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity{
	
	SearchDevicesView search_device_view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		search_device_view=new SearchDevicesView(this);
		search_device_view.setWillNotDraw(false);
		setContentView(search_device_view);
	}
}
