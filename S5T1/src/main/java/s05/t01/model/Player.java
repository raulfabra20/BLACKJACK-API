package s05.t01.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("player")
public class Player {

    @Id
    private int playerId;

    @Column("username")
    private String username;

   @Column("score")
    private int score = 0;


   private static Logger log = LoggerFactory.getLogger(Game.class);


    public Player(){
        this.username = username;
        this.score = getScore();
    }

    public Player(String username) {
        this.playerId = playerId;
        this.username = username;
        this.score = getScore();
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public int getScore(){
        return score;
    }
}
