import java.util.ArrayList;
import java.util.List;


public class Instruction {
	int id;
	TypeOfInstruction type;
	Register rs;
	Register rt;
	Register rd;
	String addr=null;
	boolean addressComputed;
	boolean correctPrediction=true;
	boolean done=false;
	boolean committed;
	String tagInActiveList=null;
	//Register[] mappings=null;//record which physical registers are the logical rs, rt and rd mapped to.
	Register rs1;
	Register rt1;
	Register rd1;
	//boolean isReady[]=null;//ready bit for each operand; needed in the instruction queue to know when the instruction is ready for issue
	boolean isReadyRs1=true;
	boolean isReadyRt1=true;
	boolean isReadyRd1;
	List<Instruction> branchMask = new ArrayList<Instruction>();
	
	public Instruction(String[] parts){
		id = Integer.parseInt(parts[0]);
		type = TypeOfInstruction.valueOf(parts[1]);
		int rs_input = Integer.parseInt(parts[2]);
		int rt_input = Integer.parseInt(parts[3]);
		int rd_input = Integer.parseInt(parts[4]);
		if(type==TypeOfInstruction.I){ 
			rs = Starter.logicalIntegerRegisterFile[rs_input];
			rt = Starter.logicalIntegerRegisterFile[rt_input];
			rd = Starter.logicalIntegerRegisterFile[rd_input];
		}
		else if(type==TypeOfInstruction.L || type==TypeOfInstruction.S || type==TypeOfInstruction.B){
			rs = Starter.logicalIntegerRegisterFile[rs_input];
			rt = Starter.logicalIntegerRegisterFile[rt_input];
			rd = DecodeUnit.R0;
		}
		else if(type==TypeOfInstruction.A || type==TypeOfInstruction.M ){
			rs = Starter.logicalFloatingRegisterFile[rs_input];
			rt = Starter.logicalFloatingRegisterFile[rt_input];
			rd = Starter.logicalFloatingRegisterFile[rd_input];
		}
		if(parts.length==6){
			if(type==TypeOfInstruction.B){
				if(parts[5]=="1"){
					correctPrediction = false;
				}
			}
			else
				addr = parts[5];
		}
	}
	
	public void print(){
		/*
		System.out.println(rs.type+rs.number+"->"+rs1.type+rs1.number);
		System.out.println(rt.type+rt.number+"->"+rt1.type+rt1.number);
		System.out.println(rd.type+rd.number+"->"+rd1.type+rd1.number);
		*/
		System.out.println(rd1.type+rd1.number+" <- "+rs1.type+rs1.number+" OP "+rt1.type+rt1.number);
		if(type==TypeOfInstruction.B)
			System.out.println(correctPrediction);
	}
	
}
