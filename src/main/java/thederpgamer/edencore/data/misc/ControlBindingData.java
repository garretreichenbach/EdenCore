package thederpgamer.edencore.data.misc;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.schema.schine.common.language.Lng;
import thederpgamer.edencore.EdenCore;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ControlBindingData {
	
	private static final Set<ControlBindingData> bindings = new HashSet<>();
	private static final byte VERSION = 0;

	public enum ControlType {
		MOUSE("MOUSE"),
		KEYBOARD("KEYBOARD"),
		JOYSTICK_PAD("JOYSTICK/PAD");
		
		private final String name;
		
		ControlType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return Lng.str(name);
		}
	}
	
	private String name;
	private String description;
	private ControlType controlType;
	private int binding;

	private ControlBindingData(String name, String description, ControlType controlType, int binding) {
		this.name = name;
		this.description = description;
		this.controlType = controlType;
		this.binding = binding;
	}
	
	private ControlBindingData(JSONObject data) {
		deserialize(data);
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public ControlType getControlType() {
		return controlType;
	}
	
	public int getBinding() {
		return binding;
	}
	
	public void setBinding(int binding) {
		this.binding = binding;
		save();
	}
	
	private JSONObject serialize() {
		JSONObject data = new JSONObject();
		data.put("version", VERSION);
		data.put("name", name);
		data.put("description", description);
		data.put("controlType", controlType.name());
		data.put("binding", binding);
		return data;
	}

	private void deserialize(JSONObject data) {
		byte version = (byte) data.getInt("version");
		name = data.getString("name");
		description = data.getString("description");
		controlType = ControlType.valueOf(data.getString("controlType"));
		binding = data.getInt("binding");
	}
	
	public static Set<ControlBindingData> getBindingsCategory(ControlType controlType) {
		Set<ControlBindingData> categoryBindings = new HashSet<>();
		for(ControlBindingData binding : bindings) {
			if(binding.controlType == controlType) categoryBindings.add(binding);
		}
		return categoryBindings;
	}
	
	public static Set<ControlBindingData> getBindings() {
		if(bindings.isEmpty()) load();
		return Collections.unmodifiableSet(bindings);
	}
	
	public static ControlBindingData getBinding(String name) {
		if(bindings.isEmpty()) load();
		for(ControlBindingData binding : bindings) {
			if(binding.name.equals(name)) return binding;
		}
		return null;
	}
	
	public static ControlBindingData getBinding(int binding) {
		if(bindings.isEmpty()) load();
		for(ControlBindingData controlBinding : bindings) {
			if(controlBinding.binding == binding) return controlBinding;
		}
		return null;
	}
	
	public static void addBinding(ControlBindingData binding) {
		bindings.add(binding);
		save();
	}
	
	public static void removeBinding(ControlBindingData binding) {
		bindings.remove(binding);
		save();
	}
	
	public static void updateBinding(ControlBindingData binding) {
		removeBinding(binding);
		addBinding(binding);
	}
	
	private static void load() {
		try {
			File jsonFile = new File(EdenCore.getInstance().getSkeleton().getResourcesFolder() + "/control_bindings.json");
			if(!jsonFile.exists()) {
				jsonFile.createNewFile();
				saveDefaults(jsonFile);
			}
			JSONObject data = new JSONObject(FileUtils.readFileToString(jsonFile));
			JSONArray bindingArray = data.getJSONArray("bindings");
			for(int i = 0; i < bindingArray.length(); i ++) bindings.add(new ControlBindingData(bindingArray.getJSONObject(i)));
		} catch(Exception exception) {
			EdenCore.getInstance().logException("An error occurred while loading control bindings", exception);
		}
	}

	private static void save() {
		try {
			File jsonFile = new File(EdenCore.getInstance().getSkeleton().getResourcesFolder() + "/control_bindings.json");
			if(!jsonFile.exists()) {
				jsonFile.createNewFile();
				saveDefaults(jsonFile);
			}
			JSONObject data = new JSONObject();
			JSONArray bindingArray = new JSONArray();
			for(ControlBindingData binding : bindings) bindingArray.put(binding.serialize());
			data.put("bindings", bindingArray);
			FileUtils.write(jsonFile, data.toString());
		} catch(Exception exception) {
			EdenCore.getInstance().logException("An error occurred while saving control bindings", exception);
		}
	}
	
	private static void saveDefaults(File jsonFile) {
		try {
			Set<ControlBindingData> dataSet = new HashSet<>(); //Todo: Figure out numpad key ids
			dataSet.add(new ControlBindingData("Guide Menu", "Opens the Guide Menu.", ControlType.KEYBOARD, 0));
			dataSet.add(new ControlBindingData("Banking Menu", "Opens the Banking Menu.", ControlType.KEYBOARD, 0));
			dataSet.add(new ControlBindingData("Exchange Menu", "Opens the Exchange Menu.", ControlType.KEYBOARD, 0));
			dataSet.add(new ControlBindingData("Build Sector Menu", "Opens the Build Sector Menu.", ControlType.KEYBOARD, 0));
			JSONObject data = new JSONObject();
			data.put("bindings", dataSet);
			FileUtils.write(jsonFile, data.toString());
		} catch(Exception exception) {
			EdenCore.getInstance().logException("An error occurred while saving default control bindings", exception);
		}
	}
}
