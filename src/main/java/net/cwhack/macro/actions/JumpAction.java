package net.cwhack.macro.actions;

import net.cwhack.macro.action.Action;
import net.cwhack.macro.exception.MacroException;
import net.cwhack.macro.exception.MacroSyntaxException;

import static net.cwhack.CwHack.MC;

public class JumpAction extends Action
{

	@Override
	public void init(String[] args) throws MacroException
	{
		if (args.length != 0)
			throw new MacroSyntaxException("argument number not matching");
	}

	@Override
	public void run()
	{
		MC.execute(() ->
		{
			if (MC.player != null && MC.player.isOnGround())
				MC.player.jump();
		});
	}
}
