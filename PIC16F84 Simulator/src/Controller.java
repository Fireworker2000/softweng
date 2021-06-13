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
	private SimGui myGUI;
	// Sim Memory
	protected Memory myMemory;
	// Interrupts
	private Interrupts myInterruptHandler;
	// Timer from Class Timer
	private Timer myTimerHandler;
	// Processor "Threads"
	private Processor myProcessor;
	// Frequency "Quarz Frequency"
	protected int quartzFrequency = 500;
	// Running Time
	protected double runtime;
	// WD
	protected int watchdog;
	// ifSleep
	protected boolean isSleep = false;
	//NOP Cycle
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
		this.myGUI = pGui;
		this.myMemory = new Memory(this);
		this.myInterruptHandler = new Interrupts(this);
		this.myTimerHandler = new Timer(this);
	}

	protected SimGui getGui() {
		return myGUI;
	}

	protected void initialize() {
		this.initializeMemory();
	}

	// initialize Memory to show in GPR Table and write a row with i * 8 in Hex
	private void initializeMemory() {
		for (int i = 0; i < 32; i++) {
			// Initialize Memory Cells
			this.myGUI.tbl_Gpr.addRow(new Object[] { Integer.toHexString(i * 8), "", "", "", "", "", "", "", "" });
		}
		this.myGUI.tbl_SpecialRegs.addRow(new Object[] { "W_Reg", "", "" });
		this.myGUI.tbl_SpecialRegs.addRow(new Object[] { "Status", "", "" });
		this.myGUI.tbl_SpecialRegs.addRow(new Object[] { "FSR", "", "" });
		this.myGUI.tbl_SpecialRegs.addRow(new Object[] { "PCL", "", "" });
		this.myGUI.tbl_SpecialRegs.addRow(new Object[] { "PCLATCH", "", "" });
		this.myGUI.tbl_SpecialRegs.addRow(new Object[] { "PC", "", "" });
		this.myGUI.tbl_SfrRegs.addRow(new Object[] { "Status:", "IRP", "RP1", "RP0", "TO", "PD", "Z", "DC", "C" });
		this.myGUI.tbl_SfrRegs.addRow(new Object[] { "", "", "", "", "", "", "", "", "" });
		this.myGUI.tbl_SfrRegs.addRow(new Object[] { "Option:", "RPu", "IEg", "TCs", "TSe", "PSA", "PS2", "PS1", "PS0" });
		this.myGUI.tbl_SfrRegs.addRow(new Object[] { "", "", "", "", "", "", "", "", "" });
		this.myGUI.tbl_SfrRegs.addRow(new Object[] { "Intcon:", "GIE", "EIE", "TIE", "IE", "RIE", "TIF", "IF", "RIF" });
		this.myGUI.tbl_SfrRegs.addRow(new Object[] { "", "", "", "", "", "", "", "", "" });
		this.myGUI.tbl_Stack.addRow(new Object[] { "" });
		this.myGUI.tbl_Stack.addRow(new Object[] { "" });
		this.myGUI.tbl_Stack.addRow(new Object[] { "" });
		this.myGUI.tbl_Stack.addRow(new Object[] { "" });
		this.myGUI.tbl_Stack.addRow(new Object[] { "" });
		this.myGUI.tbl_Stack.addRow(new Object[] { "" });
		this.myGUI.tbl_Stack.addRow(new Object[] { "" });
		this.myGUI.tbl_Stack.addRow(new Object[] { "" });
	}

	public void updateFSRTable() {
		this.myGUI.tbl_SpecialRegs.setValueAt(Integer.toHexString(this.myMemory.getwRegister()), 0, 1);
		this.myGUI.tbl_SpecialRegs.setValueAt(Integer.toBinaryString(this.myMemory.getwRegister()), 0, 2);
		this.myGUI.tbl_SpecialRegs.setValueAt(Integer.toHexString(this.myMemory.readRegisterDirect(0x03)), 1, 1);
		this.myGUI.tbl_SpecialRegs.setValueAt(Integer.toBinaryString(this.myMemory.readRegisterDirect(0x03)), 1, 2);
		this.myGUI.tbl_SpecialRegs.setValueAt(Integer.toHexString(this.myMemory.readRegisterDirect(0x04)), 2, 1);
		this.myGUI.tbl_SpecialRegs.setValueAt(Integer.toBinaryString(this.myMemory.readRegisterDirect(0x04)), 2, 2);
		this.myGUI.tbl_SpecialRegs.setValueAt(Integer.toHexString(this.myMemory.readRegisterDirect(0x02)), 3, 1);
		this.myGUI.tbl_SpecialRegs.setValueAt(Integer.toBinaryString(this.myMemory.readRegisterDirect(0x02)), 3, 2);
		this.myGUI.tbl_SpecialRegs.setValueAt(Integer.toHexString(this.myMemory.readRegisterDirect(0x0A)), 4, 1);
		this.myGUI.tbl_SpecialRegs.setValueAt(Integer.toBinaryString(this.myMemory.readRegisterDirect(0x0A)), 4, 2);
		this.myGUI.tbl_SpecialRegs.setValueAt(Integer.toHexString(this.myMemory.getProgramCounter()), 5, 1);
		this.myGUI.tbl_SpecialRegs.setValueAt(Integer.toBinaryString(this.myMemory.getProgramCounter()), 5, 2);

		this.myGUI.tbl_SfrRegs.setValueAt((this.myMemory.readRegisterDirect(0x03) & 0b00000001), 1, 8);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x03) >> 1) & 0b00000001), 1, 7);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x03) >> 2) & 0b00000001), 1, 6);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x03) >> 3) & 0b00000001), 1, 5);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x03) >> 4) & 0b00000001), 1, 4);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x03) >> 5) & 0b00000001), 1, 3);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x03) >> 6) & 0b00000001), 1, 2);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x03) >> 7) & 0b00000001), 1, 1);
		// Option Register
		this.myGUI.tbl_SfrRegs.setValueAt((this.myMemory.readRegisterDirect(0x81) & 0b00000001), 3, 8);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x81) >> 1) & 0b00000001), 3, 7);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x81) >> 2) & 0b00000001), 3, 6);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x81) >> 3) & 0b00000001), 3, 5);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x81) >> 4) & 0b00000001), 3, 4);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x81) >> 5) & 0b00000001), 3, 3);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x81) >> 6) & 0b00000001), 3, 2);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x81) >> 7) & 0b00000001), 3, 1);
		// INTCON
		this.myGUI.tbl_SfrRegs.setValueAt((this.myMemory.readRegisterDirect(0x0B) & 0b00000001), 5, 8);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x0B) >> 1) & 0b00000001), 5, 7);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x0B) >> 2) & 0b00000001), 5, 6);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x0B) >> 3) & 0b00000001), 5, 5);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x0B) >> 4) & 0b00000001), 5, 4);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x0B) >> 5) & 0b00000001), 5, 3);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x0B) >> 6) & 0b00000001), 5, 2);
		this.myGUI.tbl_SfrRegs.setValueAt(((this.myMemory.readRegisterDirect(0x0B) >> 7) & 0b00000001), 5, 1);
	}

	//Input Function Button Input
	public void input() {
		try {
			String hex = this.myGUI.getTxt_regValue().getText();
			int valuetochange = Integer.parseInt(hex, 16);
			int changeAddress = this.myGUI.returnChangeAddress();
			if (valuetochange > 255) {
				valuetochange = 255;
			}
			this.myMemory.writeRegisterDirect(changeAddress, valuetochange);
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
			this.myGUI.tbl_CodeView.addRow(new Object[] { " ", pc, code, line, label, mnemonic });
			// when PC not empty
			// Parses the string argument as a signed integer in the radix specified by the
			// second argument
			if (pc.equals("    ") == false) {
				// This is Line but in execute
				this.myMemory.programMemory[Integer.parseInt(pc, 16)] = Integer.parseInt(code, 16);
				this.pcLine[Integer.parseInt(pc, 16)] = Integer.parseInt(line, 10);
				this.debugLine[Integer.parseInt(pc, 16)] = false;
			}

		}
		System.out.println("Programm memory loaded");
	}

	public void updateFrequency(String dropdownSelection) {
		this.quartzFrequency = Integer.parseInt(dropdownSelection); 
	}

	protected void writeDestination(int d, int f, int result) {
		if (d == 0) {
			this.myMemory.setwRegister(result);
		} else {
			this.myMemory.writeRegister(f, result);
		}
	}
	
	protected void executeCmd(Instruction instruction) {
		String command = instruction.getMnemonic();
		int d = instruction.getParameterD();
		int f = instruction.getParameterF();
		int b = instruction.getParameterB();
		int k = instruction.getParameterK();
		
		if (f == 0x00 | f == 0x80) {
			f = this.myMemory.readRegisterDirect(0x04);
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
		int w = this.myMemory.getwRegister();
		int fValue = this.myMemory.readRegister(f);
		int result = w + fValue;
		
		if (result > 255) {
			this.myMemory.setCarryFlag(1);
			result = result - 256;
		} else {
			this.myMemory.setCarryFlag(0);
		}
		
		this.writeDestination(d, f, result);
		this.myMemory.checkDCFlag(w, fValue);
		this.myMemory.checkZFlag(result);
		this.myMemory.incPC();
	}

	private void ANDWF(int f, int d) {
		int w = this.myMemory.getwRegister();
		int fValue = this.myMemory.readRegister(f);
		int result = w & fValue;
		this.writeDestination(d, f, result);
		this.myMemory.checkZFlag(result);
		this.myMemory.incPC();

	}

	private void CLRF(int f) {
		this.myMemory.writeRegister(f, 0);
		this.myMemory.checkZFlag(0);
		this.myMemory.incPC();

	}

	private void CLRW() {
		this.myMemory.setwRegister(0);
		this.myMemory.checkZFlag(0);
		this.myMemory.incPC();
	}

	private void COMF(int f, int d) {
		// XOR with 0b11111111
		int result = this.myMemory.readRegister(f) ^ 0b11111111;
		this.writeDestination(d, f, result);
		this.myMemory.checkZFlag(result);
		this.myMemory.incPC();
	}

	private void DECF(int f, int d) {
		int result = this.myMemory.readRegister(f);
		if (result > 0) {
			result--;
		} else {
			result = 0xFF;
		}
		this.writeDestination(d, f, result);
		this.myMemory.checkZFlag(result);
		this.myMemory.incPC();
	}

	private void DECFSZ(int f, int d) {
		int fValue = this.myMemory.readRegister(f);
		if (fValue == 0) {
			fValue = 255;
		} else {
			fValue--;
			if (fValue == 0) {
				this.myMemory.incPC();
				this.isNop = true;
			}
		}

		this.writeDestination(d, f, fValue);
		this.myMemory.incPC();
	}

	private void INCF(int f, int d) {
		int result = this.myMemory.readRegister(f);
		if (result == 255) {
			result = 0;
		} else {
			result++;
		}
		this.writeDestination(d, f, result);
		this.myMemory.checkZFlag(result);
		this.myMemory.incPC();
	}

	private void INCFSZ(int f, int d) {
		int fValue = this.myMemory.readRegister(f);
		if (fValue == 255) {
			fValue = 0;
			this.myMemory.incPC();
			this.isNop = true;
		} else {
			fValue++;
		}
		this.writeDestination(d, f, fValue);
		this.myMemory.incPC();

	}

	private void IORWF(int f, int d) {
		int w = this.myMemory.getwRegister();
		int fValue = this.myMemory.readRegister(f);
		int result = w | fValue;
		this.writeDestination(d, f, result);
		this.myMemory.checkZFlag(result);
		this.myMemory.incPC();
	}

	private void MOVF(int f, int d) {
		int result = this.myMemory.readRegister(f);
		this.writeDestination(d, f, result);
		this.myMemory.checkZFlag(result);
		this.myMemory.incPC();
	}

	private void MOVWF(int f) {
		int w = this.myMemory.getwRegister();
		this.myMemory.writeRegister(f, w);
		this.myMemory.incPC();
	}

	private void NOP() {
		this.myMemory.incPC();
	}

	private void RLF(int f, int d) {
		int fValue = this.myMemory.readRegister(f);
		int cValue = this.myMemory.readRegisterDirect(0x03) & 0b00000001;
		if ((fValue & 128) == 128) {
			this.myMemory.setCarryFlag(1);
		} else {
			this.myMemory.setCarryFlag(0);
		}
		// Rotate left
		int result = ((fValue << 1) & 0xff) | (cValue & 1);
		this.writeDestination(d, f, result);
		this.myMemory.incPC();
	}

	private void RRF(int f, int d) {
		int fValue = this.myMemory.readRegister(f);
		int cValue = this.myMemory.readRegisterDirect(0x03) & 0b00000001;
		// Minimum Value
		if ((fValue & 1) == 1) {
			this.myMemory.setCarryFlag(1);
		} else {
			this.myMemory.setCarryFlag(0);
		}
		// Rotate Right
		int result = (fValue >> 1) | (cValue << 7);
		this.writeDestination(d, f, result);
		this.myMemory.incPC();
	}

	private void SUBWF(int f, int d) {
		int w = this.myMemory.getwRegister();
		int fValue = this.myMemory.readRegister(f);
		int result;
		if (w > fValue) {
			// to remove the negation
			result = 256 - (w - fValue);
		} else {
			result = fValue - w;
		}
		if (fValue - w < 0) {
			this.myMemory.setCarryFlag(0);
		} else {
			this.myMemory.setCarryFlag(1);
		}
		if (fValue - w > 15) {
			this.myMemory.setDCFlag(1);
		} else {
			this.myMemory.setDCFlag(0);
		}
		this.myMemory.checkZFlag(result);
		this.writeDestination(d, f, result);
		this.myMemory.incPC();

	}

	private void SWAPF(int f, int d) {
		int nibh, nibl;
		int fValue = this.myMemory.readRegister(f);
		nibh = (fValue & 0b11110000) >> 4;
		nibl = (fValue & 0b00001111) << 4;
		int result = nibh + nibl;
		this.writeDestination(d, f, result);
		this.myMemory.incPC();
	}

	private void XORWF(int f, int d) {
		int w = this.myMemory.getwRegister();
		int fValue = this.myMemory.readRegister(f);
		int result = (w ^ fValue);
		this.writeDestination(d, f, result);
		this.myMemory.checkZFlag(result);
		this.myMemory.incPC();

	}

	private void BCF(int f, int b) {
		int fValue = this.myMemory.readRegister(f);
		// Bit Mask 255
		int bitMask = 0xff;
		// shift Mask 1
		int shiftMask = 0x01;
		shiftMask = shiftMask << b;
		bitMask = shiftMask ^ bitMask;
		fValue = fValue & bitMask;
		this.myMemory.writeRegister(f, fValue);
		this.myMemory.incPC();
	}

	private void BSF(int f, int b) {
		int fValue = this.myMemory.readRegister(f);
		// Bit Mask 1
		int bitMask = 0x01;
		bitMask = bitMask << b;
		fValue = fValue | bitMask;
		this.myMemory.writeRegister(f, fValue);
		this.myMemory.incPC();
	}

	private void BTFSC(int f, int b) {
		int fValue = this.myMemory.readRegister(f);
		int bitMask = 0x01;
		bitMask = bitMask << b;
		fValue = fValue & bitMask;
		if (fValue == 0) {
			this.myMemory.incPC();
			this.isNop = true;
		}
		this.myMemory.incPC();
	}

	private void BTFSS(int f, int b) {
		int fValue = this.myMemory.readRegister(f);
		int bitMask = 0x01;
		bitMask = bitMask << b;
		fValue = fValue & bitMask;
		if ((fValue >> b) == 1) {
			this.myMemory.incPC();
			this.isNop = true;
		}
		this.myMemory.incPC();
	}

	private void ADDLW(int k) {
		int w = this.myMemory.getwRegister();
		int result = w + k;
		if (result > 255) {
			// When result > 255
			result = result - 256;
			this.myMemory.setCarryFlag(1);
		} else {
			result = k + w;
			this.myMemory.setCarryFlag(0);
		}

		this.myMemory.checkZFlag(result);
		this.myMemory.checkDCFlag(w, k);
		this.myMemory.setwRegister(result);
		this.myMemory.incPC();
	}

	private void ANDLW(int k) {
		int w = this.myMemory.getwRegister() & k;
		this.myMemory.checkZFlag(w);
		this.myMemory.setwRegister(w);
		this.myMemory.incPC();

	}

	private void CALL(int k) {
		int pc = this.myMemory.getProgramCounter();
		this.myMemory.pushToStack(pc + 1);
		this.myMemory.setProgramCounter(k);
		this.isNop = true;

	}

	private void CLRWDT() {
		this.watchdog = 0;
		this.myTimerHandler.setPrescaler(0);
		this.myMemory.setBitRegisterSpecial(0x03, 3, 1);
		this.myMemory.setBitRegisterSpecial(0x03, 4, 1);
	}

	private void GOTO(int k) {
		this.myMemory.setProgramCounter(k);
		this.isNop = true;
	}

	private void IORLW(int k) {
		int w = this.myMemory.getwRegister() | k;
		this.myMemory.checkZFlag(w);
		this.myMemory.setwRegister(w);
		this.myMemory.incPC();
	}

	private void MOVLW(int k) {
		this.myMemory.setwRegister(k);
		this.myMemory.incPC();
	}

	private void RETFIE() {
		int tos = this.myMemory.popFromStack();
		this.myMemory.setProgramCounter(tos);
		int intcon = this.myMemory.readRegisterDirect(0x0B);
		this.myMemory.setBitRegisterSpecial(intcon, 7, 1);
		this.isNop = true;
	}

	private void RETLW(int k) {
		this.myMemory.setwRegister(k);
		int tos = this.myMemory.popFromStack();
		this.myMemory.setProgramCounter(tos);
		this.isNop = true;

	}

	private void RETURN() {
		int tos = this.myMemory.popFromStack();
		this.myMemory.setProgramCounter(tos);
		this.isNop = true;

	}

	private void SLEEP() {
		this.watchdog = 0;
		this.myTimerHandler.setPrescaler(0);
		this.myMemory.setBitRegisterSpecial(0x03, 3, 0);
		this.myMemory.setBitRegisterSpecial(0x03, 4, 1);
		this.isSleep = true;

	}

	private void SUBLW(int k) {
		int w = this.myMemory.getwRegister();
		int result;
		if (w > k) {
			// to remove the negation
			result = 256 - (w - k);
			// this is actually wrong "page 13 in the thematic sheet" but the developers
			// have screwed up
			this.myMemory.setCarryFlag(0);
			this.myMemory.checkZFlag(result);
			this.myMemory.setwRegister(result);
		} else {
			result = k - w;
			this.myMemory.setCarryFlag(1);
			this.myMemory.checkZFlag(result);
			this.myMemory.setwRegister(result);
		}

		this.myMemory.checkDCFlag(k, w);
		this.myMemory.incPC();
	}

	private void XORLW(int k) {
		int w = this.myMemory.getwRegister();
		int result = (w ^ k);
		this.myMemory.setwRegister(result);
		this.myMemory.checkZFlag(result);
		this.myMemory.incPC();

	}

	public void stopctr() {
		if (this.running) {
			this.running = false;
			this.myProcessor.exit = true;
		}
	}

	public void startSimu() {
		if (this.running == false) {
			this.myProcessor = new Processor(this);
			this.running = true;
			this.myProcessor.start();
		} else {
			if (this.myProcessor.isDebugging == true) {
				this.myProcessor.isDebugging = false;
				this.myProcessor.nextStep = true;
			}
		}
	}

	public Timer getTimerHandler() {
		return myTimerHandler;
	}

	public Interrupts getInterruptHandler() {
		return myInterruptHandler;
	}

	// Reset
	public void reset() {
		this.myMemory.powerOn();
		this.myMemory.setProgramCounter(0);
		this.myMemory.setwRegister(0);
		this.myMemory.stack.clear();
		this.runtime = 0;

	}

	// MCLR-Pin (master clear)
	public void mclr() {
		this.isSleep = false;
		this.myProcessor.mclr = true;
		// this.memory.powerOn();
		this.myMemory.setProgramCounter(this.myMemory.getProgramCounter() + 1);
		// this.memory.setwRegister(0);
		// this.memory.stack.clear();
		this.runtime = 0;

	}

	public void clearProgrammMemory() {
		for (int i = 0; i < 1024; i++) {
			this.myMemory.programMemory[i] = 0x3FFF;
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
		int rowCount = this.myGUI.tbl_CodeView.getRowCount();
		// Remove rows one by one from the end of the table
		for (int i = rowCount - 1; i >= 0; i--) {
			this.myGUI.tbl_CodeView.removeRow(i);
		}
	}

	// set Activeline to Highlight
	public void setActiveline() {
		int pc = this.myMemory.getProgramCounter();
		int line = this.pcLine[pc];
		this.myGUI.setRowHighlight(line);
	}

	// Breakpoints array if true --> false, false --> true in Debug Array
	public void setBreakpoint(int pc) {
		this.debugLine[pc] = !this.debugLine[pc];
	}

	// Single Steps
	public void singleStep() {
		if (this.running) {
			this.myProcessor.nextStep = true;
		}
	}

	// Refresh I/O Pins
	public void refreshPins() {
		int trisA = this.myMemory.readRegisterDirect(0x85);
		int trisB = this.myMemory.readRegisterDirect(0x86);
		int dataA = this.myMemory.datalatcha;
		int dataB = this.myMemory.datalatchb;
		this.getGui().setTrisA(trisA);
		this.getGui().setTrisB(trisB);
		int ra = getGui().getPortA();
		int rb = getGui().getPortB();
		for (int i = 0; i < 8; i++) {
			if ((trisA & 0x01) == 1) {
				this.myMemory.setBitRegisterSpecial(0x05, i, ra & 0x01);
			} else {
				this.myMemory.setBitRegisterSpecial(0x05, i, dataA & 0x01);
			}
			dataA = dataA >> 1;
			trisA = trisA >> 1;
			ra = ra >> 1;
			if ((trisB & 0x01) == 1) {
				this.myMemory.setBitRegisterSpecial(0x06, i, rb & 0x01);
			} else {
				this.myMemory.setBitRegisterSpecial(0x06, i, dataB & 0x01);
			}
			dataB = dataB >> 1;
			trisB = trisB >> 1;
			rb = rb >> 1;
		}
		getGui().setPortA(this.myMemory.readRegisterDirect(0x05));
		getGui().setPortB(this.myMemory.readRegisterDirect(0x06));
	}


	public int getFrequency() {
		return quartzFrequency;
	}

	public void setFrequency(int frequency) {
		quartzFrequency = frequency;
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