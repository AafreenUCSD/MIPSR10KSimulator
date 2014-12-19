import java.util.Iterator;


//This class models the Integer Execution Unit
public class EStage {
	static int currentClock;
	static int limit;
	static Instruction inALU1;
	static Instruction inALU2;
	static boolean isALU1Busy = false;
	static boolean isALU2Busy = false;
	
	public static void edge(int clock){
		if(inALU1!=null && inALU1.done){
			DecodeUnit.integerBusyBitTable[inALU1.rd1.number] = false;
			CommitUnit.fromALU1 = inALU1;
			isALU1Busy = false;
		}
		if(inALU2!=null && inALU2.done){
			DecodeUnit.integerBusyBitTable[inALU2.rd1.number] = false;
			CommitUnit.fromALU2 = inALU2;
			isALU2Busy = false;
		}
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(EStage.inALU1!=null && !inALU1.done){
			isALU1Busy = true;
			inALU1.done = true;
			if(inALU1.type==TypeOfInstruction.B){
				//If it is a branch, use the misprediction bit to know how to resolve it
				if(inALU1.correctPrediction){
					removeBranchIDFromBranchMaskOfDependentInstructions();
				}
				else{
					handleMispredictedBranch(inALU1);
				}
			}
			dumpState1();
		}
		if(EStage.inALU2!=null && !inALU2.done){
			isALU2Busy = true;
			inALU2.done = true;
			dumpState2();
		}
		
	}

	public static void removeBranchIDFromBranchMaskOfDependentInstructions() {
		for(int i=0; i<DecodeUnit.activeList.size(); i++){
			Instruction instr = DecodeUnit.activeList.get(i);
			if(instr.id > inALU1.id)
				instr.branchMask.remove(inALU1);
		}
	}

	public static void handleMispredictedBranch(Instruction instr) {
		InstructionState state = retrieveStateFromBranchStack(instr);
		//abortInstructionsAfterBranch(state.branchID);
	}

	public static InstructionState retrieveStateFromBranchStack(Instruction instr) {
		InstructionState state;
		do{
		state = DecodeUnit.branchStack.pop();
		}while(state.branchID != instr.id);
		DecodeUnit.integerRegisterMapTable = state.integerRegisterMapTableSaved;
		DecodeUnit.floatingRegisterMapTable = state.floatingRegisterMapTableSaved;
		return state;
	}

	public static void abortInstructionsAfterBranch(int branchID) {
		setDestinationRegistersFree();
		flush();
	}

	public static void flush() {
		FetchUnit.flush();
		DecodeUnit.flush(inALU1);
		removeFromExecutionUnits();
		DecodeUnit.removeFromActiveList(inALU1);
	}

	public static void removeFromExecutionUnits() {
		EStage.removeFromStage();
		/*
		FPADD1.removeFromStage();
		FPADD2.removeFromStage();
		FPADD3.removeFromStage();
		FPMUL1.removeFromStage();
		FPMUL2.removeFromStage();
		FPMUL3.removeFromStage();
		*/
	}

	public static void removeFromStage() {
		if(inALU2.id > inALU1.id){
			inALU2 = null;
		}
	}

	public static void setDestinationRegistersFree() {
		Instruction instr;
		for(int i=0; i< DecodeUnit.activeList.size(); i++){
			instr = DecodeUnit.activeList.get(i);
			if(instr.id >= inALU1.id){
				DecodeUnit.integerBusyBitTable[inALU1.rd1.number] = false;
				DecodeUnit.integerFreeList.add(inALU1.rd1);
			}
		}
	}
	
	public static void dumpState1(){
		Starter.pipelineDiagram[inALU1.id][currentClock] = "E";
	}
	
	public static void dumpState2(){
		Starter.pipelineDiagram[inALU2.id][currentClock] = "E";
	}
}
