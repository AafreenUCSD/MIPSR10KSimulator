
public class FPMUL1 {
	static int currentClock;
	static int limit;
	static Instruction instr;
	static boolean isBusy;
	
	static Instruction toBeIssuedToFPMUL2;

	public static void edge(int clock){
		FPMUL2.instr = toBeIssuedToFPMUL2;
		FPMUL1.isBusy = false;
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(instr!=null && !instr.done){
			FPMUL1.isBusy = true;
			if(!FPMUL2.isBusy){
				toBeIssuedToFPMUL2 = instr;
				dumpState();
			}
		}
	}
	
	public static void dumpState(){
		Starter.pipelineDiagram[instr.id][currentClock] = "FPMUL1";
	}
}
