import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

//By Mo & David
//class Controller 
//A thread is a thread of execution in a program. The JavaVirtual Machine allows an application to have multiple threads of execution running concurrently
public class Controller extends Thread {
	// UI
	private SimGui gui;
	// Sim Memory
	protected Memory memory;
	// Interrupts
	private Interrupts intrr;
	// Timer from Class Timer
	private Timer tmr;
	// Processor "Threads"
	private Processor prc;
	// Frequence "Quarz Frequency"
	protected int Frequency = 500;
	// Running Time
	protected double runtime;
	// WD
	protected int wDog;
	// ifSleep
	protected boolean isSleep = false;
	//Nop Cycle
	protected boolean isNop = false;

	public void commandCycle() {
		this.runtime = this.runtime + (4000 / this.getFrequency());
	}

	public double getRuntime() {
		return runtime;
	}

	// is that program running? when no actions => false
	protected boolean running = false;
	// Execute speed
	private int simspeed = 500;
	protected boolean[] debugLine = new boolean[1024];
	protected int[] pcLine = new int[1024];

	public int getSimspeed() {
		return simspeed;
	}

	public void setSimspeed(int simspeed) {
		this.simspeed = simspeed;
	}

	// A Controller every time the application starts with a memory where the code
	// and commands saved
	public Controller(SimGui pGui) {
		this.gui = pGui;
		this.memory = new Memory(this);
		this.intrr = new Interrupts(this);
		this.tmr = new Timer(this);
	}

	protected SimGui getGui() {
		return gui;
	}

	protected void initialize() {
		this.initializeMemory();
	}

	// initialize Memory to show in GPR Table and write a row with i * 8 in Hex
	private void initializeMemory() {
		for (int i = 0; i < 32; i++) {
			// Initialize Memory Cells
			this.gui.tbl_Gpr.addRow(new Object[] { Integer.toHexString(i * 8), "", "", "", "", "", "", "", "" });
		}
		this.gui.tbl_SpecialRegs.addRow(new Object[] { "W_Reg", "", "" });
		this.gui.tbl_SpecialRegs.addRow(new Object[] { "Status", "", "" });
		this.gui.tbl_SpecialRegs.addRow(new Object[] { "FSR", "", "" });
		this.gui.tbl_SpecialRegs.addRow(new Object[] { "PCL", "", "" });
		this.gui.tbl_SpecialRegs.addRow(new Object[] { "PCLATCH", "", "" });
		this.gui.tbl_SpecialRegs.addRow(new Object[] { "PC", "", "" });
		this.gui.tbl_SfrRegs.addRow(new Object[] { "Status:", "IRP", "RP1", "RP0", "TO", "PD", "Z", "DC", "C" });
		this.gui.tbl_SfrRegs.addRow(new Object[] { "", "", "", "", "", "", "", "", "" });
		this.gui.tbl_SfrRegs.addRow(new Object[] { "Option:", "RPu", "IEg", "TCs", "TSe", "PSA", "PS2", "PS1", "PS0" });
		this.gui.tbl_SfrRegs.addRow(new Object[] { "", "", "", "", "", "", "", "", "" });
		this.gui.tbl_SfrRegs.addRow(new Object[] { "Intcon:", "GIE", "EIE", "TIE", "IE", "RIE", "TIF", "IF", "RIF" });
		this.gui.tbl_SfrRegs.addRow(new Object[] { "", "", "", "", "", "", "", "", "" });
		this.gui.tbl_Stack.addRow(new Object[] { "" });
		this.gui.tbl_Stack.addRow(new Object[] { "" });
		this.gui.tbl_Stack.addRow(new Object[] { "" });
		this.gui.tbl_Stack.addRow(new Object[] { "" });
		this.gui.tbl_Stack.addRow(new Object[] { "" });
		this.gui.tbl_Stack.addRow(new Object[] { "" });
		this.gui.tbl_Stack.addRow(new Object[] { "" });
		this.gui.tbl_Stack.addRow(new Object[] { "" });
	}

