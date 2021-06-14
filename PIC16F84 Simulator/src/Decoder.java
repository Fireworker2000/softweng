
public class Decoder extends AbstractDecoder {
	
	public Decoder() {}
	
	// implements the function defined in the interface
	public Instruction decodeCodeline(int line) {
		
		super.logCodeline(line);
		
		String assemblerCommand = "";
		int d = 0;
		int f = 0;
		int b = 0;
		int k = 0;
		
		int precommand = (line >> 12) & 0x0003;

		if (precommand == 0) {
			int command = (line >> 8) & 0x000f;
			int payload = line & 0x00ff;
			
			d = payload >> 7 & 0x0001;
			f = payload & 0x07F;
			assemblerCommand = decodeInstructionWithPrecommandZero(command, payload);
		} 
		else if (precommand == 1) {
			int command = (line >> 10) & 0x0003;
			int payload = line & 0x03ff;
			
			b = (payload >> 7) & 0x0007;
			f = payload & 0x007f;
			assemblerCommand = decodeInstructionWithPrecommandOne(command);
		} 
		else if (precommand == 2) {
			int command = (line >> 11) & 0x0001;
			
			k = line & 0x07ff;
			if 		(command == 0)	{ assemblerCommand = "CALL"; }
			else if (command == 1) 	{ assemblerCommand = "GOTO"; }
			else { 
				 //binary digit can either be 0 or 1, nothing else
				super.logError("Illegal Argument at CALL/GOTO decoding, command bit is neither zero nor one.");
				throw new IllegalArgumentException();
			}
		} 
		else if (precommand == 3) {
			int command = (line >> 8) & 0x000f;
			
			k = line & 0x00ff;
			assemblerCommand = decodeInstructionWithPrecommandThree(command);
		}
		else { 
			//only two binary digits available, no values other than 0, 1, 2 and 3 are possible + ", D: " + d
			super.logError("Illegal Argument at precommand assignment, decoded precommand is out of bounds.");
			throw new IllegalArgumentException();
		}
		
		Instruction fullInstruction = new Instruction(assemblerCommand, d, f, b, k);
		super.logDecodedCommand(fullInstruction);
		return fullInstruction;
	}
	
	private String decodeInstructionWithPrecommandZero(int command, int payload) {
		int d = payload >> 7 & 0x0001;
		String output = "NOP";
		
		switch (command) {
		case 7: output = "ADDWF"; break;
		case 5: output = "ANDWF"; break;
		case 1:
			if (d == 1) {output = "CLRF";}
			else		{output = "CLRW";}
			break;
		case 9:  output = "COMF";   break;
		case 3:  output = "DECF";   break;
		case 11: output = "DECFSZ"; break;
		case 10: output = "INCF";	break;
		case 15: output = "INCFSZ"; break;
		case 4:  output = "IORWF";  break;
		case 8:  output = "MOVF";   break;
		case 0:
			if (d == 1) { output = "MOVWF";}
			else {
				switch (payload) {
				case 0b01100100: output = "CLRWDT"; break;
				case 0b00001001: output = "RETFIE"; break;
				case 0b00001000: output = "RETURN"; break;
				case 0b01100011: output = "SLEEP";  break;
				default: 		 output = "NOP";    break;
				}
			}
			break;
		case 13: output = "RLF"; break;
		case 12: output = "RRF"; break;
		case 2:  output = "SUBWF"; break;
		case 14: output = "SWAPF"; break;
		case 6:  output = "XORWF"; break;
		default: {
			super.logError("Illegal Argument at command decoding (precommand 0), decoded command is out of bounds.");
			throw new IllegalArgumentException();
			}
		}
		return output;
	}

	private String decodeInstructionWithPrecommandOne(int command) {
		String output = "BCF";
		
		switch (command) {
		case 0:	output = "BCF"; break;
		case 1:	output = "BSF"; break;
		case 2:	output = "BTFSC"; break;
		case 3:	output = "BTFSS"; break;
		default: {
			super.logError("Illegal Argument at command decoding (precommand 0), decoded command is out of bounds.");
			throw new IllegalArgumentException();
			}
		}
		
		return output;
	}
	
	private String decodeInstructionWithPrecommandThree(int command) {

		if (command == 8) {
			return "IORLW";
		} 
		else if ((command >> 1) == 7) {
			return "ADDLW";
		} 
		else if (command == 9) {
			return "ANDLW";
		} 
		else if ((command >> 2) == 0) {
			return "MOVLW";
		} 
		else if ((command >> 2) == 1) {
			return "RETLW";
		} 
		else if (command == 10) {
			return "XORLW";
		} 
		else if ((command >> 1) == 6) {
			return "SUBLW";
		} 
		else {
			return "NOP";
		}
	}
	
}
