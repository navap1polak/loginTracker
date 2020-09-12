package com.imubit.loginTracker.login;

import com.imubit.loginTracker.fileWatcher.FileWatcher;
import com.imubit.loginTracker.kafka.KafkaMessageSender;
import com.imubit.loginTracker.model.FileEvent;
import com.imubit.loginTracker.model.FileListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class LinuxLoginCheckManager extends AbstractLoginListener{
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    private LinuxLoginListenerService linuxLoginListenerService = new LinuxLoginListenerService();

    private boolean checkForNewLogins;

    private Object lock = new Object();

    @PostConstruct
    public void init(){
        //get the last user logged for baseline, in order to be able to compare upon file changed
        linuxLoginListenerService.createWtmpFirstStatus();

        //start thread waiting for notification on file change,
        //upon notification it will analyze the changes
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new LinuxCheckerRunner());

        //watch the folder of /var/log,
        //upon change on wtmp, the LinuxCheckerRunner is notified and analyze for new login users
        FileWatcher watcher = new FileWatcher(new File(LinuxLoginListenerService.LOGIN_FILE_FOLDER));

        watcher.addListener(new FileListener() {

            public void onModified(FileEvent event) {
                if(event.getFile().getAbsolutePath().equals(LinuxLoginListenerService.LOGIN_FILE)) {
                    setCheckForNewLogins();
                }
            }
        }).watch();
    }

    /**
     * Called upon the wtmp file change
     * It will invoke the waiting thread of LinuxCheckerRunner
     */
    private void setCheckForNewLogins() {

        synchronized(lock){
            this.checkForNewLogins = true;
            lock.notify();
            checkForNewLogins = false;
        }
    }


    public class LinuxCheckerRunner implements Runnable{
        @Override
        public void run() {

            while (true) {
                try {
                    synchronized (lock) {
                        if (!checkForNewLogins) {
                            lock.wait();
                        }
                        List<String> loginUsers = linuxLoginListenerService.checkForLastLoggedUsers();
                        loginUsers.forEach(s -> sendMessage(s));
                    }
                } catch (InterruptedException e) {

                }
            }
        }
    }
}
