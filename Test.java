
public class Test {

	public static void main(String[] args) {
		World world= new World();
		world.searchArea(5,5);
		world.searchArea(0,0);
		world.searchArea(5,6);
		world.searchArea(5,6);
		System.out.println(world.stringify("col"));
	}

}
