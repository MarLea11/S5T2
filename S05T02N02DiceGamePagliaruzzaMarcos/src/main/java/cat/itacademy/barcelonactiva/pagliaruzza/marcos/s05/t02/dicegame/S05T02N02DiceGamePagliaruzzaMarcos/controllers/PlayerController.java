package cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.controllers;


import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.domain.User;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.dto.PlayerDTO;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.RankedPlayerNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions.UserNotFoundException;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.repository.UserRepository;
import cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.services.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
@Tag(name = "Players Dice Game Controller", description = "Endpoints for players management")
public class PlayerController {
    @Autowired
    PlayerService playerService;
    @Autowired
    UserRepository userRepository;

    @Operation(summary = "Creates a new player", description = "Creates a new player for a specific user.")
    @PostMapping
    public ResponseEntity<PlayerDTO> addPlayer(@RequestBody PlayerDTO playerDTO, Authentication authentication) {
        ResponseEntity<PlayerDTO> responseEntity;
        try{
           String email = authentication.getName();
           PlayerDTO newPlayerDTO = playerService.addPlayer(playerDTO.getName(), email);
           responseEntity = new ResponseEntity<>(newPlayerDTO, HttpStatus.CREATED);
        }catch(Exception e){
            responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Operation(summary = "Updates a player", description = "Updates a previously created player information.")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable String id, @RequestBody String name, Authentication authentication) {
        ResponseEntity<?> responseEntity;
        try{
            Long userId = getCurrentUserId(authentication);
            PlayerDTO updatePlayerDTO = playerService.updatePlayer(id, name, userId);
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
    public ResponseEntity<List<PlayerDTO>> getAllPlayers(Authentication authentication) {
        ResponseEntity<List<PlayerDTO>> responseEntity;
        try {
            Long userId = getCurrentUserId(authentication);
            List<PlayerDTO> players = playerService.getAllPlayers(userId);
            responseEntity = new ResponseEntity<>(players, HttpStatus.OK);
        }catch(Exception e) {
            responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

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

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return user.getId();
    }

}
