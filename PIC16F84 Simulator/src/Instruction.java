
public class Instruction {
	
	private static String mnemonic; //mnemonic is the "readable" version of the Assembler command
	private static int parameterD;
	private static int parameterF;
	private static int parameterB;
	private static int parameterK;
	
	public Instruction(String givenMnemonic, int givenParameterD, int givenParameterF, int givenParameterB, int givenParameterK) {
		Instruction.mnemonic = givenMnemonic;
		Instruction.parameterD = givenParameterD;
		Instruction.parameterF = givenParameterF;
		Instruction.parameterB = givenParameterB;
		Instruction.parameterK = givenParameterK;
	}

	public String getMnemonic() {
		return mnemonic;
	}
	
	public int getParameterD() {
		return parameterD;
	}
	
	public int getParameterF() {
		return parameterF;
	}

	public int getParameterB() {
		return parameterB;
	}
	
	public int getParameterK() {
		return parameterK;
	}
	
}