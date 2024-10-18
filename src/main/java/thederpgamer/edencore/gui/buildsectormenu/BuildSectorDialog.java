package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import org.schema.game.client.controller.PlayerInput;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUITabbedContent;
import org.schema.schine.input.InputState;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class BuildSectorDialog extends PlayerInput {
	
	private final BuildSectorPanel panel;
	
	public BuildSectorDialog() {
		super(GameClient.getClientState());
		(panel = new BuildSectorPanel(getState(), this)).onInit();
	}

	@Override
	public void onDeactivate() {
		
	}

	@Override
	public void handleMouseEvent(MouseEvent mouseEvent) {

	}

	@Override
	public BuildSectorPanel getInputPanel() {
		return panel;
	}

	public static class BuildSectorPanel extends GUIInputPanel {
		
		private GUITabbedContent tabbedContent;

		public BuildSectorPanel(InputState state, GUICallback guiCallback) {
			super("BuildSectorPanel", state, guiCallback, GLFrame.getWidth() / 2, GLFrame.getHeight() / 1.5);
		}

		@Override
		public void onInit() {
			super.onInit();
			GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
			contentPane.setTextBoxHeightLast((int) (getHeight() - 50));
			int lastTab = 0;
			if(tabbedContent != null) {
				lastTab = tabbedContent.getSelectedTab();
				tabbedContent.clearTabs();
			}
			tabbedContent = new GUITabbedContent(getState(), contentPane.getContent(0));
			tabbedContent.onInit();
			

			tabbedContent.setSelectedTab(lastTab);
			contentPane.getContent(0).attach(tabbedContent);
		}
	}
}
