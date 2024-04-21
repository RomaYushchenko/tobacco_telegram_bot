package com.ua.yushchenko.tabakabot.utility;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.BOT_COMMAND;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.LOAD_420_CLASSIC;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.LOAD_420_LIGHT;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

/**
 * Utility class that provided functional for work with Bot command.
 *
 * @author romanyushchenko
 * @version v.0.1
 */
public class TobaccoBotCommandUtility {

    /**
     * Merge two {@link TobaccoBotCommand}
     *
     * @param firstCommand  first bot command
     * @param secondCommand second bot command
     * @return merged two {@link TobaccoBotCommand}. Ex: firstCommand:secondCommand
     */
    public static String mergeBotCommand(final TobaccoBotCommand firstCommand, final TobaccoBotCommand secondCommand) {
        return firstCommand.getCommandString() + ":" + secondCommand.getCommandString();
    }

    /**
     * Merge {@link TobaccoBotCommand} with tobacco item
     *
     * @param firstCommand  first bot command
     * @param tobaccoItemId ID of tobacco item
     * @return merged {@link TobaccoBotCommand} with tobacco item. Ex: firstCommand:tobaccoItemId
     */
    public static String mergeBotCommand(final TobaccoBotCommand firstCommand, final Long tobaccoItemId) {
        return firstCommand.getCommandString() + ":" + tobaccoItemId;
    }

    /**
     * Check Bot message that has bot command
     *
     * @param message user message
     * @return true - if message has bot command
     */
    public static boolean isTobaccoBotCommand(final Message message) {
        if (CollectionUtils.isEmpty(message.getEntities())) {
            return false;
        }

        return !CollectionUtils.isEmpty(getMessageEntitiesByCommandType(message, BOT_COMMAND, LOAD_420_LIGHT, LOAD_420_CLASSIC));
    }

    /**
     * Get list of {@link MessageEntity} by {@link TobaccoBotCommand} from user message.
     *
     * @param message    user message
     * @param botCommand {@link TobaccoBotCommand} to find
     * @return list of {@link MessageEntity} by {@link TobaccoBotCommand} from user message.
     */
    public static List<MessageEntity> getMessageEntitiesByCommandType(final Message message,
                                                                      final TobaccoBotCommand... botCommand) {
        return message.getEntities()
                      .stream()
                      .filter(messageEntity -> Arrays.asList(botCommand)
                                                     .stream()
                                                     .map(TobaccoBotCommand::getCommandString)
                                                     .toList()
                                                     .contains(messageEntity.getType()))
                      .toList();
    }

    /**
     * Get first {@link MessageEntity} by {@link TobaccoBotCommand} from user message.
     *
     * @param message    user message
     * @param botCommand {@link TobaccoBotCommand} to find
     * @return first {@link MessageEntity} by {@link TobaccoBotCommand} from user message.
     */
    public static MessageEntity findFirstMessageEntityByCommandType(final Message message,
                                                                    final TobaccoBotCommand botCommand) {
        return getMessageEntitiesByCommandType(message, botCommand)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
