package us.tlatoani.thatpacketaddon.game_profile;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import us.tlatoani.mundocore.property_expression.EvolvingPropertyExpression;
import us.tlatoani.thatpacketaddon.skin.Skin;

public class ExprTablisknuSkinOfProfile extends EvolvingPropertyExpression<WrappedGameProfile, us.tlatoani.tablisknu.skin.Skin> {
    @Override
    public WrappedGameProfile reset(WrappedGameProfile gameProfile) {
        return new WrappedGameProfile(gameProfile.getUUID(), gameProfile.getName());
    }

    @Override
    public WrappedGameProfile set(WrappedGameProfile gameProfile, us.tlatoani.tablisknu.skin.Skin tablisknuSkin) {
        Skin skin = Skin.fromSkriptSkin(tablisknuSkin);
        if (skin == null) {
            return reset(gameProfile);
        } else {
            return skin.toGameProfile(gameProfile.getUUID(), gameProfile.getName());
        }
    }

    @Override
    public us.tlatoani.tablisknu.skin.Skin convert(WrappedGameProfile gameProfile) {
        return (us.tlatoani.tablisknu.skin.Skin) Skin.toSkriptSkin(Skin.fromGameProfile(gameProfile));
    }
}
