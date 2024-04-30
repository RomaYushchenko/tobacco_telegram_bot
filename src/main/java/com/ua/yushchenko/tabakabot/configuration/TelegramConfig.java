package com.ua.yushchenko.tabakabot.configuration;

import com.ua.yushchenko.tabakabot.builder.InformationMessageBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.admin.LoadTobaccoBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.admin.MenuBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.admin.OrderListBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.admin.ProcessOrderMenuBuilder;
import com.ua.yushchenko.tabakabot.processor.TobaccoAdminBotProcessor;
import com.ua.yushchenko.tabakabot.processor.TobaccoClientBotProcessor;
import com.ua.yushchenko.tabakabot.service.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Class that provide configuration of Telegram Bot
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TelegramConfig {

    @NonNull
    private final OrderService orderService;
    @NonNull
    private final InformationMessageBuilder informationMessageBuilder;
    @NonNull
    private final MenuBuilder menuBuilder;
    @NonNull
    private final OrderListBuilder orderListBuilder;
    @NonNull
    private final LoadTobaccoBuilder loadTobaccoBuilder;
    @NonNull
    private final ProcessOrderMenuBuilder processOrderMenuBuilder;

    @NonNull
    private TobaccoClientBotProcessor tobaccoClientBotProcessor;

    @Value("${telegram.tobacco.admin.bot.token}")
    private String tobaccoAdminBotToken;

    @Value("${telegram.tobacco.web-hook.url}")
    private String tobaccoClientWebHookUrl;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(tobaccoClientWebHookUrl).build();
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);


        try {
            log.info("Register Bot....");

            tobaccoClientBotProcessor.setWebhook(setWebhookInstance());

            TobaccoAdminBotProcessor tobaccoAdminBotProcessor = tobaccoAdminBotController();
            tobaccoAdminBotProcessor.setWebhook(setWebhookInstance());

            telegramBotsApi.registerBot(tobaccoClientBotProcessor, setWebhookInstance());
            telegramBotsApi.registerBot(tobaccoAdminBotProcessor, setWebhookInstance());

            log.info("Registered Bot.");
        } catch (TelegramApiException e) {
            log.error("Unhandled error: ", e);
        }
        return telegramBotsApi;
    }

    @Bean
    public TobaccoAdminBotProcessor tobaccoAdminBotController() {
        return new TobaccoAdminBotProcessor(informationMessageBuilder,
                                            menuBuilder,
                                            orderListBuilder,
                                            loadTobaccoBuilder,
                                            processOrderMenuBuilder,
                                            orderService,
                                            tobaccoAdminBotToken);
    }
}
