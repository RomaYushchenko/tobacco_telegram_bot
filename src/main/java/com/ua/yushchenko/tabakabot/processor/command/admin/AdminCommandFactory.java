package com.ua.yushchenko.tabakabot.processor.command.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Factory to get Admin {@link TobaccoCommand}
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Slf4j
@Component
public class AdminCommandFactory {

    private final Map<TobaccoBotCommand, TobaccoCommand> commandMap;

    public AdminCommandFactory(final @NonNull StartCommand startCommand,
                               final @NonNull Load420LightCommand load420LightCommand,
                               final @NonNull Load420ClassicCommand load420ClassicCommand,
                               final @NonNull GetAllOrdersByUserCommand getAllOrdersByUserCommand,
                               final @NonNull GetAllOrdersCommand getAllOrdersCommand,
                               final @NonNull GetAllUsersCommand getAllUsersCommand,
                               final @NonNull ProcessingOrderMenuCommand processingOrderMenuCommand,
                               final @NonNull BackCommand backCommand) {
        this.commandMap = new HashMap<>();

        commandMap.put(TobaccoBotCommand.START, startCommand);
        commandMap.put(TobaccoBotCommand.LOAD_420_LIGHT, load420LightCommand);
        commandMap.put(TobaccoBotCommand.LOAD_420_CLASSIC, load420ClassicCommand);
        commandMap.put(TobaccoBotCommand.GET_ALL_ORDERS_BY_USER, getAllOrdersByUserCommand);
        commandMap.put(TobaccoBotCommand.GET_ALL_ORDERS, getAllOrdersCommand);
        commandMap.put(TobaccoBotCommand.GET_ALL_USERS, getAllUsersCommand);
        commandMap.put(TobaccoBotCommand.PROCESSING_ORDERS_MENU, processingOrderMenuCommand);
        commandMap.put(TobaccoBotCommand.BACK, backCommand);
    }

    /**
     * Gets {@link TobaccoCommand} by list of {@link TobaccoBotCommand}
     *
     * @param tobaccoBotCommands list of {@link TobaccoBotCommand}
     * @return {@link TobaccoCommand} by list of {@link TobaccoBotCommand}
     */
    public TobaccoCommand retrieveCommand(final List<Object> tobaccoBotCommands) {
        log.info("retrieveCommand.E: [ADMIN] finding {} commands", tobaccoBotCommands);

        if (CollectionUtils.isEmpty(tobaccoBotCommands)) {
            log.error("retrieveCommand.X: [ADMIN] Unhandled callback command!!!");
            return null;
        }

        final TobaccoCommand tobaccoCommand =
                commandMap.getOrDefault((TobaccoBotCommand) tobaccoBotCommands.get(0), null);

        log.info("retrieveCommand.X: [ADMIN] found {} command of instance", tobaccoCommand);
        return tobaccoCommand;
    }
}
