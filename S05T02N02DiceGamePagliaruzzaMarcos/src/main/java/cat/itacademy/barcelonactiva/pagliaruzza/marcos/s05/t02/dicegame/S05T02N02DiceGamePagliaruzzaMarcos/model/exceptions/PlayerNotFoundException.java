package cat.itacademy.barcelonactiva.pagliaruzza.marcos.s05.t02.dicegame.S05T02N02DiceGamePagliaruzzaMarcos.model.exceptions;

public class PlayerNotFoundException extends RuntimeException{
    public PlayerNotFoundException(String id) {
        super("Player with ID " + id + " not found.");
    }
}
