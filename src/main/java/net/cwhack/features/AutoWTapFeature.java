package net.cwhack.features;

import net.cwhack.events.AttackEntityListener;
import net.cwhack.events.PostActionListener;
import net.cwhack.events.PreActionListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.EnumSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

import static net.cwhack.CwHack.MC;

public class AutoWTapFeature extends Feature implements AttackEntityListener, PreActionListener, PostActionListener
{


	private final EnumSetting<Mode> mode = new EnumSetting<>("mode", "the mode it uses", Mode.values(), Mode.NORMAL, this);

	public AutoWTapFeature()
	{
		super("AutoWTap", "Automatically sprint reset after each hit");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(AttackEntityListener.class, this);
		eventManager.add(PreActionListener.class, this);
		eventManager.add(PostActionListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(AttackEntityListener.class, this);
		eventManager.remove(PreActionListener.class, this);
		eventManager.remove(PostActionListener.class, this);
	}

	private boolean attacked;
	private boolean sprinting;

	@Override
	public void onAttackEntity(AttackEntityEvent event)
	{
		if (mode.getValue() == Mode.PACKET)
		{
			if (MC.player.isSprinting())
			{
				MC.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(MC.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
				MC.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(MC.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
			}
			return;
		}
		attacked = true;
		sprinting = MC.player.isSprinting();
	}

	@Override
	public void onPreAction()
	{
		if (mode.getValue() == Mode.PACKET)
			return;
		if (attacked && sprinting)
		{
			MC.player.setSprinting(false);
		}
	}

	@Override
	public void onPostAction()
	{
		if (mode.getValue() == Mode.PACKET)
			return;
		if (attacked && sprinting)
		{
			MC.player.setSprinting(true);
		}
		attacked = false;
	}

	private enum Mode
	{
		NORMAL,
		PACKET
	}
}
