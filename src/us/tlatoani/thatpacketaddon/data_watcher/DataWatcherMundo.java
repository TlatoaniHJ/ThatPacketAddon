package us.tlatoani.thatpacketaddon.data_watcher;

import ch.njol.skript.lang.ExpressionType;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.Entity;
import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.thatpacketaddon.syntaxes.Converters;

public class DataWatcherMundo {

    public static void load() {
        Converters.registerWatchables();
        Registration.registerType(WrappedDataWatcher.class, "datawatcher");
        Registration.registerExpression(ExprNewDataWatcher.class, WrappedDataWatcher.class, ExpressionType.PROPERTY,
                "new datawatcher [for %-entity%]");
        MundoPropertyExpression.registerPropertyExpression(ExprWatchingEntity.class, Entity.class, "datawatcher",
                "watching entity");
        Registration.registerExpression(ExprWatchedValueOfDataWatcher.class, Object.class, ExpressionType.COMBINED,
                Converters.getWatchableNames().map(name -> "watched " + name + " %number% of %datawatcher%").toArray(String[]::new))
                .document("Watched Value of DataWatcher", "1.0", "desc coming soon");
        Registration.registerExpression(ExprWatchedIndexes.class, Number.class, ExpressionType.PROPERTY,
                "watched (indexes|indices) of %datawatcher%");
    }
}
