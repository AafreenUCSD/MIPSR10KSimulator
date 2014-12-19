import java.util.Iterator;

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
		if(fromAddressQueue!=null && fromAddressQueue.committed)
			DecodeUnit.addressQueue.remove(fromAddressQueue);
	}
	
	public static void calc(int clock){
		currentClock = clock;
		count = 0;
		if(fromALU1!=null && fromALU1.done &&!fromALU1.committed && isInorderCommit(fromALU1) && count<4){
			dumpState1();
			count++;
			fromALU1.committed = true;
			DecodeUnit.activeList.remove(fromALU1);
			DecodeUnit.integerFreeList.add(fromALU1.rd1);
		}
		if(fromALU2!=null && fromALU2.done &&!fromALU2.committed && isInorderCommit(fromALU2) && count<4){
			dumpState2();
			count++;
			fromALU2.committed = true;
			DecodeUnit.activeList.remove(fromALU2);
			DecodeUnit.integerFreeList.add(fromALU2.rd1);
		}
		if(fromFPADD3!=null && fromFPADD3.done && !fromFPADD3.committed && isInorderCommit(fromFPADD3) && count<4){
			dumpState3();
			count++;
			fromFPADD3.committed = true;
			DecodeUnit.activeList.remove(fromFPADD3);
			DecodeUnit.floatingFreeList.add(fromFPADD3.rd1);
		}
		if(fromFPMUL3!=null && fromFPMUL3.done && !fromFPMUL3.committed && isInorderCommit(fromFPMUL3) && count<4){
			dumpState4();
			count++;
			fromFPMUL3.committed = true;
			DecodeUnit.activeList.remove(fromFPMUL3);
			DecodeUnit.floatingFreeList.add(fromFPMUL3.rd1);
		}
		if(fromAddressQueue!=null && fromAddressQueue.done && isInorderCommit(fromAddressQueue) && !fromAddressQueue.committed  && count<4){
			dumpState5();
			count++;
			fromAddressQueue.committed = true;
			DecodeUnit.activeList.remove(fromAddressQueue);
			//updateDependencyMatrix();
			DecodeUnit.floatingFreeList.add(fromAddressQueue.rt1);
		}
		count = 0;
	}
	
	public static boolean isInorderCommit(Instruction instr){
		Instruction other;
		for(int i=0 ;i< DecodeUnit.activeList.size(); i++){
				other = DecodeUnit.activeList.get(i);
				if(other.id < instr.id && !other.committed){
					return false;
				}
		}
		return true;
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