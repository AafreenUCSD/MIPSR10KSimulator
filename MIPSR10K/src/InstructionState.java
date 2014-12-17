
public class InstructionState {
	String alternateBranchAddr;
	static int[] integerRegisterMapTable = new int[32];
	static int[] floatingRegisterMapTable = new int[33];
	
	public InstructionState(String alternateBranchAddr, int[] integerRegisterMapTable, int[] floatRegisterMapTable){
		this.alternateBranchAddr = alternateBranchAddr;
		for(int i=0; i<integerRegisterMapTable.length; i++){
			this.integerRegisterMapTable[i] = integerRegisterMapTable[i];
		}
		for(int i=0; i<floatingRegisterMapTable.length; i++){
			this.floatingRegisterMapTable[i] = floatingRegisterMapTable[i];
		}
	}
}
