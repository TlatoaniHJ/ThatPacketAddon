package us.tlatoani.thatpacketaddon.packet_field_alias;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.log.SkriptLogger;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.event.Event;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.event_scope.MundoEventScope;
import us.tlatoani.mundocore.event_scope.ScopeUtil;
import us.tlatoani.mundocore.grouped_list.GroupedList;
import us.tlatoani.mundosyntax.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Tlatoani on 10/15/17.
 */
public class ScopePacketFieldAliases extends MundoEventScope {
    private PacketType packetType;
    private GroupedList.Key key;

    public static final Character SEPARATOR = '=';
    public static final String SYNTAX_VAR_NAME = "p";

    @Override
    protected void afterInit() {}

    @Override
    public void unregister(Trigger trigger) {
        ExprPacketFieldAlias.unregisterAliases(key);
    }

    @Override
    public void unregisterAll() {
        ExprPacketFieldAlias.unregisterAllAliases();
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        packetType = ((Literal<PacketType>) literals[0]).getSingle();
        SectionNode topNode = (SectionNode) SkriptLogger.getNode();
        try {
            List<PacketFieldAlias> aliases = new ArrayList<>();
            for (Node node : topNode) {
                SkriptLogger.setNode(node);
                if (node instanceof SectionNode) {
                    Skript.error("Packet info aliases should not be sections!");
                    return false;
                }
                int separatorIndex = node.getKey().indexOf(SEPARATOR);
                if (separatorIndex == -1) {
                    Skript.error("Packet info aliases should be in the format '<new syntax> " + SEPARATOR + " <old syntax>'");
                    return false;
                }
                String syntax = node.getKey().substring(0, separatorIndex).trim();
                Optional<String> validatedSyntax = validateAliasSyntax(syntax);
                if (!validatedSyntax.isPresent()) {
                    return false;
                }
                String original = node.getKey().substring(separatorIndex + 1).trim();
                Optional<PacketFieldAlias> aliasOptional = PacketFieldAlias.create(packetType, validatedSyntax.get(), original);
                if (!aliasOptional.isPresent()) {
                    Skript.error("Invalid target syntax in the packet info alias!");
                    return false;
                }
                aliases.add(aliasOptional.get());
            }
            key = ExprPacketFieldAlias.registerAliases(aliases);
            return true;
        } finally {
            ScopeUtil.removeSubNodes(topNode);
        }
    }

    private static Optional<String> validateAliasSyntax(String syntax) {
        if (syntax.contains("%" + SYNTAX_VAR_NAME + "=")) {
            Skript.error("Invalid alias syntax in the packet info alias!");
            return Optional.empty();
        }
        String spComptabile = syntax.replace("%packet%", "%" + SYNTAX_VAR_NAME + "=-packet%");
        SyntaxPiece syntaxPiece;
        try {
            syntaxPiece = SyntaxParser.parse(spComptabile);
        } catch (IllegalArgumentException e) {
            Logging.debug(ScopePacketFieldAliases.class, e);
            Skript.error("Invalid alias syntax in the packet info alias!");
            return Optional.empty();
        } catch (Exception e) {
            Logging.reportException(ScopePacketFieldAliases.class, e);
            Skript.error("An error occurred while validating the alias syntax");
            return Optional.empty();
        }
        VariableUsage usage = syntaxPiece.getVariableUsage(SYNTAX_VAR_NAME);
        /*if (usage == VariableUsage.NONE) {
            Skript.error("Alias syntax does not contain '%packet%'!");
            return Optional.empty();
        } else if (usage == VariableUsage.INCONISTENT) {
            Skript.error("Alias syntax does not require the use of '%packet%' in its syntax!");
            return Optional.empty();
        } else*/ if (usage == VariableUsage.CONFLICTING) {
            Skript.error("Alias syntax allows conflicting usage of '%packet%'!");
            return Optional.empty();
        }
        VariableCollective variableCollective = syntaxPiece.getVariables();
        ExpressionConstraints constraints = variableCollective.getExpression(SYNTAX_VAR_NAME);
        if (variableCollective.size() > (constraints == null ? 0 : 1) || variableCollective.isVaryingOption(SYNTAX_VAR_NAME)) {
            Skript.error("Invalid alias syntax in the packet info alias!");
            return Optional.empty();
        }
        if (constraints != null) {
            for (ExpressionConstraints.Type type : constraints.types) {
                if (type.classInfo.getC() != PacketContainer.class || !type.isSingle) {
                    Skript.error("Invalid alias syntax in the packet info alias!");
                    return Optional.empty();
                }
            }
        }
        return Optional.of(syntaxPiece.actualSyntax(0));
    }

    @Override
    public String toString(Event event, boolean b) {
        return "packet field aliases for " + packetType;
    }
}
