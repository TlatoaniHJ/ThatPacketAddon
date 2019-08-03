package us.tlatoani.thatpacketaddon.json;

import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.variables.SerializedVariable;
import ch.njol.yggdrasil.Fields;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.tlatoani.mundocore.registration.Registration;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;

public class JSONMundo {

    public static void load() {
        Registration.registerType(JSONObject.class, "jsonobject")
                .document("JSONObject", "1.6.4", "A JSONObject, a type of data structure used for storing information in the form of "
                        + "keys/indexes and values (like a list variable). Useful in Skript for transmitting complex information that isn't represented by a type "
                        + "in cases where list variables can't be used (ex. returning values from expressions/functions (ex. the JSON Field of Packet expression)).")
                .parser(new Registration.SimpleParser<JSONObject>() {
                    @Override
                    public JSONObject parse(String s, ParseContext parseContext) {
                        try {
                            return (JSONObject) (new JSONParser()).parse(s);
                        } catch (ParseException | ClassCastException e) {
                            return null;
                        }
                    }
                }).serializer(new Serializer<JSONObject>() {
            @Override
            public Fields serialize(JSONObject jsonObject) {
                JSONObject toBecomeString = new JSONObject();
                jsonObject.forEach((o, o2) -> {
                    Object serializedValue = serializeJSONElement(o2);
                    toBecomeString.put(o, serializedValue);
                });
                Fields fields = new Fields();
                fields.putObject("value", toBecomeString.toJSONString());
                return fields;
            }

            @Override
            public void deserialize(JSONObject jsonObject, Fields fields) throws StreamCorruptedException {
                try {
                    JSONObject fromString = (JSONObject) (new JSONParser()).parse((String) fields.getObject("value"));
                    fromString.forEach((o, o2) -> {
                        jsonObject.put(o, deserializeJSONElement(o2));
                    });
                } catch (ParseException | ClassCastException | NullPointerException e) {
                    throw new StreamCorruptedException();
                }
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            public boolean canBeInstantiated(Class<? extends JSONObject> c) {
                return c == JSONObject.class;
            }

            protected boolean canBeInstantiated() {
                return true;
            }
        });
        Registration.registerEffect(EffPutJsonInListVariable.class,
                "put json %jsonobject% in listvar %objects%", "put jsons %jsonobjects% in listvar %objects%")
                .document("Put JSON in List Variable", "1.6.4",
                        "Puts all of the information stored inside the specified jsonobject into the specified list variable. "
                                + "This is needed as storing json data in list variables is currently the only way to "
                                + "manipulate information in ThatPacketAddon's jsonobjects other than raw string manipulation. "
                                + "");
        Registration.registerExpression(ExprListVariableAsJson.class, JSONObject.class, ExpressionType.PROPERTY,
                "json (of|from) (listvar|list variable) %objects%", "jsons (of|from) (listvar|list variable) %objects%")
                .document("JSON from List Variable", "1.6.4",
                        "An expression for a jsonobject constructed from the information stored inside the specified list variable. ");
    }

    public static Object serializeJSONElement(Object object) {
        if (object instanceof JSONArray) {
            JSONArray result = new JSONArray();
            for (Object elem : (JSONArray) object) {
                Object serializedElem = serializeJSONElement(elem);
                if (serializedElem != null) {
                    result.add(serializedElem);
                }
            }
            return result;
        }
        SerializedVariable.Value value = Classes.serialize(object);
        if (value == null) {
            return null;
        }
        JSONObject valueJSON = new JSONObject();
        valueJSON.put("type", value.type);
        valueJSON.put("data", new String(value.data));
        return valueJSON;
    }

    public static Object deserializeJSONElement(Object object) {
        if (object instanceof JSONArray) {
            JSONArray result = new JSONArray();
            for (Object serializedElem : (JSONArray) object) {
                Object deserializedElem = deserializeJSONElement(serializedElem);
                result.add(deserializedElem);
            }
            return result;
        }
        JSONObject jsonObject = (JSONObject) object;
        String type = (String) jsonObject.get("type");
        String dataString = (String) jsonObject.get("data");
        if (dataString == null) {
            dataString = (String) jsonObject.get("Data");
        }
        byte[] data = dataString.getBytes();
        return Classes.deserialize(type, data);
    }
}
