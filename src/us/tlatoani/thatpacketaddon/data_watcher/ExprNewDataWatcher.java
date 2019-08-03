package us.tlatoani.thatpacketaddon.data_watcher;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.Optional;

public class ExprNewDataWatcher extends SimpleExpression<WrappedDataWatcher> {
    private Optional<Expression<Entity>> entityExpression;

    @Override
    protected WrappedDataWatcher[] get(Event e) {
        Optional<Entity> entityOptional = entityExpression.map(expr -> expr.getSingle(e));
        return new WrappedDataWatcher[]{
                entityOptional.map(WrappedDataWatcher::new).orElseGet(WrappedDataWatcher::new)
        };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WrappedDataWatcher> getReturnType() {
        return WrappedDataWatcher.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "new datawatcher" + entityExpression.map(expr -> " for " + expr).orElse("");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        entityExpression = Optional.ofNullable((Expression<Entity>) exprs[0]);
        return true;
    }
}
