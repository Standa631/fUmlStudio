package net.belehradek.umleditor.classdiagram;

import java.util.List;
import java.util.Map;

import de.tesis.dynaware.grapheditor.core.skins.defaults.DefaultConnectionSkin;
import de.tesis.dynaware.grapheditor.core.skins.defaults.connection.SimpleConnectionSkin;
import de.tesis.dynaware.grapheditor.model.GConnection;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import net.belehradek.umleditor.Arrow;
import net.belehradek.umleditor.ArrowUtils;

public class AssociationConnectionSkin extends DefaultConnectionSkin {
	
	private static final String STYLE_CLASS = "tree-connection";
	
	protected Arrow arrow = new Arrow();
	protected Group root;

	public AssociationConnectionSkin(GConnection connection) {
		super(connection);
		arrow = new Arrow();
		arrow.setManaged(false);
        arrow.getStyleClass().setAll(STYLE_CLASS);
		root = new Group(super.getRoot(), arrow);
	}
	
	@Override
	public void draw(List<Point2D> points, Map<GConnection, List<Point2D>> allPoints) {
		super.draw(points, allPoints);

		if (points.size() >= 2) {
			Point2D start = points.get(points.size()-2);
			Point2D end = points.get(points.size()-1);
			
			arrow.setHeadWidth(10);
			arrow.setHeadLength(10);

			ArrowUtils.draw(arrow, start, end, 0);
		}
	}
	
  @Override
  public Node getRoot() {
      return root;
  }
}