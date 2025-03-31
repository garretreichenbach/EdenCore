package thederpgamer.edencore.data.misc;

import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.other.HashList;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import thederpgamer.edencore.EdenCore;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ControlBindingData {

	private static final HashList<StarMod, ControlBindingData> bindings = new HashList<>();
	private static final byte VERSION = 0;

	private String name;
	private String description;
	private StarMod mod;
	private int binding;

	private ControlBindingData(String name, String description, int binding, StarMod mod) {
		this.name = name;
		this.description = description;
		this.binding = binding;
		this.mod = mod;
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

	public int getBinding() {
		return binding;
	}

	public void setBinding(int binding) {
		this.binding = binding;
		save(mod);
	}

	private JSONObject serialize() {
		JSONObject data = new JSONObject();
		data.put("version", VERSION);
		data.put("mod", mod.getName());
		data.put("name", name);
		data.put("description", description);
		data.put("binding", binding);
		return data;
	}

	private void deserialize(JSONObject data) {
		byte version = (byte) data.getInt("version");
		name = data.getString("name");
		mod = StarLoader.getModFromName(data.getString("mod")).getRealMod();
		description = data.getString("description");
		binding = data.getInt("binding");
	}

	public static void registerBinding(StarMod mod, String name, String description, int defaultBinding) {
		File file = getBindingsFile(mod);
		if(file.exists()) {
			if(bindings.containsKey(mod)) {
				// Check if a binding with the same name already exists
				for(ControlBindingData bindingData : bindings.get(mod)) {
					if(bindingData.getName().equals(name)) {
						EdenCore.getInstance().logInfo("Control binding \"" + name + "\" already exists for mod \"" + mod.getName() + "\"");
						break; // Exit if it already exists
					}
				}
			} else {
				try {
					bindings.add(mod, new ControlBindingData(name, description, defaultBinding, mod));
					save(mod);
				} catch(Exception exception) {
					EdenCore.getInstance().logException("An error occurred while creating control bindings file", exception);
				}
			}
		} else {
			try {
				file.createNewFile();
				bindings.add(mod, new ControlBindingData(name, description, defaultBinding, mod));
				save(mod);
			} catch(Exception exception) {
				EdenCore.getInstance().logException("An error occurred while creating control bindings file", exception);
			}
		}
	}

	private static File getBindingsFile(StarMod mod) {
		return new File(mod.getSkeleton().getResourcesFolder() + "/control_bindings.json");
	}

	public static ArrayList<ControlBindingData> getModBindings(StarMod mod) {
		if(bindings.getList(mod).isEmpty()) load(mod);
		return new ArrayList<>(bindings.getList(mod));
	}

	public static void save(StarMod mod) {
		File file = getBindingsFile(mod);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch(Exception exception) {
				EdenCore.getInstance().logException("An error occurred while creating control bindings file", exception);
			}
		}
		try {
			JSONArray data = new JSONArray();
			for(ControlBindingData bindingData : bindings.getList(mod)) data.put(bindingData.serialize());
			FileWriter writer = new FileWriter(file);
			writer.write(data.toString());
			writer.flush();
			writer.close();
		} catch(Exception exception) {
			EdenCore.getInstance().logException("An error occurred while saving control bindings file", exception);
		}
	}

	public static void load(StarMod mod) {
		File file = getBindingsFile(mod);
		if(file.exists()) {
			try {
				JSONArray data = new JSONArray(FileUtils.readFileToString(file));
				for(int i = 0; i < data.length(); i++) {
					JSONObject bindingData = data.getJSONObject(i);
					bindings.add(mod, new ControlBindingData(bindingData));
				}
			} catch(Exception exception) {
				EdenCore.getInstance().logException("An error occurred while loading control bindings file", exception);
			}
		}
	}

	public static HashList<StarMod, ControlBindingData> getBindings() {
		return bindings;
	}

	public static ArrayList<ControlBindingData> getAllBindings() {
		ArrayList<ControlBindingData> allBindings = new ArrayList<>();
		for(StarMod mod : bindings.keySet()) allBindings.addAll(bindings.get(mod));
		return allBindings;
	}
}
