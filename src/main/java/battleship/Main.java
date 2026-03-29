package battleship;

public class Main
{
	public static void main(String[] args)
	{
		ScoreboardDatabase.initializeDatabase();

		System.out.println("***  Battleship  ***");

		Tasks.menu();
	}
}