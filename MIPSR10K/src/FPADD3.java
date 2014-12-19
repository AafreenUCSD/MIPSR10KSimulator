public class FPADD3 {
	static int currentClock;
	static int limit;
	static Instruction instr;
	static boolean isBusy;
	static Instruction toBeCommitted;
	
	public static void edge(int clock){
		CommitUnit.fromFPADD3 = toBeCommitted;
		FPADD3.isBusy = false;
		if(instr!=null)
			instr.done = true;
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(instr!=null){
			FPADD3.isBusy = true;
		//Check if it can be passed to the writeback stage
			toBeCommitted = instr;
			dumpState();
		}
	}
	
	public static void dumpState(){
		Starter.pipelineDiagram[instr.id][currentClock] = "FPADD3";
	}	
}
