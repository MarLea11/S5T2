package cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos;

import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Game;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Player;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.GameRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.PlayerRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.services.GameService;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.utils.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GameTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private Converter converter;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateGame() {
        String id = "1";

        Player existingPlayer = new Player(id, "Marcos", LocalDateTime.now(), new ArrayList<>());
        Game game = new Game();
        game.setDiceOne(3);
        game.setDiceTwo(4);
        game.setWon(true);
        existingPlayer.getGamesList().add(game);

        when(playerRepository.findById(id)).thenReturn(Optional.of(existingPlayer));
        when(gameRepository.save(any(Game.class))).thenReturn(game);
        when(playerRepository.save(any(Player.class))).thenReturn(existingPlayer);

        Game result = gameService.createGame(id);

        assertNotNull(result);

        verify(playerRepository, times(1)).findById(id);
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    public void testCreateGameException() {
        String id = "1";

        when(playerRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(PlayerNotFoundException.class, () -> {
            gameService.createGame(id);
        });

        assertEquals("Player with ID " + id + " not found.", exception.getMessage());

        verify(playerRepository, times(1)).findById(id);
        verify(gameRepository, times(0)).save(any(Game.class));
        verify(playerRepository, times(0)).save(any(Player.class));
    }

    @Test
    public void testDeleteGame() {
        String id = "1";

        Game game1 = new Game();
        game1.setDiceOne(3);
        game1.setDiceTwo(4);
        game1.setWon(true);

        Game game2 = new Game();
        game2.setDiceOne(1);
        game2.setDiceTwo(2);
        game2.setWon(false);

        Player existingPlayer = new Player(id, "Marcos", LocalDateTime.now(), new ArrayList<>());
        existingPlayer.getGamesList().add(game1);
        existingPlayer.getGamesList().add(game2);

        when(playerRepository.findById(id)).thenReturn(Optional.of(existingPlayer));

        gameService.deleteGame(id);

        verify(playerRepository, times(1)).findById(id);
        verify(gameRepository, times(1)).deleteAll(existingPlayer.getGamesList());
        verify(playerRepository, times(1)).save(existingPlayer);

        assertTrue(existingPlayer.getGamesList().isEmpty());
    }

    @Test
    public void testDeletePlayerGameException() {
        String id = "1";

        when(playerRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(PlayerNotFoundException.class, () -> {
            gameService.deleteGame(id);
        });

        assertEquals("Player with ID " + id + " not found.", exception.getMessage());

        verify(playerRepository, times(1)).findById(id);
        verify(gameRepository, times(0)).deleteAll(anyList());
        verify(playerRepository, times(0)).save(any(Player.class));
    }

    @Test
    public void testGetAllGames() {
        String id = "1";

        Game game1 = new Game();
        game1.setDiceOne(3);
        game1.setDiceTwo(4);
        game1.setWon(true);

        Game game2 = new Game();
        game2.setDiceOne(1);
        game2.setDiceTwo(2);
        game2.setWon(false);

        Player existingPlayer = new Player(id, "Marcos", LocalDateTime.now(), new ArrayList<>());
        existingPlayer.getGamesList().add(game1);
        existingPlayer.getGamesList().add(game2);

        when(playerRepository.findById(id)).thenReturn(Optional.of(existingPlayer));

        List<Game> result = gameService.getAllGames(id);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(game1, result.get(0));
        assertEquals(game2, result.get(1));

        verify(playerRepository, times(1)).findById(id);
    }

    @Test
    public void testGetAllGamesException() {
        String id = "1";

        when(playerRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(PlayerNotFoundException.class, () -> {
            gameService.getAllGames(id);
        });

        assertEquals("Player with ID " + id + " not found.", exception.getMessage());

        verify(playerRepository, times(1)).findById(id);
    }

}

