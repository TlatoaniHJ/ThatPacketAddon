package us.tlatoani.thatpacketaddon.data_watcher;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.iterator.EmptyIterator;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.Iterator;

public class ExprWatchedIndexes extends SimpleExpression<Number> {
    private Expression<WrappedDataWatcher> dataWatcherExpression;

    @Override
    protected Number[] get(Event event) {
        WrappedDataWatcher dataWatcher = dataWatcherExpression.getSingle(event);
        if (dataWatcher == null) {
            return new Number[0];
        }
        return dataWatcher.getIndexes().toArray(new Number[0]);
    }

    @Override
    public Iterator iterator(Event event) {
        WrappedDataWatcher dataWatcher = dataWatcherExpression.getSingle(event);
        if (dataWatcher == null) {
            return new EmptyIterator<>();
        }
        return dataWatcher.getIndexes().iterator();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "watched indexes of " + dataWatcherExpression;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        dataWatcherExpression = (Expression<WrappedDataWatcher>) exprs[0];
        return true;
    }
}
