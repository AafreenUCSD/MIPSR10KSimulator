import java.util.*;

public class DecodeUnit {
	
	static List<String> sInstructions;
	static List<Instruction> instructions;
	static int currentClock;
	static int issueLimit;
	
	static Register R0;
	static Register rs1,rt1,rd1;
	
	// Using arraylists instead of queues since removals might happen in the
	// middle of the queue
	// This queue must poll the busyBitTable to check is the operands of an
	// instruction are all available so that the
	// instruction can be issued to the ExecutionUnit
	static ArrayList<Instruction> integerQueue = new ArrayList<Instruction>(); //16 entries
	static ArrayList<Instruction> floatingQueue = new ArrayList<Instruction>();//16 entries
	
	// Needs to be a CircularQueue
	//static ArrayList<Instruction> loadStoreQueue = new ArrayList<Instruction>();//16 entries
	static ArrayList<Instruction> addressQueue = new ArrayList<Instruction>();
	static Stack<InstructionState> branchStack = new Stack<InstructionState>();//4 entries

	static Register[] integerRegisterMapTable = new Register[32];//32 entries for 32 logical integer registers
	static Register[] floatingRegisterMapTable = new Register[33];//32 entries for 32 logical floating register

	static Queue<Register> integerFreeList = new LinkedList<Register>();//64 integer registers to start with
	static Queue<Register> floatingFreeList = new LinkedList<Register>();//64 floating registers to start with

	static boolean[] integerBusyBitTable = new boolean[64];//busy bits for each integer physical register
	static boolean[] floatingBusyBitTable = new boolean[64];//busy bits for each floating physical register

	// Remove instructions upon commit
	static ArrayList<Instruction> activeList = new ArrayList<Instruction>();

	public static void initFreeTables() {
		for (int i = 1; i < 64; i++) {
			// Think how to implement Hi and Lo registers
			integerFreeList.add(new Register("R", i));
			floatingFreeList.add(new Register("F", i - 1));
		}
	}

	public static void edge(int clock){
		//Send to instruction buffer if any of the queues or active list is full (Not sure..check this condition!)
		IssueUnit.instructions = instructions;
		if(instructions!=null)
		 writeToInstructionQueue();
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(sInstructions!=null && sInstructions.size()!=0){
			decodeAndRename();
			putIntoActiveList();
			dumpState();
			draw(clock);
		}
	}
	
	public static void draw(int clock){
		if(instructions!=null){
			for(int i=0;i <instructions.size(); i++){
				instructions.get(i).print();
			}
		}
	}
	
	public static void decodeAndRename() {
		instructions = new ArrayList<Instruction>();
		for (int i = 0; i < sInstructions.size(); i++) {
				Instruction instr = decode(sInstructions.get(i));
				rename(instr);
				appendBranchMask(instr);
				instructions.add(instr);
		}
	}
	
	public static void appendBranchMask(Instruction instr) {
		int i=0;
		for(InstructionState state : branchStack){
			instr.branchMask[i] = state.branchID;
			i++;
		}
	}

	public static Instruction decode(String sInstruction) {
		Instruction i;
		String[] parts = sInstruction.split(" ");
		i = new Instruction(parts);
		return i;
	}

	public static void rename(Instruction instr) {
		if (instr.type == TypeOfInstruction.valueOf("I")) {
			assignRegisterFromIntegerFreeList(instr);
		} else if (instr.type == TypeOfInstruction.valueOf("A")
				|| instr.type == TypeOfInstruction.valueOf("M")) {
			assignRegisterFromFloatingFreeList(instr);
		} else if (instr.type == TypeOfInstruction.valueOf("B")) {
			InstructionState state = new InstructionState(null,instr,integerRegisterMapTable, floatingRegisterMapTable);
			if (branchStack.size() < 4) {
				branchStack.push(state);
			} else {
				//Stall decoding until one of the branches resolves. Decode resumes in the same cycle as this branch is resolved.
			}
		} else if(instr.type == TypeOfInstruction.valueOf("L")){
			assignRegistersToLoad(instr);
		} else if(instr.type == TypeOfInstruction.valueOf("S")){
			assignRegistersToStore(instr);
		}
	}

	public static void assignRegistersToLoad(Instruction instr) {
		if(integerRegisterMapTable[instr.rs.number]==null){
			rs1 = integerFreeList.remove();
			integerRegisterMapTable[instr.rs.number] = rs1;
		}
		else{
			rs1 = integerRegisterMapTable[instr.rs.number];
		}
		instr.rs1 = rs1;
		
		rt1 = integerFreeList.remove();
		integerRegisterMapTable[instr.rt.number] = rt1;
		integerBusyBitTable[rt1.number] = true;
		instr.rt1 = rt1;
		
		instr.rd1 = R0;
	}
	
	public static void assignRegistersToStore(Instruction instr) {
		if(integerRegisterMapTable[instr.rs.number]==null){
			rs1 = integerFreeList.remove();
			integerRegisterMapTable[instr.rs.number] = rs1;
		}
		else{
			rs1 = integerRegisterMapTable[instr.rs.number];
		}
		instr.rs1 = rs1;
		
		if(integerRegisterMapTable[instr.rt.number]==null){
			rt1 = integerFreeList.remove();
			integerRegisterMapTable[instr.rt.number] = rt1;
		}
		else{
			rt1 = integerRegisterMapTable[instr.rt.number];
		}
		instr.rt1 = rt1;
	}

