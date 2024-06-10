package cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.controllers;

import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.Game;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class GameController {
    @Autowired
    GameService gameService;

    @Operation(summary = "Creates a new game", description = "Creates a new game for a specific player.")
    @PostMapping("/{id}/games")
    public ResponseEntity<?> createGame(@PathVariable String id) {
        ResponseEntity<?> responseEntity;
        try {
            Game game = gameService.createGame(id);
            responseEntity = new ResponseEntity<>(game, HttpStatus.CREATED);
        }catch(PlayerNotFoundException e) {
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    // TODO: delete message successfully
    @Operation(summary = "Delete games", description = "Deletes all the games played by a specific player.")
    @DeleteMapping("/{id}/games")
    public ResponseEntity<String> deleteGame(@PathVariable String id) {
        ResponseEntity<String> responseEntity;
        try {
            gameService.deleteGame(id);
            responseEntity = new ResponseEntity<>("Games of the user " + id + "deleted successfully.", HttpStatus.NO_CONTENT);
        }catch(PlayerNotFoundException e) {
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Operation(summary = "Get all the games", description = "Retrieve a list of all the games.")
    @GetMapping("/{id}/games")
    public ResponseEntity<?> getAllGames(@PathVariable String id) {
        ResponseEntity<?> responseEntity;
        try {
            List<Game> games = gameService.getAllGames(id);
            responseEntity = new ResponseEntity<>(games, HttpStatus.OK);
        }catch(PlayerNotFoundException e) {
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

}
