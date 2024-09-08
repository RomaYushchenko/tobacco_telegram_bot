package com.ua.yushchenko.tabakabot.model.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Enum to provide command of tobacco bot
 *
 * @author romanyushchenko
 * @version v.0.1
 */
public enum TobaccoBotCommand {

    BOT_COMMAND("bot_command"),
    START("start"),
    TOBACCO_LIST("tobacco_list"),
    TABAKA_420_LIGHT("420_light"),
    TABAKA_420_CLASSIC("420_classic"),
    ORDER_420_LIGHT("order_420_light"),
    ORDER("order"),
    ORDER_LIST("order_list"),
    COAL("coal"),
    SEND_ORDER_REQUEST("send_order_request"),
    BACK("back"),
    REMOVE_ORDER("remove_order"),
    ORDER_STATUS("order_status"),
    ORDERED_STATISTICS_MENU("osm"),
    GLOBAL_ORDERED_STATISTICS_MENU("global_osm"),
    USER_ORDERED_STATISTICS_MENU("user_osm"),

    //ADMIN COMMANDS
    GET_ALL_ORDERS("get_all_orders"),
    GET_ALL_ORDERS_BY_USER("get_all_orders_by_user"),
    GET_ALL_USERS("get_all_users"),
    LOAD_420_LIGHT("load_420_light"),
    LOAD_420_CLASSIC("load_420_classic"),
    PROCESSING_ORDERS_MENU("pom"),
    PLANNED_MENU("pm"),
    REJECT_MENU("rm"),
    ORDERED_MENU("om"),
    COMPLETED_ORDER_MENU("com");


    private final String commandString;

    TobaccoBotCommand(final String commandString) {
        this.commandString = commandString;
    }

    public static TobaccoBotCommand getEnumByString(String commandString) {
        for (final TobaccoBotCommand tobaccoBotCommand : TobaccoBotCommand.values()) {
            if (Objects.equals(tobaccoBotCommand.getCommandString(), commandString)) {
                return tobaccoBotCommand;
            }
        }

        if (commandString.contains(ORDER.getCommandString())) {
            return ORDER;
        }

        if (commandString.contains(BACK.getCommandString())) {
            return BACK;
        }

        if (commandString.contains(REMOVE_ORDER.getCommandString())) {
            return REMOVE_ORDER;
        }

        return null;
    }

    public static List<Object> getListCommandsByString(final String commandsString) {
        if (!commandsString.contains(":")) {
            final TobaccoBotCommand enumByString = getEnumByString(commandsString);

            return Objects.nonNull(enumByString)
                    ? List.of(enumByString)
                    : List.of();
        }

        final List<Object> tobaccoBotCommands = new ArrayList<>();

        final String[] splitBotCommands = commandsString.split(":");

        for (final String splitBotCommand : splitBotCommands) {
            final TobaccoBotCommand enumByString = getEnumByString(splitBotCommand);

            if (Objects.nonNull(enumByString)) {
                tobaccoBotCommands.add(enumByString);
            } else {
                tobaccoBotCommands.add(Long.valueOf(splitBotCommand));
            }

        }

        return tobaccoBotCommands;
    }

    public String getCommandString() {
        return commandString;
    }
}