	public static void assignRegisterFromIntegerFreeList(Instruction instr) {
		if(integerRegisterMapTable[instr.rs.number]==null){
			rs1 = integerFreeList.remove();
			integerRegisterMapTable[instr.rs.number] = rs1;
		}
		else{
			rs1 = integerRegisterMapTable[instr.rs.number];
		}
		instr.rs1 = rs1;
		
		if(integerRegisterMapTable[instr.rt.number]==null){
			rt1 = integerFreeList.remove();
			integerRegisterMapTable[instr.rt.number] = rt1;
		}
		else{
			rt1 = integerRegisterMapTable[instr.rt.number];
		}
		instr.rt1 = rt1;
		
		if(instr.rd.number==0){ //if destination is R0
			integerRegisterMapTable[instr.rd.number] = R0;
			instr.rd1 = R0;
		}
		else{
			rd1 = integerFreeList.remove();
			integerRegisterMapTable[instr.rd.number] = rd1;
			integerBusyBitTable[rd1.number] = true;
			instr.rd1 = rd1;
		}		
	}

	public static void assignRegisterFromFloatingFreeList(Instruction instr) {
		if(floatingRegisterMapTable[instr.rs.number]==null){
			rs1 = floatingFreeList.remove();
			floatingRegisterMapTable[instr.rs.number] = rs1;
		}
		else{
			rs1 = floatingRegisterMapTable[instr.rs.number];
		}
		instr.rs1 = rs1;
		
		if(floatingRegisterMapTable[instr.rt.number]==null){
			rt1 = floatingFreeList.remove();
			floatingRegisterMapTable[instr.rt.number] = rt1;
		}
		else{
			rt1 = floatingRegisterMapTable[instr.rt.number];
		}
		instr.rt1 = rt1;
		
		rd1 = floatingFreeList.remove();
		floatingRegisterMapTable[instr.rd.number] = rd1;
		floatingBusyBitTable[rd1.number] = true;	
		instr.rd1 = rd1;
	}

	public static void putIntoActiveList() {
		for (int i = 0; i < instructions.size(); i++) {
			if(activeList.size()<16){
				activeList.add(instructions.get(i));
			}
			else{
				//put into instruction buffer
			}
		}
	}

	public static void writeToInstructionQueue() {
		for (int i = 0; i < instructions.size(); i++) {
			Instruction instr = instructions.get(i);
			if(instr!=null){
			if (instr.type == TypeOfInstruction.valueOf("I")
					|| instr.type == TypeOfInstruction.valueOf("B")) {
				if (integerQueue.size() < 16) {
					integerQueue.add(instr);
				} else {
					// put into instruction buffer
				}
			} else if ((instr.type == TypeOfInstruction.valueOf("L") || instr.type == TypeOfInstruction.valueOf("S"))) {
				if (addressQueue.size() < 16) {
					addressQueue.add(instr);
				} else {
					// put into instruction buffer (the same one as above?)
				}
			} else if ((instr.type == TypeOfInstruction.valueOf("A") || instr.type == TypeOfInstruction.valueOf("M"))) {
				if (floatingQueue.size() < 16) {
					floatingQueue.add(instr);
				}
			}
			}
		}
	}
	
	public static void flush(Instruction branch){
		flushFromIntegerQueue(branch);
		flushFromFloatingQueue(branch);
		flushFromAddressQueue(branch);
	}

	public static void flushFromAddressQueue(Instruction branch) {
		Instruction instr;
		Iterator<Instruction> it = DecodeUnit.addressQueue.iterator();
		while(it.hasNext()){
			instr = it.next();
			if(instr.id > branch.id){
				it.remove();
			}
		}
	}
	
	public static void flushFromFloatingQueue(Instruction branch) {
		Instruction instr;
		Iterator<Instruction> it = DecodeUnit.integerQueue.iterator();
		while(it.hasNext()){
			instr = it.next();
			if(instr.id > branch.id){
				it.remove();
			}
		}
	}

	public static void flushFromIntegerQueue(Instruction branch) {
		Instruction instr;
		Iterator<Instruction> it = DecodeUnit.floatingQueue.iterator();
		while(it.hasNext()){
			instr = it.next();
			if(instr.id > branch.id){
				it.remove();
			}
		}
	}
	
	public static void removeFromActiveList(Instruction branch) {
		Instruction instr;
		Iterator<Instruction> it = DecodeUnit.activeList.iterator();
		while(it.hasNext()){
			instr = it.next();
			if(instr.id > branch.id){
				it.remove();
			}
		}
	}

	public static void dumpState(){
		for(int i=0; i<instructions.size(); i++){
			if(instructions.get(i)!=null && !instructions.get(i).done)
				Starter.pipelineDiagram[instructions.get(i).id][currentClock] = "D"; 
		}
	}
}
