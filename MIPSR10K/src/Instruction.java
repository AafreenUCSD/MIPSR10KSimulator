
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
		int rs_input = Integer.parseInt(parts[1]);
		int rt_input = Integer.parseInt(parts[2]);
		int rd_input = Integer.parseInt(parts[2]);
		if(type==TypeOfInstruction.valueOf("I")){
			rs = new Register("R", rs_input);
			rt = new Register("R", rt_input);
			rd = new Register("R", rd_input);
		}
		else if(type==TypeOfInstruction.valueOf("A") || type==TypeOfInstruction.valueOf("M") ){
			rs = new Register("F", rs_input);
			rt = new Register("F", rt_input);
			rd = new Register("F", rd_input);
		}
		if(parts.length>3)
			addr = parts[3];
	}
	
	public void print(){
		System.out.println(rs.type+rs.number+"->"+rs1.type+rs1.number);
		System.out.println(rt.type+rt.number+"->"+rt1.type+rt1.number);
		System.out.println(rd.type+rd.number+"->"+rd1.type+rd1.number);
	}
}
