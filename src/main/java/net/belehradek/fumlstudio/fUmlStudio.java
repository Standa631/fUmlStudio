/*
 * Copyright (C) 2005 - 2014 by TESIS DYNAware GmbH
 */
package net.belehradek.fumlstudio;

import javafx.application.Application;
import javafx.stage.Stage;
import net.belehradek.fumlstudio.controller.fUmlStudioController;
import de.tesis.dynaware.grapheditor.GraphEditor;

/**
 * A demo application to show uses of the {@link GraphEditor} library.
 */
public class fUmlStudio extends Application {

    @Override
    public void start(final Stage stage) throws Exception {
    	fUmlStudioController cont = new fUmlStudioController();
    	cont.showWindow(stage);
    }

    public static void main(final String[] args) {
        launch(args);
    }
}