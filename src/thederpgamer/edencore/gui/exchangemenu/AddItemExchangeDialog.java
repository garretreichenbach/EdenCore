package thederpgamer.edencore.gui.exchangemenu;

import api.utils.gui.GUIInputDialog;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/24/2021]
 */
public class AddItemExchangeDialog extends GUIInputDialog {

    @Override
    public AddItemExchangePanel createPanel() {
        return new AddItemExchangePanel(getState(), this);
    }

    @Override
    public AddItemExchangePanel getInputPanel() {
        return (AddItemExchangePanel) super.getInputPanel();
    }
}
