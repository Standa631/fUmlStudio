package net.belehradek.fuml.codegeneration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.modeldriven.alf.uml.Behavior;
import org.modeldriven.alf.uml.Class_;
import org.modeldriven.alf.uml.Classifier;
import org.modeldriven.alf.uml.Comment;
import org.modeldriven.alf.uml.Dependency;
import org.modeldriven.alf.uml.Element;
import org.modeldriven.alf.uml.ElementImport;
import org.modeldriven.alf.uml.Feature;
import org.modeldriven.alf.uml.Generalization;
import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.Namespace;
import org.modeldriven.alf.uml.Operation;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.PackageImport;
import org.modeldriven.alf.uml.PackageableElement;
import org.modeldriven.alf.uml.Property;
import org.modeldriven.alf.uml.Reception;
import org.modeldriven.alf.uml.Stereotype;
import org.modeldriven.alf.uml.TemplateBinding;
import org.modeldriven.alf.uml.TemplateParameter;
import org.modeldriven.alf.uml.TemplateSignature;
import org.modeldriven.alf.uml.TemplateableElement;
import org.modeldriven.alf.uml.StereotypeApplication.TaggedValue;

import net.belehradek.fuml.core.UmlFrameworkWrapper;
import net.belehradek.fuml.core.UmlWrapper;

public class ExtendedMethodWrapper extends MethodWrapper {

	protected Object rootModel;
	
	public ExtendedMethodWrapper(Object model, Object rootModel) {
		super(model);
		this.rootModel = rootModel;
	}

	@Override
	protected Object callMethod(String methodName) {
		if (model instanceof Package) {
			Package p = (Package) model;
			if (methodName.equals("classes")) {
//				List<NamedElement> classes = (List<NamedElement>) super.callMethod("getOwnedMember");
//				List<NamedElement> out = new ArrayList<>(classes);
//				out.removeIf(l -> !(l instanceof Class_));
//				return out;
				return UmlFrameworkWrapper.getAllClasses(p, false);
			}
		}
		
		if (model instanceof Class_) {
			Class_ c = (Class_) model;
			if (methodName.equals("isStereoActivity")) {
				Package root = (Package) rootModel;
				List<NamedElement> members = new ArrayList<>(root.getOwnedMember());
				members.removeIf(l -> !(l instanceof Stereotype));
				Stereotype stereo = (Stereotype) members.get(0);
				return c.isStereotypeApplied(stereo);
			}
		}
		
		return super.callMethod(methodName);
	}
	
	@Override
	public MethodWrapper wrap(Object o) {
		return new ExtendedMethodWrapper(o, rootModel);
	}
}
