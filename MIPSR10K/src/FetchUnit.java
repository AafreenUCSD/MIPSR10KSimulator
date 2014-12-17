import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FetchUnit {
	
	static int currentClock;
	static int issueLimit;
	static String[] instructions;
	static BufferedReader reader;
	static int skipLines;
	
	//The edge() and calc() of FetchUnit
	public static void edge(int clock){
		DecodeUnit.sInstructions = instructions;
	}
	
	public static void calc(int clock, int lineNo){
		currentClock = clock;
		skipLines = lineNo;
		fetch();
	}
	
	public static void draw(int clock){
		//Go over each stage of the pipeline and find out which (four) instructions it is holding at this particular clock
		//System.out.println("          	"+FetchUnit.currentClock);
		for(int i=0; i<instructions.length; i++){
			//System.out.println(instructions[i] +"	"+ "IF");
			System.out.println(instructions[i]);
		}
	}
	
	public static void fetch(){
		instructions = new String[issueLimit];
		String line = null;
		try {
				/*
				for(int i=0; i<skipLines; i++){
					reader.readLine();
				}
				*/
				for(int i=0; i<issueLimit; i++){
					line = reader.readLine();
					instructions[i] = line;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}	
}
