package us.tlatoani.thatpacketaddon.minecraft_key;

import com.comphenix.protocol.wrappers.MinecraftKey;
import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;

public class ExprPrefixOfKey extends MundoPropertyExpression<MinecraftKey, String> {
    @Override
    public String convert(MinecraftKey minecraftKey) {
        return minecraftKey.getPrefix();
    }
}
