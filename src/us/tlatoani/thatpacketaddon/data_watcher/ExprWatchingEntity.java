package us.tlatoani.thatpacketaddon.data_watcher;

import ch.njol.skript.classes.Changer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.Entity;
import us.tlatoani.mundocore.property_expression.ChangeablePropertyExpression;

public class ExprWatchingEntity extends ChangeablePropertyExpression<WrappedDataWatcher, Entity> {
    @Override
    public void change(WrappedDataWatcher dataWatcher, Entity entity, Changer.ChangeMode changeMode) {
        dataWatcher.setEntity(entity);
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public Entity convert(WrappedDataWatcher dataWatcher) {
        return dataWatcher.getEntity();
    }
}
