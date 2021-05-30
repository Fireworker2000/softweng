import static org.junit.Assert.*;				
import org.junit.Test;	

public class Tester {
	
	/*
	 * Has already found a bug
	 */
	@Test
	public void testDecoderWithNOP() {
		Decoder testDecoder = new Decoder();
		int line = 0b00000000000000;	//has to be zero
		int actualParameterD = 0;		//has to be zero
		int actualParameterF = 0;		//has to be zero
		int actualParameterB = 0;		//has to be zero
		int actualParameterK = 0;		//has to be zero
		
		Instruction decodedInstruction = testDecoder.decodeCodeline(line);
		assertEquals(decodedInstruction.getMnemonic(), "NOP");
		assertEquals(decodedInstruction.getParameterD(), actualParameterD);
		assertEquals(decodedInstruction.getParameterF(), actualParameterF);
		assertEquals(decodedInstruction.getParameterB(), actualParameterB);
		assertEquals(decodedInstruction.getParameterK(), actualParameterK);
	}
	
	@Test
	public void testDecoderWithBTFSC() {
		Decoder testDecoder = new Decoder();
		int actualPrecommand = 0b01;		//has to be one
		int actualCommand = 0b10;			//has to be two
		int actualParameterB = 0b000; 		//can be any 3-digit binary number
		int actualParameterF = 0b0000000;	//can be any 7-digit binary number
		int actualParameterD = 0;			//has to be zero
		int actualParameterK = 0;			//has to be zero
		int actualLine = (actualPrecommand << 12) + (actualCommand << 10) + (actualParameterB << 7) + actualParameterF;
		
		Instruction decodedInstruction = testDecoder.decodeCodeline(actualLine);
		assertEquals(decodedInstruction.getMnemonic(), "BTFSC");
		assertEquals(decodedInstruction.getParameterD(), actualParameterD);
		assertEquals(decodedInstruction.getParameterF(), actualParameterF);
		assertEquals(decodedInstruction.getParameterB(), actualParameterB);
		assertEquals(decodedInstruction.getParameterK(), actualParameterK);
	}	
}