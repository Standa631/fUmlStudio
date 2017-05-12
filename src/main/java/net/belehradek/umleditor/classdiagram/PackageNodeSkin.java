package net.belehradek.umleditor.classdiagram;

import java.util.ArrayList;
import java.util.List;

import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.utils.GeometryUtils;
import javafx.css.PseudoClass;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import net.belehradek.umleditor.GNodeSkinFix;

public class PackageNodeSkin extends GNodeSkinFix {

    private static final String STYLE_CLASS_SELECTION_HALO = "titled-node-selection-halo";
    private static final String STYLE_CLASS_HEADER = "package-node-header";
    private static final String STYLE_CLASS_TITLE = "titled-node-title";
    private static final String STYLE_CLASS_CONTENT = "titled-node-content";
    private static final String STYLE_CLASS_ATTRIBUTE = "titled-node-attribute";
    private static final String STYLE_CLASS_ATTRIBUTES = "titled-node-attributes";
    private static final String STYLE_CLASS_OPERATION = "titled-node-operation";
    private static final String STYLE_CLASS_OPERATIONS = "titled-node-operations";
    
    private static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");

    private static final double HALO_OFFSET = 5;
    private static final double HALO_CORNER_SIZE = 10;

    private static final int HEADER_HEIGHT = 20;

    private final Rectangle selectionHalo = new Rectangle();

    private VBox content = new VBox();
    private HBox header = new HBox();
    private Label title = new Label();
    
    private final List<GConnectorSkin> inputConnectorSkins = new ArrayList<>();
    private final List<GConnectorSkin> outputConnectorSkins = new ArrayList<>();

