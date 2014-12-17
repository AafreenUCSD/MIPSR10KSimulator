import java.util.ArrayList;


public class IssueUnit {
	public static int currentClock;
	static int limit;
	static Instruction[] instructions;
	static ArrayList<Instruction> integerQueue = new ArrayList<Instruction>();
	static ArrayList<Instruction> floatingQueue = new ArrayList<Instruction>();
	// Needs to be a CircularQueue
	static ArrayList<Instruction> addressQueue = new ArrayList<Instruction>();
	static boolean[][] indeterminationMatrix = new boolean[16][16];
	static boolean[][] dependencyMatrix = new boolean[16][16];
	
	public static void edge(int clock){
		//Send to appropriate FU based on instruction type
	}
	
	public static void calc(int clock){
		//see which instructions can be issued together using indetermination and dependency matrices
	}
}
