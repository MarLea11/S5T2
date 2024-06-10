package cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.controllers;


import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.dto.PlayerDTO;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.RankedPlayerNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.services.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {
    @Autowired
    PlayerService playerService;

    @Operation(summary = "Creates a new player", description = "Creates a new player for a specific user.")
    @PostMapping
    public ResponseEntity<PlayerDTO> addPlayer(@RequestBody PlayerDTO playerDTO, @RequestHeader("Authorization") String token) {
        ResponseEntity<PlayerDTO> responseEntity;
        //String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        try{
           PlayerDTO newPlayerDTO = playerService.addPlayer(playerDTO.getName());
           responseEntity = new ResponseEntity<>(newPlayerDTO, HttpStatus.CREATED);
        }catch(Exception e){
            responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Operation(summary = "Updates a player", description = "Updates a previously created player information.")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable String id, @RequestBody String name) {
        ResponseEntity<?> responseEntity;
        try{
            PlayerDTO updatePlayerDTO = playerService.updatePlayer(id, name);
            responseEntity = new ResponseEntity<>(updatePlayerDTO, HttpStatus.OK);
        }catch(PlayerNotFoundException e) {
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Operation(summary = "Get all the players", description = "Retrieves a list with all the players.")
    @GetMapping
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        ResponseEntity<List<PlayerDTO>> responseEntity;
        try {
            List<PlayerDTO> players = playerService.getAllPlayers();
            responseEntity = new ResponseEntity<>(players, HttpStatus.OK);
        }catch(Exception e) {
            responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    //TODO: exception control empty players list
    @Operation(summary = "Get all players by win rate", description = "Retrieves a list of players sorted by win rate.")
    @GetMapping("/ranking")
    public ResponseEntity<Double> getAverageWinRate() {
        ResponseEntity<Double> responseEntity;
        try {
            double averageWinRate = playerService.getAverageWinRate();
            responseEntity = new ResponseEntity<>(averageWinRate, HttpStatus.OK);
        }catch(Exception e) {
            responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Operation(summary = "Get the player with the worst win rate", description = "Retrieves the player with the worst win rate.")
    @GetMapping("/ranking/loser")
    public ResponseEntity<?> getWorstPlayerWinRate() {
        ResponseEntity<?> responseEntity;
        try {
            List<PlayerDTO> worstPlayer = playerService.getWorstPlayerWinRate();
            responseEntity = new ResponseEntity<>(worstPlayer, HttpStatus.OK);
        }catch(RankedPlayerNotFoundException e) {
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Operation(summary = "Get the player with the best win rate", description = "Retrieves the player with the best win rate.")
    @GetMapping("/ranking/winner")
    public ResponseEntity<?> getBestPlayerWinRate() {
        ResponseEntity<?> responseEntity;
        try {
            List<PlayerDTO> bestPlayer = playerService.getBestPlayerWinRate();
            responseEntity = new ResponseEntity<>(bestPlayer, HttpStatus.OK);
        }catch(RankedPlayerNotFoundException e) {
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

}
