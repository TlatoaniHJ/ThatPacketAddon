package us.tlatoani.thatpacketaddon.server_ping;

import ch.njol.skript.classes.Changer;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import us.tlatoani.mundocore.property_expression.ChangeablePropertyExpression;

public class ExprVersionNameOfServerPing extends ChangeablePropertyExpression<WrappedServerPing, String> {
    @Override
    public void change(WrappedServerPing wrappedServerPing, String s, Changer.ChangeMode changeMode) {
        wrappedServerPing.setVersionName(s);
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public String convert(WrappedServerPing wrappedServerPing) {
        return wrappedServerPing.getVersionName();
    }
}
