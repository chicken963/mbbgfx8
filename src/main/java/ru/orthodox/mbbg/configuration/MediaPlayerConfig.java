package ru.orthodox.mbbg.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent;

@Configuration
public class MediaPlayerConfig {
    @Value("${app.player.factoryOptions:#{null}}")
    private String[] factoryOptions;

    @Bean
    public MediaPlayerFactory mediaPlayerFactory() {
        return new MediaPlayerFactory(factoryOptions);
    }

    @Bean
    public MediaPlayerEventAdapter mediaPlayerEventAdapter(){
        return new MediaPlayerEventAdapter();
    }

    @Bean
    public AudioPlayerComponent audioPlayerComponent(MediaPlayerFactory mediaPlayerFactory) {
        return new AudioPlayerComponent(mediaPlayerFactory);
    }
}
