package net.cwhack.keybind;

import net.cwhack.CwHack;
import net.cwhack.events.KeyPressListener;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;

import static net.cwhack.CwHack.MC;

public class KeybindManager implements KeyPressListener
{

	private final HashMap<Integer, ArrayList<Keybind>> keybinds = new HashMap<>();

	public KeybindManager()
	{
		CwHack.CWHACK.getEventManager().add(KeyPressListener.class, this);
		addDefaultKeybinds();
	}

	public ArrayList<Keybind> getAllKeybinds()
	{
		ArrayList<Keybind> result = new ArrayList<>();
		keybinds.forEach((i, a) -> result.addAll(a));
		return result;
	}

	public void removeAll()
	{
		keybinds.clear();
		addDefaultKeybinds();
	}

	public void addKeybind(Keybind keybind)
	{
		int key = keybind.getKey();
		if (keybinds.containsKey(key))
			keybinds.get(key).add(keybind);
		else
		{
			keybinds.put(key, new ArrayList<>());
			addKeybind(keybind);
		}
	}

	public void removeKeybind(String name)
	{
		keybinds.forEach((key, value) ->
				value.removeIf(k ->
						k.getName().equals(name)
				)
		);
	}

	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		if (MC.currentScreen != null)
			return;
		if (!keybinds.containsKey(event.getKeyCode()))
			return;

		for (Keybind keybind : keybinds.get(event.getKeyCode()))
		{
			if (keybind.shouldActiveOnPress() && event.getAction() == GLFW.GLFW_PRESS)
				keybind.execute();
			if (keybind.shouldActiveOnRelease() && event.getAction() == GLFW.GLFW_RELEASE)
				keybind.execute();
		}
	}

	private void addDefaultKeybinds()
	{
		addKeybind(new Keybind("ctrl-gui", GLFW.GLFW_KEY_RIGHT_CONTROL, true, false, "gui"));
	}
}
