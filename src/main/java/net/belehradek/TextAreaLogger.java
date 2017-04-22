package net.belehradek;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TextAreaLogger extends TextArea {
	
	protected StringBuilder buffer = null;
	protected OutputStream outputStream = null;
	protected PrintStream printStream = null;
	
	public TextAreaLogger() {
		super();
		buffer = new StringBuilder();
	}
	
	public OutputStream getOutputStream() {
		if (outputStream == null) {
			outputStream = new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					buffer.append(Character.toChars((b + 256) % 256));
				    if ((char) b == '\n') {
				    	Platform.runLater(new Runnable() {
							@Override
							public void run() {
								String s = buffer.toString();
								TextAreaLogger.this.appendText(s);
						    	TextAreaLogger.this.setScrollTop(Double.MAX_VALUE);
						    	buffer.delete(0, s.length());
							}
						});
				    }
				}
			};
		}
		return outputStream;
	}
	
	public PrintStream getPrintStream() {
		if (printStream == null) {
			printStream = new PrintStream(getOutputStream());
		}
		return printStream;
	}
}
