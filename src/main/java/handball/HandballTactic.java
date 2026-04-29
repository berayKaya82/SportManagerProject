package handball;

import sport.ITactic;
import domain.PlayStyle;

public class HandballTactic implements ITactic {
    private  final PlayStyle playStyle;

    public HandballTactic(PlayStyle playStyle){
        if(playStyle == null){
            throw new IllegalArgumentException("playstyle cannot be null");
        }
        this.playStyle=playStyle;
    }

    @Override
    public PlayStyle getPlayStyle(){
        return playStyle;
    }
    @Override
    public String toString(){
        return "HandballTactic{" + "playStyle=" + playStyle + "}";

    }
}

