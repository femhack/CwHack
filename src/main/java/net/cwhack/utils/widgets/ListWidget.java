package net.cwhack.utils.widgets;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Collections;
import java.util.List;

public abstract class ListWidget extends AbstractParentElement implements Drawable
{


	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{

	}

	@Override
	public List<? extends Element> children()
	{
		return Collections.emptyList();
	}


}