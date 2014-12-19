public class CommitUnit {
	/*
	 * Need to ensure in-order commits using active list == reorder buffer
	 */
	static int currentClock;
	static int limit;
	static Instruction fromALU1;
	static Instruction fromALU2;
	static Instruction fromFPADD3;
	static Instruction fromFPMUL3;
	static Instruction fromAddressQueue;
	static int count=0;
	
	public static void edge(int clock){
		
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(fromALU1!=null && !fromALU1.done){
			dumpState1();
			count++;
			fromALU1.done = true;
			DecodeUnit.integerFreeList.add(fromALU1.rd1);
		}
		if(fromALU2!=null && !fromALU2.done){
			dumpState2();
			count++;
			fromALU2.done = true;
			DecodeUnit.integerFreeList.add(fromALU2.rd1);
		}
		if(fromFPADD3!=null && isInorderCommit(fromFPADD3)){
			dumpState3();
			count++;
			fromFPADD3.done = true;
			DecodeUnit.activeList.remove(fromFPADD3);
			DecodeUnit.integerFreeList.add(fromFPADD3.rd1);
		}
		if(fromFPMUL3!=null && isInorderCommit(fromFPMUL3)){
			dumpState4();
			count++;
			//fromFPMUL3.done = true;
			DecodeUnit.activeList.remove(fromFPMUL3);
			DecodeUnit.integerFreeList.add(fromFPMUL3.rd1);
		}
		if(fromAddressQueue!=null && !fromAddressQueue.done && count<4 && isInorderCommit(fromAddressQueue)){
			dumpState5();
			count++;
			fromAddressQueue.done = true;
			IssueUnit.addressQueue.remove(fromAddressQueue);
			DecodeUnit.activeList.remove(fromAddressQueue);
			//updateDependencyMatrix();
			DecodeUnit.integerFreeList.add(fromAddressQueue.rd1);
		}
		count = 0;
	}
	
	public static boolean isInorderCommit(Instruction instr){
		Instruction other;
		for(int i=0 ;i< DecodeUnit.activeList.size(); i++){
				other = DecodeUnit.activeList.get(i);
				if(other.id < instr.id && !other.done){
					return false;
				}
		}
		return true;
	}

	public static void updateDependencyMatrix() {
		if(fromAddressQueue!=null && fromAddressQueue.type==TypeOfInstruction.valueOf("S")){
			int index = DecodeUnit.addressQueue.indexOf(fromAddressQueue);
			for(int row=0; row<DecodeUnit.addressQueue.size(); row++){
				AStage.dependencyMatrix[row][index] = false;
			}
		}
	}
	
	public static void dumpState1(){
		Starter.pipelineDiagram[fromALU1.id][currentClock] = "C"; 
	}
	
	public static void dumpState2(){
		Starter.pipelineDiagram[fromALU2.id][currentClock] = "C"; 
	}
	
	public static void dumpState3(){
		Starter.pipelineDiagram[fromFPADD3.id][currentClock] = "C"; 
	}
	
	public static void dumpState4(){
		Starter.pipelineDiagram[fromFPMUL3.id][currentClock] = "C"; 
	}
	
	public static void dumpState5(){
		Starter.pipelineDiagram[fromAddressQueue.id][currentClock] = "C"; 
	}
}