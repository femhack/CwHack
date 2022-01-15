package net.cwhack.gui.screen;

import net.cwhack.feature.Feature;
import net.cwhack.feature.FeatureList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public class GuiScreen extends Screen
{

	private TextFieldWidget searchText;

	private final ArrayList<Feature> foundFeatures = new ArrayList<>();
	private final TreeMap<Feature, int[]> feature2pos = new TreeMap<>(Comparator.comparing(Feature::getName));

	public GuiScreen()
	{
		super(new LiteralText(""));
	}

	@Override
	public void init()
	{
		searchText = new TextFieldWidget(MC.textRenderer, width / 2 - 75, 25, 150, 20, new LiteralText(""));
		addSelectableChild(searchText);
		setInitialFocus(searchText);
		searchText.setTextFieldFocused(true);
		ButtonWidget configButton = new ButtonWidget(50, 25, 100, 20, new LiteralText("Configs..."), b -> MC.setScreen(new ConfigScreen(this)));
		addDrawableChild(configButton);
		ButtonWidget keybindButton = new ButtonWidget(width - 150, 25, 100, 20, new LiteralText("Keybinds..."), b -> MC.setScreen(new KeybindScreen(this)));
		addDrawableChild(keybindButton);
	}

	@Override
	public void tick()
	{
		searchText.tick();
		foundFeatures.clear();
		feature2pos.clear();
		FeatureList features = CWHACK.getFeatures();
		ArrayList<String> foundFeatureNames = new ArrayList<>();
		Set<String> featureNames = features.getAllFeatureNames();
		featureNames.forEach(e ->
		{
			if (e.startsWith(searchText.getText()))
				foundFeatureNames.add(e);
		});
		foundFeatureNames.sort(Comparator.naturalOrder());
		foundFeatureNames.forEach(e -> foundFeatures.add(features.getFeature(e)));
		final int endY = 8;
		var ref = new Object()
		{
			int x;
			int y = 75;
		};
		ref.x = width / 2 - 50 - foundFeatures.size() / ((height - ref.y + endY) / 16) * 50;
		foundFeatures.forEach(e ->
		{
			feature2pos.put(e, new int[]{ref.x, ref.y});
			ref.y += 16;
			if (ref.y + endY > height)
			{
				ref.x += 100;
				ref.y = 75;
			}
		});
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		renderBackground(matrices);

		super.render(matrices, mouseX, mouseY, delta);

		matrices.push();
		matrices.translate(0, 0, 300);

		searchText.render(matrices, mouseX, mouseY, delta);

		feature2pos.forEach((f, p) ->
		{
			int color = f.isEnabled() ? 0x00ff00 : isHoveringOver(mouseX, mouseY, p[0], p[1], p[0] + 100, p[1] + 8) ? 0xffffff : 0xa0a0a0;
			MC.textRenderer.draw(matrices, f.getName(), p[0], p[1], color);
		});

		matrices.pop();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		switch (button)
		{
			case 0 -> {
				var ref = new Object()
				{
					Feature isOnFeature = null;
				};
				feature2pos.forEach((f, p) ->
				{
					if (ref.isOnFeature != null)
						return;
					if (isHoveringOver((int) mouseX, (int) mouseY, p[0], p[1], p[0] + 100, p[1] + 8))
						ref.isOnFeature = f;
				});
				if (ref.isOnFeature != null)
					MC.setScreen(ref.isOnFeature.getSettingScreen());
				return true;
			}
			case 1 -> {
				var ref = new Object()
				{
					Feature isOnFeature = null;
				};
				feature2pos.forEach((f, p) ->
				{
					if (ref.isOnFeature != null)
						return;
					if (isHoveringOver((int) mouseX, (int) mouseY, p[0], p[1], p[0] + 100, p[1] + 8))
						ref.isOnFeature = f;
				});
				if (ref.isOnFeature != null)
					ref.isOnFeature.toggle();
				return true;
			}
			default -> {
				return super.mouseReleased(mouseX, mouseY, button);
			}
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers)
	{
		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	private boolean isHoveringOver(int mouseX, int mouseY, int x1, int y1, int x2, int y2)
	{
		return mouseX > Math.min(x1, x2) && mouseX < Math.max(x1, x2) && mouseY > Math.min(y1, y2) && mouseY < Math.max(y1, y2);
	}

	@Override
	public boolean shouldPause()
	{
		return false;
	}
}
