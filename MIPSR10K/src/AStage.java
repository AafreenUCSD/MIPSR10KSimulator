import java.util.Iterator;


public class AStage {
	public static int currentClock;
	public static Instruction instr;
	public static boolean isAStageBusy;
	public static Instruction toBeSentToMStage;
	//public static boolean[][] indeterminationMatrix = new boolean[16][16];
	public static boolean[][] dependencyMatrix = new boolean[16][16];
	
	public static void edge(int clock){
		if(instr!=null && instr.addressComputed){
			isAStageBusy = false;
			MStage.instr = toBeSentToMStage;
		}
			
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(instr!=null && !instr.done){
			isAStageBusy = true;
			//updateIndeterminationMatrix();
			//Decide using dependencyMatrix whether we can send this instruction to MStage
			if(!MStage.isMStageBusy){
				if(instr.type==TypeOfInstruction.valueOf("S")){
					if(allOlderStoresCommitted()){
						toBeSentToMStage = instr;
						instr.addressComputed = true;
						dumpState();
					}
				}
				else{
					if(allPriorLoadsComputedAddress() && allProducerStoresCommitted()){
						toBeSentToMStage = instr;
						instr.addressComputed = true;
						dumpState();
					}
				}
			}
		}
	}
	
	public static boolean allPriorLoadsComputedAddress() {
		Iterator<Instruction> it = DecodeUnit.addressQueue.iterator();
		while(it.hasNext()){
			Instruction older = it.next();
			if(older.type==TypeOfInstruction.L && older.id < instr.id && !older.addressComputed){
				return false;
			}
		}
		return true;
	}
	
	public static boolean allProducerStoresCommitted() {
		Iterator<Instruction> it = DecodeUnit.addressQueue.iterator();
		while(it.hasNext()){
			Instruction older = it.next();
			if(older.type==TypeOfInstruction.S && older.id < instr.id && instr.rs1 == older.rt1 && older.committed){
				return false;
			}
		}
		return true;
	}
	
	public static boolean allOlderStoresCommitted() {
		Iterator<Instruction> it = DecodeUnit.addressQueue.iterator();
		while(it.hasNext()){
			Instruction older = it.next();
			if(older.type==TypeOfInstruction.S && older.id < instr.id && older.committed){
				return false;
			}
		}
		return true;
	}
	
	/*
	public static boolean allProducerStoresCommitted() {
		for(int row = 0; row < DecodeUnit.addressQueue.size(); row++){
			Instruction older = DecodeUnit.addressQueue.get(row);
			if(older.type==TypeOfInstruction.valueOf("S") && older.id < instr.id && dependencyMatrix[instr.id][older.id]==true && !older.done){
				return false;
			}
		}
		return true;
	}

	public static boolean allOlderStoresCommitted() {
			for(int row=0; row<DecodeUnit.addressQueue.size(); row++){
				Instruction older = DecodeUnit.addressQueue.get(row);
				if(older.type== instr.type && older.id < instr.id && !older.done)
					return false;
			}
		return true;
	}
	*/
	
	/*
	public static void updateIndeterminationMatrix() {
		if(instr!=null){
			int index = DecodeUnit.addressQueue.indexOf(instr);
			for(int row=0; row<DecodeUnit.addressQueue.size(); row++){
				indeterminationMatrix[row][index] = false;
			}
		}
	}
	*/
	public static void dumpState(){
		Starter.pipelineDiagram[instr.id][currentClock] = "A";
	}
}
