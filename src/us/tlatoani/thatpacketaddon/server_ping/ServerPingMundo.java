package us.tlatoani.thatpacketaddon.server_ping;

import ch.njol.skript.lang.ExpressionType;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.json.simple.JSONObject;
import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;
import us.tlatoani.mundocore.registration.Registration;

public class ServerPingMundo {

    public static void load() {
        Registration.registerType(WrappedServerPing.class, "serverping")
                .document("Server Ping", "1.0",
                        "A type representing the information sent to a client when looking in the \"Play Multiplier\" menu. "
                        + "The associated packettype is status_server_server_info.");
        Registration.registerExpression(ExprNewServerPing.class, WrappedServerPing.class, ExpressionType.SIMPLE,
                "new server ping")
                .document("New Server Ping", "1.0",
                        "An expression for a new server ping.");
        MundoPropertyExpression.registerPropertyExpressionCondition(CondPlayersVisibleInServerPing.class, "serverping",
                "players [are] visible in [server] ping %")
                .document("Players are Visible in Server Ping", "1.0",
                        "Whether the player count and possibly a player preview will be displayed by the specified server ping. "
                        + "If this is false, instead of a player count and possibly a player preview, "
                        + "the specified server ping will display \"???\" where the player count should be.");
        MundoPropertyExpression.registerPropertyExpression(ExprMOTDOfServerPing.class, JSONObject.class, "serverping",
                "motd of [server] ping %", "[server] ping %'s motd")
                .document("MOTD of Server Ping", "1.0",
                        "An expression for the MOTD (message of the day) that the specified server ping will display.");
        MundoPropertyExpression.registerPropertyExpression(ExprPlayerCountOfServerPing.class, Number.class, "serverping",
                "players online (of|in) [server] ping %", "[server] ping %'s players online",
                "max players (of|in) [server] ping %", "[server] ping %'s max players")
                .document("Players Online or Max Players of Server Ping", "1.0",
                        "An expression for either the amount of players online or the player limit displayed by the specified server ping. "
                        + "You can pretty much set these to be whatever you want, they can even be negative.")
                .example("packet field aliases for status_server_server_info:"
                        , "\tpong [of %packet%] = server ping field 0 of %packet%"
                        , ""
                        , "on packet event status_server_server_info:"
                        , "\tset players online in ping pong to 777"
                        , "\tset max players in ping pong to -69420");
        MundoPropertyExpression.registerPropertyExpression(ExprProfilesOfServerPing.class, WrappedGameProfile.class, "serverping",
                "player preview of [server] ping %", "[server] ping %'s player preview")
                .document("Player Preview of Server Ping", "1.0",
                        "A list expression of game profiles whose names "
                        + "will be displayed by the specified server ping as a preview of the players on your server. "
                        + "As with other parts of the server ping, "
                        + "these profiles don't actually have to be in any way related to the players actually online on your server.");
        MundoPropertyExpression.registerPropertyExpression(ExprVersionNameOfServerPing.class, String.class, "serverping",
                "version [name] of [server] ping %", "[server] ping %'s version [name]")
                .document("Version Name of Server Ping", "1.0",
                        "An expression for the string that will be displayed by the specified server ping as the version of your server "
                        + "if the client is not compatible with your server. "
                        + "This version name is not actually used for checking version compatibility; "
                        + "instead, the Version Protocol of Server Ping expression is used.");
        MundoPropertyExpression.registerPropertyExpression(ExprVersionProtocolOfServerPing.class, Number.class, "serverping",
                "version protocol of [server] ping %", "[server] ping %'s version protocol")
                .document("Version Protocol of Server Ping", "1.0",
                        "An expression for the version protocol specified by the specified server ping. "
                        + "This is used to check whether the client is compatible with your server.");
    }
}
