package net.belehradek;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Uses Font Awesome by Dave Gandy - http://fontawesome.io/icons/
 */
public enum AwesomeIcon {

    PLUS(0xf067),
    TIMES(0xf00d),
    MAP(0xf03e),
    PROJECT_FILE(0xf0c5),
	CODE_FILE(0xf1c9),
	DIAGRAM_FILE(0xf1c5),
	TRANSFORM_FILE(0xf074),
	FLOPPY(0xf0c7);

    private static final String STYLE_CLASS = "icon";
    private static final String FONT_AWESOME = "FontAwesome";
    private int unicode;

    /**
     * Creates a new awesome icon for the given unicode value.
     * 
     * @param unicode the unicode value as an integer
     */
    private AwesomeIcon(final int unicode) {
        this.unicode = unicode;
    }

    /**
     * Returns a new {@link Node} containing the icon.
     * 
     * @return a new node containing the icon
     */
    public Node node() {
        final Text text = new Text(string());
        text.getStyleClass().setAll(STYLE_CLASS);
        text.setFont(Font.font(FONT_AWESOME));
        return text;
    }
    
    public Node node(double size) {
        final Text text = new Text(string());
        text.getStyleClass().setAll(STYLE_CLASS);
        text.setFont(Font.font(FONT_AWESOME, size));
        return text;
    }
    
    public void toText(Text t) {
    	t.setText(string());
    	t.getStyleClass().setAll(STYLE_CLASS);
        t.setFont(Font.font(FONT_AWESOME));
    }
    
    public String string() {
        return String.valueOf((char) unicode);
    }
    
    public Label label() {
    	final Label text = new Label(string(), node());
        text.getStyleClass().setAll(STYLE_CLASS);
        text.setFont(Font.font(FONT_AWESOME));
        return text;
    }
}
