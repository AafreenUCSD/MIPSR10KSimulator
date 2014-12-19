import java.io.*;

public class Starter{
	
	static String traceFile;
	static int issueLimit = 4;
	static int clockCycles = 100;
	static BufferedReader reader;
	static int readFrom = 0;
	static Register[] logicalIntegerRegisterFile;
	static Register[] logicalFloatingRegisterFile;
	static String[][] pipelineDiagram = new String[50][100];
	static String[] columnNames = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20",
		"21","22","23","24","25","26","27","28","29","30"};
		//"31","32","33","34","35","36","37","38","39","40",
		//"41","42","43","44","45","46","47","48","49","50"};
	
	public static void main(String[] args) {
		/*
		 * Input the trace file and other parameters to the fetch unit to begin
		 * simulation. The input format should be: Name of trace file, Number of
		 * instructions issued at a time, Total number of clock cycles
		 */

		parse(args);
		initialize();
		/*
		 * The idea behind calc and edge: The calc stage does all the main
		 * computation, The edge stage is the transition to the next cycle. We
		 * will keep a global calc() and edge(). And the global calc() will call
		 * the calc() of all the stages. Similarly, the global edge() will call
		 * the edge() of all the stages. So each stage would record what
		 * instruction is using it in that particular cycle. This is extremely
		 * helpful when printing pipeline diagram and debugging as well.
		 */
		for (int clock = 1; clock <= clockCycles; clock++) {
			calc(clock);
			edge(clock);
		}
		PipeLine.createAndShowGUI();
	}

	public static void initialize() {
		FetchUnit.issueLimit = issueLimit;
		FetchUnit.reader = reader;
		DecodeUnit.issueLimit = issueLimit;
		DecodeUnit.initFreeTables();
		DecodeUnit.R0 = new Register("R",0);
		logicalIntegerRegisterFile = new Register[32];
		for(int i=0; i<32; i++){
			logicalIntegerRegisterFile[i] = new Register("R",i);
		}
		logicalFloatingRegisterFile = new Register[32];
		for(int i=0; i<32; i++){
			logicalFloatingRegisterFile[i] = new Register("F",i);
		}
		/*
		CommitUnit.limit = issueLimit;
		*/
	}

	public static void calc(int clock) {
		FetchUnit.calc(clock);
		DecodeUnit.calc(clock);
		IssueUnit.calc(clock);
		FPADD1.calc(clock);
		FPADD2.calc(clock);
		FPADD3.calc(clock);
		FPMUL1.calc(clock);
		FPMUL2.calc(clock);
		FPMUL3.calc(clock);
		EStage.calc(clock);
		AStage.calc(clock);
		MStage.calc(clock);
		CommitUnit.calc(clock);
	}

	public static void edge(int clock) {
		FetchUnit.edge(clock);
		DecodeUnit.edge(clock);
		IssueUnit.edge(clock);
		FPADD1.edge(clock);
		FPADD2.edge(clock);
		FPADD3.edge(clock);
		FPMUL1.edge(clock);
		FPMUL2.edge(clock);
		FPMUL3.edge(clock);
		EStage.edge(clock);
		AStage.edge(clock);
		MStage.edge(clock);
		CommitUnit.edge(clock);
	}
	
	public static void parse(String[] args){
		
		if(args[0].isEmpty()){
			System.out.println("No trace file provided. Aborting!...");
			System.exit(0);
		}
		
		traceFile = args[0];
		System.out.println("Provided trace file: "+traceFile);
		try {
			reader = new BufferedReader(new FileReader(traceFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!args[1].isEmpty()){
			issueLimit = Integer.parseInt(args[1]);
		}
		
		if(!args[2].isEmpty()){
			clockCycles = Integer.parseInt(args[2]);
		}
	}

	public static void draw(int clock){
		DecodeUnit.draw(clock);
	}
}
