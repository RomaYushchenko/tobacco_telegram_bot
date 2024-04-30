package com.ua.yushchenko.tabakabot.processor.command.client;

import java.util.HashMap;
import java.util.Map;

import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        commandMap.put(TobaccoBotCommand.BACK, backCommand);
    }

    public TobaccoCommand retrieveCommand(final TobaccoBotCommand tobaccoBotCommand) {
        log.info("retrieveCommand.E: finding {} command", tobaccoBotCommand);
        TobaccoCommand tobaccoCommand = commandMap.getOrDefault(tobaccoBotCommand, null);
        log.info("retrieveCommand.X: found {} command of instance", tobaccoCommand);
        return tobaccoCommand;
    }
}
