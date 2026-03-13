package domain;

public class Player {
    private String name;
    private int age;
    private int energy;
    private int condition;
    private int injuryRisk;
    private InjuryStatus injuryStatus;

    public Player(String name,int age) {
        this.name = name;
        this.age = age;
        energy = 100;
        condition = 100;
        injuryRisk = 0;
        injuryStatus = InjuryStatus.HEALTHY;
    }
}
