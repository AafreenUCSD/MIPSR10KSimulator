public class FPMUL3 {
	static int currentClock;
	static int limit;
	static Instruction instr;
	static boolean isBusy;
	static Instruction toBeCommitted;

	public static void edge(int clock){
		CommitUnit.fromFPMUL3 = toBeCommitted;
		FPMUL3.isBusy = false;
		if(instr!=null)
		instr.done = true;
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(instr!=null && !instr.committed){
			FPMUL3.isBusy = true;
			//Check if it can be passed to the writeback stage
			toBeCommitted = instr;
			dumpState();
			//DecodeUnit.floatingBusyBitTable[instr.rd1.number] = false;
		}
	}
	
	public static void dumpState(){
		Starter.pipelineDiagram[instr.id][currentClock] = "FPMUL3";
	}
}