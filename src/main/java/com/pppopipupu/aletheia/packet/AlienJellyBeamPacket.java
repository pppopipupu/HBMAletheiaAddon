package com.pppopipupu.aletheia.packet;

import com.pppopipupu.aletheia.ClientProxy;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class AlienJellyBeamPacket implements IMessage {

    private int entityId;

    public AlienJellyBeamPacket() {}

    public AlienJellyBeamPacket(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
    }

    public static class Handler implements IMessageHandler<AlienJellyBeamPacket, IMessage> {

        @Override
        public IMessage onMessage(AlienJellyBeamPacket message, MessageContext ctx) {
            ClientProxy.addAlienJellyRay(message.entityId);
            return null;
        }
    }
}
