
public abstract class abstractDecoder implements decoderInterface {
	
    private Logger logger = Logger.commandLogger();
    
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
    
    // the logging part is delegated to the Logger implementation
    void logCodeline(int line) {
        logger.log("Command in Hex: " + Integer.toHexString(line));
    }
    
    void logDecodedCommand(Instruction instruction) {
    	
    	String decodedMnemonic = instruction.getMnemonic();
    	String parameterD = Integer.toBinaryString(instruction.getParameterD());
    	String parameterF = Integer.toBinaryString(instruction.getParameterF());
    	String parameterB = Integer.toBinaryString(instruction.getParameterB());
    	String parameterK = Integer.toBinaryString(instruction.getParameterK());
    	
    	String message = "Decoded mnemonic: " + decodedMnemonic + ", D: " + parameterD + ", F: " + parameterF + ", B: " + parameterB + ", K: " + parameterK;
    	logger.log(message);
    }
	
    void logError(String message) {
    	this.setLogger(Logger.errorLogger());
    	logger.log(message);
    	this.setLogger(Logger.commandLogger());
    }
    
}
