package us.tlatoani.thatpacketaddon.server_ping;

import ch.njol.skript.classes.Changer;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import us.tlatoani.mundocore.property_expression.ChangeablePropertyExpression;

public class CondPlayersVisibleInServerPing extends ChangeablePropertyExpression<WrappedServerPing, Boolean> {
    @Override
    public void change(WrappedServerPing wrappedServerPing, Boolean bool, Changer.ChangeMode changeMode) {
        wrappedServerPing.setPlayersVisible(bool);
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public Boolean convert(WrappedServerPing wrappedServerPing) {
        return wrappedServerPing.isPlayersVisible();
    }
}
