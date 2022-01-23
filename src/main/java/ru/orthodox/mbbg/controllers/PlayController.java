package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.orthodox.mbbg.services.PlayService;

@Controller
@RequiredArgsConstructor
public class PlayController {
    @Autowired
    private PlayService playService;

    public void startPlaying(){
        playService.play();
    }

    public void pausePlaying(){
        playService.pause();
    }

    public void nextTrack(){
        playService.next();
    }


    public void previousTrack() {
        playService.previous();
    }
}