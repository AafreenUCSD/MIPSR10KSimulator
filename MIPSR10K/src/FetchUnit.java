import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FetchUnit {
	
	static String traceFile;
	static int issueLimit=4;
	static int clockCycles=100;
	
	public static String[] fetch(){
		BufferedReader br = null;
		String[] instructions = new String[issueLimit];
		String line = null;
		try {
			br = new BufferedReader(new FileReader(traceFile));
			for(int i=0; i<issueLimit; i++){
				line = br.readLine();
				instructions[i] = line;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instructions;
	}
	
	public static void parse(String[] args){
		
		if(args[0].isEmpty()){
			System.out.println("No trace file provided. Aborting!...");
			System.exit(0);
		}
		
		traceFile = args[0];
		System.out.println("Provided trace file: "+traceFile);
		
		if(!args[1].isEmpty()){
			issueLimit = Integer.parseInt(args[1]);
		}
		
		if(!args[2].isEmpty()){
			clockCycles = Integer.parseInt(args[2]);
		}
	}
	
	public static void main(String[] args){
		
		/*
		 * Input the trace file and other parameters to the fetch unit to begin simulation. 
		 * The input format should be: 
		 * Name of trace file, Number of instructions issued at a time, Total number of clock cycles
		*/
		
		parse(args);
		String[] instructions=null;
		for(int i=0; i<clockCycles; i++){
			instructions = fetch();
		}
		for(int i=0; i<issueLimit;i++){
			System.out.println(instructions[i]);
		}
	}
}
