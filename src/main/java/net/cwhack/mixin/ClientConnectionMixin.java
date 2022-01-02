package net.cwhack.mixin;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.cwhack.event.EventManager;
import net.cwhack.events.PacketInputListener.PacketInputEvent;
import net.cwhack.mixinterface.IClientConnection;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements IClientConnection
{

	@Inject(at = {@At(value = "INVOKE",
			target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;)V",
			ordinal = 0)},
			method = {
					"channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V"},
			cancellable = true)
	private void onChannelRead0(ChannelHandlerContext channelHandlerContext,
	                            Packet<?> packet, CallbackInfo ci)
	{
		PacketInputEvent event = new PacketInputEvent(channelHandlerContext, packet);
		EventManager.fire(event);

		if(event.isCancelled())
			ci.cancel();
	}

	@Shadow
	private Channel channel;

	@Shadow
	private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener)
	{

	}

	@Shadow
	private PacketListener packetListener;

	@Shadow
	public void disconnect(Text disconnectReason)
	{

	}

	@Shadow
	private int packetsReceivedCounter;

	@Override
	public void receivePacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet)
	{
		if (this.channel.isOpen()) {
			try {
				handlePacket(packet, this.packetListener);
			} catch (OffThreadException var4) {
			} catch (ClassCastException var5) {
				LogManager.getLogger().error("Received {} that couldn't be processed", packet.getClass(), var5);
				this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_packet"));
			}

			++this.packetsReceivedCounter;
		}
	}
}
