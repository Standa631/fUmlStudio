package net.belehradek;

import java.io.PrintStream;

import org.modeldriven.alf.uml.Element;
import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.Class_;

public class Global {
	
	protected static PrintStream loggerStream = null;
	
	protected static String logPrefix = "";
	
	public static void log(String text) {
		System.out.println(logPrefix + text);
		
		if (loggerStream != null) {
			loggerStream.println(logPrefix + text);
		}
	}
	
	public static void log(Package pack) {
		if (pack == null) return;
		
		log(pack.getClass().getName() + ": " + pack.getName());
		for (NamedElement e : pack.getMember()) {
			log(e);
		}
	}
	
	public static void log(NamedElement e) {
		log(e.getClass().getName() + ": " + e.getName());
	}
	
	public static PrintStream getLoggerStream() {
		return loggerStream;
	}
	
	public static void setLoggerStream(PrintStream loggerStream) {
		Global.loggerStream = loggerStream;
	}
}
