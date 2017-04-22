package net.belehradek.fuml.codegeneration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import net.belehradek.Global;
import net.belehradek.NullWriter;

public class TemplateEngineHelper {
	
	public static void transform(File template, Object model, File output) throws IOException, TemplateException {
		@SuppressWarnings("deprecation")
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		//cfg.setLogTemplateExceptions(false);
		
		cfg.setObjectWrapper(new ModelObjectWrapper(cfg.getIncompatibleImprovements()));
		
		cfg.setDirectoryForTemplateLoading(template.getParentFile());
		Template temp = cfg.getTemplate(template.getName());
		
		output.getParentFile().mkdirs();
		output.createNewFile();
		Writer out = new FileWriter(output);
		Environment env = temp.createProcessingEnvironment(model, out);
		env.process();
		//TemplateModel x = env.getVariable("testattribute");
		out.close();
	}
	
	public static void transform(File template, Object model) throws IOException, TemplateException {
		@SuppressWarnings("deprecation")
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		
		cfg.setObjectWrapper(new ModelObjectWrapper(cfg.getIncompatibleImprovements()));
		
		cfg.setDirectoryForTemplateLoading(template.getParentFile());
		Template temp = cfg.getTemplate(template.getName());
		
		Environment env = temp.createProcessingEnvironment(model, new NullWriter());
		env.process();
	}
}
