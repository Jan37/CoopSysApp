package com.example.coopsysapp.util;

import com.example.coopsysapp.User;

public class Data {
    private static Data mInstance= null;

    public User[] userList = {};
    public boolean firstStart = true;

	protected Data(){}

    public static synchronized Data getInstance(){
    	if(null == mInstance){
    		mInstance = new Data();
    	}
    	return mInstance;
    }
    
    public User[] getUserList() {
		return userList;
	}

	public void setUserList(User[] userList) {
		this.userList = userList;
	}
	
	public boolean isFirstStart() {
		return firstStart;
	}

	public void setFirstStart(boolean firstStart) {
		this.firstStart = firstStart;
	}
}
