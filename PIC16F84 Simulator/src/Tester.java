import static org.junit.Assert.*;				
import org.junit.Test;	

public class Tester {
	
	/*
	 * Tests Instruction class method getMnemonic - does it return the stuff you put in? 
	 */
	@Test
	public void testInstructionclassGetMnemonic() {
		String actualMnemonic = "Mnemonic";	//can be any example String
		int actualParameterD = 4;			//can be any integer
		int actualParameterF = 6;			//can be any integer
		int actualParameterB = 2; 			//can be any integer
		int actualParameterK = 11;			//can be any integer
		Instruction testInstruction = new Instruction(actualMnemonic, actualParameterD, actualParameterF, actualParameterB, actualParameterK);
		assertEquals(testInstruction.getMnemonic(), actualMnemonic);
	}
	
	/*
	 * Tests Instruction class method getParameterD - does it return the stuff you put in? 
	 */
	@Test
	public void testInstructionclassGetParameterD() {
		String actualMnemonic = "Mnemonic";	//can be any example String
		int actualParameterD = 4;			//can be any integer
		int actualParameterF = 60;			//can be any integer
		int actualParameterB = 20; 			//can be any integer
		int actualParameterK = 110;			//can be any integer
		Instruction testInstruction = new Instruction(actualMnemonic, actualParameterD, actualParameterF, actualParameterB, actualParameterK);
		assertEquals(testInstruction.getParameterD(), actualParameterD);
	}
	
	/*
	 * Tests Instruction class method getParameterF - does it return the stuff you put in? 
	 */
	@Test
	public void testInstructionclassGetParameterF() {
		String actualMnemonic = "Mnemonic";	//can be any example String
		int actualParameterD = 40;			//can be any integer
		int actualParameterF = 6;			//can be any integer
		int actualParameterB = 20; 			//can be any integer
		int actualParameterK = 110;			//can be any integer
		Instruction testInstruction = new Instruction(actualMnemonic, actualParameterD, actualParameterF, actualParameterB, actualParameterK);
		assertEquals(testInstruction.getParameterF(), actualParameterF);
	}
	
	/*
	 * Tests Instruction class method getParameterB - does it return the stuff you put in? 
	 */
	@Test
	public void testInstructionclassGetParameterB() {
		String actualMnemonic = "Mnemonic";	//can be any example String
		int actualParameterD = 4;			//can be any integer
		int actualParameterF = 60;			//can be any integer
		int actualParameterB = 20; 			//can be any integer
		int actualParameterK = 110;			//can be any integer
		Instruction testInstruction = new Instruction(actualMnemonic, actualParameterD, actualParameterF, actualParameterB, actualParameterK);
		assertEquals(testInstruction.getParameterB(), actualParameterB);
	}
	
	/*
	 * Tests Instruction class method getParameterK - does it return the stuff you put in? 
	 */
	@Test
	public void testInstructionclassGetParameterK() {
		String actualMnemonic = "Mnemonic";	//can be any example String
		int actualParameterD = 40;			//can be any integer
		int actualParameterF = 60;			//can be any integer
		int actualParameterB = 20; 			//can be any integer
		int actualParameterK = 11;			//can be any integer
		Instruction testInstruction = new Instruction(actualMnemonic, actualParameterD, actualParameterF, actualParameterB, actualParameterK);
		assertEquals(testInstruction.getParameterK(), actualParameterK);
	}

	/*
	 * Has already found a bug
	 * Tests decoder decoding on easy instruction without parameters
	 */
	@Test
	public void testDecoderWithoutParameters() {
		Decoder testDecoder = new Decoder();
		
		String actualMnemonic = "NOP";
		int line = 0b00000000000000;	//has to be zero
		
		Instruction decodedInstruction = testDecoder.decodeCodeline(line);
		assertEquals(decodedInstruction.getMnemonic(), actualMnemonic);
	}
	
