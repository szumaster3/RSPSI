package com.rspsi;

import com.google.common.primitives.Ints;
import com.rspsi.resources.ResourceLoader;
import com.rspsi.util.FXUtils;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.table.TableModel;
import java.util.*;

public class PickCoordinatesWindow extends Application {

	private Stage stage;
	private boolean okClicked;

	private Map<String, String> coordMap = new HashMap<>();

	@FXML
	private TextField xCoordinate;

	@FXML
	private TextField yCoordinate;

	@FXML
	private ComboBox<String> locationComboBox;

	@FXML
	private Button okButton;

	@FXML
	private Button cancelButton;

	@FXML
	private Spinner<Integer> widthSpinner;

	@FXML
	private Spinner<Integer> lengthSpinner;

	private BooleanProperty completed = new SimpleBooleanProperty(false);

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/coords.fxml"));
		loader.setController(this);

		Parent content = loader.load();
		Scene scene = new Scene(content);

		primaryStage.setTitle("Please enter coordinates");
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());
		primaryStage.setAlwaysOnTop(true);

		FXUtils.centerStage(primaryStage);
		primaryStage.centerOnScreen();

		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				primaryStage.hide();
				okClicked = true;
			} else if (event.getCode() == KeyCode.COMMA) {
				if (xCoordinate.isFocused()) {
					yCoordinate.requestFocus();
					event.consume();
				}
			}
		});

		widthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1, 1));
		lengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1, 1));
		FXUtils.addSpinnerFocusListeners(widthSpinner, lengthSpinner);

		primaryStage.setOnShown(evt -> xCoordinate.requestFocus());

		okButton.setOnAction(evt -> {
			primaryStage.hide();
			okClicked = true;
		});

		cancelButton.setOnAction(evt -> {
			reset();
			primaryStage.hide();
		});

		locationComboBox.setOnAction(e -> {
			String selected = locationComboBox.getValue();
			if (selected == null) return;

			String coords = coordMap.get(selected);
			if (coords == null) return;

			String[] split = coords.split(",");

			xCoordinate.setText(split[0]);
			yCoordinate.setText(split[1]);
		});
	}

	public void loadLocations(TableModel model) {
		List<String> locations = new ArrayList<>();
		coordMap.clear();

		for (int i = 0; i < model.getRowCount(); i++) {
			String coords = (String) model.getValueAt(i, 0);
			String name = (String) model.getValueAt(i, 1);

			String[] split = coords.split(",");
			String display = name + " - " + split[0] + "," + split[1];

			locations.add(display);
			coordMap.put(display, coords);
		}

		locationComboBox.getItems().setAll(locations);

		if (!locations.isEmpty()) {
			locationComboBox.getSelectionModel().selectFirst();
		}
	}

	public void show() {
		reset();
		stage.sizeToScene();
		xCoordinate.requestFocus();
		stage.showAndWait();

		if (!okClicked)
			reset();
	}

	public int getXCoordinate() {
		Integer val = Ints.tryParse(xCoordinate.getText());
		return val != null ? val : 0;
	}

	public int getYCoordinate() {
		Integer val = Ints.tryParse(yCoordinate.getText());
		return val != null ? val : 0;
	}

	public int getWidth() {
		return widthSpinner.getValue();
	}

	public int getLength() {
		return lengthSpinner.getValue();
	}

	public boolean valid() {
		return !xCoordinate.getText().isEmpty()
				&& !yCoordinate.getText().isEmpty()
				&& okClicked;
	}

	public void reset() {
		okClicked = false;
	}

	public BooleanProperty getCompleted() {
		return completed;
	}
}