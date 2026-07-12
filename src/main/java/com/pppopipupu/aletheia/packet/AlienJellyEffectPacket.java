package com.pppopipupu.aletheia.packet;

import com.pppopipupu.aletheia.ClientProxy;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class AlienJellyEffectPacket implements IMessage {

    public AlienJellyEffectPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<AlienJellyEffectPacket, IMessage> {

        @Override
        public IMessage onMessage(AlienJellyEffectPacket message, MessageContext ctx) {
            ClientProxy.alienJellyRayTicks = 100;
            return null;
        }
    }
}