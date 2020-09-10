package com.imubit.loginTracker.model;

import java.util.EventListener;

public interface FileListener extends EventListener {



    public void onModified(FileEvent event);



}
