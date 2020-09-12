package com.imubit.loginTracker.login;

import com.imubit.loginTracker.controller.TrackerController;
import com.imubit.loginTracker.service.TrackerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Abstract class for getting new logged users from operating system.
 * Need to extend for each operating system
 */
public abstract class AbstractLoginListener {

    @Autowired
    private TrackerService trackerService;

    public abstract void init();

    protected void notifyOnNewUsers(List<String> loginUsers){
        trackerService.newLoginUsers(loginUsers);

    }
}
