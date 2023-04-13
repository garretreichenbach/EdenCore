package thederpgamer.edencore.gui.eventeditor;

import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.target.DefenseTarget;
import thederpgamer.edencore.data.event.target.DestroyTarget;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class EventEditorFrame extends JFrame {

	private final EventData eventData;
	
	public EventEditorFrame(EventData eventData) {
		this.eventData = eventData;
		setTitle("Event Editor");
		setSize(800, 600);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		init();
		setVisible(true);
	}

	private void init() {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		{ //Details collapsible panel
			final JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setBorder(BorderFactory.createTitledBorder("Details"));
			//Make the panel collapsible
			final JToggleButton toggleButton = new JToggleButton("Details");
			toggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					panel.setVisible(toggleButton.isSelected());
				}
			});
			add(toggleButton);

			//Event Name entry
			panel.add(new JLabel("Event Name:"));
			final JTextField eventNameField = new JTextField(eventData.getName(), 20);
			eventNameField.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					eventData.setName(eventNameField.getText());
				}
			});
			panel.add(eventNameField);
			panel.add(new JLabel("Event Type: " + eventData.getEventType()));

			//Event description entry
			panel.add(new JLabel("Event Description:"));
			final JTextArea eventDescriptionArea = new JTextArea(eventData.getDescription(), 5, 20);
			eventDescriptionArea.setLineWrap(true);
			eventDescriptionArea.setWrapStyleWord(true);
			eventDescriptionArea.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					eventData.setDescription(eventDescriptionArea.getText());
				}
			});

			final JCheckBox pvpCheckBox = new JCheckBox();
			final JCheckBox pveCheckBox = new JCheckBox();

			//PvP toggle
			panel.add(new JLabel("PvP:"));
			pvpCheckBox.setSelected(eventData.isPvp());
			pvpCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					eventData.setPvp(pvpCheckBox.isSelected());
					pveCheckBox.setEnabled(!pvpCheckBox.isSelected());
				}
			});

			//PvE toggle
			panel.add(new JLabel("PvE:"));
			pveCheckBox.setSelected(eventData.isPve());
			pveCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					eventData.setPve(pveCheckBox.isSelected());
					pvpCheckBox.setEnabled(!pveCheckBox.isSelected());
				}
			});
			add(panel);
		}

		{ //Targets collapsible panel
			final JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setBorder(BorderFactory.createTitledBorder("Targets"));
			//Make the panel collapsible
			final JToggleButton toggleButton = new JToggleButton("Targets");
			toggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					panel.setVisible(toggleButton.isSelected());
				}
			});
			add(toggleButton);

			//List of targets
			final JList<String> targetList = new JList<>(eventData.getTargetsStrings());

			final JButton addButton = new JButton("Add");
			addButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//New target dialog
					final JDialog dialog = new JDialog(EventEditorFrame.this, "New Target", true);
					dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
					dialog.setSize(300, 200);
					dialog.setLocationRelativeTo(null);
					dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

					//Target name entry
					dialog.add(new JLabel("Target Name:"));
					final JTextField targetNameField = new JTextField(20);
					dialog.add(targetNameField);

					//Target type selection
					dialog.add(new JLabel("Target Type:"));
					final JComboBox<String> targetTypeComboBox = new JComboBox<>(new String[] {"DESTROY", "DEFEND"});
					dialog.add(targetTypeComboBox);

					//Target faction selection
					dialog.add(new JLabel("Target Faction:"));
					final JComboBox<String> targetFactionComboBox = new JComboBox<>(new String[] {"ENEMY", "TRADERS", "SQUAD_1_FACTION", "SQUAD_2_FACTION", "SQUAD_3_FACTION", "SQUAD_4_FACTION"});
					dialog.add(targetFactionComboBox);

					//Target count entry
					dialog.add(new JLabel("Target Count:"));
					final JTextField targetCountField = new JTextField(20);
					dialog.add(targetCountField);

					//Target blueprint selection
					dialog.add(new JLabel("Target Blueprint:"));
					final JComboBox<String> targetBlueprintComboBox = new JComboBox<>(getBPNames());
					dialog.add(targetBlueprintComboBox);

					//Create target button
					JButton createButton = new JButton("Create");
					createButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							//Create new target
							switch(targetTypeComboBox.getSelectedIndex()) {
								case 0:
									eventData.addTarget(new DestroyTarget(targetNameField.getText(), targetFactionComboBox.getSelectedIndex(), Integer.parseInt(targetCountField.getText()), targetBlueprintComboBox.getSelectedIndex()));
									break;
								case 1:
									eventData.addTarget(new DefenseTarget(targetNameField.getText(), targetFactionComboBox.getSelectedIndex(), Integer.parseInt(targetCountField.getText()), targetBlueprintComboBox.getSelectedIndex()));
									break;
							}
						}
					});
				}
			});
			panel.add(addButton);
		}
	}

	public static String[] getBPNames() {
		List<String> bpNames = new ArrayList<>();
		for(BlueprintEntry entry : BluePrintController.active.readBluePrints()) bpNames.add(entry.getName());
		return bpNames.toArray(new String[0]);
	}
}
