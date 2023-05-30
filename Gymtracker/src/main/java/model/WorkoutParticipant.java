package model;

public class WorkoutParticipant {

    private String participantName;

    public int getId() {
        return id;
    }

    private int id;

    public WorkoutParticipant(String participantName, int id) {
        this.participantName = participantName;
        this.id = id;
    }

    public String getParticipantName() {
        return participantName;
    }
    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }
}
