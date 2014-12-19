
public class FPADD2 {
	static int currentClock;
	static int limit;
	static Instruction instr;
	static boolean isBusy;
	
	static Instruction toBeIssuedToFPADD3;

	public static void edge(int clock){
		FPADD3.instr = toBeIssuedToFPADD3;
		FPADD2.isBusy = false;
		if(instr!=null)
			DecodeUnit.floatingBusyBitTable[instr.rd1.number] = false;
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(instr!=null && !instr.done){
			FPADD2.isBusy = true;
			if(!FPADD3.isBusy){
				toBeIssuedToFPADD3 = instr;
				dumpState();
			}
		}
	}
	
	public static void dumpState(){
		Starter.pipelineDiagram[instr.id][currentClock] = "FPADD2";
	}
}
