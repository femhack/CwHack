package net.cwhack.features;

import net.cwhack.events.PacketOutputListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.mixinterface.IClientPlayerInteractionManager;
import net.cwhack.setting.DecimalSetting;
import net.cwhack.setting.IntegerSetting;
import net.cwhack.utils.BlockUtils;
import net.cwhack.utils.InventoryUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static net.cwhack.CwHack.MC;

public class FastBreakFeature extends Feature implements PacketOutputListener, UpdateListener
{

	private final DecimalSetting range = new DecimalSetting("range", "how far can you reach the block", 4.5, this);
	private final IntegerSetting delay = new IntegerSetting("delay", "the delay for it to send stop digging packet", 0, this);

	private int delayClock = 0;
	private boolean letgo = false;

	private boolean mining = false;
	private BlockPos miningBlock = null;
	private Direction miningDirection = null;

	public FastBreakFeature()
	{
		super("FastBreak", "break fast");
	}

	@Override
	protected void onEnable()
	{
		delayClock = 0;
		eventManager.add(PacketOutputListener.class, this);
		eventManager.add(UpdateListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(PacketOutputListener.class, this);
		eventManager.remove(UpdateListener.class, this);
		cancelMining();
	}

	@Override
	public void onSendPacket(PacketOutputEvent event)
	{
		if (letgo)
			return;

		if (!(event.getPacket() instanceof PlayerActionC2SPacket packet))
			return;

		if (BlockUtils.getBlock(packet.getPos()) == Blocks.BEDROCK)
			return;

		if (packet.getAction() == Action.START_DESTROY_BLOCK)
		{
			event.cancel();

			int oldSlot = MC.player.getInventory().selectedSlot;
			InventoryUtils.selectItemFromHotbar(item -> item == Items.NETHERITE_PICKAXE);
			((IClientPlayerInteractionManager) MC.interactionManager).cwSyncSelectedSlot();

			letgo = true;
			MC.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, packet.getPos(), packet.getDirection()));
			letgo = false;
			MC.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, packet.getPos(), packet.getDirection()));

			MC.player.getInventory().selectedSlot = oldSlot;
			((IClientPlayerInteractionManager) MC.interactionManager).cwSyncSelectedSlot();

			mining = true;
			miningBlock = packet.getPos();
			miningDirection = packet.getDirection();
		}
	}

	public void cancelMining()
	{
		if (!mining)
			return;
		mining = false;
		MC.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, miningBlock, miningDirection));
		((IClientPlayerInteractionManager) MC.interactionManager).setCurrentBreakingPos(new BlockPos(-1, -1, -1));
		((IClientPlayerInteractionManager) MC.interactionManager).setBreakingBlock(false);
		delayClock = 0;
	}

	public boolean isMining()
	{
		return mining && isEnabled();
	}

	@Override
	public void onUpdate()
	{
		if (!mining)
			return;

		if (!BlockUtils.hasBlock(miningBlock))
		{
			cancelMining();
			return;
		}

		if (!BlockUtils.isBlockReachable(miningBlock, range.getValue()))
		{
			cancelMining();
			return;
		}

		if (MC.player.isUsingItem())
			return;

		if (delayClock > 0)
		{
			delayClock--;
			return;
		}
		delayClock = delay.getValue();

		int oldSlot = MC.player.getInventory().selectedSlot;
		InventoryUtils.selectItemFromHotbar(item -> item == Items.NETHERITE_PICKAXE);
		((IClientPlayerInteractionManager) MC.interactionManager).cwSyncSelectedSlot();

		MC.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, miningBlock, miningDirection));

		MC.player.getInventory().selectedSlot = oldSlot;
	}
}
