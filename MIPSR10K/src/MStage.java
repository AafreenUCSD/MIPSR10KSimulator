//Memory access stage for load store instructions
public class MStage {
	public static int currentClock;
	public static Instruction instr;
	public static Instruction toBeSentToCommit;
	public static boolean isMStageBusy;
	
	public static void edge(int clock){
		CommitUnit.fromAddressQueue = toBeSentToCommit;
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(instr!=null && !instr.done){
			isMStageBusy = true;
			toBeSentToCommit = instr;
			dumpState();
		}
	}
	
	public static void dumpState(){
		Starter.pipelineDiagram[instr.id][currentClock] = "M";
	}
}
