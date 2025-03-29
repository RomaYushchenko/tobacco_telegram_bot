package com.ua.yushchenko.tabakabot.utility;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.BOT_COMMAND;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.LOAD_420_CLASSIC;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.LOAD_420_LIGHT;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
     * Merge three {@link TobaccoBotCommand}
     *
     * @param firstCommand  first bot command
     * @param secondCommand second bot command
     * @return merged two {@link TobaccoBotCommand}. Ex: firstCommand:secondCommand:thredCommand
     */
    public static String mergeBotCommand(final TobaccoBotCommand firstCommand,
                                         final TobaccoBotCommand secondCommand,
                                         final TobaccoBotCommand theredCommand) {
        return firstCommand.getCommandString() + ":" + secondCommand.getCommandString() +
                ":" + theredCommand.getCommandString();
    }

    /**
     * Merge three {@link TobaccoBotCommand} and user ID
     *
     * @param firstCommand  first bot command
     * @param secondCommand second bot command
     * @return merged two {@link TobaccoBotCommand}. Ex: firstCommand:secondCommand:thredCommand:1111
     */
    public static String mergeBotCommand(final TobaccoBotCommand firstCommand,
                                         final TobaccoBotCommand secondCommand,
                                         final TobaccoBotCommand theredCommand,
                                         final Long userId) {
        return firstCommand.getCommandString() + ":" + secondCommand.getCommandString() +
                ":" + theredCommand.getCommandString() +
                ":" + userId;
    }

    /**
     * Merge three {@link TobaccoBotCommand} and user ID and item ID
     *
     * @param firstCommand  first bot command
     * @param secondCommand second bot command
     * @return merged two {@link TobaccoBotCommand}. Ex: firstCommand:secondCommand:thredCommand:111:222
     */
    public static String mergeBotCommand(final TobaccoBotCommand firstCommand,
                                         final TobaccoBotCommand secondCommand,
                                         final TobaccoBotCommand theredCommand,
                                         final Long userId,
                                         final Long itemId) {
        return firstCommand.getCommandString() + ":" + secondCommand.getCommandString() +
                ":" + theredCommand.getCommandString() +
                ":" + userId +
                ":" + itemId;
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

        return !CollectionUtils.isEmpty(
                getMessageEntitiesByCommandType(message, BOT_COMMAND, LOAD_420_LIGHT, LOAD_420_CLASSIC, LOAD_420_LIGHT));
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
                      .filter(messageEntity -> Arrays.stream(botCommand)
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

    /**
     * Gets first text of Telegram message entity and convert to TobaccoBotCommand
     *
     * @param message Telegram message Entity
     * @return first text of Telegram message entity
     */
    public static TobaccoBotCommand getFirstCommandOfMessageEntityBotCommand(final Message message) {
        final String botCommand =
                Optional.ofNullable(findFirstMessageEntityByCommandType(message, BOT_COMMAND))
                        .map(MessageEntity::getText)
                        .orElse(null);

        if (botCommand == null) {
            return null;
        }

        final String[] splitBotCommand = botCommand.split("/");
        return TobaccoBotCommand.getEnumByString(splitBotCommand[1]);
    }
}
