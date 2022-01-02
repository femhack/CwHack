package net.cwhack.macro.actions;

import net.cwhack.macro.action.Action;
import net.cwhack.macro.exception.MacroException;
import net.cwhack.macro.exception.MacroInvalidArgumentException;
import net.cwhack.macro.exception.MacroSyntaxException;
import net.cwhack.mixinterface.IKeyboard;
import org.lwjgl.glfw.GLFW;

import static net.cwhack.CwHack.MC;

public class PressKeyAction extends Action
{

	private int key;

	@Override
	public void init(String[] args) throws MacroException
	{
		if (args.length != 1)
			throw new MacroSyntaxException("argument number not matching");
		try
		{
			key = Integer.parseInt(args[0]);
		} catch (Exception e)
		{
			throw new MacroInvalidArgumentException("can't parse the value");
		}
	}

	@Override
	public void run()
	{
		MC.execute(this::runInner);
	}

	private void runInner()
	{
		IKeyboard iKeyboard = (IKeyboard) MC.keyboard;
		long window = MC.getWindow().getHandle();
		MC.keyboard.onKey(window, key, 0, GLFW.GLFW_PRESS, 0);
		MC.keyboard.onKey(window, key, 0, GLFW.GLFW_RELEASE, 0);
		iKeyboard.cwOnChar(window, key, 0);
	}

}
