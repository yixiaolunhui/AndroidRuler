package com.ggx.ruler;

import android.app.Application;

public class MyAppliaction extends Application{

	private static MyAppliaction application;
	
	
	@Override
	public void onCreate() {		
		super.onCreate();
		application=this;
		CrashHandler catchHandler = CrashHandler.getInstance();  
        catchHandler.init(getApplicationContext());
        
	}
	
	public static MyAppliaction getInstance(){
		return application;
	}
}
