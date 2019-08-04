package us.tlatoani.thatpacketaddon.syntaxes;

import ch.njol.skript.util.Version;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.nbt.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.reflection.Reflection;
import us.tlatoani.thatpacketaddon.player_info_data.ExprGameModeOfPlayerInfoData;
import us.tlatoani.thatpacketaddon.syntaxes_legacy.ExprJSONObjectOfPacket;
import us.tlatoani.thatpacketaddon.syntaxes_legacy.ExprObjectOfPacket;
import us.tlatoani.thatpacketaddon.util.PacketFieldDocumentationBuilder;
import us.tlatoani.thatpacketaddon.util.PacketField;
import us.tlatoani.thatpacketaddon.util.WatchableType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Converters {

    private static final List<PacketField> fields = new ArrayList<>();

    public static PacketField getField(int ix) {
        return fields.get(ix);
    }

    public static Stream<String> getFieldNames() {
        return fields.stream().map(field -> field.name);
    }

    private static final List<WatchableType> watchables = new ArrayList<>();

    public static WatchableType getWatchable(int ix) {
        return watchables.get(ix);
    }

    public static Stream<String> getWatchableNames() {
        return watchables.stream().map(watchable -> watchable.name);
    }

    public static <T> PacketFieldDocumentationBuilder registerField(
            String name,
            Class<T> type,
            Function<PacketContainer, StructureModifier<T>> function
    ) {
        fields.add(PacketField.simple(name, type, function));
        return new PacketFieldDocumentationBuilder(name, type);
    }

    public static <F, T> PacketFieldDocumentationBuilder registerField(
            String name,
            Class<T> type,
            Function<PacketContainer, StructureModifier<F>> function,
            Function<? super F, ? extends T> get,
            Function<? super T, ? extends F> set
    ) {
        fields.add(PacketField.converted(name, type, function, get, set));
        return new PacketFieldDocumentationBuilder(name, type);
    }
    
    public static <T> void registerWatchable(
            String name,
            Class<T> type
    ) {
        registerWatchable(name, type, () -> Registry.get(type, false));
    }

    public static <T> void registerWatchable(
            String name,
            Class<T> type,
            Function<Boolean, WrappedDataWatcher.Serializer> serializerSupplier
    ) {
        WrappedDataWatcher.Serializer serializer = null;
        try {
            serializer = serializerSupplier.apply(false);
        } catch (RuntimeException ignored) {}
        watchables.add(WatchableType.simple(name, type, serializer));
    }



    public static <T> void registerWatchable(
            String name,
            Class<T> type,
            Supplier<WrappedDataWatcher.Serializer> serializerSupplier
    ) {
        WrappedDataWatcher.Serializer serializer = null;
        try {
            serializer = serializerSupplier.get();
        } catch (RuntimeException ignored) {}
        watchables.add(WatchableType.simple(name, type, serializer));
    }

    public static <F, T> void registerWatchable(
            String name,
            Class<T> type,
            Class<F> wrappedType,
            Function<? super F, ? extends T> get,
            Function<? super T, ? extends F> set
    ) {
        registerWatchable(name, type, wrappedType, () -> Registry.get(wrappedType, false), get, set);
    }

    public static <F, T> void registerWatchable(
            String name,
            Class<T> type,
            Class<F> wrappedType,
            Function<Boolean, WrappedDataWatcher.Serializer> serializerSupplier,
            Function<? super F, ? extends T> get,
            Function<? super T, ? extends F> set
    ) {
        WrappedDataWatcher.Serializer serializer = null;
        try {
            serializer = serializerSupplier.apply(false);
        } catch (RuntimeException ignored) {}
        watchables.add(WatchableType.converted(name, type, wrappedType, serializer, get, set));
    }
    
    public static <F, T> void registerWatchable(
            String name,
            Class<T> type,
            Class<F> wrappedType,
            Supplier<WrappedDataWatcher.Serializer> serializerSupplier,
            Function<? super F, ? extends T> get,
            Function<? super T, ? extends F> set
    ) {
        WrappedDataWatcher.Serializer serializer = null;
        try {
            serializer = serializerSupplier.get();
        } catch (RuntimeException ignored) {}
        watchables.add(WatchableType.converted(name, type, wrappedType, serializer, get, set));
    }

    public static <F, T> void registerOptionalWatchable(
            String name,
            Class<T> type,
            Class<F> wrappedType,
            Function<? super F, ? extends T> get,
            Function<? super T, ? extends F> set
    ) {
        registerOptionalWatchable(name, type, wrappedType, b -> Registry.get(wrappedType, b), get, set);
    }

    public static <F, T> void registerOptionalWatchable(
            String name,
            Class<T> type,
            Class<F> wrappedType,
            Function<Boolean, WrappedDataWatcher.Serializer> serializerSupplier,
            Function<? super F, ? extends T> get,
            Function<? super T, ? extends F> set
    ) {
        WrappedDataWatcher.Serializer serializer = null;
        try {
            serializer = serializerSupplier.apply(true);
        } catch (RuntimeException ignored) {}
        watchables.add(WatchableType.optional(name, type, wrappedType, serializer, get, set));
    }

    public static void registerFields() {
        registerField("Object", Object.class, PacketContainer::getModifier) // tested w/ client_tab_complete
                .document("1.0", "The value of the field numbered at the specified index of the specified packet's fields.");
        registerField("Boolean", Boolean.class, PacketContainer::getBooleans).document("1.0"); // tested w/ client_look
        registerField("String", String.class, PacketContainer::getStrings).document("1.0"); // tested w/ client_tab_complete
        registerField("String Array", String[].class, PacketContainer::getStringArrays).document("1.0"); // tested w/ sign editing

        //numbers
        registerField("Byte", Number.class, PacketContainer::getBytes, Function.identity(), Number::byteValue).document("1.0");
        registerField("Short", Number.class, PacketContainer::getShorts, Function.identity(), Number::shortValue).document("1.0");
        registerField("Int", Number.class, PacketContainer::getIntegers, Function.identity(), Number::intValue).document("1.0"); // tested w/ client_tab_complete
        registerField("Long", Number.class, PacketContainer::getLongs, Function.identity(), Number::longValue).document("1.0");
        registerField("Float", Number.class, PacketContainer::getFloat, Function.identity(), Number::floatValue).document("1.0"); // tested w/ client_look
        registerField("Double", Number.class, PacketContainer::getDoubles, Function.identity(), Number::doubleValue).document("1.0");
        registerField("Byte Array", Number[].class, PacketContainer::getByteArrays, // tested w/ encryption_begin
                bytes -> {
                    Number[] result = new Number[bytes.length];
                    for (int i = 0; i < bytes.length; i++) {
                        result[i] = bytes[i];
                    }
                    return result;
                },
                numbers -> {
                    byte[] result = new byte[numbers.length];
                    for (int i = 0; i < numbers.length; i++) {
                        result[i] = numbers[i].byteValue();
                    }
                    return result;
                }
        ).document("1.0");
        registerField("Int Array", Number[].class, PacketContainer::getIntegerArrays,
                ints -> {
                    Number[] result = new Number[ints.length];
                    for (int i = 0; i < ints.length; i++) {
                        result[i] = ints[i];
                    }
                    return result;
                },
                numbers -> {
                    int[] result = new int[numbers.length];
                    for (int i = 0; i < numbers.length; i++) {
                        result[i] = numbers[i].intValue();
                    }
                    return result;
                }
        ).document("1.0");

        //item
        registerField("Itemstack", ItemStack.class, PacketContainer::getItemModifier).document("1.0"); // tested w/ set_creative_slot
        registerField("Itemstack Array", ItemStack[].class, PacketContainer::getItemArrayModifier).document("1.0"); // tested w/ window_items (1.10.2)
        registerField("Itemstack List", ItemStack[].class, PacketContainer::getItemListModifier, // tested w/ window_items
                list -> list.toArray(new ItemStack[0]),
                Arrays::asList
        ).document("1.0");
        registerField("Block", ItemStack.class, PacketContainer::getBlocks, ItemStack::new, ItemStack::getType) // tested w/ block_action
                .document("1.0",
                        "The value of the field numbered at the specified index of the specified packet's block fields. "
                        + "Note that this is represented in Skript as an itemstack with the material of the block.");
        registerField("Blockdata", ItemStack.class, PacketContainer::getBlockData, // tested w/ block_change
                Converters::fromWrappedBlockData,
                Converters::toWrappedBlockData
        ).document("1.0",
                "The value of the field numbered at the specified index of the specified packet's blockdata fields. "
                + "Note that this is represented in Skript as an itemstack with the material and data of the blockdata.");
        //Thanks to ashcr0w for help with the following converter
        try {
            Class nmsItemClass = Reflection.getMinecraftClass("Item");
            Reflection.MethodInvoker asNMSCopy = Reflection.getTypedMethod(
                    Reflection.getCraftBukkitClass("inventory.CraftItemStack"),
                    "asNMSCopy",
                    Reflection.getMinecraftClass("ItemStack"),
                    ItemStack.class
            );
            Reflection.MethodInvoker getNMSItem = Reflection.getTypedMethod(
                    Reflection.getMinecraftClass("ItemStack"),
                    "getItem",
                    nmsItemClass
            );
            Reflection.MethodInvoker asNewCraftStack = Reflection.getTypedMethod(
                    Reflection.getCraftBukkitClass("inventory.CraftItemStack"),
                    "asNewCraftStack",
                    Reflection.getCraftBukkitClass("inventory.CraftItemStack"),
                    nmsItemClass
            );
            EquivalentConverter<ItemStack> itemConvert = new EquivalentConverter<ItemStack>() {

                @Override
                public ItemStack getSpecific(Object o) {
                    return (ItemStack) asNewCraftStack.invoke(null, o);
                }

                public Object getGeneric(ItemStack itemStack) {
                    return getNMSItem.invoke(asNMSCopy.invoke(null, itemStack));
                }

                public Object getGeneric(Class<?> aClass, ItemStack itemStack) {
                    return getGeneric(itemStack);
                }

                @Override
                public Class<ItemStack> getSpecificType() {
                    return ItemStack.class;
                }
            };
            registerField("Item", ItemStack.class, // tested w/ set_cooldown
                    packet -> packet.getModifier().withType(nmsItemClass, itemConvert)).document("1.0");
        } catch (Exception e) {
            Logging.reportException(ExprObjectOfPacket.class, e);
        }

        //location
        registerField("Location", Location.class, // tested w/ block_action
                PacketContainer::getBlockPositionModifier,
                Converters::fromBlockPosition,
                Converters::toBlockPosition
        ).document("1.0",
                "The value of the field numbered at the specified index of the specified packet's location fields. "
                + "Note that Minecraft's BlockPosition class does not have information about the world, "
                + "so the returned location will be in your server's main world. "
                + "Presumably (if you have more than one world) you will want to set this to be the appropriate world given the context.");
        registerField("Location Collection", Location[].class, // tested w/ server_explosion
                PacketContainer::getBlockPositionCollectionModifier,
                collection -> collection.stream().map(Converters::fromBlockPosition).toArray(Location[]::new),
                array -> Arrays.stream(array).map(Converters::toBlockPosition).collect(Collectors.toList())
        ).document("1.0",
                "The value of the field numbered at the specified index of the specified packet's location collection fields. "
                        + "Note that Minecraft's BlockPosition class does not have information about the world, "
                        + "so the returned locations will be in your server's main world. "
                        + "Presumably (if you have more than one world) you will want to set them to be the appropriate world given the context.");

        //other
        registerField("UUID", String.class, PacketContainer::getUUIDs, UUID::toString, UUID::fromString) // tested w/ named_entity_spawn
                .document("1.0",
                "The value of the field numbered at the specified index of the specified packet's UUID fields. "
                        + "Note that UUIDs will be returned as a string with dashes, and strings used for setting should also have dashes.");
        registerField("Worldtype", WorldType.class, PacketContainer::getWorldTypeModifier).document("1.0"); // tested w/ respawn
        registerField("Minecraft Key", MinecraftKey.class, PacketContainer::getMinecraftKeys).document("1.0"); // tested w/ select_advancement_tab

        registerField("Collection", Object[].class, // tested w/ scoreboard_team
                packet -> packet.getSpecificModifier(Collection.class),
                Collection::toArray,
                Arrays::asList
        ).document("1.0");

        Reflection.ConstructorInvoker packetDataSerializerConstructor = Reflection.getConstructor(
                Reflection.getMinecraftClass("PacketDataSerializer"), ByteBuf.class);
        registerField("Byte Buffer", Number[].class, // tested w/ custom_payload (server and client)
                packet -> packet.getSpecificModifier(ByteBuf.class),
                byteBuf -> {
                    Number[] result = new Number[byteBuf.writerIndex()];
                    for (int i = 0; i < byteBuf.writerIndex(); i++) {
                        result[i] = byteBuf.getByte(i);
                    }
                    return result;
                },
                numbers -> {
                    byte[] bytes = new byte[numbers.length];
                    for (int i = 0; i < numbers.length; i++) {
                        bytes[i] = numbers[i].byteValue();
                    }
                    ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
                    return  (ByteBuf) packetDataSerializerConstructor.invoke(byteBuf);
                }
        ).document("1.0");
        registerField("Gamemode", GameMode.class, PacketContainer::getGameModes, // tested w/ respawn
                EnumWrappers.NativeGameMode::toBukkit,
                ExprGameModeOfPlayerInfoData::toNative
        ).document("1.0",
                "The value of the field numbered at the specified index of the specified packet's gamemode fields. "
                        + "Note that ProtocolLib's NativeGameMode class also has a NOT_SET mode, which will appear as the field being not set. "
                        + "If you want to deal more directly with the NOT_SET mode, you can use the NativeGameMode Field of Packet expression.");
        registerField("Difficulty", Difficulty.class, PacketContainer::getDifficulties, // tested w/ respawn
                Converters::fromWrapperDifficulty,
                Converters::toWrapperDifficulty
        ).document("1.0");
        registerField("Profile", WrappedGameProfile.class, PacketContainer::getGameProfiles).document("1.0"); // tested w/ start (login_client)
        registerField("PlayerInfoData List", PlayerInfoData[].class, // tested w/ player_info
                PacketContainer::getPlayerInfoDataLists,
                list -> list.toArray(new PlayerInfoData[0]),
                Arrays::asList
        ).document("1.0");
        registerField("Server Ping", WrappedServerPing.class, PacketContainer::getServerPings).document("1.0"); // tested w/ server_info
        registerField("Vector", Vector.class, PacketContainer::getVectors).document("1.0",
                "The value of the field numbered at the specified index of the specified packet's playerinfodata list fields. "
                        + "Note that you need a version of Skript that has vectors in order to use this packet field."); // tested w/ use_entity
        registerField("Dimension ID", Number.class, // tested w/ login
                PacketContainer::getDimensions,
                Function.identity(),
                Number::intValue)
                .document("1.0",
                "The value of the field numbered at the specified index of the specified packet's dimension id fields. "
                        + "0 represents the overworld, -1 represents the nether, and 1 represents the end.");
        registerField("Chat Component", JSONObject.class, // tested w/ chat
                PacketContainer::getChatComponents,
                Converters::fromWrappedChatComponent,
                Converters::toWrappedChatComponent
        ).document("1.0",
                "The value of the field numbered at the specified index of the specified packet's chat component fields. "
                        + "The chat component is represented as a jsonobject.");
        registerField("Chat Component Array", JSONObject[].class, // tested w/ update_sign (1.8)
                PacketContainer::getChatComponentArrays,
                array -> Arrays.stream(array).map(Converters::fromWrappedChatComponent).toArray(JSONObject[]::new),
                array -> Arrays.stream(array).map(Converters::toWrappedChatComponent).toArray(WrappedChatComponent[]::new)
        ).document("1.0",
                "The value of the field numbered at the specified index of the specified packet's chat component array fields. "
                        + "The chat components are represented as a jsonobject.");
        registerField("NBT", JSONObject.class, PacketContainer::getNbtModifier, // tested w/ tile_entity_data
                Converters::fromNBTBase,
                Converters::toNBTBase
        ).document("1.0",
                "The value of the field numbered at the specified index of the specified packet's NBT fields. "
                        + "The NBT, as well as any NBT values contained in it, is represented as a jsonobject with three parts:"
                        + "\nThe name, with index/key \"name\", which is just the name (sometimes null)"
                        + "\nThe type, with index/key \"type\", which is the type of NBT, and can have the following values:"
                        + "\n- \"byte\", \"short\", \"int\", \"long\", \"float\", \"double\", the basic number types"
                        + "\n- \"string\", for strings"
                        + "\n- \"byte_array\" and \"int_array\", for basic number array types"
                        + "\n- \"compound\", for an NBT compound"
                        + "\n- \"list_something\", such as \"list_string\" or \"list_compound\", for a list of some other type"
                        + "\nThe value, with index/key \"value\", which is the actual value:"
                        + "\n- In the cases of the number types and string type this is just the number/string "
                        + "(when accessing using a list variable in Skript it's like {_nbt::value}"
                        + "\n- In the case of the number array types and the list type this is an array of the objects "
                        + "(when accessing using a list variable in Skript it's like {_nbt::value::*}, ex. {_nbt::value::1} for the first element)"
                        + "\n- In the case of the compound it's a JSONObject of the NBT values, "
                        + "each also having its own name, type and value "
                        + "(when accessing through a list variable in Skript it's: {_nbt::value::example_name::name} for the name "
                        + "(which is the same as the index, so it will be \"example_name\"), "
                        + "{_nbt::value::example_name::type} for the type, let's say \"string\", "
                        + "and {_nbt::value::example_name::value} for the actual value, let's say \"pie\")");
        registerField("NBT List", JSONObject[].class, PacketContainer::getListNbtModifier, // tested w/ map_chunk (chonky)
                list -> {
                    JSONObject[] array = new JSONObject[list.size()];
                    for (int i = 0; i < array.length; i++) {
                        array[i] = fromNBTBase(list.get(i));
                    }
                    return array;
                },//list.stream().map(Converters::fromNBTBase).toArray(JSONObject[]::new)
                array -> {
                    List<NbtBase<?>> list = new ArrayList<>(array.length);
                    for (JSONObject jsonObject : array) {
                        list.add(toNBTBase(jsonObject));
                    }
                    return list;
                }
                //array -> Arrays.stream(array).map(Converters::toNBTBase).collect(Collectors.toList())
        ).document("1.0",
                "The value of the field numbered at the specified index of the specified packet's NBT list fields. "
                        + "The NBT values contained int list, as well as any NBT values contained in those NBT values, "
                        + "are represented as a jsonobject with three parts:"
                        + "\nThe name, with index/key \"name\", which is just the name (sometimes null)"
                        + "\nThe type, with index/key \"type\", which is the type of NBT, and can have the following values:"
                        + "\n- \"byte\", \"short\", \"int\", \"long\", \"float\", \"double\", the basic number types"
                        + "\n- \"string\", for strings"
                        + "\n- \"byte_array\" and \"int_array\", for basic number array types"
                        + "\n- \"compound\", for an NBT compound"
                        + "\n- \"list_something\", such as \"list_string\" or \"list_compound\", for a list of some other type"
                        + "\nThe value, with index/key \"value\", which is the actual value:"
                        + "\n- In the cases of the number types and string type this is just the number/string "
                        + "(when accessing using a list variable in Skript it's like {_nbt::value}"
                        + "\n- In the case of the number array types and the list type this is an array of the objects "
                        + "(when accessing using a list variable in Skript it's like {_nbt::value::*}, ex. {_nbt::value::1} for the first element)"
                        + "\n- In the case of the compound it's a JSONObject of the NBT values, "
                        + "each also having its own name, type and value "
                        + "(when accessing through a list variable in Skript it's: {_nbt::value::example_name::name} for the name "
                        + "(which is the same as the index, so it will be \"example_name\"), "
                        + "{_nbt::value::example_name::type} for the type, let's say \"string\", "
                        + "and {_nbt::value::example_name::value} for the actual value, let's say \"pie\")");
        registerField("Data Watcher", WrappedDataWatcher.class, PacketContainer::getDataWatcherModifier);
        registerField("Watchable Collection", WrappedDataWatcher.class,
                PacketContainer::getWatchableCollectionModifier,
                WrappedDataWatcher::new,
                WrappedDataWatcher::getWatchableObjects
        );

        //protocollib enums
        // tested w/ border alias pieish stuff
        Method[] packetMethods = PacketContainer.class.getMethods();
        for (Method method : packetMethods) {
            if (method.getReturnType() != StructureModifier.class) {
                continue;
            }
            try {
                ParameterizedType returnType = (ParameterizedType) method.getGenericReturnType();
                Class param = (Class) returnType.getActualTypeArguments()[0];
                if (param.isEnum() && param.getName().startsWith("com.comphenix.protocol.wrappers.EnumWrappers")) {
                    String name = param.getSimpleName();
                    if (name.equals("Difficulty")) {
                        continue;
                    }
                    registerField(name, String.class, packet -> {
                        try {
                            return (StructureModifier) method.invoke(packet);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            Logging.reportException(Converters.class, e);
                            return null;
                        }
                    }, Object::toString, str -> Enum.valueOf(param, str.toUpperCase()))
                            .document("1.0",
                                    "The value of the field numbered at the specified index of the specified packet's " + name.toLowerCase() + " fields. "
                                            + "This is an automatically generated enum packet field, "
                                            + "and is represented in Skript as a string (the name of the value of the field). "
                                            + "The returned name will always be in uppercase, "
                                            + "however when setting this expression the cases used do not matter."
                                            + "The different values of this enum are "
                                            + Arrays.stream(param.getEnumConstants()).map(e -> '"' + ((Enum) e).name() + '"').collect(Collectors.joining(", "))
                                            + ".");
                }
            } catch (ClassCastException e) {
                Logging.debug(Converters.class, e);
            }
        }
    }

    public static void registerWatchables() {
        Version minecraftVersion =
                new Version(Bukkit.getVersion().substring(
                        Bukkit.getVersion().indexOf("MC") + 3, Bukkit.getVersion().indexOf(')')));
        registerWatchable("object", Object.class, () -> null);
        registerWatchable("boolean", Boolean.class); // tested w/ named_entity_spawn
        registerWatchable("string", String.class);

        //numbers
        registerWatchable("byte", Number.class, Byte.class, Function.identity(), Number::byteValue);
        registerWatchable("int", Number.class, Integer.class, Function.identity(), Number::intValue);
        registerWatchable("float", Number.class, Float.class, Function.identity(), Number::floatValue);

        //item
        if (!minecraftVersion.isSmallerThan(new Version(1, 11))) {
            registerWatchable("itemstack", ItemStack.class, Registry::getItemStackSerializer);
        }
        registerOptionalWatchable("blockdata optional", ItemStack.class, WrappedBlockData.class,
                Registry::getBlockDataSerializer,
                Converters::fromWrappedBlockData,
                Converters::toWrappedBlockData
        );

        //location
        registerWatchable("location", Location.class, BlockPosition.class,
                () -> Registry.getBlockPositionSerializer(false),
                Converters::fromBlockPosition,
                Converters::toBlockPosition
        );
        registerOptionalWatchable("location optional", Location.class, BlockPosition.class,
                Registry::getBlockPositionSerializer,
                Converters::fromBlockPosition,
                Converters::toBlockPosition
        );

        //other
        registerOptionalWatchable("uuid optional", String.class, UUID.class, UUID::toString, UUID::fromString);
        registerWatchable("vector3f", Vector.class, Vector3F.class,
                Registry::getVectorSerializer,
                vector3F -> {
                    Vector vector = new Vector();
                    vector.setX(vector3F.getX());
                    vector.setY(vector3F.getY());
                    vector.setZ(vector3F.getZ());
                    return vector;
                },
                vector -> {
                    Vector3F vector3F = new Vector3F();
                    vector3F.setX((float) vector.getX());
                    vector3F.setY((float) vector.getY());
                    vector3F.setZ((float) vector.getZ());
                    return vector3F;
                }
        );
        registerWatchable("chatcomponent", JSONObject.class, WrappedChatComponent.class,
                () -> Registry.getChatComponentSerializer(false),
                Converters::fromWrappedChatComponent,
                Converters::toWrappedChatComponent
        );
        try {
            registerWatchable("nbt json", JSONObject.class, NbtBase.class,
                    Registry::getNBTCompoundSerializer,
                    Converters::fromNBTBase,
                    Converters::toNBTBase
            );
        } catch (IllegalArgumentException ignored) {}

        //enums
        registerWatchable("direction", String.class, EnumWrappers.Direction.class,
                Registry::getDirectionSerializer,
                Enum::name,
                EnumWrappers.Direction::valueOf
        );
    }

    public static Difficulty fromWrapperDifficulty(EnumWrappers.Difficulty difficulty) {
        return Difficulty.valueOf(difficulty.name());
    }

    public static EnumWrappers.Difficulty toWrapperDifficulty(Difficulty difficulty) {
        return EnumWrappers.Difficulty.valueOf(difficulty.name());
    }

    public static ItemStack fromWrappedBlockData(WrappedBlockData blockData) {
        ItemStack itemStack = new ItemStack(blockData.getType());
        itemStack.setData(new MaterialData(blockData.getType(), new Integer(blockData.getData()).byteValue()));
        return itemStack;
    }

    public static WrappedBlockData toWrappedBlockData(ItemStack itemStack) {
        return WrappedBlockData.createData(itemStack.getType(), itemStack.getData().getData());
    }

    public static Location fromBlockPosition(BlockPosition pos) {
        return pos.toLocation(Bukkit.getWorlds().get(0));
    }

    public static BlockPosition toBlockPosition(Location loc) {
        return new BlockPosition(loc.toVector());
    }

    public static JSONObject fromWrappedChatComponent(WrappedChatComponent chatComponent) {
        String fromJson = chatComponent.getJson();
        Logging.debug(ExprJSONObjectOfPacket.class,"FromJson: " + fromJson);
        JSONParser parser = new JSONParser();
        JSONObject toJson = null;
        try {
            Object parsedJson = parser.parse(fromJson);
            if (parsedJson instanceof JSONObject) {
                toJson = (JSONObject) parsedJson;
            } else if (parsedJson instanceof String) {
                toJson = new JSONObject();
                toJson.put("text", parsedJson);
            } else {
                throw new IllegalStateException("The json: " + fromJson + "; is neither a jsonobject nor a string");
            }
        } catch (ParseException | IllegalStateException e) {
            Logging.debug(ExprJSONObjectOfPacket.class, e);
        }
        return toJson;
    }

    public static WrappedChatComponent toWrappedChatComponent(JSONObject jsonObject) {
        return WrappedChatComponent.fromJson(jsonObject.toJSONString());
    }

    public static JSONObject fromNBTBase(NbtBase nbtBase) {
        if (nbtBase == null) {
            return null;
        }
        JSONObject result = new JSONObject();
        result.put("name", nbtBase.getName());
        if (nbtBase.getType() == NbtType.TAG_LIST) {
            result.put("type", "list_" + ((NbtList) nbtBase).getElementType().toString().substring(4).toLowerCase());
        } else {
            result.put("type", nbtBase.getType().toString().substring(4).toLowerCase());
        }
        switch (nbtBase.getType()) {
            case TAG_BYTE:
            case TAG_SHORT:
            case TAG_INT:
            case TAG_LONG:
            case TAG_FLOAT:
            case TAG_DOUBLE:
            case TAG_STRING:
                result.put("value", nbtBase.getValue());
                return result;
            case TAG_BYTE_ARRAY:
                JSONArray jsonByteArray = new JSONArray();
                for (byte b : (byte[]) nbtBase.getValue()) {
                    jsonByteArray.add(b);
                }
                result.put("value", jsonByteArray);
                return result;
            case TAG_INT_ARRAY:
                JSONArray jsonIntArray = new JSONArray();
                for (int i : (int[]) nbtBase.getValue()) {
                    jsonIntArray.add(i);
                }
                result.put("value", jsonIntArray);
                return result;
            case TAG_LIST:
                JSONArray jsonArray = new JSONArray();
                for (Object elem : (NbtList) nbtBase) {
                    jsonArray.add(elem);
                }
                result.put("value", jsonArray);
                return result;
            case TAG_COMPOUND:
                JSONObject jsonObject = new JSONObject();
                for (NbtBase member : (NbtCompound) nbtBase) {
                    if (member.getType() == NbtType.TAG_END) continue;
                    jsonObject.put(member.getName(), fromNBTBase(member));
                }
                result.put("value", jsonObject);
                return result;
        }
        return null;
    }

    public static NbtBase toNBTBase(JSONObject jsonObject) {
        try {
            String name1 = (String) jsonObject.get("name");
            String typeName1 = (String) jsonObject.get("type");
            Object value1 = jsonObject.get("value");
            return toNBTBase(name1, typeName1, value1);
        } catch (ClassCastException | IllegalArgumentException | NullPointerException e) {
            Logging.debug(ExprJSONObjectOfPacket.class, e);
            return null;
        }
    }

    public static NbtBase toNBTBase(String name, String typeName, Object value) {
        NbtType type;
        String elemTypeName;
        if (typeName.startsWith("list")) {
            type = NbtType.TAG_LIST;
            elemTypeName = typeName.substring(5);
        } else {
            type = NbtType.valueOf("TAG_" + typeName.toUpperCase());
            elemTypeName = null;
        }
        Number number = value instanceof Number ? (Number) value : null;
        JSONArray jsonArray = value instanceof JSONArray ? (JSONArray) value : null;
        switch (type) {
            case TAG_BYTE:
                return NbtFactory.of(name, number.byteValue());
            case TAG_SHORT:
                return NbtFactory.of(name, number.shortValue());
            case TAG_INT:
                return NbtFactory.of(name, number.intValue());
            case TAG_LONG:
                return NbtFactory.of(name, number.longValue());
            case TAG_FLOAT:
                return NbtFactory.of(name, number.floatValue());
            case TAG_DOUBLE:
                return NbtFactory.of(name, number.doubleValue());
            case TAG_STRING:
                return NbtFactory.of(name, (String) value);
            case TAG_BYTE_ARRAY:
                byte[] bytes = new byte[jsonArray.size()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = ((Number) jsonArray.get(i)).byteValue();
                }
                return NbtFactory.of(name, bytes);
            case TAG_INT_ARRAY:
                int[] ints = new int[jsonArray.size()];
                for (int i = 0; i < ints.length; i++) {
                    ints[i] = ((Number) jsonArray.get(i)).intValue();
                }
                return NbtFactory.of(name, ints);
            case TAG_LIST:
                NbtBase[] nbtBases = new NbtBase[jsonArray.size()];
                for (int i = 0; i < nbtBases.length; i++) {
                    nbtBases[i] = toNBTBase(NbtList.EMPTY_NAME, elemTypeName, jsonArray.get(i));
                }
                return NbtFactory.ofList(name, nbtBases);
            case TAG_COMPOUND:
                NbtCompound nbtCompound = NbtFactory.ofCompound(name);
                ((JSONObject) value).forEach((__, maybeJSONObject) -> {
                    JSONObject jsonObject = (JSONObject) maybeJSONObject;
                    String name1 = (String) jsonObject.get("name");
                    String typeName1 = (String) jsonObject.get("type");
                    Object value1 = jsonObject.get("value");
                    nbtCompound.put(toNBTBase(name1, typeName1, value1));
                });
                return nbtCompound;
        }
        throw new IllegalArgumentException("Illegal NbtType: " + type);
    }
}
