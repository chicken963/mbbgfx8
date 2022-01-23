package ru.orthodox.mbbg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.orthodox.mbbg.mediaPlayer.PlayerComponent;


@Component
public class PlayService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PlayerComponent playerComponent;

    public void play() {
        if (playerComponent.isStarted()){
            playerComponent.resume();
        } else {
            playerComponent.play();
        }
    }

    public void pause(){
        playerComponent.pause();
    }

    public void resume() {
    }

    public void stop() {
    }

    public void next(){
        playerComponent.next();
    }

    public void previous() {
        playerComponent.previous();
    }
}
