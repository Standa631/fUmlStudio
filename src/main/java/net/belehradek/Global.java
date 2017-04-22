package net.belehradek;

import java.io.PrintStream;

import org.modeldriven.alf.uml.Element;
import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.Package;

public class Global {
	
	protected static PrintStream loggerStream = null;
	
	public static void log(String text) {
		System.out.println("Log: " + text);
		
		if (loggerStream != null) {
			loggerStream.println("Log: " + text);
		}
	}
	
	public static void log(Package pack) {
		log(pack.getClass().getName() + ": " + pack.getName());
		for (NamedElement e : pack.getMember()) {
			log(e.getClass().getName() + ": " + e.getName());
		}
	}
	
	public static PrintStream getLoggerStream() {
		return loggerStream;
	}
	
	public static void setLoggerStream(PrintStream loggerStream) {
		Global.loggerStream = loggerStream;
	}
}
