package com.imubit.loginTracker.fileWatcher;

import com.imubit.loginTracker.model.FileEvent;
import com.imubit.loginTracker.model.FileListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileWatcher implements Runnable {

    protected static final List<WatchService> watchServices = new ArrayList<>();

    protected List<FileListener> listeners = new ArrayList<>();

    protected final File folder;


    public FileWatcher(File folder) {
        this.folder = folder;
    }

    public List<FileListener> getListeners() {
        return listeners;
    }

    public FileWatcher addListener(FileListener listener){
        listeners.add(listener);
        return this;
    }


    public FileWatcher setListeners(List<FileListener> listeners) {
        this.listeners = listeners;
        return this;
    }

    public void watch() {
        if (folder.exists()) {
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @Override
    public void run() {

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            Path path = Paths.get(folder.getAbsolutePath());
            path.register(watchService,  ENTRY_MODIFY);
            watchServices.add(watchService);

            boolean poll = true;
            while (poll) {
                poll = pollEvents(watchService);
            }

        } catch (IOException | InterruptedException | ClosedWatchServiceException e) {
            Thread.currentThread().interrupt();
        }
    }


    protected boolean pollEvents(WatchService watchService) throws InterruptedException {

        WatchKey key = watchService.take();

        Path path = (Path) key.watchable();

        for (WatchEvent<?> event : key.pollEvents()) {

            notifyListeners(event.kind(), path.resolve((Path) event.context()).toFile());

        }

        return key.reset();

    }

    public static List<WatchService> getWatchServices() {

        return Collections.unmodifiableList(watchServices);

    }

    protected void notifyListeners(WatchEvent.Kind<?> kind, File file) {

        FileEvent event = new FileEvent(file);


        if (kind == ENTRY_MODIFY) {

            for (FileListener listener : listeners) {

                listener.onModified(event);

            }

        }
    }


}
