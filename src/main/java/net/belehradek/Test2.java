package net.belehradek;

import java.util.List;

import org.modeldriven.alf.syntax.units.Member;
import org.modeldriven.alf.syntax.units.OperationDefinition;
import org.modeldriven.alf.syntax.units.UnitDefinition;

public class Test2 {

	public static void main(String[] args) {
		new Test2().run();
	}

	public void run() {
		Global.log("Test2");
		
		alfParseTest();
		
		Global.log("Done");
	}
	
	public void alfParseTest() {
		MyAlf alf = new MyAlf();
		alf.setIsVerbose(true);
		alf.setIsParseOnly(true);
		alf.setModelDirectory("C:\\Users\\Bel2\\DIP\\fUmlTest\\src\\main\\alf");
		alf.setLibraryDirectory("C:\\Users\\Bel2\\DIP\\fUmlGradlePlugin\\Libraries");
		UnitDefinition unit = alf.parse("App", false);
		alf.process(unit);
		
		List<Member> members = unit.getDefinition().getOwnedMember();
		for (Member m : members) {
			if (m instanceof OperationDefinition) {
				OperationDefinition d = (OperationDefinition) m;
				if (d.getName().equals("runActivity"))
					d.setName("BlaBlaBla");
				
			}
			
			Global.log(m._toString(true));
			Global.log("Position: " + m.getFileName() + "[" + m.getLine() + ":" + m.getColumn() + "]");
		}
	}
}
