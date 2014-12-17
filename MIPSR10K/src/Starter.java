import java.io.*;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class Starter{
	
	static String traceFile;
	static int issueLimit = 4;
	static int clockCycles = 100;
	static BufferedReader reader;
	static int readFrom = 0;
	static String[] instructions;
	
	 //create object of table and tablem model
	 static JTable jTable = new JTable();
	 
	 public Starter(){
		 /*
		 DefaultTableModel dtm = new DefaultTableModel(0, 0);

		 //add header of the table
		 String header[] = new String[] { "1","2","3","4","5","6","7","8","9","10" };

		 //add header in table model
		 dtm.setColumnIdentifiers(header);
		 //set model into the table object
		 tbl.setModel(dtm);
		 */
		 
		 /*From Java ranch
		 TableColumnModel tcm = jTable.getColumnModel();
		 //The TableColumnModel represents the form for every column in your table. 
		 TableColumn tc = tcm.getColumn(0);
		 //TableColumn represents the form for a particular column.
		 tc.setHeaderValue("My New Value");
		 */
	 }
	 
	public static void main(String[] args) {
		/*
		 * Input the trace file and other parameters to the fetch unit to begin
		 * simulation. The input format should be: Name of trace file, Number of
		 * instructions issued at a time, Total number of clock cycles
		 */

		parse(args);
		initialize();
		// String[] sInstructions=null;
		// Instruction[] instructions=null;

		// DecodeUnit.initFreeTables();
		// sInstructions = fetch();
		// instructions = DecodeUnit.decodeAndRename(sInstructions, clock,
		// issueLimit);
		// DecodeUnit.putIntoActiveList(instructions, clock, issueLimit);
		// DecodeUnit.writeToQueue(instructions, clock, issueLimit);

		/*
		 * The idea behind calc and edge: The calc stage does all the main
		 * computation, The edge stage is the transition to the next cycle. We
		 * will keep a global calc() and edge(). And the global calc() will call
		 * the calc() of all the stages. Similarly, the global edge() will call
		 * the edge() of all the stages. So each stage would record what
		 * instruction is using it in that particular cycle. This is extremely
		 * helpful when printing pipeline diagram and debugging as well.
		 */
		for (int clock = 1; clock <= clockCycles; clock++) {
			calc(clock);
			draw(clock);
			edge(clock);
			readFrom = readFrom + issueLimit;
		}
	}

	public static void initialize() {
		FetchUnit.issueLimit = issueLimit;
		FetchUnit.reader = reader;
		DecodeUnit.issueLimit = issueLimit;
		DecodeUnit.initFreeTables();
		/*
		IssueUnit.limit = issueLimit;
		FPADD1.limit = issueLimit;
		CommitUnit.limit = issueLimit;
		*/
	}

	public static void calc(int clock) {
		FetchUnit.calc(clock, readFrom);
		DecodeUnit.calc(clock);
		/*
		FPADD1.calc(clock);
		FPADD2.calc(clock);
		FPADD3.calc(clock);
		FPMUL1.calc(clock);
		FPMUL2.calc(clock);
		FPMUL3.calc(clock);
		EStage.calc(clock);
		AStage.calc(clock);
		CommitUnit.calc(clock);
		*/
	}

	public static void edge(int clock) {
		FetchUnit.edge(clock);
		DecodeUnit.edge(clock);
		/*
		FPADD1.edge(clock);
		FPADD2.edge(clock);
		FPADD3.edge(clock);
		FPMUL1.edge(clock);
		FPMUL2.edge(clock);
		FPMUL3.edge(clock);
		EStage.edge(clock);
		AStage.edge(clock);
		CommitUnit.edge(clock);
		*/
	}
	
	public static void parse(String[] args){
		
		if(args[0].isEmpty()){
			System.out.println("No trace file provided. Aborting!...");
			System.exit(0);
		}
		
		traceFile = args[0];
		System.out.println("Provided trace file: "+traceFile);
		try {
			reader = new BufferedReader(new FileReader(traceFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!args[1].isEmpty()){
			issueLimit = Integer.parseInt(args[1]);
		}
		
		if(!args[2].isEmpty()){
			clockCycles = Integer.parseInt(args[2]);
		}
	}

	public static void draw(int clock){
		FetchUnit.draw(clock);
		DecodeUnit.draw(clock);
	}
}
