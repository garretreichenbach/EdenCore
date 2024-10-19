package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import org.schema.game.client.controller.PlayerInput;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.buildsectordata.BuildSectorData;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class BuildSectorEditEntityUserPermissionsDialog extends PlayerInput {
	
	private final BuildSectorEditEntityUserPermissionsPanel panel;
	
	public BuildSectorEditEntityUserPermissionsDialog(int entityID, String username, BuildSectorData buildSectorData) {
		super(GameClient.getClientState());
		(panel = new BuildSectorEditEntityUserPermissionsPanel(getState(), this, username, buildSectorData, entityID)).onInit();
	}

	@Override
	public void onDeactivate() {
		panel.cleanUp();
	}

	@Override
	public void handleMouseEvent(MouseEvent mouseEvent) {

	}

	@Override
	public BuildSectorEditEntityUserPermissionsPanel getInputPanel() {
		return panel;
	}

	public static class BuildSectorEditEntityUserPermissionsPanel extends GUIInputPanel {

		private final BuildSectorData buildSectorData;
		private final String username;
		private final int entityID;

		public BuildSectorEditEntityUserPermissionsPanel(InputState state, GUICallback guiCallback, String username, BuildSectorData buildSectorData, int entityID) {
			super("BuildSectorEditEntityUserPermissionsPanel", state, guiCallback, GLFrame.getWidth() / 2, GLFrame.getHeight() / 1.5);
			this.username = username;
			this.buildSectorData = buildSectorData;
			this.entityID = entityID;
		}

		@Override
		public void onInit() {
			super.onInit();
			GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
			contentPane.setTextBoxHeightLast((int) (getHeight() - 50));
			BuildSectorPermissionsScrollableList permissionsScrollableList = new BuildSectorPermissionsScrollableList(getState(), contentPane.getContent(0), username, buildSectorData, entityID);
			permissionsScrollableList.onInit();
			contentPane.getContent(0).attach(permissionsScrollableList);
		}
	}
}
