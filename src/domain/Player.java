package domain;

import java.util.Objects;
/**
 * Represents a player in the sports manager game.
 *
 * <p>Each player has physical attributes (energy, condition, injury risk)
 * that change over time through training and matches.
 * Sport-specific attributes such as position and skill ratings
 * are expected to be handled by sport-specific extensions.</p>
 */
public class Player {
    private int id;
    private String name;
    private int age;
    private Gender gender;
    private int energy;
    private int condition;
    private int injuryRisk;
    private InjuryStatus injuryStatus;

    public Player(int id,String name,int age,Gender gender) {
        if (id < 0)
            throw new IllegalArgumentException("ID cannot be less than zero!");

        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty");

        if (age <= 0)
            throw new IllegalArgumentException("Age must be positive");

        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");

        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        energy = 100;
        condition = 100;
        injuryRisk = 0;
        injuryStatus = InjuryStatus.HEALTHY;
    }
    public int getId(){return id;}
    public void setId(int id) {
        if (id <= 0)
            throw new IllegalArgumentException("ID must be positive");
        this.id = id;
    }
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    public int getAge(){return age;}
    public void setAge(int age){this.age = age;}
    public Gender getGender(){return gender;}
    public void setGender(Gender gender){this.gender = gender;}
    public int getEnergy(){return energy;}
    public void setEnergy(int energy){
        if (energy < 0 || energy > 100)
            throw new IllegalArgumentException("Energy must be between 0 and 100");
        this.energy = energy;}
    public int getCondition(){return condition;}
    public void setCondition(int condition){
        if (condition < 0 || condition > 100)
            throw new IllegalArgumentException("Condition must be between 0 and 100");
        this.condition = condition;}
    public int getInjuryRisk(){return injuryRisk;}
    public void setInjuryRisk(int injuryRisk){
        if (injuryRisk < 0 || injuryRisk > 100)
            throw new IllegalArgumentException("Injury risk must be between 0 and 100");
    this.injuryRisk = injuryRisk;}
    public InjuryStatus getInjuryStatus(){return injuryStatus;}
    public void setInjuryStatus(InjuryStatus injuryStatus){this.injuryStatus = injuryStatus;}
    @Override
    public String toString() {
        return name + " (ID:" + id + ", " + gender + ", Age:" + age + ")";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player p = (Player) o;
        return id == p.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
