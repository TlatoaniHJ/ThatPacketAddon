package us.tlatoani.thatpacketaddon.skin;

public class SkinConverter {

    public static Object toTablisknuSkin(Skin skin) {
        return new us.tlatoani.tablisknu.skin.Skin(skin.value, skin.signature);
    }

    public static Skin fromTablisknuSkin(Object skin) {
        us.tlatoani.tablisknu.skin.Skin tablisknuSkin = (us.tlatoani.tablisknu.skin.Skin) skin;
        return new Skin(tablisknuSkin.value, tablisknuSkin.signature);
    }
}
