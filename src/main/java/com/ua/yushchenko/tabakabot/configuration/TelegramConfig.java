package com.ua.yushchenko.tabakabot.configuration;

import com.ua.yushchenko.tabakabot.processor.TobaccoAdminBotProcessor;
import com.ua.yushchenko.tabakabot.processor.TobaccoClientBotProcessor;
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
    private TobaccoClientBotProcessor tobaccoClientBotProcessor;
    @NonNull
    private TobaccoAdminBotProcessor tobaccoAdminBotProcessor;

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
            telegramBotsApi.registerBot(tobaccoClientBotProcessor, setWebhookInstance());

            tobaccoAdminBotProcessor.setWebhook(setWebhookInstance());
            telegramBotsApi.registerBot(tobaccoAdminBotProcessor, setWebhookInstance());

            log.info("Registered Bot.");
        } catch (TelegramApiException e) {
            log.error("Unhandled error: ", e);
        }

        return telegramBotsApi;
    }
}
