package us.tlatoani.thatpacketaddon.game_profile;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.ExpressionType;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;
import us.tlatoani.mundocore.registration.DocumentationBuilder;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.thatpacketaddon.skin.Skin;

public class GameProfileMundo {

    public static void load() {
        Registration.registerType(WrappedGameProfile.class, "gameprofile")
                .document("Game Profile", "1.0",
                        "A game profile is meant to represent the profile of a player, "
                        + "and contains a name, UUID, and skin.");
        Registration.registerExpression(ExprNewGameProfile.class, WrappedGameProfile.class, ExpressionType.COMBINED,
                "game profile with name %string% [uuid %-string%] [skin %-skin%]")
                .document("New Game Profile", "1.0",
                        "An expression for a game profile with the specified name and optionally the specified UUID and skin. "
                        + "If the UUID is not specified a random UUID will be created and used. "
                        + "If the skin is not specified the profile will not have a skin associated with it.");
        MundoPropertyExpression.registerPropertyExpression(ExprUUIDOfProfile.class, String.class, "gameprofile",
                "uuid of profile %", "profile %'s uuid")
                .document("UUID of Game Profile", "1.0",
                        "An expression for the UUID of the specified profile.")
                .changer(Changer.ChangeMode.SET, String.class, "1.0",
                        "Sets the specified profile to a new profile having the specified UUID but otherwise the same as the previous profile.")
                .changer(Changer.ChangeMode.RESET, "1.0",
                        "Sets the specified profile to a new profile having a random UUID but otherwise the same as the previous profile.");
        MundoPropertyExpression.registerPropertyExpression(ExprNameOfProfile.class, String.class, "gameprofile",
                "name of profile %", "profile %'s name")
                .document("Name of Game Profile", "1.0",
                        "An expression for the name of the specified profile.")
                .changer(Changer.ChangeMode.SET, String.class, "1.0",
                        "Sets the specified profile to a new profile having the specified name but otherwise the same as the previous profile.");
        DocumentationBuilder.Changeable builder;
        Class<?> skinClass;
        if (Skin.isTablisknuSkinUsed()) {
            skinClass = us.tlatoani.tablisknu.skin.Skin.class;
            builder = MundoPropertyExpression.registerPropertyExpression(ExprTablisknuSkinOfProfile.class, us.tlatoani.tablisknu.skin.Skin.class, "gameprofile", "skin of profile %", "profile %'s skin");
        } else {
            skinClass = Skin.class;
            builder = MundoPropertyExpression.registerPropertyExpression(ExprThatPacketAddonSkinOfProfile.class, Skin.class, "gameprofile", "skin of profile %", "profile %'s skin");
        }
        builder
                .document("Skin of Game Profile", "1.0",
                "An expression for the skin of the specified profile.")
                .changer(Changer.ChangeMode.SET, skinClass, "1.0", "Sets the specified profile to a new profile having the specified skin but otherwise the same as the previous profile.")
                .changer(Changer.ChangeMode.RESET, "1.0", "Sets the specified profile to a new profile not having a skin but otherwise the same as the previous profile.");
        MundoPropertyExpression.registerPropertyExpression(ExprProfileOfPlayer.class, WrappedGameProfile.class, "player", "game profile")
                .document("Game Profile of Player", "1.0",
                        "An expression for the game profile corresponding to the specified profile.");

    }
}
