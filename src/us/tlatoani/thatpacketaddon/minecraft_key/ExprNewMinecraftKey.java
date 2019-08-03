package us.tlatoani.thatpacketaddon.minecraft_key;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.wrappers.MinecraftKey;
import org.bukkit.event.Event;

import java.util.Optional;

public class ExprNewMinecraftKey extends SimpleExpression<MinecraftKey> {
    private Optional<Expression<String>> prefixExpression;
    private Expression<String> keyExpression;

    @Override
    protected MinecraftKey[] get(Event event) {
        Optional<String> prefix = prefixExpression.map(expr -> expr.getSingle(event));
        String key = keyExpression.getSingle(event);
        if (key == null) {
            return new MinecraftKey[0];
        }
        return new MinecraftKey[]{prefix.map(p -> new MinecraftKey(p, key)).orElse(new MinecraftKey(key))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MinecraftKey> getReturnType() {
        return MinecraftKey.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "minecraft key with"
                + prefixExpression.map(expr -> " prefix " + expr).orElse("")
                + " key " + keyExpression;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        prefixExpression = Optional.ofNullable((Expression<String>) exprs[0]);
        keyExpression = (Expression<String>) exprs[1];
        return true;
    }
}