	public void updateFSRTable() {
		this.gui.tbl_SpecialRegs.setValueAt(Integer.toHexString(this.memory.getwRegister()), 0, 1);
		this.gui.tbl_SpecialRegs.setValueAt(Integer.toBinaryString(this.memory.getwRegister()), 0, 2);
		this.gui.tbl_SpecialRegs.setValueAt(Integer.toHexString(this.memory.readRegisterDirect(0x03)), 1, 1);
		this.gui.tbl_SpecialRegs.setValueAt(Integer.toBinaryString(this.memory.readRegisterDirect(0x03)), 1, 2);
		this.gui.tbl_SpecialRegs.setValueAt(Integer.toHexString(this.memory.readRegisterDirect(0x04)), 2, 1);
		this.gui.tbl_SpecialRegs.setValueAt(Integer.toBinaryString(this.memory.readRegisterDirect(0x04)), 2, 2);
		this.gui.tbl_SpecialRegs.setValueAt(Integer.toHexString(this.memory.readRegisterDirect(0x02)), 3, 1);
		this.gui.tbl_SpecialRegs.setValueAt(Integer.toBinaryString(this.memory.readRegisterDirect(0x02)), 3, 2);
		this.gui.tbl_SpecialRegs.setValueAt(Integer.toHexString(this.memory.readRegisterDirect(0x0A)), 4, 1);
		this.gui.tbl_SpecialRegs.setValueAt(Integer.toBinaryString(this.memory.readRegisterDirect(0x0A)), 4, 2);
		this.gui.tbl_SpecialRegs.setValueAt(Integer.toHexString(this.memory.getProgramCounter()), 5, 1);
		this.gui.tbl_SpecialRegs.setValueAt(Integer.toBinaryString(this.memory.getProgramCounter()), 5, 2);

		this.gui.tbl_SfrRegs.setValueAt((this.memory.readRegisterDirect(0x03) & 0b00000001), 1, 8);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x03) >> 1) & 0b00000001), 1, 7);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x03) >> 2) & 0b00000001), 1, 6);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x03) >> 3) & 0b00000001), 1, 5);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x03) >> 4) & 0b00000001), 1, 4);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x03) >> 5) & 0b00000001), 1, 3);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x03) >> 6) & 0b00000001), 1, 2);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x03) >> 7) & 0b00000001), 1, 1);
		// Option Register
		this.gui.tbl_SfrRegs.setValueAt((this.memory.readRegisterDirect(0x81) & 0b00000001), 3, 8);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x81) >> 1) & 0b00000001), 3, 7);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x81) >> 2) & 0b00000001), 3, 6);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x81) >> 3) & 0b00000001), 3, 5);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x81) >> 4) & 0b00000001), 3, 4);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x81) >> 5) & 0b00000001), 3, 3);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x81) >> 6) & 0b00000001), 3, 2);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x81) >> 7) & 0b00000001), 3, 1);
		// INTCON
		this.gui.tbl_SfrRegs.setValueAt((this.memory.readRegisterDirect(0x0B) & 0b00000001), 5, 8);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x0B) >> 1) & 0b00000001), 5, 7);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x0B) >> 2) & 0b00000001), 5, 6);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x0B) >> 3) & 0b00000001), 5, 5);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x0B) >> 4) & 0b00000001), 5, 4);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x0B) >> 5) & 0b00000001), 5, 3);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x0B) >> 6) & 0b00000001), 5, 2);
		this.gui.tbl_SfrRegs.setValueAt(((this.memory.readRegisterDirect(0x0B) >> 7) & 0b00000001), 5, 1);
	}

