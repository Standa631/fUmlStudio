package net.belehradek.umleditor.classdiagram;

import java.util.List;
import java.util.Map;

import de.tesis.dynaware.grapheditor.core.skins.defaults.connection.SimpleConnectionSkin;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.utils.Arrow;
import javafx.geometry.Point2D;
import net.belehradek.umleditor.ArrowUtils;

public class Test2ConnectionSkin extends SimpleConnectionSkin {
	
	private static final String STYLE_CLASS = "tree-connection";

	public Test2ConnectionSkin(GConnection connection) {
		super(connection);
	}
	
	@Override
	public void draw(List<Point2D> points, Map<GConnection, List<Point2D>> allPoints) {
		super.draw(points, allPoints);
		
		if (points.size() >= 2) {
			Point2D end = points.get(points.size()-1);
			Point2D start = end.subtract(new Point2D(10,0));
			Arrow arrow = new Arrow();
	        arrow.setManaged(false);
	        arrow.getStyleClass().setAll(STYLE_CLASS);
			ArrowUtils.draw(arrow, start, end, 15);
		}
	}
}
