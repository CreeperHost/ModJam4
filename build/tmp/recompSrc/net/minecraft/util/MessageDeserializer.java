package net.minecraft.util;

import com.google.common.collect.BiMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageDeserializer extends ByteToMessageDecoder
{
    private static final Logger logger = LogManager.getLogger();
    private static final Marker field_150799_b = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.logMarkerPackets);
    private static final String __OBFID = "CL_00001252";

    protected void decode(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List p_decode_3_) throws IOException
    {
        if (p_decode_2_.readableBytes() != 0)
        {
            PacketBuffer packetbuffer = new PacketBuffer(p_decode_2_);
            int i = packetbuffer.readVarIntFromBuffer();
            Packet packet = Packet.generatePacket((BiMap)p_decode_1_.channel().attr(NetworkManager.attrKeyReceivable).get(), i);

            if (packet == null)
            {
                throw new IOException("Bad packet id " + i);
            }
            else
            {
                packet.readPacketData(packetbuffer);

                if (packetbuffer.readableBytes() > 0)
                {
                    throw new IOException("Packet was larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + i);
                }
                else
                {
                    p_decode_3_.add(packet);

                    if (logger.isDebugEnabled())
                    {
                        logger.debug(field_150799_b, " IN: [{}:{}] {}[{}]", new Object[] {p_decode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get(), Integer.valueOf(i), packet.getClass().getName(), packet.serialize()});
                    }
                }
            }
        }
    }
}