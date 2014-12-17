import java.util.*;

public class DecodeUnit {
	
	static String[] sInstructions;
	static Instruction[] instructions;
	static int currentClock;
	static int issueLimit;
	
	// Using arraylists instead of queues since removals might happen in the
	// middle of the queue
	// This queue must poll the busyBitTable to check is the operands of an
	// instruction are all available so that the
	// instruction can be issued to the ExecutionUnit
	static ArrayList<Instruction> integerQueue = new ArrayList<Instruction>();
	static ArrayList<Instruction> floatingQueue = new ArrayList<Instruction>();
	// Needs to be a CircularQueue
	static ArrayList<Instruction> addressQueue = new ArrayList<Instruction>();
	static Stack<InstructionState> branchStack = new Stack<InstructionState>();

	static int[] integerRegisterMapTable = new int[32];
	static int[] floatingRegisterMapTable = new int[33];

	static Queue<Register> integerFreeList = new LinkedList<Register>();
	static Queue<Register> floatingFreeList = new LinkedList<Register>();

	static boolean[] integerBusyBitTable = new boolean[64];
	static boolean[] floatingBusyBitTable = new boolean[64];

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
		IssueUnit.instructions = instructions;
	}
	
	public static void calc(int clock){
		currentClock = clock;
		if(sInstructions!=null){
			decodeAndRename();
			putIntoActiveList();
			writeToQueue();
			checkOperandReadinessAndFUAvailability();
		}
	}
	
	public static void draw(int clock){
		if(instructions!=null){
			for(int i=0;i <instructions.length; i++){
				instructions[i].print();
			}
		}
	}
	
	public static void checkOperandReadinessAndFUAvailability() {
		//check if an instruction can be issued
	}

	public static void decodeAndRename() {
		instructions = new Instruction[issueLimit];
		for (int i = 0; i < sInstructions.length; i++) {
			Instruction instr = decode(sInstructions[i]);
			rename(instr);
			instructions[i] = instr;
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
			InstructionState state = new InstructionState(null,
					integerRegisterMapTable, floatingRegisterMapTable);
			if (branchStack.size() < 4) {
				branchStack.push(state);
			} else {
				// stop decoding
			}
		}
	}

	public static void assignRegisterFromIntegerFreeList(Instruction instr) {
		Register r1 = integerFreeList.remove();
		Register r2 = integerFreeList.remove();
		Register r3 = integerFreeList.remove();
		instr.rs1 = r1;
		instr.rt1 = r2;
		instr.rd1 = r3;
		integerBusyBitTable[r1.number] = true;
		integerBusyBitTable[r2.number] = true;
		integerBusyBitTable[r3.number] = true;
	}

	public static void assignRegisterFromFloatingFreeList(Instruction instr) {
		Register r1 = floatingFreeList.remove();
		Register r2 = floatingFreeList.remove();
		Register r3 = floatingFreeList.remove();
		instr.rs1 = r1;
		instr.rt1 = r2;
		instr.rd1 = r3;
		floatingBusyBitTable[r1.number] = true;
		floatingBusyBitTable[r2.number] = true;
		floatingBusyBitTable[r3.number] = true;
	}

	public static void putIntoActiveList() {
		for (int i = 0; i < instructions.length; i++) {
			activeList.add(instructions[i]);
		}
	}

	public static void writeToQueue() {
		for (int i = 0; i < instructions.length; i++) {
			Instruction instr = instructions[i];
			if (instr.type == TypeOfInstruction.valueOf("I")
					|| instr.type == TypeOfInstruction.valueOf("B")) {
				if (integerQueue.size() < 16) {
					integerQueue.add(instr);
				} else {
					// put into instruction buffer
				}
			} else if ((instr.type == TypeOfInstruction.valueOf("L") || instr.type == TypeOfInstruction
					.valueOf("S"))) {
				if (addressQueue.size() < 16) {
					addressQueue.add(instr);
				} else {
					// put into instruction buffer (the same one as above?)
				}
			} else if ((instr.type == TypeOfInstruction.valueOf("A") || instr.type == TypeOfInstruction
					.valueOf("M"))) {
				if (floatingQueue.size() < 16) {
					floatingQueue.add(instr);
				}
			}
		}
	}

}
