public class Interrupts {

	private Controller ctr;
	private int rb0;
	private int rb4;
	private int rb5;
	private int rb6;
	private int rb7;
	/**
	 * 0 = no edge 1 = positive edge 2 = negative edge
	 */
	private int rb0edge;

	// RB4:7 changed
	private boolean rbchanged;
	// T0IF T0IE are 1

	public Interrupts(Controller ctr) {
		this.ctr = ctr;
	}

	public void updateValues(int rb) {
		if (this.rb0 != (rb & 0x01)) {
			this.rb0 = rb & 0x01;
			if (rb0 == 1) {
				rb0edge = 1;
			} else {
				rb0edge = 2;
			}
		} else {
			rb0edge = 0;
		}
		if (this.rb4 != (rb & 0x10)) {
			rbchanged = true;
			this.rb4 = rb & 0x10;
		}
		if (this.rb5 != (rb & 0x20)) {
			rbchanged = true;
			this.rb5 = rb & 0x20;
		}
		if (this.rb6 != (rb & 0x40)) {
			rbchanged = true;
			this.rb6 = rb & 0x40;
		}
		if (this.rb7 != (rb & 0x80)) {
			rbchanged = true;
			this.rb7 = rb & 0x80;
		}
	}

	public void checkRbInterr() {
		// 1 = Interrupt on rising edge of RB0/INT pin
		// 0 = Interrupt on falling edge of RB0/INT pin
		if ((((this.ctr.myMemory.readRegisterDirect(0x81) & 0x40) >> 6) & 0x01) == 0x01) {
			/**
			 * RB0/INT Interrupt Flag bit 1 = The RB0/INT interrupt occurred 0 = The RB0/INT
			 * interrupt did not occur
			 */

			if (rb0edge == 1) {
				int intcon = this.ctr.myMemory.readRegisterDirect(0x0B);
				intcon = intcon | 0x02;
				this.ctr.myMemory.writeRegisterDirect(0x0B, intcon);
			}
		}

		else {

			if (rb0edge == 2) {

				int intcon = this.ctr.myMemory.readRegisterDirect(0x0B);
				intcon = intcon | 0x02;
				this.ctr.myMemory.writeRegisterDirect(0x0B, intcon);
			}
		}
		/**
		 * RB Port Change Interrupt Flag bit 1 = When at least one of the RB7:RB4 pins
		 * changed state (must be cleared in software) 0 = None of the RB7:RB4 pins have
		 * changed state
		 */
		if (rbchanged) {
			int intcon = this.ctr.myMemory.readRegisterDirect(0x0B);
			intcon = intcon | 0x01;
			this.ctr.myMemory.writeRegisterDirect(0x0B, intcon);
		}
	}

	protected boolean checkInterruptFlags() {
		int t0if = (this.ctr.myMemory.readRegisterDirect(0x0B) >> 2) & 0x01;
		int t0ie = (this.ctr.myMemory.readRegisterDirect(0x0B) >> 5) & 0x01;
		if (t0if == 1 && t0ie == 1) {
			return true;
		}

		int intf = (this.ctr.myMemory.readRegisterDirect(0x0B) >> 1) & 0x01;
		int inte = (this.ctr.myMemory.readRegisterDirect(0x0B) >> 4) & 0x01;
		if (intf == 1 && inte == 1) {
			return true;
		}
		int rbif = (this.ctr.myMemory.readRegisterDirect(0x0B)) & 0x01;
		int rbie = (this.ctr.myMemory.readRegisterDirect(0x0B) >> 3) & 0x01;
		if (rbif == 1 && rbie == 1) {
			return true;
		}
		int eeif = (this.ctr.myMemory.readRegisterDirect(0x88) >> 4) & 0x01;
		int eeie = (this.ctr.myMemory.readRegisterDirect(0x0B) >> 6) & 0x01;
		if (eeif == 1 && eeie == 1) {
			return true;
		}

		return false;
	}

	public void checkInterrupt() {
		int intcon = this.ctr.myMemory.readRegisterDirect(0x0B);
		int gie = (intcon >> 7) & 0x01;
		if (gie == 1 && checkInterruptFlags()) {
			this.ctr.myMemory.pushToStack(this.ctr.myMemory.getProgramCounter());
			// Disable other interrupts & GIE bit = 0
			this.ctr.myMemory.writeRegisterDirect(0x0B, 0x7f & intcon);
			this.ctr.myMemory.setProgramCounter(0x04);
			this.ctr.isSleep = false;
		}
	}
}
