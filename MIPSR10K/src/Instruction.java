
public class Instruction {
	TypeOfInstruction type;
	Register rs;
	Register rt;
	Register rd;
	String addr=null;
	String branchMask=null;
	boolean done=false;
	String tagInActiveList=null;
	//Register[] mappings=null;//record which physical registers are the logical rs, rt and rd mapped to.
	Register rs1;
	Register rt1;
	Register rd1;
	boolean isReady[]=null;//ready bit for each operand; needed in the instruction queue to know when the instruction is ready for issue
	
	public Instruction(String[] parts){
		type = TypeOfInstruction.valueOf(parts[0]);
		int rs1 = Integer.parseInt(parts[1]);
		int rs2 = Integer.parseInt(parts[2]);
		if(type==TypeOfInstruction.valueOf("I")){
			rs = new Register("int", rs1);
			rt = new Register("int", rs2);
		}
		else if(type==TypeOfInstruction.valueOf("A") || type==TypeOfInstruction.valueOf("M") ){
			rs = new Register("float", rs1);
			rt = new Register("float", rs2);
		}
		if(parts.length>3)
			addr = parts[3];
	}
	
	public void print(){
		System.out.println(rs.number+"->"+rs1.number);
		System.out.println(rt.number+"->"+rt1.number);
		System.out.println(rd.number+"->"+rd1.number);
	}
}
