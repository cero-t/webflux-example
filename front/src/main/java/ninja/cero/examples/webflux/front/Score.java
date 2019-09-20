package ninja.cero.examples.webflux.front;

public class Score {
    public int id;
    public String type;
    public int score;

    @Override
    public String toString() {
        return this.id + " " + this.type + " " + this.score;
    }
}
