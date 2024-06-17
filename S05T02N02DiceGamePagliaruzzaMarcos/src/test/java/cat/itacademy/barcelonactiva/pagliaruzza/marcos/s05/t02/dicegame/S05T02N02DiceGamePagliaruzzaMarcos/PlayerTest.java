package cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos;

import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Game;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Player;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Role;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.User;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.dto.PlayerDTO;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.RankedPlayerNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.GameRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.PlayerRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.UserRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.services.PlayerService;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.utils.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PlayerTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Converter converter;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PlayerService playerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddPlayer() {
        String userEmail = "test@example.com";
        Long userId = 1L;
        String playerId = "1";
        String name = "Marcos";

        User user = new User(userId, "username", "password", userEmail, Role.USER);

        Player player = new Player(playerId, name, LocalDateTime.now(), new ArrayList<>(), userId);
        Player savedPlayer = new Player(playerId, name, LocalDateTime.now(), new ArrayList<>(), userId);
        PlayerDTO playerDTO = new PlayerDTO(playerId, name, LocalDateTime.now(), 0.0);

        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(user));
        when(playerRepository.save(any(Player.class))).thenReturn(savedPlayer);
        when(converter.toPlayerDTO(any(Player.class))).thenReturn(playerDTO);

        PlayerDTO result = playerService.addPlayer(name, userEmail);

        assertNotNull(result);
        assertEquals(playerId, result.getId());
        assertEquals(name, result.getName());
        assertNotNull(result.getRegistrationDate());

        verify(playerRepository, times(1)).save(any(Player.class));
        verify(converter, times(1)).toPlayerDTO(any(Player.class));
    }


    @Test
    public void testUpdatePlayer() {
        String userEmail = "test@example.com";
        Long userId = 1L;
        String playerId = "1";
        String name = "Leandro";

        User user = new User(userId, "username", "password", userEmail, Role.USER);

        Player existingPlayer = new Player(playerId, "Leandro", LocalDateTime.now(), new ArrayList<>(), userId);
        Player updatedPlayer = new Player(playerId, name, LocalDateTime.now(), new ArrayList<>(), userId);
        PlayerDTO playerDTO = new PlayerDTO(playerId, name, LocalDateTime.now(), 0.0);

        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(user));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.save(any(Player.class))).thenReturn(updatedPlayer);
        when(converter.toPlayerDTO(any(Player.class))).thenReturn(playerDTO);

        PlayerDTO result = playerService.updatePlayer(playerId, name, userId);

        assertNotNull(result);
        assertEquals(playerId, result.getId());
        assertEquals(name, result.getName());

        verify(playerRepository, times(1)).findById(playerId);
        verify(playerRepository, times(1)).save(any(Player.class));
        verify(converter, times(1)).toPlayerDTO(any(Player.class));
    }


    @Test
    public void testUpdatePlayerNameException() {
        String userEmail = "test@example.com";
        Long userId = 1L;
        String playerId = "1";
        String name = "Leandro";

        User user = new User(userId, "username", "password", userEmail, Role.USER);

        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(user));
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(PlayerNotFoundException.class, () -> {
            playerService.updatePlayer(playerId, name, userId);
        });

        assertEquals("Player with ID " + playerId + " not found.", exception.getMessage());

        verify(playerRepository, times(1)).findById(playerId);
        verify(playerRepository, times(0)).save(any(Player.class));
        verify(converter, times(0)).toPlayerDTO(any(Player.class));
    }

    @Test
    public void testGetAllPlayers() {
        String userEmail = "test@example.com";
        Long userId = 1L;

        User user = new User(userId, "username", "password", userEmail, Role.USER);

        Player player1 = new Player("1", "Leandro", LocalDateTime.now(), new ArrayList<>(), userId);
        Game game1 = new Game();
        game1.setDiceOne(3);
        game1.setDiceTwo(4);
        game1.setWon(true);
        player1.getGamesList().add(game1);

        Player player2 = new Player("2", "Marcos", LocalDateTime.now(), new ArrayList<>(), userId);
        Game game2 = new Game();
        game2.setDiceOne(1);
        game2.setDiceTwo(2);
        game2.setWon(false);
        player2.getGamesList().add(game2);
        List<Player> players = List.of(player1, player2);

        PlayerDTO playerDTO1 = new PlayerDTO("1", "Leandro", LocalDateTime.now(), 1.0);
        PlayerDTO playerDTO2 = new PlayerDTO("2", "Marcos", LocalDateTime.now(), 0.0);

        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(user));
        when(playerRepository.findAllByUserId(userId)).thenReturn(players);
        when(converter.toPlayerDTO(player1)).thenReturn(playerDTO1);
        when(converter.toPlayerDTO(player2)).thenReturn(playerDTO2);
        when(converter.calculateWinRate(player1.getGamesList())).thenReturn(1.0);
        when(converter.calculateWinRate(player2.getGamesList())).thenReturn(0.0);

        List<PlayerDTO> result = playerService.getAllPlayers(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals(1.0, result.get(0).getWinRate());
        assertEquals("2", result.get(1).getId());
        assertEquals(0.0, result.get(1).getWinRate());    verify(playerRepository, times(1)).findAllByUserId(userId);
        verify(converter, times(1)).toPlayerDTO(player1);
        verify(converter, times(1)).toPlayerDTO(player2);
        verify(converter, times(1)).calculateWinRate(player1.getGamesList());
        verify(converter, times(1)).calculateWinRate(player2.getGamesList());
    }

    @Test
    public void testGetAverageWinRate() {
        Player player1 = new Player("1", "Leandro", LocalDateTime.now(), new ArrayList<>(), 1L);
        Game game1 = new Game();
        game1.setDiceOne(3);
        game1.setDiceTwo(4);
        game1.setWon(true);
        player1.getGamesList().add(game1);

        Player player2 = new Player("2", "Marcos", LocalDateTime.now(), new ArrayList<>(), 2L);
        Game game2 = new Game();
        game2.setDiceOne(1);
        game2.setDiceTwo(2);
        game2.setWon(false);
        player2.getGamesList().add(game2);

        List<Player> players = List.of(player1, player2);

        when(playerRepository.findAll()).thenReturn(players);
        when(converter.calculateWinRate(player1.getGamesList())).thenReturn(1.0);
        when(converter.calculateWinRate(player2.getGamesList())).thenReturn(0.0);

        double result = playerService.getAverageWinRate();

        assertEquals(0.5, result);

        verify(playerRepository, times(1)).findAll();
        verify(converter, times(1)).calculateWinRate(player1.getGamesList());
        verify(converter, times(1)).calculateWinRate(player2.getGamesList());
    }

    @Test
    public void testGetWorstPlayer() {
        Player player1 = new Player("1", "Marcos", LocalDateTime.now(), new ArrayList<>(), 1L);
        Game game1 = new Game();
        game1.setDiceOne(3);
        game1.setDiceTwo(4);
        game1.setWon(true);
        player1.getGamesList().add(game1);

        Player player2 = new Player("2", "Leandro", LocalDateTime.now(), new ArrayList<>(), 2L);
        Game game2 = new Game();
        game2.setDiceOne(1);
        game2.setDiceTwo(2);
        game2.setWon(false);
        player2.getGamesList().add(game2);

        List<Player> players = List.of(player1, player2);

        PlayerDTO playerDTO2 = new PlayerDTO("2", "Joaquin", LocalDateTime.now(), 0.0);

        when(playerRepository.findAll()).thenReturn(players);
        when(converter.calculateWinRate(player1.getGamesList())).thenReturn(1.0);
        when(converter.calculateWinRate(player2.getGamesList())).thenReturn(0.0);
        when(converter.toPlayerDTO(player2)).thenReturn(playerDTO2);

        List<PlayerDTO> result = playerService.getWorstPlayerWinRate();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("2", result.get(0).getId());
        assertEquals(0.0, result.get(0).getWinRate());

        verify(playerRepository, times(1)).findAll();
        verify(converter, times(2)).calculateWinRate(player1.getGamesList());
        verify(converter, times(2)).calculateWinRate(player2.getGamesList());
        verify(converter, times(1)).toPlayerDTO(player2);
    }

    @Test
    public void testGetWorstPlayerException() {
        when(playerRepository.findAll()).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(RankedPlayerNotFoundException.class, () -> {
            playerService.getWorstPlayerWinRate();
        });

        assertEquals("The player with the worst average win rate doesn't exist.", exception.getMessage());

        verify(playerRepository, times(1)).findAll();
    }

    @Test
    public void testGetBestPlayer() {
        Player player1 = new Player("1", "Marcos", LocalDateTime.now(), new ArrayList<>(), 1L);
        Game game1 = new Game();
        game1.setDiceOne(3);
        game1.setDiceTwo(4);
        game1.setWon(true);
        player1.getGamesList().add(game1);

        Player player2 = new Player("2", "Leandro", LocalDateTime.now(), new ArrayList<>(), 2L);
        Game game2 = new Game();
        game2.setDiceOne(1);
        game2.setDiceTwo(2);
        game2.setWon(false);
        player2.getGamesList().add(game2);

        List<Player> players = List.of(player1, player2);

        PlayerDTO playerDTO1 = new PlayerDTO("1", "Joaquin", LocalDateTime.now(), 1.0);

        when(playerRepository.findAll()).thenReturn(players);
        when(converter.calculateWinRate(player1.getGamesList())).thenReturn(1.0);
        when(converter.calculateWinRate(player2.getGamesList())).thenReturn(0.0);
        when(converter.toPlayerDTO(player1)).thenReturn(playerDTO1);

        List<PlayerDTO> result = playerService.getBestPlayerWinRate();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals(1.0, result.get(0).getWinRate());

        verify(playerRepository, times(1)).findAll();
        verify(converter, times(2)).calculateWinRate(player1.getGamesList());
        verify(converter, times(2)).calculateWinRate(player2.getGamesList());
        verify(converter, times(1)).toPlayerDTO(player1);
    }

    @Test
    public void testGetBestPlayerException() {
        when(playerRepository.findAll()).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(RankedPlayerNotFoundException.class, () -> {
            playerService.getBestPlayerWinRate();
        });

        assertEquals("The player with the best average win rate doesn't exist.", exception.getMessage());

        verify(playerRepository, times(1)).findAll();
    }

}
