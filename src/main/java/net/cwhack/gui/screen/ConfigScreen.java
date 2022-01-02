package net.cwhack.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public class ConfigScreen extends Screen
{

	private final Screen prevScreen;
	private final File[] configList;
	private final ArrayList<ButtonWidget> loadButtons = new ArrayList<>();

	public ConfigScreen(Screen prevScreen)
	{
		super(new LiteralText(""));
		this.prevScreen = prevScreen;
		Path configDir = CWHACK.getCwHackDirectory().resolve("config");
		configList = new File(String.valueOf(configDir)).listFiles();
	}

	@Override
	protected void init()
	{
		ButtonWidget doneButton = new ButtonWidget(width / 2 - 100, height - 50, 200, 20, new LiteralText("Done"), b -> MC.setScreen(prevScreen));
		addDrawableChild(doneButton);
		ButtonWidget saveButton = new ButtonWidget(width / 2 - 100, height - 80, 200, 20, new LiteralText("Save config"), b -> MC.setScreen(new SaveConfigScreen(this)));
		addDrawableChild(saveButton);
		int y = 50;
		for (File f : configList)
		{
			ButtonWidget button = new ButtonWidget(width / 3 * 2, y, 100, 20, new LiteralText("Load"), new ButtonWidget.PressAction()
			{
				private final String path = f.getAbsolutePath();
				@Override
				public void onPress(ButtonWidget button)
				{
					CWHACK.getFeatures().loadFromFile(path);
				}
			});
			addDrawableChild(button);
			loadButtons.add(button);
			y += 32;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		renderBackground(matrices);

		super.render(matrices, mouseX, mouseY, delta);

		int x = width / 3;
		int y = 50;
		for (File file : configList)
		{
			MC.textRenderer.draw(matrices, file.getName(), x, y, 0xffffff);
			y += 32;
		}
	}
}
