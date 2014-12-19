
public class InstructionState {
	Instruction instr;
	String alternateBranchAddrSaved;
	int branchID;
	static Register[] integerRegisterMapTableSaved = new Register[32];
	static Register[] floatingRegisterMapTableSaved = new Register[33];
	
	public InstructionState(String alternateBranchAddr, Instruction instruction, Register[] integerRegisterMapTable, Register[] floatingRegisterMapTable){
		instr = instruction;
		alternateBranchAddrSaved = alternateBranchAddr;
		branchID = instruction.id;
		for(int i=0; i<integerRegisterMapTable.length; i++){
			integerRegisterMapTableSaved[i] = integerRegisterMapTable[i];
		}
		for(int i=0; i<floatingRegisterMapTable.length; i++){
			floatingRegisterMapTableSaved[i] = floatingRegisterMapTable[i];
		}
	}
}
