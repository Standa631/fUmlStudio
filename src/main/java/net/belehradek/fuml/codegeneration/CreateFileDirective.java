package net.belehradek.fuml.codegeneration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import net.belehradek.Global;

public class CreateFileDirective implements TemplateDirectiveModel {
	
	protected static final String PARAM_NAME_FILENAME = "fileName";
	protected static final String PARAM_NAME_FILEPATH = "filePath";
	
	protected File rootDir;
	
	public CreateFileDirective(File rootDir) {
		this.rootDir = rootDir;
	}

    public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body)
            throws TemplateException, IOException {

    	String fileName = params.get(PARAM_NAME_FILENAME).toString();
    	//String filePath = (String) params.get(PARAM_NAME_FILEPATH);
    	
    	Global.log("CreateFileDirective: " + fileName);

        if (body != null) {
            //body.render(env.getOut());
        	
        	File file = new File(rootDir, fileName);
        	file.createNewFile();
        	FileWriter fw = new FileWriter(file);
        	BufferedWriter bw = new BufferedWriter(fw);
        	body.render(bw);
        	bw.close();
        }
    }
}