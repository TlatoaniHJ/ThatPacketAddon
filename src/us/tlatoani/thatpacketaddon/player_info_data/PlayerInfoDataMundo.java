package us.tlatoani.thatpacketaddon.player_info_data;

import ch.njol.skript.lang.ExpressionType;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.GameMode;
import org.json.simple.JSONObject;
import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;
import us.tlatoani.mundocore.registration.Registration;

public class PlayerInfoDataMundo {

    public static void load() {
        Registration.registerType(PlayerInfoData.class, "playerinfodata")
                .document("PlayerInfoData", "1.0", "A type describing a tab in the tablist, usually describing a player.");
        Registration.registerExpression(ExprNewPlayerInfoData.class, PlayerInfoData.class, ExpressionType.COMBINED,
                "playerinfodata with profile %gameprofile% display name %jsonobject% [latency %-number%] [[game[ ]]mode %gamemode%]")
                .document("New PlayerInfoData", "1.0",
                        "An expression for a new playerinfodata with the specified profile and display name, "
                        + "either the specified latency or 0 if the latency is not specified, "
                        + "and either the specified gamemode or the NOT_SET gamemode if the gamemode is not specified.");
        MundoPropertyExpression.registerPropertyExpression(ExprProfileOfPlayerInfoData.class, WrappedGameProfile.class,
                "playerinfodata", "[game] profile of [player]info[data] %", "[player]info[data] %'s [game] profile")
                .document("Game Profile of PlayerInfoData", "1.0",
                        "The profile contained in this playerinfodata. "
                        + "Determines the skin that will be displayed as an icon if this playerinfodata is used as a tab in the tablist.");
        MundoPropertyExpression.registerPropertyExpression(ExprLatencyOfPlayerInfoData.class, Number.class,
                "playerinfodata", "(ping|latency) of [player]info[data] %", "[player]info[data] %'s (ping|latency)")
                .document("Latency of PlayerInfoData", "1.0",
                        "The latency contained in this playerinfodata, respresented as an integer amount of milliseconds. "
                        + "Different values of latency will result in different latency bar icons being displayed "
                        + "if the specified playerinfodata is used as a tab in the tablist:"
                        + "\nlatency < 0 ms will show a red X"
                        + "\n0 ms <= latency < 150 ms will show 5 bars"
                        + "\n150 ms <= latency < 300 ms will show 4 bars"
                        + "\n300 ms <= latency < 600 ms will show 3 bars"
                        + "\n600 ms <= latency < 1000 ms = 1 second will show 2 bars"
                        + "\n1000 ms = 1 second <= latency will show 1 bar");
        MundoPropertyExpression.registerPropertyExpression(ExprGameModeOfPlayerInfoData.class, GameMode.class,
                "playerinfodata", "game[ ]mode of [player]info[data] %", "[player]info[data] %'s game[ ]mode")
                .document("Gamemode of PlayerInfoData", "1.0",
                        "An expression for the gamemode contained in the specified playerinfodata. "
                        + "Note that ProtocolLib's NativeGameMode class also has a NOT_SET mode, "
                        + "which will appear in Skript as the expression being not set.");
        MundoPropertyExpression.registerPropertyExpression(ExprDisplayNameOfPlayerInfoData.class, JSONObject.class,
                "playerinfodata","display name of [player]info[data] %", "[player]info[data] %'s display name")
                .document("Display Name of PlayerInfoData", "1.0",
                        "An expression for the display name contained in the specified playerinfodata. "
                        + "If the specified playerinfodata is used as a tab in the tablist, "
                        + "this expression determines what actual name shown on the tab. "
                        + "The name of the game profile contained in the specified playerinfodata is only used internally.");
    }
}
