import java.util.Stack;

//Controller Memory where to save the program and commands
public class Memory extends Thread {
	private Controller ctr;
	protected int[] programMemory = new int[1024];
	// EEPROM
	protected int[] eeprom = new int[64];
	// data latch a
	protected int datalatcha;
	// data latch b
	protected int datalatchb;

	private int tmr0incBreak;

	// Bank 0 and 1 GPR: general purpose register
	private int[] gpr = new int[256];
	// Work register
	private int wRegister = 0;
	// Program counter
	private int programCounter = 0;
	// Stack
	protected Stack<Integer> stack = new Stack<Integer>();

	public Memory(Controller pCtr) {
		this.ctr = pCtr;
		this.powerOn();
	}

	protected void powerOn() {
		for (int i = 0; i < 256; i++) {
			this.gpr[i] = 0;
		}
		// initialize Power on values
		// Status register TO PD set
		this.writeRegisterDirect(0x03, 0b00011000);
		// Option Register set
		this.writeRegisterDirect(0x81, 0b11111111);
		// TRIS A
		this.writeRegisterDirect(0x85, 0b00011111);
		// TRIS B
		this.writeRegisterDirect(0x86, 0b11111111);
	}

	protected void incPC() {
		this.programCounter++;
	}

	public void run() {
		while (true) {
			for (int i = 0; i < 256; i++) {
				this.ctr.getGui().tbl_Gpr.setValueAt(Integer.toHexString(this.gpr[i]), i / 8, (i % 8) + 1);
			}
			ctr.refreshPins();
			ctr.updateFSRTable();
			ctr.updateStack(this.stack);
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * @return the gpr
	 */
	protected int[] getGpr() {
		return gpr;
	}

	/**
	 * @param gpr the gpr to set
	 */
	protected void setGpr(int[] gpr) {
		this.gpr = gpr;
	}

	// Read register
	protected int readRegisterDirect(int adress) {
		return this.gpr[adress];
	}

	// Assign a value to a specific register
	protected void writeRegisterDirect(int adress, int value) {
		// Memory Set
		switch (adress) {
		case 0x00:
		case 0x80:
			// indirect register
			this.gpr[0x00] = value;
			this.gpr[0x80] = value;
			break;
		case 0x02:
		case 0x82:
			// PCL
			this.gpr[0x02] = value;
			this.gpr[0x82] = value;
			break;
		case 0x03:
		case 0x83:
			// Status
			this.gpr[0x03] = value;
			this.gpr[0x83] = value;
			break;
		case 0x04:
		case 0x84:
			// FSR
			this.gpr[0x04] = value;
			this.gpr[0x84] = value;
			break;
		case 0x0A:
		case 0x8A:
			// PCLATCH
			this.gpr[0x0A] = value;
			this.gpr[0x8A] = value;
			break;
		case 0x0B:
		case 0x8B:
			// INTCON
			this.gpr[0x0B] = value;
			this.gpr[0x8B] = value;
			break;
		default:
			if (this.getRP0() == 0) {
				if(adress == 0x01 && ((this.readRegisterDirect(0x81) >> 3) & 0x01) == 0)
				{
					ctr.getTmr().setPrescaler(0);
				}
			}
			if (adress == 0x05) {
				this.datalatcha = value;
			}
			if (adress == 0x06) {

				this.datalatchb = value;
			}
			this.gpr[adress] = value;
		}

	}

	public void setBitRegisterSpecial(int address, int bit, int value) {
		if (value == 0) {
			int fValue = this.readRegisterDirect(address);
			int bitMask = 0xff;
			int shiftMask = 0x01;
			shiftMask = shiftMask << bit;
			bitMask = shiftMask ^ bitMask;
			fValue = fValue & bitMask;
			this.gpr[address] = fValue;
		} else if (value == 1) {
			int fValue = this.readRegisterDirect(address);
			int bitMask = 0x01;
			bitMask = bitMask << bit;
			fValue = fValue | bitMask;
			this.gpr[address] = fValue;
		}
	}

	protected int readRegister(int adress) {

		if (this.getRP0() == 1) {
			if (adress < 0x80) {
				return this.gpr[0x80 + adress];
			} else {
				System.out.println("readRegister adress is too long for Bank1");
				return this.gpr[adress];
			}
		} else {
			return this.gpr[adress];
		}
	}

	// Assign a value to a specific register in Bank1 if RP0 is set
	protected void writeRegister(int adress, int value) {
		if (this.getRP0() == 1) {
			if (adress < 0x80) {
				// Use Bank 1 (Switch Cases from Direct)
				this.writeRegisterDirect(adress + 0x80, value);
			} else {
				System.out.println("writeRegister adress is too long for Bank1");
				this.writeRegisterDirect(adress, value);
			}
		} else {
			this.writeRegisterDirect(adress, value);
		}
	}

	/**
	 * @return the wRegister
	 */
	protected int getwRegister() {
		return wRegister;
	}

	/**
	 * @return the programCounter
	 */
	protected int getProgramCounter() {
		return programCounter;
	}

	/**
	 * @param programCounter the programCounter to set
	 */
	protected void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}

	/**
	 * @param wRegister the wRegister to set
	 */
	protected void setwRegister(int wRegister) {
		this.wRegister = wRegister;
	}

	// Push to stack
	protected void pushToStack(int toPush) {
		// Stack size 8
		if (stack.size() < 8) {
			this.stack.push(toPush);
		} else if (stack.size() == 8) {
			this.stack.clear();
			this.stack.push(toPush);
		}
	}

	// Pop from stack
	protected int popFromStack() {
		return this.stack.pop();
	}

	protected int getRP0() {
		return (this.gpr[0x03] >> 5) & 0x0001;
	}

	// ZeroFlag
	protected void checkZFlag(int value) {
		if (value == 0) {
			this.writeRegisterDirect(0x03, this.gpr[0x03] | 0x04);

		} else {
			this.writeRegisterDirect(0x03, this.gpr[0x03] & 0xfb);
		}
	}

	// Set Carry Flag
	protected void setCarryFlag(int cf) {
		if (cf == 1) {
			this.writeRegisterDirect(0x03, this.gpr[0x03] | 0b00000001);
		} else {
			this.writeRegisterDirect(0x03, this.gpr[0x03] & 0b11111110);
		}
	}

	// Set DC Flag
	protected void setDCFlag(int dc) {
		if (dc == 1) {
			this.writeRegisterDirect(0x03, this.gpr[0x03] | 0b00000010);
		} else {
			this.writeRegisterDirect(0x03, this.gpr[0x03] & 0b11111101);
		}
	}

	// DC Flag checking
	protected void checkDCFlag(int int_1, int int_2) {
		if ((int_1 & 0x0f) + (int_2 & 0x0f) > 0x0f) {
			this.setDCFlag(1);
		} else {
			this.setDCFlag(0);
		}
	}

	// Register Bit change like BSF/BCF
	public void setRegisterBit(int address, int bit, int value) {
		if (value == 0) {
			int fValue = this.readRegisterDirect(address);
			if (address == 0x05) {
				fValue = this.datalatcha;
			} else if (address == 0x06) {
				fValue = this.datalatchb;
			}
			int bitMask = 0xff;
			int shiftMask = 0x01;
			shiftMask = shiftMask << bit;
			bitMask = shiftMask ^ bitMask;
			fValue = fValue & bitMask;
			this.writeRegisterDirect(address, fValue);
		} else if (value == 1) {
			int fValue = this.readRegisterDirect(address);
			if (address == 0x05) {
				fValue = this.datalatcha;
			} else if (address == 0x06) {
				fValue = this.datalatchb;
			}
			int bitMask = 0x01;
			bitMask = bitMask << bit;
			fValue = fValue | bitMask;
			this.writeRegisterDirect(address, fValue);
		}
	}

	public int getProgrammLine() {
		int line = this.programMemory[this.getProgramCounter()];
		return line;
	}

	public void incTimer() {
		this.gpr[0x01] = this.gpr[0x01] + 1;
	}

	public void clrTimer() {
		this.gpr[0x01] = 0;
	}

	public void setTmr0incBreak(int tmr0incBreak) {
		this.tmr0incBreak = tmr0incBreak;
	}

	public int getTmr0incBreak() {
		return tmr0incBreak;
	}
}
