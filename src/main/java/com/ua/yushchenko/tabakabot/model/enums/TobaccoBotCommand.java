package com.ua.yushchenko.tabakabot.model.enums;

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

    //ADMIN COMMANDS
    GET_ALL_ORDERS("get_all_orders"),
    GET_ALL_ORDERS_BY_USER("get_all_orders_by_user"),
    GET_ALL_USERS("get_all_users"),
    LOAD_420_LIGHT("load_420_light"),
    LOAD_420_CLASSIC("load_420_classic");


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

    public String getCommandString() {
        return commandString;
    }
}
