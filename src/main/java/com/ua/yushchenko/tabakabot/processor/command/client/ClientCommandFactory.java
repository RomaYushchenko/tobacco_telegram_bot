package com.ua.yushchenko.tabakabot.processor.command.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Factory to get Client {@link TobaccoCommand}
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Slf4j
@Component
public class ClientCommandFactory {

    private final Map<TobaccoBotCommand, TobaccoCommand> commandMap;

    @Autowired
    public ClientCommandFactory(final @NonNull StartCommand startCommand,
                                final @NonNull Tobacco420LightCommand tobacco420LightCommand,
                                final @NonNull Tobacco420ClassicCommand tobacco420ClassicCommand,
                                final @NonNull CoalCommand coalCommand,
                                final @NonNull OrderListCommand orderListCommand,
                                final @NonNull OrderCommand orderCommand,
                                final @NonNull SendRequestOrderCommand sendRequestOrderCommand,
                                final @NonNull RemoveOrderCommand removeOrderCommand,
                                final @NonNull OrderStatusCommand orderStatusCommand,
                                final @NonNull OrderedStatisticsMenuCommand orderedStatisticsMenuCommand,
                                final @NonNull BackCommand backCommand) {

        commandMap = new HashMap<>();

        commandMap.put(TobaccoBotCommand.START, startCommand);
        commandMap.put(TobaccoBotCommand.TABAKA_420_LIGHT, tobacco420LightCommand);
        commandMap.put(TobaccoBotCommand.TABAKA_420_CLASSIC, tobacco420ClassicCommand);
        commandMap.put(TobaccoBotCommand.COAL, coalCommand);
        commandMap.put(TobaccoBotCommand.ORDER_LIST, orderListCommand);
        commandMap.put(TobaccoBotCommand.ORDER, orderCommand);
        commandMap.put(TobaccoBotCommand.SEND_ORDER_REQUEST, sendRequestOrderCommand);
        commandMap.put(TobaccoBotCommand.REMOVE_ORDER, removeOrderCommand);
        commandMap.put(TobaccoBotCommand.ORDER_STATUS, orderStatusCommand);
        commandMap.put(TobaccoBotCommand.ORDERED_STATISTICS_MENU, orderedStatisticsMenuCommand);
        commandMap.put(TobaccoBotCommand.BACK, backCommand);
    }

    /**
     * Gets {@link TobaccoCommand} by list of {@link TobaccoBotCommand}
     *
     * @param tobaccoBotCommands list of {@link TobaccoBotCommand}
     * @return {@link TobaccoCommand} by list of {@link TobaccoBotCommand}
     */
    public TobaccoCommand retrieveCommand(final List<Object> tobaccoBotCommands) {
        log.info("retrieveCommand.E: [CLIENT] finding {} command", tobaccoBotCommands);

        if (CollectionUtils.isEmpty(tobaccoBotCommands)) {
            log.error("retrieveCommand.X: [CLIENT] Unhandled callback command!!!");
            return null;
        }

        final TobaccoCommand tobaccoCommand =
                commandMap.getOrDefault((TobaccoBotCommand) tobaccoBotCommands.get(0), null);

        log.info("retrieveCommand.X: [CLIENT] found {} command of instance", tobaccoCommand);
        return tobaccoCommand;
    }
}
