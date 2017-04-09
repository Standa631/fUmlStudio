package net.belehradek.fumlstudio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class TemplateEngineHelper {
	
	public static void transform(File template, Object model, File output) throws IOException, TemplateException {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		//cfg.setLogTemplateExceptions(false);
		
		cfg.setDirectoryForTemplateLoading(template.getParentFile());
		Template temp = cfg.getTemplate(template.getName());
		Writer out = new FileWriter(output);
		temp.process(model, out);
		out.close();
	}
}
