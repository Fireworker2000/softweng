
public class Processor extends Thread {
	private Controller ctr;
	private Decoder decoder;
	protected boolean exit = false;
	protected boolean isDebugging = false;
	protected boolean nextStep = false;
	protected boolean mclr = false;

	public Processor(Controller ctr) {
		this.ctr = ctr;
		this.decoder = new Decoder();
	}

	public void run() {
		// running = true;
		// execute command
		while (!exit) {
			// Integer.parseInt(code, 16); Base 16 HEX
			ctr.setActiveline();
			if (this.ctr.debugLine[this.ctr.memory.getProgramCounter()] == true) {
				this.isDebugging = true;
			}
			ctr.memory.writeRegisterDirect(0x02, this.ctr.memory.getProgramCounter() & 0xff);
			if(ctr.isNop == true)
			{
				Instruction instruction = decoder.decodeCodeline(0x00);
				ctr.executeCmd(instruction);
				ctr.isNop = false;
				ctr.memory.setProgramCounter(ctr.memory.getProgramCounter() - 1);
			}
			else {				
				Instruction instruction = decoder.decodeCodeline(this.ctr.memory.getProgrammLine());
				ctr.executeCmd(instruction);
			}
			
			ctr.commandCycle();
			ctr.refreshPins();
			ctr.getTmr().updateValues(ctr.memory.readRegisterDirect(0x05));
			ctr.getTmr().checkIncrement();
			ctr.getIntrr().updateValues(ctr.memory.readRegisterDirect(0x06));
			ctr.getIntrr().checkRbInterr();
			ctr.getIntrr().checkInterrupt();
			if (exit) {
				break;
			}
			if (this.ctr.isSleep) {
				while (!this.mclr) {
					ctr.getIntrr().updateValues(ctr.memory.readRegisterDirect(0x06));
					ctr.getIntrr().checkRbInterr();
					if(ctr.getIntrr().checkInterruptFlags()) {
						ctr.wakeUp();
					}
					try {
						sleep(100);
					} catch (InterruptedException e) {
						// Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (this.isDebugging) {
				while (!nextStep) {
					try {
						sleep(100);
					} catch (InterruptedException e) {
						// Auto-generated catch block
						e.printStackTrace();
					}
				}
				nextStep = false;
			} else {
				try {
					Thread.sleep(ctr.getSimspeed());
				} catch (InterruptedException e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		this.exit = false;
	}
}
