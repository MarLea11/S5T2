package cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.services;

import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Player;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.dto.PlayerDTO;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.RankedPlayerNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.GameRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.PlayerRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.UserRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.utils.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private Converter converter;

    public PlayerDTO addPlayer(String name) {
        Player player = new Player();
        player.setName(name == null || name.isEmpty() ? "UNKNOWN" : name);
        player.setRegistrationDate(LocalDateTime.now());
        player.setGamesList(new ArrayList<>());
        Player newPlayer = playerRepository.save(player);
        return converter.toPlayerDTO(newPlayer);
    }

    public PlayerDTO updatePlayer(String id, String name) {
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        Player updatedPlayer;
        if(optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            player.setName(name);
            updatedPlayer = playerRepository.save(player);
        } else {
            throw new PlayerNotFoundException(id);
        }
        return converter.toPlayerDTO(updatedPlayer);
    }

    public List<PlayerDTO> getAllPlayers() {
        List<Player> players = playerRepository.findAll();
        return players.stream()
                .map(player -> {
                    PlayerDTO playerDTO = converter.toPlayerDTO(player);
                    playerDTO.setWinRate(converter.calculateWinRate(player.getGamesList()));
                    return playerDTO;
                }).collect(Collectors.toList());
    }

    public double getAverageWinRate() {
        List<Player> players = playerRepository.findAll();

        return players.stream()
                .mapToDouble(player -> converter.calculateWinRate(player.getGamesList()))
                .average()
                .orElse(0.0);
    }

    public List<PlayerDTO> getWorstPlayerWinRate() {
        List<Player> players = playerRepository.findAll();

        double worstWinRate = players.stream()
                .mapToDouble(player -> converter.calculateWinRate(player.getGamesList()))
                .min()
                .orElseThrow(()-> new RankedPlayerNotFoundException("The player with the worst average win rate doesn't exist."));

        return players.stream()
                .filter(player -> converter.calculateWinRate(player.getGamesList()) == worstWinRate)
                .map(converter::toPlayerDTO)
                .collect(Collectors.toList());
    }

    public List<PlayerDTO> getBestPlayerWinRate() {
        List<Player> players = playerRepository.findAll();

        double bestWinRate = players.stream()
                .mapToDouble(player -> converter.calculateWinRate(player.getGamesList()))
                .max()
                .orElseThrow(()-> new RankedPlayerNotFoundException("The player with the best average win rate doesn't exist."));

        return players.stream()
                .filter(player -> converter.calculateWinRate(player.getGamesList()) == bestWinRate)
                .map(converter::toPlayerDTO)
                .collect(Collectors.toList());
    }

}
