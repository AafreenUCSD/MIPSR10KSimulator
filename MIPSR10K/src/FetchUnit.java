import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FetchUnit {
	
	static int currentClock;
	static int issueLimit;
	static List<String> instructions;
	static BufferedReader reader;
	static int countFetched=0;
	static String line;
	static boolean stopFetching;
	
	//The edge() and calc() of FetchUnit
	public static void edge(int clock){
		//if active list or integer or floating queue is full, then
		//send this instruction to decode buffer
		DecodeUnit.sInstructions = instructions;
	}
	
	public static void calc(int clock){
		currentClock = clock;
		instructions = new ArrayList<String>();
		if(!stopFetching){
			fetch();
			dumpState();
		}
	}
	
	public static void draw(int clock){
		//Go over each stage of the pipeline and find out which (four) instructions it is holding at this particular clock
		//System.out.println("          	"+FetchUnit.currentClock);
		for(int i=0; i<instructions.size(); i++){
			//System.out.println(instructions[i] +"	"+ "IF");
			System.out.println(instructions.get(i));
		}
	}
	
	public static void fetch(){
		try {
						for(int i=0; i<issueLimit; i++){
						line = reader.readLine();
						if(line!=null){
							countFetched++;
							instructions.add(countFetched+" "+line);
						}
						else{
							stopFetching = true;
							break;
						}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}	
	
	public static void flush(){
		instructions = null;
	}
	
	public static void dumpState(){
		for(int i=0; i<instructions.size(); i++){
				Starter.pipelineDiagram[countFetched-i][currentClock] = "F"; 
				//Starter.pipelineDiagram[countFetched-i][currentClock-1] = Integer.toString(countFetched+i); 
		}
	}
}
