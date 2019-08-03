package us.tlatoani.thatpacketaddon.server_ping;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprNewServerPing extends SimpleExpression<WrappedServerPing> {
    @Override
    protected WrappedServerPing[] get(Event e) {
        return new WrappedServerPing[]{new WrappedServerPing()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WrappedServerPing> getReturnType() {
        return WrappedServerPing.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "new server ping";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
