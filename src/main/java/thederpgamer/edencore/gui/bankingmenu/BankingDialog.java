package thederpgamer.edencore.gui.bankingmenu;

import api.common.GameClient;
import api.utils.game.PlayerUtils;
import api.utils.gui.SimplePlayerTextInput;
import org.schema.game.client.controller.PlayerInput;
import org.schema.game.client.controller.PlayerOkCancelInput;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationHighlightCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalArea;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalButtonTablePane;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.playerdata.PlayerData;
import thederpgamer.edencore.data.playerdata.PlayerDataManager;
import thederpgamer.edencore.gui.elements.GUILabeledTextInput;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class BankingDialog extends PlayerInput {

	private final BankingPanel panel;

	public BankingDialog() {
		super(GameClient.getClientState());
		(panel = new BankingPanel(getState(), this)).onInit();
	}

	@Override
	public void onDeactivate() {
		
	}

	@Override
	public void handleMouseEvent(MouseEvent mouseEvent) {

	}

	@Override
	public BankingPanel getInputPanel() {
		return panel;
	}
	
	public static class BankingPanel extends GUIInputPanel {
		
		public BankingPanel(InputState state, GUICallback guiCallback) {
			super("BankingPanel", state, 800, 500, guiCallback, "", "");
		}

		@Override
		public void onInit() {
			super.onInit();
			GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
			contentPane.setTextBoxHeightLast(48);
			final PlayerData playerData = PlayerDataManager.getInstance().getClientOwnData();
			final long storedCredits = playerData.getStoredCredits();
			final long currentCredits = GameClient.getClientPlayerState().getCredits();
			GUITextOverlay storedCreditsText = new GUITextOverlay(10, 10, getState());
			storedCreditsText.setFont(FontLibrary.FontSize.MEDIUM.getFont());
			storedCreditsText.onInit();
			storedCreditsText.setTextSimple("Stored Credits: " + storedCredits);
			contentPane.getContent(0).attach(storedCreditsText);
			
			GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 3, 1, contentPane.getContent(0));
			buttonPane.onInit();
			buttonPane.addButton(0, 0, "Deposit Credits", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						(new SimplePlayerTextInput("Deposit Credits", "Enter Amount to Deposit") {
							@Override
							public boolean onInput(String s) {
								try {
									long amount = Long.parseLong(s.trim());
									if(amount > 0 && amount <= currentCredits) {
										playerData.setStoredCredits(playerData.getStoredCredits() + amount);
										PlayerDataManager.getInstance().setPlayerCredits(GameClient.getClientPlayerState(), currentCredits - amount);
										return true;
									}
								} catch(NumberFormatException ignored) {}
								return false;
							}
						}).activate();
					}
				}

				@Override
				public boolean isOccluded() {
					return currentCredits <= 0;
				}
			}, new GUIActivationHighlightCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return true;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return currentCredits > 0;
				}

				@Override
				public boolean isHighlighted(InputState inputState) {
					return currentCredits > 0;
				}
			});
			buttonPane.addButton(1, 0, "Withdraw Credits", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						(new SimplePlayerTextInput("Withdraw Credits", "Enter Amount to Withdraw") {
							@Override
							public boolean onInput(String s) {
								try {
									long amount = Long.parseLong(s.trim());
									if(amount > 0 && amount <= storedCredits) {
										playerData.setStoredCredits(playerData.getStoredCredits() - amount);
										PlayerDataManager.getInstance().setPlayerCredits(GameClient.getClientPlayerState(), currentCredits + amount);
										return true;
									}
								} catch(NumberFormatException ignored) {}
								return false;
							}
						}).activate();
					}
				}

				@Override
				public boolean isOccluded() {
					return storedCredits <= 0;
				}
			}, new GUIActivationHighlightCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return true;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return storedCredits > 0;
				}

				@Override
				public boolean isHighlighted(InputState inputState) {
					return storedCredits > 0;
				}
			});
			buttonPane.addButton(2, 0, "Send Credits", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						(new PlayerOkCancelInput("Send_Credits", getState(),"Send Credits", "Enter Player Name and Amount to Send") {
							
							private GUILabeledTextInput amountInput;
							private GUILabeledTextInput playerInput;
							private GUILabeledTextInput subjectInput;
							private GUILabeledTextInput messageInput;
							
							@Override
							public void activate() {
								GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
								amountInput = new GUILabeledTextInput(10, 10, getState(), "Amount: ", GUILabeledTextInput.LEFT);
								amountInput.onInit();
								contentPane.attach(amountInput);
								playerInput = new GUILabeledTextInput(10, 10, getState(), "Player: ", GUILabeledTextInput.LEFT);
								playerInput.onInit();
								contentPane.attach(playerInput);
								playerInput.getPos().y += amountInput.getHeight() + 10;
								subjectInput = new GUILabeledTextInput(10, 10, getState(), "Subject: ", GUILabeledTextInput.LEFT);
								subjectInput.setTextBox(true);
								subjectInput.onInit();
								subjectInput.setText("Credit Transfer");
								contentPane.addNewTextBox((int) (subjectInput.getHeight() + 2));
								contentPane.attach(subjectInput);
								messageInput = new GUILabeledTextInput(10, 30, getState(), "Message: ", GUILabeledTextInput.LEFT);
								messageInput.setTextBox(true);
								messageInput.onInit();
								messageInput.setText("Sent " + amountInput.getText() + " credits to " + playerInput.getText());
								contentPane.addNewTextBox((int) (messageInput.getHeight() + 2));
								contentPane.attach(messageInput);
								super.activate();
							}
							
							@Override
							public void onDeactivate() {
								
							}

							@Override
							public void pressedOK() {
								String amountText = amountInput.getText().trim();
								String playerText = playerInput.getText().trim();
								long amount = 0;
								PlayerData playerData = null;
								try {
									amount = Long.parseLong(amountText);
									playerData = PlayerDataManager.getInstance().getFromName(playerText, false);
								} catch(Exception ignored) {}
								if(amount > 0) {
									if(amount <= storedCredits) {
										if(playerData != null) {
											playerData.setStoredCredits(playerData.getStoredCredits() + amount);
											playerData.addTransaction(new PlayerData.PlayerBankTransactionData(amount, PlayerDataManager.getInstance().getClientOwnData(), playerData, subjectInput.getText(), messageInput.getText(), PlayerData.PlayerBankTransactionData.TransactionType.TRANSFER));
											PlayerDataManager.getInstance().getClientOwnData().setStoredCredits(storedCredits - amount);
											deactivate();
										} else PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Player not found.");
									} else PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Invalid amount to send.");
								} else PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Invalid amount to send.");
							} 
						}).activate();
					}
				}

				@Override
				public boolean isOccluded() {
					return false;
				}
			}, new GUIActivationHighlightCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return true;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return storedCredits > 0;
				}

				@Override
				public boolean isHighlighted(InputState inputState) {
					return storedCredits > 0;
				}
			});
			contentPane.getContent(0).attach(buttonPane);
			buttonPane.getPos().y += storedCreditsText.getHeight() + 10;
			
			contentPane.addNewTextBox(300);
			PlayerBankingTransactionScrollableList transactionList = new PlayerBankingTransactionScrollableList(getState(), contentPane.getContent(1), playerData);
			transactionList.onInit();
			contentPane.getContent(1).attach(transactionList);
		}
	}
}
