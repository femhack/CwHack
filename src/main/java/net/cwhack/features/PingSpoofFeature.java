package net.cwhack.features;

import net.cwhack.events.PacketInputListener;
import net.cwhack.events.PacketOutputListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.IntegerSetting;
import net.minecraft.network.Packet;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static net.cwhack.CwHack.MC;

public class PingSpoofFeature extends Feature implements PacketOutputListener, PacketInputListener
{

	private IntegerSetting ping = new IntegerSetting("ping", "the ping that will be added onto your current ping", 0);

	private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1000);

	public PingSpoofFeature()
	{
		super("PingSpoof", "delay all of your outgoing and incoming packets");
		addSetting(ping);
	}

	@Override
	public void onEnable()
	{
		eventManager.add(PacketOutputListener.class, this);
		eventManager.add(PacketInputListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(PacketOutputListener.class, this);
		eventManager.remove(PacketInputListener.class, this);
	}

	@Override
	public void onSendPacket(PacketOutputEvent event)
	{
		event.cancel();
		//new Thread(() -> sendPacket(event.getPacket())).start();
		scheduler.schedule(() -> MC.getNetworkHandler().getConnection().send(event.getPacket()), ping.getValue(), TimeUnit.MILLISECONDS);
	}

	private void sendPacket(Packet<?> packet)
	{
		try
		{
			Thread.sleep(ping.getValue());
		} catch (InterruptedException e)
		{
			throw new RuntimeException("");
		}

		//MC.getNetworkHandler().sendPacket(packet); // this will cause an infinite recursion
		MC.getNetworkHandler().getConnection().send(packet);
	}

	@Override
	public void onReceivePacket(PacketInputEvent event)
	{

	}
}
