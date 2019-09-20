package ninja.cero.examples.webflux.resource.score;

public class Score {
    public int id;
    public String type;
    public int score;

    public Score(int id, String type, int score) {
        this.id = id;
        this.type = type;
        this.score = score;
    }

    @Override
    public String toString() {
        return this.id + " " + this.type + " " + this.score;
    }
}
