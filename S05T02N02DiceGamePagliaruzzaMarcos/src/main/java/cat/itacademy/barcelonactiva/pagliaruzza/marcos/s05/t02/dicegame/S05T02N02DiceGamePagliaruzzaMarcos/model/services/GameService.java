package cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.services;

import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Game;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Player;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.GameRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.PlayerRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.utils.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class GameService {

    @Autowired
    GameRepository gameRepository;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    private Converter converter;

    public Game createGame(String id) {
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        Game game;
        Random random = new Random();
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            game = new Game();
            game.setDiceOne(random.nextInt(6) + 1);
            game.setDiceTwo(random.nextInt(6) + 1);
            game.setWon(game.getDiceOne() + game.getDiceTwo() == 7);
            gameRepository.save(game);
            player.getGamesList().add(game);
            playerRepository.save(player);
        } else {
            throw new PlayerNotFoundException(id);
        }
        return game;
    }

    public void deleteGame(String id) {
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            gameRepository.deleteAll(player.getGamesList());
            player.getGamesList().clear();
            playerRepository.save(player);
        } else {
            throw new PlayerNotFoundException(id);
        }
    }

    public List<Game> getAllGames(String id) {
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        List<Game> games;
        if(optionalPlayer.isPresent()) {
            games = optionalPlayer.get().getGamesList();
        } else {
            throw new PlayerNotFoundException(id);
        }
        return games;
    }

}