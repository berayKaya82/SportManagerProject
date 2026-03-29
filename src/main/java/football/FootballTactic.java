package football;

import sport.ITactic;
import domain.PlayStyle;

public class FootballTactic implements ITactic {
  private  final PlayStyle playStyle;

  public FootballTactic(PlayStyle playStyle){
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
      return "FootballTactic{" + "playStyle=" + playStyle + "}";

}
}