    public PackageNodeSkin(final GNode node) {
        super(node);
        
        //content.widthProperty().bind(getRoot().widthProperty());

        //getRoot().getChildren().add(border);
        //getRoot().getStyleClass().setAll(STYLE_CLASS_BORDER);
        //getRoot().setMinSize(MIN_WIDTH, MIN_HEIGHT);

        addSelectionHalo();
        addSelectionListener();

        createContent();

        content.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::filterMouseDragged);
    }

    @Override
    public void initialize() {
        super.initialize();
    }
    
    public void setName(String name) {
    	title.setText("Package " + name);
    }

    @Override
    public void setConnectorSkins(final List<GConnectorSkin> connectorSkins) {

        removeAllConnectors();

        inputConnectorSkins.clear();
        outputConnectorSkins.clear();

        if (connectorSkins != null) {
            for (final GConnectorSkin connectorSkin : connectorSkins) {

                final boolean isInput = connectorSkin.getConnector().getType().contains("input");
                final boolean isOutput = connectorSkin.getConnector().getType().contains("output");

                if (isInput) {
                    inputConnectorSkins.add(connectorSkin);
                } else if (isOutput) {
                    outputConnectorSkins.add(connectorSkin);
                }

                if (isInput || isOutput) {
                    getRoot().getChildren().add(connectorSkin.getRoot());
                }
            }
        }

        setConnectorsSelected(isSelected());
    }

    @Override
    public void layoutConnectors() {
        layoutLeftAndRightConnectors();
        layoutSelectionHalo();
    }

    @Override
    public Point2D getConnectorPosition(final GConnectorSkin connectorSkin) {

        final Node connectorRoot = connectorSkin.getRoot();

        final double x = connectorRoot.getLayoutX() + connectorSkin.getWidth() / 2;
        final double y = connectorRoot.getLayoutY() + connectorSkin.getHeight() / 2;

        if (inputConnectorSkins.contains(connectorSkin)) {
            return new Point2D(x, y);
        } else {
            // Subtract 1 to align start-of-connection correctly. Compensation for rounding errors?
            return new Point2D(x - 1, y);
        }
    }

    /**
     * Creates the content of the node skin - header, title, close button, etc.
     */
    private void createContent() {
        header.getStyleClass().setAll(STYLE_CLASS_HEADER);
        title.getStyleClass().setAll(STYLE_CLASS_TITLE);
        content.getStyleClass().setAll(STYLE_CLASS_CONTENT);

        header.getChildren().add(title);
        content.getChildren().add(header);
        getRoot().getChildren().add(content);

//        content.minWidthProperty().bind(getRoot().widthProperty());
//        content.prefWidthProperty().bind(getRoot().widthProperty());
//        content.maxWidthProperty().bind(getRoot().widthProperty());
//        content.minHeightProperty().bind(getRoot().heightProperty());
//        content.prefHeightProperty().bind(getRoot().heightProperty());
//        content.maxHeightProperty().bind(getRoot().heightProperty());
    }

    /**
     * Lays out all connectors.
     */
    private void layoutLeftAndRightConnectors() {

        final int inputCount = inputConnectorSkins.size();
        final double inputOffsetY = (getRoot().getHeight() - HEADER_HEIGHT) / (inputCount + 1);

        for (int i = 0; i < inputCount; i++) {

            final GConnectorSkin inputSkin = inputConnectorSkins.get(i);
            final Node connectorRoot = inputSkin.getRoot();

            final double layoutX = GeometryUtils.moveOnPixel(0 - inputSkin.getWidth() / 2);
            final double layoutY = GeometryUtils.moveOnPixel((i + 1) * inputOffsetY - inputSkin.getHeight() / 2);

            connectorRoot.setLayoutX(layoutX);
            connectorRoot.setLayoutY(layoutY + HEADER_HEIGHT);
        }

        final int outputCount = outputConnectorSkins.size();
        final double outputOffsetY = (getRoot().getHeight() - HEADER_HEIGHT) / (outputCount + 1);

        for (int i = 0; i < outputCount; i++) {

            final GConnectorSkin outputSkin = outputConnectorSkins.get(i);
            final Node connectorRoot = outputSkin.getRoot();

            final double layoutX = GeometryUtils.moveOnPixel(getRoot().getWidth() - outputSkin.getWidth() / 2);
            final double layoutY = GeometryUtils.moveOnPixel((i + 1) * outputOffsetY - outputSkin.getHeight() / 2);

            connectorRoot.setLayoutX(layoutX);
            connectorRoot.setLayoutY(layoutY + HEADER_HEIGHT);
        }
    }

    /**
     * Adds the selection halo and initializes some of its values.
     */
    private void addSelectionHalo() {

        content.getChildren().add(selectionHalo);

        selectionHalo.setManaged(false);
        selectionHalo.setMouseTransparent(false);
        selectionHalo.setVisible(false);

        selectionHalo.setLayoutX(-HALO_OFFSET);
        selectionHalo.setLayoutY(-HALO_OFFSET);

        selectionHalo.getStyleClass().add(STYLE_CLASS_SELECTION_HALO);
    }

    /**
     * Lays out the selection halo based on the current width and height of the node skin region.
     */
    private void layoutSelectionHalo() {

        if (selectionHalo.isVisible()) {

            selectionHalo.setWidth(content.getWidth() + 2 * HALO_OFFSET);
            selectionHalo.setHeight(content.getHeight() + 2 * HALO_OFFSET);

            final double cornerLength = 2 * HALO_CORNER_SIZE;
            final double xGap = content.getWidth() - 2 * HALO_CORNER_SIZE + 2 * HALO_OFFSET;
            final double yGap = content.getHeight() - 2 * HALO_CORNER_SIZE + 2 * HALO_OFFSET;

            selectionHalo.setStrokeDashOffset(HALO_CORNER_SIZE);
            selectionHalo.getStrokeDashArray().setAll(cornerLength, yGap, cornerLength, xGap);
        }
    }

    /**
     * Adds a listener to react to whether the node is selected or not and change the style accordingly.
     */
    private void addSelectionListener() {

        selectedProperty().addListener((v, o, n) -> {

            if (n) {
                selectionHalo.setVisible(true);
                layoutSelectionHalo();
                content.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
                getRoot().toFront();
            } else {
                selectionHalo.setVisible(false);
                content.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
            }

            setConnectorsSelected(n);
        });
    }

    /**
     * Removes any input and output connectors from the list of children, if they exist.
     */
    private void removeAllConnectors() {

        for (final GConnectorSkin connectorSkin : inputConnectorSkins) {
            getRoot().getChildren().remove(connectorSkin.getRoot());
        }

        for (final GConnectorSkin connectorSkin : outputConnectorSkins) {
            getRoot().getChildren().remove(connectorSkin.getRoot());
        }
    }

    /**
     * Adds or removes the 'selected' pseudo-class from all connectors belonging to this node.
     * 
     * @param isSelected {@code true} to add the 'selected' pseudo-class, {@code false} to remove it
     */
    private void setConnectorsSelected(final boolean isSelected) {

        for (final GConnectorSkin skin : inputConnectorSkins) {
            if (skin instanceof TitledConnectorSkin) {
                ((TitledConnectorSkin) skin).setSelected(isSelected);
            }
        }

        for (final GConnectorSkin skin : outputConnectorSkins) {
            if (skin instanceof TitledConnectorSkin) {
                ((TitledConnectorSkin) skin).setSelected(isSelected);
            }
        }
    }

    /**
     * Stops the node being dragged if it isn't selected.
     * 
     * @param event a mouse-dragged event on the node
     */
    private void filterMouseDragged(final MouseEvent event) {
        if (event.isPrimaryButtonDown() && !isSelected()) {
            event.consume();
        }
    }
}