	/*
	 * Tests decoder decoding on a more complex instruction with parameters B and F
	 */
	@Test
	public void testDecoderWithParametersBF() {
		Decoder testDecoder = new Decoder();
		
		String actualMnemonic = "BTFSC";
		int actualPrecommand = 0b01;		//has to be one
		int actualCommand = 0b10;			//has to be two (10 in binary)
		int actualParameterF = 0b0000001;	//can be any 7-digit binary number
		int actualParameterB = 0b001; 		//can be any 3-digit binary number

		int actualLine = (actualPrecommand << 12) + (actualCommand << 10) + (actualParameterB << 7) + actualParameterF;
		
		Instruction decodedInstruction = testDecoder.decodeCodeline(actualLine);
		assertEquals(decodedInstruction.getMnemonic(), actualMnemonic);
		assertEquals(decodedInstruction.getParameterF(), actualParameterF);
		assertEquals(decodedInstruction.getParameterB(), actualParameterB);
	}
	
	/*
	 * Tests decoder decoding on a more complex instruction with parameters F and D
	 */
	@Test
	public void testDecoderWithParametersFD() {
		Decoder testDecoder = new Decoder();
		
		String actualMnemonic = "ADDWF";
		int actualPrecommand = 0b00;		//has to be zero
		int actualCommand = 0b0111;			//has to be seven (0111 in binary)
		int actualParameterD = 1;			//can be any single binary digit
		int actualParameterF = 0b0000001;	//can be any 7-digit binary number
		
		int actualLine = (actualPrecommand << 12) + (actualCommand << 8) + (actualParameterD << 7) + actualParameterF;
		
		Instruction decodedInstruction = testDecoder.decodeCodeline(actualLine);
		assertEquals(decodedInstruction.getMnemonic(), actualMnemonic);
		assertEquals(decodedInstruction.getParameterD(), actualParameterD);
		assertEquals(decodedInstruction.getParameterF(), actualParameterF);
	}
	
	/*
	 * Tests decoder decoding on a more complex instruction with 8-digit parameter K
	 */
	@Test
	public void testDecoderWithEightDigitParameterK() {
		Decoder testDecoder = new Decoder();
		
		String actualMnemonic = "ANDLW";
		int actualPrecommand = 0b11;		//has to be three (11 in binary)
		int actualCommand = 0b1001;			//has to be nine (1001 in binary)
		int actualParameterK = 0b0000001;	//can be any 8-digit binary number
		
		int actualLine = (actualPrecommand << 12) + (actualCommand << 8) + actualParameterK;
		
		Instruction decodedInstruction = testDecoder.decodeCodeline(actualLine);
		assertEquals(decodedInstruction.getMnemonic(), actualMnemonic);
		assertEquals(decodedInstruction.getParameterK(), actualParameterK);
	}
	
	/*
	 * Tests decoder decoding on a more complex instruction with 11-digit parameter K
	 */
	@Test
	public void testDecoderWithCALL() {
		Decoder testDecoder = new Decoder();
		
		String actualMnemonic = "CALL";
		int actualPrecommand = 0b10;			//has to be two (10 in binary)
		int actualCommand = 0b0;				//has to be zero
		int actualParameterK = 0b00000000001;	//can be any 11-digit number
		
		int actualLine = (actualPrecommand << 12) + (actualCommand << 11) + actualParameterK;
		
		Instruction decodedInstruction = testDecoder.decodeCodeline(actualLine);
		assertEquals(decodedInstruction.getMnemonic(), actualMnemonic);
		assertEquals(decodedInstruction.getParameterK(), actualParameterK);
	}
	
	/*
	 * Tests decoder decoding on the other complex instruction with 11-digit parameter K
	 */
	@Test
	public void testDecoderWithGOTO() {
		Decoder testDecoder = new Decoder();
		
		String actualMnemonic = "GOTO";
		int actualPrecommand = 0b10;			//has to be two (10 in binary)
		int actualCommand = 0b1;				//has to be one
		int actualParameterK = 0b00000000001;	//can be any 11-digit number
		
		int actualLine = (actualPrecommand << 12) + (actualCommand << 11) + actualParameterK;
		
		Instruction decodedInstruction = testDecoder.decodeCodeline(actualLine);
		assertEquals(decodedInstruction.getMnemonic(), actualMnemonic);
		assertEquals(decodedInstruction.getParameterK(), actualParameterK);
	}
}