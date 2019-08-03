package us.tlatoani.thatpacketaddon.minecraft_key;

import ch.njol.skript.lang.ExpressionType;
import com.comphenix.protocol.wrappers.MinecraftKey;
import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;
import us.tlatoani.mundocore.registration.Registration;

public class MinecraftKeyMundo {

    public static void load() {
        Registration.registerType(MinecraftKey.class, "minecraftkey")
                .toString(key -> "minecraft key with prefix \"" + key.getPrefix() + "\" key \"" + key.getKey() + "\"")
                .document("Minecraft Key", "1.0",
                        "A type of key used by Minecraft in packets consisting of a prefix and a key string.");
        Registration.registerExpression(ExprNewMinecraftKey.class, MinecraftKey.class, ExpressionType.COMBINED,
                "minecraft key with [prefix %-string%] key %string%")
                .document("New Minecraft Key", "1.0",
                        "An expression for a new minecraft key with the specified key string "
                        + "and either the specified prefix or the prefix \"minecraft\" if no prefix is specified.");
        MundoPropertyExpression.registerPropertyExpression(ExprPrefixOfKey.class, String.class,
                "minecraftkey", "prefix of minecraft key %", "minecraft key %'s prefix")
                .document("Prefix of Minecraft Key", "1.0",
                        "An expression for the prefix of the specified minecraft key.");
        MundoPropertyExpression.registerPropertyExpression(ExprKeyOfKey.class, String.class,
                "minecraftkey", "key of minecraft key %", "minecraft key %'s key")
                .document("Key of Minecraft Key", "1.0",
                        "An expression for the key string of the specified minecraft key.");
    }
}
