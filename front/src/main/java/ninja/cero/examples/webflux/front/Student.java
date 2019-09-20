package ninja.cero.examples.webflux.front;

public class Student {
	public int id;
	public String name;

	@Override
	public String toString() {
		return id + ":" + name;
	}
}
