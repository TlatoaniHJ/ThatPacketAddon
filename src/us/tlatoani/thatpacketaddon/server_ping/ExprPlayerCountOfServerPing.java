package us.tlatoani.thatpacketaddon.server_ping;

import ch.njol.skript.classes.Changer;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import us.tlatoani.mundocore.property_expression.ChangeablePropertyExpression;

public class ExprPlayerCountOfServerPing extends ChangeablePropertyExpression<WrappedServerPing, Number> {
    @Override
    public void change(WrappedServerPing wrappedServerPing, Number number, Changer.ChangeMode changeMode) {
        if (property.contains("online")) {
            wrappedServerPing.setPlayersOnline(number.intValue());
        } else {
            wrappedServerPing.setPlayersMaximum(number.intValue());
        }
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public Number convert(WrappedServerPing wrappedServerPing) {
        if (property.contains("online")) {
            return wrappedServerPing.getPlayersOnline();
        } else {
            return wrappedServerPing.getPlayersMaximum();
        }
    }
}
