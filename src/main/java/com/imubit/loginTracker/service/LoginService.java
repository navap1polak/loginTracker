package com.imubit.loginTracker.service;

import com.imubit.loginTracker.model.FileEvent;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class LoginService implements Runnable {
    private LinkedList<FileEvent> fileEventQueue = new LinkedList<>();

    public void addEvent(FileEvent event){
        fileEventQueue.push(event);
    }

    @Override
    public void run() {
        while(!fileEventQueue.isEmpty()){

        }

    }
}