//Input Function Button Input
	public void input() {
		try {
			String hex = this.gui.getTxt_regValue().getText();
			int valuetochange = Integer.parseInt(hex, 16);
			int changeAddress = this.gui.returnChangeAddress();
			if (valuetochange > 255) {
				valuetochange = 255;
			}
			this.memory.writeRegisterDirect(changeAddress, valuetochange);
		} catch (NumberFormatException nfe) {
			// not a valid hex
			System.out.println("Not a valid hex");
		}
	}

	// Open the text file and save the content in different arrays (substrings)
	protected void openLstFile(File pFile) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(pFile));
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * PC: program counter where the code starts Code: the command mask in hex line:
		 * line count (Number) Label: like start, end etc.. mnemonic: Mnemonic symbols
		 * and Comments
		 */
		String st;
		while ((st = br.readLine()) != null) {
			System.out.println(st);

			String pc = st.substring(0, 4);
			String code = st.substring(5, 9);
			String line = st.substring(20, 25);
			String label = "";
			// see if there is a label at all
			if (st.charAt(27) != ' ') {
				int label_index = 27;
				while (st.charAt(label_index) != ' ') {
					label = label + st.charAt(label_index);
					label_index++;
				}
			}
			// mnemonics
			String mnemonic = st.substring(36);
			// code view: add a Row to define the information
			this.gui.tbl_CodeView.addRow(new Object[] { " ", pc, code, line, label, mnemonic });
			// when PC not empty
			// Parses the string argument as a signed integer in the radix specified by the
			// second argument
			if (pc.equals("    ") == false) {
				// This is Line but in execute
				this.memory.programMemory[Integer.parseInt(pc, 16)] = Integer.parseInt(code, 16);
				this.pcLine[Integer.parseInt(pc, 16)] = Integer.parseInt(line, 10);
				this.debugLine[Integer.parseInt(pc, 16)] = false;
			}

		}
		System.out.println("Programm memory loaded");
	}

	public void updateFrequency(String dropdownSelection) {
		this.Frequency = Integer.parseInt(dropdownSelection); 
	}

	protected void writeDestination(int d, int f, int result) {
		if (d == 0) {
			this.memory.setwRegister(result);
		} else {
			this.memory.writeRegister(f, result);
		}
	}

	// Commands execute function
	protected void executeCommand(int line) {
		System.out.println("Command " + Integer.toHexString(line));
		// Decoder Page 56 "Themenblatt_Simulator"
		int precommand = (line >> 12) & 0x0003;

		if (precommand == 0) {
			int command = (line >> 8) & 0x000f;
			int payload = line & 0x00ff;
			this.precommandZero(command, payload);
		} else if (precommand == 1) {
			int command = (line >> 10) & 0x0003;
			int payload = line & 0x03ff;
			this.precommandOne(command, payload);
		} 
		else if (precommand == 2) {
			int command = (line >> 11) & 0x0001;
			int k = line & 0x07ff;
			
			if (command == 0) 		{ this.CALL(k); } 
			else if (command == 1) 	{ this.GOTO(k);	}	
		} 
		else if (precommand == 3) {
			int command = (line >> 8) & 0x000f;
			int k = line & 0x00ff;
			this.precommandThree(command, k);
		} 
		
		// Runtime
		this.getGui().lblRunTime.setText(this.getRuntime() + " �s");
		System.out.println(this.getRuntime());
	}
	
	private void precommandZero(int command, int payload) {
		int d = payload >> 7 & 0x0001;
		int f = payload & 0b01111111;
		if (f == 0x00 | f == 0x80) {
			f = this.memory.readRegisterDirect(0x04);
		}
		
		switch (command) {
		case 7:
			this.ADDWF(f, d); 
			break;
		case 5:
			this.ANDWF(f, d); 
			break;
		case 1:
			if (d == 1) {this.CLRF(f);}
			else		{this.CLRW();}
			break;
		case 9:
			this.COMF(f, d); 
			break;
		case 3:
			this.DECF(f, d); 
			break;
		case 11:
			this.DECFSZ(f, d);
			break;
		case 10:
			this.INCF(f, d);
			break;
		case 15:
			this.INCFSZ(f, d);
			break;
		case 4:
			this.IORWF(f, d);
			break;
		case 8:
			this.MOVF(f, d);
			break;
		case 0:
			if (d == 1) { this.MOVWF(f); } 
			else {
				switch (payload) {
				case 0b01100100:
					this.CLRWDT();
					break;
				case 0b00001001:
					this.RETFIE();
					break;
				case 0b00001000:
					this.RETURN();
					break;
				case 0b01100011:
					this.SLEEP();
					break;
				default:
					this.NOP();
					break;
				}
			}
		case 13:
			this.RLF(f, d);
			break;
		case 12:
			this.RRF(f, d);
			break;
		case 2:
			this.SUBWF(f, d);
			break;
		case 14:
			this.SWAPF(f, d);
			break;
		case 6:
			this.XORWF(f, d);
			break;
		}
	}

	private void precommandOne(int command, int payload) {
		int b = (payload >> 7) & 0x0007;
		int f = payload & 0x007f;
		if (f == 0x00 | f == 0x80) {
			f = this.memory.readRegisterDirect(0x04);
		}
		
		switch (command) {
		case 0:
			this.BCF(f, b);
			break;
		case 1:
			this.BSF(f, b);
			break;
		case 2:
			this.BTFSC(f, b);
			break;
		case 3:
			this.BTFSS(f, b);
			break;
		}
	}
	
	private void precommandThree(int command, int k) {
		if (command == 8) {
			this.IORLW(k);
		} 
		else if ((command >> 1) == 7) {
			this.ADDLW(k);
		} 
		else if (command == 9) {
			this.ANDLW(k);
		} 
		else if ((command >> 2) == 0) {
			this.MOVLW(k);
		} 
		else if ((command >> 2) == 1) {
			this.RETLW(k);
		} 
		else if (command == 10) {
			this.XORLW(k);
		} 
		else if ((command >> 1) == 6) {
			this.SUBLW(k);
		} 
		else {
			this.NOP();
		}
	}
	
	protected void executeCmd(Instruction instruction) {
		String command = instruction.getMnemonic();
		int d = instruction.getParameterD();
		int f = instruction.getParameterF();
		int b = instruction.getParameterB();
		int k = instruction.getParameterK();
		
		if (f == 0x00 | f == 0x80) {
			f = this.memory.readRegisterDirect(0x04);
		}
		
		//instructions ordered according to PIC documentation, page 56
		switch (command) {
		//Byte-oriented file register operations, see PIC docu
		case "ADDWF": 
			this.ADDWF(f, d);
			break;
		case "ANDWF": 
			this.ANDWF(f, d);
			break;
		case "CLRF":
			this.CLRF(f);
			break;
		case "CLRW": 
			this.CLRW();
			break;
		case "COMF": 
			this.COMF(f, d);
			break;
		case "DECF": 
			this.DECF(f, d);
			break;
		case "DECFSZ": 
			this.DECFSZ(f, d);
			break;
		case "INCF": 
			this.INCF(f, d);
			break;
		case "INCFSZ": 
			this.INCFSZ(f, d);
			break;
		case "IORWF": 
			this.IORWF(f, d);
			break;
		case "MOVF": 
			this.MOVF(f, d);
			break;
		case "MOVWF": 
			this.MOVWF(f);
			break;
		case "NOP": 
			this.NOP();
			break;
		case "RLF": 
			this.RLF(f, d);
			break;
		case "RRF": 
			this.RRF(f, d);
			break;
		case "SUBWF": 
			this.SUBWF(f, d);
			break;
		case "SWAPF": 
			this.SWAPF(f, d);
			break;
		case "XORWF": 
			this.XORWF(f, d);
			break;
		
		//Bit-oriented file register operations, see PIC docu
		case "BCF": 
			this.BCF(f, b);
			break;
		case "BSF": 
			this.BSF(f, b);
			break;
		case "BTFSC": 
			this.BTFSC(f, b);
			break;
		case "BTFSS": 
			this.BTFSS(f, b);
			break;
			
		//Literal and control operations, see PIC docu
		case "ADDLW": 
			this.ADDLW(k);
			break;
		case "ANDLW": 
			this.ANDLW(k);
			break;
		case "CALL": 
			this.CALL(k);
			break;
		case "CLRWDT": 
			this.CLRWDT();
			break;
		case "GOTO": 
			this.GOTO(k);
			break;
		case "IORLW": 
			this.IORLW(k);
			break;
		case "MOVLW": 
			this.MOVLW(k);
			break;
		case "RETFIE": 
			this.RETFIE();
			break;
		case "RETLW": 
			this.RETLW(k);
			break;
		case "RETURN": 
			this.RETURN();
			break;
		case "SLEEP": 
			this.SLEEP();
			break;
		case "SUBLW": 
			this.SUBLW(k);
			break;
		case "XORLW": 
			this.XORLW(k);
			break;
		}
		

		this.getGui().lblRunTime.setText(this.getRuntime() + " �s");
	}
	
	private void ADDWF(int f, int d) {
		System.out.println("ADDWF");
		int w = this.memory.getwRegister();
		int fValue = this.memory.readRegister(f);
		int result = w + fValue;
		
		if (result > 255) {
			this.memory.setCarryFlag(1);
			result = result - 256;
		} else {
			this.memory.setCarryFlag(0);
		}
		
		this.writeDestination(d, f, result);
		this.memory.checkDCFlag(w, fValue);
		this.memory.checkZFlag(result);
		this.memory.incPC();
	}

	private void ANDWF(int f, int d) {
		System.out.println("ANDWF");
		int w = this.memory.getwRegister();
		int fValue = this.memory.readRegister(f);
		int result = w & fValue;
		this.writeDestination(d, f, result);
		this.memory.checkZFlag(result);
		this.memory.incPC();

	}

	private void CLRF(int f) {
		System.out.println("CLRF");
		this.memory.writeRegister(f, 0);
		this.memory.checkZFlag(0);
		this.memory.incPC();

	}

	private void CLRW() {
		System.out.println("CLRW");
		this.memory.setwRegister(0);
		this.memory.checkZFlag(0);
		this.memory.incPC();
	}

	private void COMF(int f, int d) {
		System.out.println("COMF");
		// XOR with 0b11111111
		int result = this.memory.readRegister(f) ^ 0b11111111;
		this.writeDestination(d, f, result);
		this.memory.checkZFlag(result);
		this.memory.incPC();
	}

	private void DECF(int f, int d) {
		System.out.println("DECF");
		int result = this.memory.readRegister(f);
		if (result > 0) {
			result--;
		} else {
			result = 0xFF;
		}
		this.writeDestination(d, f, result);
		this.memory.checkZFlag(result);
		this.memory.incPC();
	}

	private void DECFSZ(int f, int d) {
		System.out.println("DECFSZ");
		int fValue = this.memory.readRegister(f);
		if (fValue == 0) {
			fValue = 255;
		} else {
			fValue--;
			if (fValue == 0) {
				this.memory.incPC();
				this.isNop = true;
			}
		}

		this.writeDestination(d, f, fValue);
		this.memory.incPC();
	}

	private void INCF(int f, int d) {
		System.out.println("INCF");
		int result = this.memory.readRegister(f);
		System.out.println(f);
		if (result == 255) {
			result = 0;
		} else {
			result++;
		}
		this.writeDestination(d, f, result);
		this.memory.checkZFlag(result);
		this.memory.incPC();
	}

	private void INCFSZ(int f, int d) {
		System.out.println("INCFSZ");
		int fValue = this.memory.readRegister(f);
		if (fValue == 255) {
			fValue = 0;
			this.memory.incPC();
			this.isNop = true;
		} else {
			fValue++;
		}
		this.writeDestination(d, f, fValue);
		this.memory.incPC();

	}

	private void IORWF(int f, int d) {
		System.out.println("IORWF");
		int w = this.memory.getwRegister();
		int fValue = this.memory.readRegister(f);
		int result = w | fValue;
		this.writeDestination(d, f, result);
		this.memory.checkZFlag(result);
		this.memory.incPC();
	}

	private void MOVF(int f, int d) {
		System.out.println("MOVF");
		int result = this.memory.readRegister(f);
		this.writeDestination(d, f, result);
		this.memory.checkZFlag(result);
		this.memory.incPC();
	}

	private void MOVWF(int f) {
		System.out.println("MOVWF");
		int w = this.memory.getwRegister();
		this.memory.writeRegister(f, w);
		this.memory.incPC();
	}

	private void NOP() {
		System.out.println("NOP");
		this.memory.incPC();
	}

	private void RLF(int f, int d) {
		System.out.println("RLF");
		int fValue = this.memory.readRegister(f);
		int cValue = this.memory.readRegisterDirect(0x03) & 0b00000001;
		if ((fValue & 128) == 128) {
			this.memory.setCarryFlag(1);
		} else {
			this.memory.setCarryFlag(0);
		}
		// Rotate left
		int result = ((fValue << 1) & 0xff) | (cValue & 1);
		this.writeDestination(d, f, result);
		this.memory.incPC();
	}

	private void RRF(int f, int d) {
		System.out.println("RRF");
		int fValue = this.memory.readRegister(f);
		int cValue = this.memory.readRegisterDirect(0x03) & 0b00000001;
		// Minimum Value
		if ((fValue & 1) == 1) {
			this.memory.setCarryFlag(1);
		} else {
			this.memory.setCarryFlag(0);
		}
		// Rotate Right
		int result = (fValue >> 1) | (cValue << 7);
		this.writeDestination(d, f, result);
		this.memory.incPC();
	}

	private void SUBWF(int f, int d) {
		System.out.println("SUBWF");
		int w = this.memory.getwRegister();
		int fValue = this.memory.readRegister(f);
		int result;
		if (w > fValue) {
			// to remove the negation
			result = 256 - (w - fValue);
		} else {
			result = fValue - w;
		}
		if (fValue - w < 0) {
			this.memory.setCarryFlag(0);
		} else {
			this.memory.setCarryFlag(1);
		}
		if (fValue - w > 15) {
			this.memory.setDCFlag(1);
		} else {
			this.memory.setDCFlag(0);
		}
		this.memory.checkZFlag(result);
		this.writeDestination(d, f, result);
		this.memory.incPC();

	}

	private void SWAPF(int f, int d) {
		System.out.println("SWAPF");
		int nibh, nibl;
		int fValue = this.memory.readRegister(f);
		nibh = (fValue & 0b11110000) >> 4;
		nibl = (fValue & 0b00001111) << 4;
		int result = nibh + nibl;
		this.writeDestination(d, f, result);
		this.memory.incPC();
	}

	private void XORWF(int f, int d) {
		System.out.println("XORWF");
		int w = this.memory.getwRegister();
		int fValue = this.memory.readRegister(f);
		int result = (w ^ fValue);
		this.writeDestination(d, f, result);
		this.memory.checkZFlag(result);
		this.memory.incPC();

	}

	private void BCF(int f, int b) {
		System.out.println("BCF");
		int fValue = this.memory.readRegister(f);
		// Bit Mask 255
		int bitMask = 0xff;
		// shift Mask 1
		int shiftMask = 0x01;
		shiftMask = shiftMask << b;
		bitMask = shiftMask ^ bitMask;
		fValue = fValue & bitMask;
		this.memory.writeRegister(f, fValue);
		this.memory.incPC();
	}

	private void BSF(int f, int b) {
		System.out.println("BSF");
		int fValue = this.memory.readRegister(f);
		// Bit Mask 1
		int bitMask = 0x01;
		bitMask = bitMask << b;
		fValue = fValue | bitMask;
		this.memory.writeRegister(f, fValue);
		this.memory.incPC();
	}

	private void BTFSC(int f, int b) {
		System.out.println("BTFSC");
		int fValue = this.memory.readRegister(f);
		int bitMask = 0x01;
		bitMask = bitMask << b;
		fValue = fValue & bitMask;
		if (fValue == 0) {
			this.memory.incPC();
			this.isNop = true;
		}
		this.memory.incPC();
	}

	private void BTFSS(int f, int b) {
		System.out.println("BTFSS");
		int fValue = this.memory.readRegister(f);
		int bitMask = 0x01;
		bitMask = bitMask << b;
		fValue = fValue & bitMask;
		if ((fValue >> b) == 1) {
			this.memory.incPC();
			this.isNop = true;
		}
		this.memory.incPC();
	}

	private void ADDLW(int k) {

		System.out.println("ADDLW");
		int w = this.memory.getwRegister();
		int result = w + k;
		if (result > 255) {
			// When result > 255
			result = result - 256;
			this.memory.setCarryFlag(1);
		} else {
			result = k + w;
			this.memory.setCarryFlag(0);
		}

		this.memory.checkZFlag(result);
		this.memory.checkDCFlag(w, k);
		this.memory.setwRegister(result);
		this.memory.incPC();
	}

	private void ANDLW(int k) {
		System.out.println("ANDLW");
		int w = this.memory.getwRegister() & k;
		this.memory.checkZFlag(w);
		this.memory.setwRegister(w);
		this.memory.incPC();

	}

	private void CALL(int k) {
		System.out.println("CALL");
		int pc = this.memory.getProgramCounter();
		this.memory.pushToStack(pc + 1);
		this.memory.setProgramCounter(k);
		this.isNop = true;

	}

	private void CLRWDT() {
		System.out.println("CLRWDT");
		this.wDog = 0;
		this.tmr.setPrescaler(0);
		this.memory.setBitRegisterSpecial(0x03, 3, 1);
		this.memory.setBitRegisterSpecial(0x03, 4, 1);
	}

	private void GOTO(int k) {
		System.out.println("GOTO");
		this.memory.setProgramCounter(k);
		this.isNop = true;
	}

	private void IORLW(int k) {
		System.out.println("IORLW");
		int w = this.memory.getwRegister() | k;
		this.memory.checkZFlag(w);
		this.memory.setwRegister(w);
		this.memory.incPC();
	}

	private void MOVLW(int k) {
		System.out.println("MOVLW");
		this.memory.setwRegister(k);
		this.memory.incPC();
	}

	private void RETFIE() {
		System.out.println("RETFIE");
		int tos = this.memory.popFromStack();
		this.memory.setProgramCounter(tos);
		int intcon = this.memory.readRegisterDirect(0x0B);
		this.memory.setBitRegisterSpecial(intcon, 7, 1);
		this.isNop = true;
	}

	private void RETLW(int k) {
		System.out.println("RETLW");
		this.memory.setwRegister(k);
		int tos = this.memory.popFromStack();
		this.memory.setProgramCounter(tos);
		this.isNop = true;

	}

	private void RETURN() {
		System.out.println("RETURN");
		int tos = this.memory.popFromStack();
		this.memory.setProgramCounter(tos);
		this.isNop = true;

	}

	private void SLEEP() {
		System.out.println("SLEEP");
		this.wDog = 0;
		this.tmr.setPrescaler(0);
		this.memory.setBitRegisterSpecial(0x03, 3, 0);
		this.memory.setBitRegisterSpecial(0x03, 4, 1);
		this.isSleep = true;

	}

	private void SUBLW(int k) {
		System.out.println("SUBLW");
		int w = this.memory.getwRegister();
		int result;
		if (w > k) {
			// to remove the negation
			result = 256 - (w - k);
			// this is actually wrong "page 13 in the thematic sheet" but the developers
			// have screwed up
			this.memory.setCarryFlag(0);
			this.memory.checkZFlag(result);
			this.memory.setwRegister(result);
		} else {
			result = k - w;
			this.memory.setCarryFlag(1);
			this.memory.checkZFlag(result);
			this.memory.setwRegister(result);
		}

		this.memory.checkDCFlag(k, w);
		this.memory.incPC();
	}

	private void XORLW(int k) {
		System.out.println("XORLW");
		int w = this.memory.getwRegister();
		int result = (w ^ k);
		this.memory.setwRegister(result);
		this.memory.checkZFlag(result);
		this.memory.incPC();

	}

	public void stopctr() {
		if (this.running) {
			this.running = false;
			this.prc.exit = true;
		}
	}

	public void startSimu() {
		if (this.running == false) {
			this.prc = new Processor(this);
			this.running = true;
			this.prc.start();
		} else {
			if (this.prc.isDebugging == true) {
				this.prc.isDebugging = false;
				this.prc.nextStep = true;
			}
		}
	}

	public Timer getTmr() {
		return tmr;
	}

	public Interrupts getIntrr() {
		return intrr;
	}

	// Reset
	public void reset() {
		this.memory.powerOn();
		this.memory.setProgramCounter(0);
		this.memory.setwRegister(0);
		this.memory.stack.clear();
		this.runtime = 0;

	}

	// MCLR-Pin (master clear)
	public void mclr() {
		this.isSleep = false;
		this.prc.mclr = true;
		// this.memory.powerOn();
		this.memory.setProgramCounter(this.memory.getProgramCounter() + 1);
		// this.memory.setwRegister(0);
		// this.memory.stack.clear();
		this.runtime = 0;

	}

	public void clearProgrammMemory() {
		for (int i = 0; i < 1024; i++) {
			this.memory.programMemory[i] = 0x3FFF;
		}
	}

	public void clearPCLine() {
		for (int i = 0; i < 1024; i++) {
			this.pcLine[i] = 0;
		}
	}

	public void clearDebugLine() {
		for (int i = 0; i < 1024; i++) {
			this.debugLine[i] = false;
		}
	}

	// Clear Code View
	public void clearCodeTable() {
		int rowCount = this.gui.tbl_CodeView.getRowCount();
		// Remove rows one by one from the end of the table
		for (int i = rowCount - 1; i >= 0; i--) {
			this.gui.tbl_CodeView.removeRow(i);
		}
	}

	// set Activeline ti Highlight
	public void setActiveline() {
		int pc = this.memory.getProgramCounter();
		int line = this.pcLine[pc];
		this.gui.setRowHighlight(line);
	}

	// Breakpoints array if true --> false, false --> true in Debug Array
	public void setBreakpoint(int pc) {
		this.debugLine[pc] = !this.debugLine[pc];
	}

	// Single Steps
	public void singleStep() {
		if (this.running) {
			this.prc.nextStep = true;
		}
	}

	// Refresh I/O Pins
	public void refreshPins() {
		int trisa = this.memory.readRegisterDirect(0x85);
		int trisb = this.memory.readRegisterDirect(0x86);
		int dataa = this.memory.datalatcha;
		int datab = this.memory.datalatchb;
		this.getGui().setTrisA(trisa);
		this.getGui().setTrisB(trisb);
		int ra = getGui().getPortA();
		int rb = getGui().getPortB();
		for (int i = 0; i < 8; i++) {
			if ((trisa & 0x01) == 1) {
				this.memory.setBitRegisterSpecial(0x05, i, ra & 0x01);
			} else {
				this.memory.setBitRegisterSpecial(0x05, i, dataa & 0x01);
			}
			dataa = dataa >> 1;
			trisa = trisa >> 1;
			ra = ra >> 1;
			if ((trisb & 0x01) == 1) {
				this.memory.setBitRegisterSpecial(0x06, i, rb & 0x01);
			} else {
				this.memory.setBitRegisterSpecial(0x06, i, datab & 0x01);
			}
			datab = datab >> 1;
			trisb = trisb >> 1;
			rb = rb >> 1;
		}
		getGui().setPortA(this.memory.readRegisterDirect(0x05));
		getGui().setPortB(this.memory.readRegisterDirect(0x06));
	}


	public int getFrequency() {
		return Frequency;
	}

	public void setFrequency(int frequency) {
		Frequency = frequency;
	}

	public void wakeUp() {
		this.isSleep = false;
	}
	public void updateStack(Stack<Integer> stack) {
		for(int i = 0; i < stack.size(); i++)
		{
			this.getGui().tbl_Stack.setValueAt(stack.elementAt(i), i, 0);
		}
	}
}