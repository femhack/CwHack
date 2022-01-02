package net.cwhack.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public class SaveConfigScreen extends Screen
{

	private final Screen prevScreen;
	private ButtonWidget doneButton;
	private TextFieldWidget textWidget;

	public SaveConfigScreen(Screen prevScreen)
	{
		super(new LiteralText(""));
		this.prevScreen = prevScreen;
	}

	@Override
	protected void init()
	{
		doneButton = new ButtonWidget(width / 2 - 100, height - 50, 200, 20, new LiteralText("Done"), b -> done());
		addDrawableChild(doneButton);
		textWidget = new TextFieldWidget(MC.textRenderer, width / 2 - 200, height / 3, 400, 20, new LiteralText(""));
		addSelectableChild(textWidget);
		setInitialFocus(textWidget);
		textWidget.setTextFieldFocused(true);
	}

	private void done()
	{
		CWHACK.getFeatures().saveAsFile(CWHACK.getConfigDirectory().resolve(textWidget.getText()).toString() + ".cw");
		MC.setScreen(prevScreen);
	}

	@Override
	public void tick()
	{
		textWidget.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, MC.textRenderer, "Save config as ...", width / 2, height / 3 - 20, 0xffffff);
	}
}
