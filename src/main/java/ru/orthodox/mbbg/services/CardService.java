package ru.orthodox.mbbg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.*;
import ru.orthodox.mbbg.repositories.CardRepository;
import ru.orthodox.mbbg.repositories.RoundRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardService {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private RoundService roundService;

    private final static Map<Integer, String> ROUND_NUMBER_START_LETTERS = new HashMap<Integer, String>(){{
        put(1, "A");
        put(2, "B");
        put(3, "C");
        put(4, "D");
        put(5, "E");
    }};

    public void generateTickets(Game targetGame) {
        int ticketsNumber = 10;
        List<Card> cards = new LinkedList<>();
        List<Round> rounds = roundRepository.findByIds(targetGame.getRoundIds());
        for (Round round : rounds) {

            List<AudioTrack> roundAudioTracks = roundService.getAudioTracks(round);

            int ticketWidth = round.getWidth();
            int ticketHeight = round.getHeight();
            int ticketSize = ticketHeight * ticketWidth;

            List<String> roundArtists = roundAudioTracks.stream()
                    .map(AudioTrack::getArtist)
                    .collect(Collectors.toList());

            for (int i = 0; i < ticketsNumber; i++) {
                List<CardItem> cardItems = new LinkedList<>();
                Collections.shuffle(roundArtists);
                List<String> artistsForTicket = roundArtists.subList(0, ticketSize);
                artistsForTicket.forEach(artist -> {
                    int index = artistsForTicket.indexOf(artist);
                    int x = index % ticketWidth;
                    int y = index / ticketWidth;
                    cardItems.add(new CardItem(x, y, artist));
                });
                Card card = Card.builder()
                        .id(UUID.randomUUID())
                        .cardItems(cardItems)
                        .progress(0)
                        .height(ticketHeight)
                        .width(ticketWidth)
                        .number(generateTicketNumber(rounds.indexOf(round), i))
                        .build();
                cardRepository.save(card);
                cards.add(card);
            }

            round.setCardsIds(cards.stream().map(Card::getId).collect(Collectors.toList()));
            roundRepository.save(round);
        }
    }

    private String generateTicketNumber(int roundNumber, int i) {
        return ROUND_NUMBER_START_LETTERS.get(roundNumber) + (100 + i);
    }
}
