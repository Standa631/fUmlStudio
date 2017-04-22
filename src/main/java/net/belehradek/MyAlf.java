package net.belehradek;

import java.io.IOException;

import org.modeldriven.alf.fuml.execution.AlfCompiler;
import org.modeldriven.alf.uml.ElementFactory;
import org.modeldriven.alf.uml.Package;

public class MyAlf extends AlfCompiler {

	@Override
	public void saveModel(String arg0, Package arg1) throws IOException {
		Global.log("MyAlf: saveModel()");
	}

	@Override
	protected ElementFactory createElementFactory() {
		Global.log("MyAlf: createElementFactory()");
		return null;
	}

	@Override
	protected void printUsage() {
		Global.log("MyAlf: printUsage()");
	}
	
}
