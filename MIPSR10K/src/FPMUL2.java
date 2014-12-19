
public class FPMUL2 {
	static int currentClock;
	static int limit;
	static Instruction instr;
	static boolean isBusy;
	
	static Instruction toBeIssuedToFPMUL3;
	
	public static void edge(int clock){
		FPMUL3.instr = toBeIssuedToFPMUL3;
		FPMUL2.isBusy = false;
		if(instr!=null)
			DecodeUnit.floatingBusyBitTable[instr.rd1.number] = false;
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(instr!=null && !instr.done){
			FPMUL2.isBusy = true;
			if(!FPMUL3.isBusy){
				toBeIssuedToFPMUL3 = instr;
				dumpState();
			}
		}
	}
	
	public static void dumpState(){
		Starter.pipelineDiagram[instr.id][currentClock] = "FPMUL2";
	}
}
