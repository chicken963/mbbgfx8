package ru.orthodox.mbbg.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.Card;
import ru.orthodox.mbbg.services.LocalFilesService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CardRepository {

    @Autowired
    private LocalFilesService localFilesService;

    @Value("${cards.json.filepath}")
    private String cardsInfoFilePath;

    private File cardsFile;

    @PostConstruct
    private void init() {
        this.cardsFile = new File(cardsInfoFilePath);
    }

    public List<Card> findAllCards() {
        return localFilesService.readEntityListFromFile(cardsFile, Card.class);
    }

    public Card findById(UUID id) {
        return findAllCards().stream()
                .filter(card -> card.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Card> findByIds(List<UUID> ids) {
        return findAllCards().stream()
                .filter(card -> ids.contains(card.getId()))
                .collect(Collectors.toList());
    }

    public void save(Card card) {
        localFilesService.write(card, cardsFile);
    }
}
