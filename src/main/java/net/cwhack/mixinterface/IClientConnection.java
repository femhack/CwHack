package net.cwhack.mixinterface;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Packet;

public interface IClientConnection
{
	void receivePacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet);
}
