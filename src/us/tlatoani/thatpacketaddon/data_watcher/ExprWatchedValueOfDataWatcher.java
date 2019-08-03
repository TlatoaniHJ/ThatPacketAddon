package us.tlatoani.thatpacketaddon.data_watcher;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import us.tlatoani.thatpacketaddon.syntaxes.Converters;
import us.tlatoani.thatpacketaddon.util.WatchableType;

public class ExprWatchedValueOfDataWatcher extends SimpleExpression<Object> {
    private WatchableType watchableType;
    private Expression<Number> indexExpression;
    private Expression<WrappedDataWatcher> dataWatcherExpression;

    @Override
    protected Object[] get(Event event) {
        Number index = indexExpression.getSingle(event);
        WrappedDataWatcher dataWatcher = dataWatcherExpression.getSingle(event);
        if (index == null || dataWatcher == null) {
            return new Object[0];
        }
        Object object = dataWatcher.getObject(index.intValue());
        return new Object[]{watchableType.get(object)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        return watchableType.type;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "watched " + watchableType.name + " " + indexExpression + " of " + dataWatcherExpression;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        watchableType = Converters.getWatchable(matchedPattern);
        indexExpression = (Expression<Number>) exprs[0];
        dataWatcherExpression = (Expression<WrappedDataWatcher>) exprs[1];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Number index = indexExpression.getSingle(event);
        WrappedDataWatcher dataWatcher = dataWatcherExpression.getSingle(event);
        if (index == null || dataWatcher == null) {
            return;
        }
        if (mode == Changer.ChangeMode.SET) {
            Object value = watchableType.set(delta[0]);
            dataWatcher.setObject(index.intValue(), watchableType.serializer, value);
        } else if (mode == Changer.ChangeMode.DELETE) {
            dataWatcher.remove(index.intValue());
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE) {
            return CollectionUtils.array(Object.class);
        }
        return null;
    }
}
