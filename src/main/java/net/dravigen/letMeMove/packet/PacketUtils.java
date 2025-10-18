package net.dravigen.letMeMove.packet;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import java.io.*;

public class PacketUtils {
    public static final String ANIMATION_SYNC_CHANNEL = "LMM:AnimationSyncPacket";
    public static final String HUNGER_EXHAUSTION_CHANNEL = "LMM:ExhaustionPacket";

    public static void animationStoCSync(ResourceLocation ID, NetServerHandler serverHandler) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeUTF(ID.getResourceDomain());
            dos.writeUTF(ID.getResourcePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(ANIMATION_SYNC_CHANNEL, bos.toByteArray());

        serverHandler.sendPacket(packet);
    }

    public static void animationCtoSSync(ResourceLocation ID) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeUTF(ID.getResourceDomain());
            dos.writeUTF(ID.getResourcePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(ANIMATION_SYNC_CHANNEL, bos.toByteArray());

        Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet);
    }

    public static void handleAnimationSync(Packet250CustomPayload packet, EntityPlayer player) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
            DataInputStream dis = new DataInputStream(bis);

            ResourceLocation ID = new ResourceLocation(dis.readUTF(), dis.readUTF());

            ((ICustomMovementEntity) player).llm_$setAnimation(ID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void sendExhaustionToServer(float exhaustion) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeFloat(exhaustion);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(HUNGER_EXHAUSTION_CHANNEL, bos.toByteArray());

        Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet);
    }

    public static void handleExhaustionFromClient(Packet250CustomPayload packet, EntityPlayer player) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
            DataInputStream dis = new DataInputStream(bis);

            float exhaustion = dis.readFloat();

            player.addExhaustion(exhaustion);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
