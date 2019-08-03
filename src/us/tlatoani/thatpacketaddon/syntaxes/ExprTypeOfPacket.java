package us.tlatoani.thatpacketaddon.syntaxes;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;

/**
 * Created by Tlatoani on 10/15/17.
 */
public class ExprTypeOfPacket extends MundoPropertyExpression<PacketContainer, PacketType> {
    @Override
    public PacketType convert(PacketContainer packetContainer) {
        return packetContainer.getType();
    }
}
