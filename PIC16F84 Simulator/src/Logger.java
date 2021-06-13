@FunctionalInterface
public interface Logger {
	
	void log(String message);
	
    static Logger commandLogger() {
        return message -> System.out.println("LOG - " + message);
    }
    
    static Logger errorLogger() {
        return message -> System.out.println("ERROR - " + message); 
    }
	
}

