package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;

import java.io.File;

public class PicturesGeneratedEvent extends ApplicationEvent {
    private final File rootDirectory;

    public PicturesGeneratedEvent(File source) {
        super(source);
        this.rootDirectory = source;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }
}
