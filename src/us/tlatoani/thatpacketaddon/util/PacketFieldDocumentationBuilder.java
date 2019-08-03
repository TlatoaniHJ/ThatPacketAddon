package us.tlatoani.thatpacketaddon.util;

import ch.njol.skript.classes.Changer;
import us.tlatoani.mundocore.registration.DocumentationBuilder;
import us.tlatoani.mundocore.registration.Registration;

public class PacketFieldDocumentationBuilder {
    private final String name;
    private final Class<?> type;

    public PacketFieldDocumentationBuilder(String name, Class<?> type) {
        this.name = name;
        this.type = type.isArray() ? type.getComponentType() : type;
    }

    public DocumentationBuilder.Expression document(String originVersion, String description) {
        return new DocumentationBuilder.Expression(
                "PacketField",
                new String[]{name.toLowerCase() + " [packet] field [%number%] [of %packet%]"},
                type,
                null)
                .requiredPlugins(Registration.getCurrentRequiredPlugins())
                .document(name + " Field of Packet", originVersion, description
                        + " The index defaults to 0 if unspecified and the packet expression defaults to event-packet if unspecified.")
                .changer(Changer.ChangeMode.SET, type, originVersion, "Sets the value of the specified field");
    }

    public DocumentationBuilder.Expression document(String originVersion) {
        return document(originVersion,"The value of the field numbered at the specified index of the specified packet's " + name.toLowerCase() + " fields.");
    }
}
