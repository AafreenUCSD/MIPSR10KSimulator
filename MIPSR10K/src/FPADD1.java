
public class FPADD1 {
	static int currentClock;
	static int limit;
	static Instruction instr;
	static boolean isBusy;
	
	static Instruction toBeIssuedToFPADD2;

	public static void edge(int clock){
		FPADD2.instr = toBeIssuedToFPADD2;
		FPADD1.isBusy = false;
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(instr!=null && !instr.done){
			FPADD1.isBusy = true;
			if(!FPADD2.isBusy){
				toBeIssuedToFPADD2 = instr;
				dumpState();
			}
		}
	}
	
	public static void dumpState(){
			Starter.pipelineDiagram[instr.id][currentClock] = "FPADD1";
	}
}
