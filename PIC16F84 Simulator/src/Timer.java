
public class Timer {
	private Controller ctr;

	public Timer(Controller ctr) {
		this.ctr = ctr;
	}

	private int ra4;
	private int clkout = 1;
	private int ra4edge;
	private int prescaler = 0;

	/**
	 * 0 = no edge 1 = positive edge 2 = negative edge
	 */
	public void updateValues(int ra) {
		if (this.ra4 != ((ra & 0x10) >> 4)) {
			this.ra4 = ((ra & 0x10) >> 4);
			if (this.ra4 == 1) {
				ra4edge = 1;
			} else {
				ra4edge = 2;
			}
		} else {
			ra4edge = 0;
		}

	}

	public void checkIncrement() {
		// in if T0CS = 1
		// edge checking 1, 2 Neg
		// : T0SE: TMR0 Source Edge Select bit
		// 1 = Increment on high-to-low transition on RA4/T0CKI pin
		// 0 = Increment on low-to-high transition on RA4/T0CKI pin
		if (((this.ctr.myMemory.readRegisterDirect(0x81) & 0x20) >> 5) == 0x01) {
			// RA0
			if (((this.ctr.myMemory.readRegisterDirect(0x81) & 0x10) >> 4) == 0x01) {
				if (ra4edge == 2) {
					this.increment();
				}

			} else {
				if (ra4edge == 1) {
					this.increment();
				}
			}

		} else { // Clk Out

			System.out.println("Timer, Prescaler: " + this.prescaler);
			if (this.clkout == 1) {
				this.increment();
			}
		}

	}

	private void increment() {
		this.prescaler++;
		int psa = (((this.ctr.myMemory.readRegisterDirect(0x81) >> 3) & 0x01));
		int psavalue = (this.ctr.myMemory.readRegisterDirect(0x81) & 0x07);
		System.out.println("PSV" + psavalue);
		int tmr0 = (this.ctr.myMemory.readRegisterDirect(0x01));
		// 2 hoch Value * 2
		// PSA0 TMR0, PSA1 WD
		if ((psa == 0 && prescaler == Math.pow(2.0, psavalue) * 2) || psa == 1) {
			if (tmr0 == 255)
			// Tmr0 inc
			{
				//System.out.println("Timer Interr");
				ctr.myMemory.clrTimer();
				ctr.myMemory.checkZFlag(0);
				ctr.myMemory.setRegisterBit(0x0B, 2, 1);

			} else {
				ctr.myMemory.incTimer();
			}
			prescaler = 0;
		}
	}

	public int getClkout() {
		return clkout;
	}

	public void setPrescaler(int prescaler) {
		this.prescaler = prescaler;
	}
}
