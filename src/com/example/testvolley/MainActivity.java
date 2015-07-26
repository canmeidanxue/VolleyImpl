package com.example.testvolley;

import com.net.impl.NetRequest;
import com.wizarpos.atool.net.volley2.BaseRequest.ResponseListener;
import com.wizarpos.atool.net.volley2.Response;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		NetRequest.getInstance().addRequest(serviceCode, params, new ResponseListener() {
			
			@Override
			public void onSuccess(Response response) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFaild(Response response) {
				// TODO Auto-generated method stub
				
			}
		});
	}


}
