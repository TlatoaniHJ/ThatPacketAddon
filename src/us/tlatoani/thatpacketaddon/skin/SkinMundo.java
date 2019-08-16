package us.tlatoani.thatpacketaddon.skin;

import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.yggdrasil.Fields;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.thatpacketaddon.ThatPacketAddon;

import java.io.StreamCorruptedException;
import java.util.UUID;

public class SkinMundo {

    public static void load() {
        Registration.registerType(Skin.class, "skin", "skintexture")
                .document("Skin Texture", "1.0",
                        "Represents a skin, possibly of a player. Write 'steve' or 'alex' for these respective skins. "
                                + "This is a duplicate of the skin type found in Tablisknu. If you are using Tablisknu, "
                                + "ThatPacketAddon will use that skin type instead of this one (this won't affect anything since they are identical).")
                .example("skin with name \"eyJ0aW1lc3RhbXAiOjE0NzQyMTc3NjkwMDAsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJJbnZlbnRpdmVHYW1lcyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE5MmI0NTY2ZjlhMjg2OTNlNGMyNGFiMTQxNzJjZDM0MjdiNzJiZGE4ZjM0ZDRhNjEwODM3YTQ3ZGEwZGUifX19\" signature \"pRQbSEnKkNmi0uW7r8H4xzoWS3E4tkWNbiwwRYgmvITr0xHWSKii69TcaYDoDBXGBwZ525Ex5z5lYe5Xg6zb7pyBPiTJj8J0QdKenQefVnm6Vi1SAR1uN131sRddgK2Gpb2z0ffsR9USDjJAPQtQwCqz0M7sHeXUJhuRxnbznpuZwGq+B34f1TqyVH8rcOSQW9zd+RY/MEUuIHxmSRZlfFIwYVtMCEmv4SbhjLNIooGp3z0CWqDhA7GlJcDFb64FlsJyxrAGnAsUwL2ocoikyIQceyj+TVyGIEuMIpdEifO6+NkCnV7v+zTmcutOfA7kHlj4d1e5ylwi3/3k4VKZhINyFRE8M8gnLgbVxNZ4mNtI3ZMWmtmBnl9dVujyo+5g+vceIj5Admq6TOE0hy7XoDVifLWyNwO/kSlXl34ZDq1MCVN9f1ryj4aN7BB8/Tb2M4sJf3YoGi0co0Hz/A4y14M5JriG21lngw/vi5Pg90GFz64ASssWDN9gwuf5xPLUHvADGo0Bue8KPZPyI0iuIi/3sZCQrMcdyVcur+facIObTQhMut71h8xFeU05yFkQUOKIQswaz2fpPb/cEypWoSCeQV8T0w0e3YKLi4RaWWvKS1MFJDHn7xMYaTk0OhALJoV5BxRD8vJeRi5jYf3DjEgt9+xB742HrbVRDlJuTp4=\"")
                .example("player's skin")
                .example("alex")
                .example("steve")
                .parser(new Registration.SimpleParser<Skin>() {
                    @Override
                    public Skin parse(String s, ParseContext parseContext) {
                        if (s.equalsIgnoreCase("STEVE")) {
                            return Skin.STEVE;
                        } else if (s.equalsIgnoreCase("ALEX")) {
                            return Skin.ALEX;
                        } else {
                            return null;
                        }
                    }
                }).serializer(new Serializer<Skin>() {
            @Override
            public Fields serialize(Skin skin) {
                Fields fields = new Fields();
                fields.putObject("value", skin.value);
                fields.putObject("signature", skin.signature);
                fields.putObject("uuid", skin.uuid.toString());
                return fields;
            }

            @Override
            public void deserialize(Skin skin, Fields fields) {
                throw new UnsupportedOperationException("Skin does not have a nullary constructor!");
            }

            @Override
            public Skin deserialize(Fields fields) throws StreamCorruptedException {
                try {
                    String value = (String) fields.getObject("value");
                    String signature = (String) fields.getObject("signature");
                    String uuid = fields.contains("uuid") ? (String) fields.getObject("uuid") : null;
                    Logging.debug(ThatPacketAddon.class, "value: " + value + ", signature: " + signature + ", uuid: " + uuid);
                    if (uuid == null) {
                        return new Skin(value, signature);
                    } else {
                        return new Skin(value, signature, UUID.fromString(uuid));
                    }
                } catch (StreamCorruptedException | ClassCastException e) {
                    try {
                        String value = (String) fields.getObject("value");
                        Logging.debug(ThatPacketAddon.class, "value: " + value);
                        Object parsedObject = new JSONParser().parse(value);
                        Logging.debug(ThatPacketAddon.class, "parsedobject: " + parsedObject);
                        JSONObject jsonObject;
                        if (parsedObject instanceof JSONObject) {
                            jsonObject = (JSONObject) parsedObject;
                        } else {
                            jsonObject = (JSONObject) ((JSONArray) parsedObject).get(0);
                        }
                        return Skin.fromJSON(jsonObject);
                    } catch (ParseException | ClassCastException e1) {
                        throw new StreamCorruptedException();
                    }
                }
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            public boolean canBeInstantiated(Class<? extends Skin> c) {
                return false;
            }

            protected boolean canBeInstantiated() {
                return false;
            }
        });
        Registration.registerExpression(ExprSkinWith.class, Skin.class, ExpressionType.PROPERTY,
                "skin [texture] (with|of) value %string% signature %string%")
                .document("Skin with Value", "1.0",
                        "An expression for a skin with the specified value and signature. "
                                + "This is a duplicate of the Skin with Value Expression in Tablisknu. If you are using Tablisknu, "
                                + "ThatPacketAddon will use that expression (along with Tablisknu's skin type) instead of this one. "
                                + "This will not change anything about usage as they are identical.");
    }
}
