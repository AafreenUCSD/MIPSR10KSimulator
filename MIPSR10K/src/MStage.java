//Memory access stage for load store instructions
public class MStage {
	public static int currentClock;
	public static Instruction instr;
	public static Instruction toBeSentToCommit;
	public static boolean isMStageBusy;
	
	public static void edge(int clock){
		if(instr!=null && instr.done){
			DecodeUnit.integerBusyBitTable[instr.rt1.number] = false;
			CommitUnit.fromAddressQueue = toBeSentToCommit;
			isMStageBusy = false;
		}
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(instr!=null && instr.addressComputed &&!instr.committed){
			isMStageBusy = true;
			instr.done = true;
			toBeSentToCommit = instr;
			dumpState();
		}
	}
	
	public static void dumpState(){
		Starter.pipelineDiagram[instr.id][currentClock] = "M";
	}
}
