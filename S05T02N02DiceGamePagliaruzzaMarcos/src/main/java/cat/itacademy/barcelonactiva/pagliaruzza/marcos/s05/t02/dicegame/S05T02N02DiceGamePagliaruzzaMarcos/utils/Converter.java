package cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.utils;

import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Game;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Player;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.dto.PlayerDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Converter {

    public PlayerDTO toPlayerDTO(Player player) {
        PlayerDTO newPlayerDTO = new PlayerDTO();
        newPlayerDTO.setId(player.getId());
        newPlayerDTO.setName(player.getName());
        newPlayerDTO.setWinRate(calculateWinRate(player.getGamesList()));
        return newPlayerDTO;
    }

    public double calculateWinRate(List<Game> gamesList) {
        double winRate;
        long winGames;

        if(gamesList.isEmpty()) {
            winRate = 0.0;
        } else {
            winGames = gamesList.stream().filter(Game::isWon).count();
            winRate = (double)winGames/gamesList.size();
        }
        return winRate;
    }

}
