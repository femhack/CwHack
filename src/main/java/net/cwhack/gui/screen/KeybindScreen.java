package net.cwhack.gui.screen;

import net.cwhack.keybind.Keybind;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public class KeybindScreen extends Screen
{

	private final Screen prevScreen;
	private final ArrayList<ButtonWidget> editButtons = new ArrayList<>();

	public KeybindScreen(Screen prevScreen)
	{
		super(new LiteralText(""));
		this.prevScreen = prevScreen;
	}

	@Override
	protected void init()
	{
		ButtonWidget doneButton = new ButtonWidget(width / 2 - 100, height - 50, 200, 20, new LiteralText("Done"), b -> MC.setScreen(prevScreen));
		addDrawableChild(doneButton);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);

		int x = width / 3;
		int y = 50;
		ArrayList<Keybind> keybinds = CWHACK.getKeybindManager().getAllKeybinds();
		for (Keybind keybind : keybinds)
		{
			MC.textRenderer.draw(matrices, keybind.getName(), x, y, 0xffffff);
			y += 32;
		}
	}

}
