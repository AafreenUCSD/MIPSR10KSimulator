import java.util.ArrayList;	
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class IssueUnit {
	public static int currentClock;
	static int limit;
	public static List<Instruction> instructions;
	public static Instruction toBeIssuedToALU1, toBeIssuedToALU2, toBeIssuedToAddressCalculation;
	
	public static Instruction toBeIssuedToFPADD1;
	public static Instruction toBeIssuedToFPMUL1;
	
	public static void edge(int clock){
		//Send to appropriate FU based on instruction type
		//Remove from queues; except address queue
		EStage.inALU1 = toBeIssuedToALU1;
		EStage.inALU2 = toBeIssuedToALU2;
		AStage.instr = toBeIssuedToAddressCalculation;
		FPADD1.instr = toBeIssuedToFPADD1;
		FPMUL1.instr = toBeIssuedToFPMUL1;

		DecodeUnit.integerQueue.remove(toBeIssuedToALU1);
		DecodeUnit.integerQueue.remove(toBeIssuedToALU2);
		DecodeUnit.floatingQueue.remove(toBeIssuedToFPMUL1);
		DecodeUnit.floatingQueue.remove(toBeIssuedToFPADD1);
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(instructions!=null && instructions.size()!=0){
			checkIntegerOperandReadinessAndFUAvailability();
			checkFloatingOperandReadinessAndFUAvailability();
			issueLoadStoreInstruction();
			dumpState();
		}
	}

	public static void issueLoadStoreInstruction() {
		//Put any newly added instructions from addressQueue into the matrices.
		//See which instructions can be issued together using indetermination and dependency matrices
		//We will use the index of the instruction in the loadStore queue as the index into the dependency matrix
		/*
		Iterator<Instruction> it = DecodeUnit.addressQueue.iterator();
		while(it.hasNext()){
			Instruction instr = it.next();
			toBeIssuedToAddressCalculation = instr;
			break;
		}
		*/
		//for(int i=0; i<addressQueue.size(); i++){
		if(!DecodeUnit.addressQueue.isEmpty()){
			Instruction instr = (Instruction) DecodeUnit.addressQueue.remove();
			DecodeUnit.addressQueue.add(instr);
			if(instr.type==TypeOfInstruction.valueOf("L")){
				if(isRsReady(instr) && !AStage.isAStageBusy){
						toBeIssuedToAddressCalculation = instr;
						//break;
				}
			} else{
				if(isRsReady(instr) && isRtReady(instr) && !AStage.isAStageBusy){
					toBeIssuedToAddressCalculation = instr;
					//break;
				}
			}
		}
	}

	public static boolean isRtReady(Instruction instr) {
		return !DecodeUnit.floatingBusyBitTable[instr.rt1.number] || instr.rt1==DecodeUnit.R0;
	}

	public static boolean isRsReady(Instruction instr) {
		return !DecodeUnit.floatingBusyBitTable[instr.rs1.number] || instr.rs1==DecodeUnit.R0;
	}

	public static void checkIntegerOperandReadinessAndFUAvailability() {
		//Issue stage is when the 4 decoded instructions get written to the integer queue.
		//And EStage is when some instructions leave that queue and enter a functional unit.
		
		//Find the first branch in the integer queue 
		//whose operands are available
		//Assign it to ALU1 if ALU1 is free
		boolean gotBranch = false;
		boolean gotALU1 = false;
		
		for(int i=0; i<DecodeUnit.integerQueue.size(); i++){
			Instruction instr = DecodeUnit.integerQueue.get(i);
			if(instr.type==TypeOfInstruction.valueOf("B")){
				if(isRsIntReady(instr) && isRtIntReady(instr)){
					if(!EStage.isALU1Busy){
						toBeIssuedToALU1 = instr;
						gotBranch = true;
						break;
					}
				}
			}
		}
		
		//Find the first non-branch in the integer queue 
		//whose operands are available
		//Assign it to ALU2 if ALU2 is free
		//else to ALU1 if ALU1 is free
		if(!gotBranch){
		for(int i=0; i<DecodeUnit.integerQueue.size(); i++){
			Instruction instr = DecodeUnit.integerQueue.get(i);
			if(instr.type==TypeOfInstruction.valueOf("I")){
				if(isRsIntReady(instr) && isRtIntReady(instr)){
					if(!EStage.isALU1Busy){
						toBeIssuedToALU1 = instr;
						gotALU1 = true;
						break;
					}
				}
			}
		}
		}
		
		if(gotALU1){
			DecodeUnit.integerQueue.remove(toBeIssuedToALU1);
		for(int i=0; i<DecodeUnit.integerQueue.size(); i++){
			Instruction instr = DecodeUnit.integerQueue.get(i);
			if(instr.type==TypeOfInstruction.valueOf("I")){
				if(isRsIntReady(instr) && isRtIntReady(instr)){
					if(!EStage.isALU2Busy){
						toBeIssuedToALU2 = instr;
						break;
					}
				}
			}
		}
		}
	}
	
	public static boolean isRsIntReady(Instruction instr) {
		return !DecodeUnit.integerBusyBitTable[instr.rs1.number] || instr.rs1==DecodeUnit.R0; 
	}
	
	public static boolean isRtIntReady(Instruction instr) {
		return !DecodeUnit.integerBusyBitTable[instr.rt1.number] || instr.rt1==DecodeUnit.R0; 
	}

	public static void checkFloatingOperandReadinessAndFUAvailability() {
		for(int i=0; i<DecodeUnit.floatingQueue.size(); i++){
			Instruction instr = DecodeUnit.floatingQueue.get(i);
			if(isRsReady(instr) && isRtReady(instr)){
					if(instr.type==TypeOfInstruction.valueOf("A")){
						if(!FPADD1.isBusy){
							toBeIssuedToFPADD1 = instr;
							break;
						}
					}
				}			
		}
		
		for(int i=0; i<DecodeUnit.floatingQueue.size(); i++){
			Instruction instr = DecodeUnit.floatingQueue.get(i);
			if(isRsReady(instr) && isRtReady(instr)){
				if(instr.type==TypeOfInstruction.valueOf("M") && !instr.done){
					if(!FPMUL1.isBusy){
						toBeIssuedToFPMUL1 = instr;
						break;
					}
				}
			}			
		}
	}

	public static void dumpState(){
		for(int i=0; i<instructions.size(); i++){
			if(!instructions.get(i).done)
				Starter.pipelineDiagram[instructions.get(i).id][currentClock] = "I"; 
		}
	}
}