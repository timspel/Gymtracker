package model;

public class Set {
    private int setNumber;
    private int repetitions;
    private double weight;

    public Set(int setNumber, int repetitions, double weight){
        this.setNumber = setNumber;
        this.repetitions = repetitions;
        this.weight = weight;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }
    public void setRepetitions(int repetitions){
        this.repetitions = repetitions;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getSetNumber() {
        return setNumber;
    }
    public int getRepetitions() {
        return repetitions;
    }
    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("%s x %skg", repetitions, weight);
    }
}
