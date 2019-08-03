package us.tlatoani.thatpacketaddon.server_ping;

import ch.njol.skript.classes.Changer;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import us.tlatoani.mundocore.property_expression.ChangeablePropertyExpression;

public class ExprVersionProtocolOfServerPing extends ChangeablePropertyExpression<WrappedServerPing, Number> {
    @Override
    public void change(WrappedServerPing wrappedServerPing, Number number, Changer.ChangeMode changeMode) {
        wrappedServerPing.setVersionProtocol(number.intValue());
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public Number convert(WrappedServerPing wrappedServerPing) {
        return wrappedServerPing.getVersionProtocol();
    }
}
