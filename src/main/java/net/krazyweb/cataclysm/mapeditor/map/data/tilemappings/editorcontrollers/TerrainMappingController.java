package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.CataclysmDefinitions;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TerrainMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import net.krazyweb.util.AutoCompletePopup;


public class TerrainMappingController extends MappingController {

	@FXML
	private TextField terrainType;

	private TerrainMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof  TerrainMapping) {

			this.mapping = (TerrainMapping) mapping;
			terrainType.setText(this.mapping.terrain);

			terrainType.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.terrain = newValue;
			});

			AutoCompletePopup.bind(terrainType, CataclysmDefinitions.terrain);

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type TerrainMapping.");
		}
	}

}
