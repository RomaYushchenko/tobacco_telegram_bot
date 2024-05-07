package com.ua.yushchenko.tabakabot.controller;

import com.ua.yushchenko.tabakabot.processor.TobaccoAdminBotProcessor;
import com.ua.yushchenko.tabakabot.processor.TobaccoClientBotProcessor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Represents of controller for handling Telegram Bot requests
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class BotController {

    @NonNull
    private final TobaccoClientBotProcessor tobaccoClientBotProcessor;
    @NonNull
    private final TobaccoAdminBotProcessor tobaccoAdminBotProcessor;

    /**
     * Handle {@link Update} request for Telegram Bot Client
     *
     * @param update instance of {@link Update}
     * @return instance of {@link ResponseEntity}
     */
    @RequestMapping(value = "/callback/client/update", method = RequestMethod.POST)
    public ResponseEntity<?> onUpdateReceivedClient(@RequestBody Update update) {
        try {
            tobaccoClientBotProcessor.onWebhookUpdateReceived(update);

            return ResponseEntity.ok()
                                 .build();
        } catch (final Exception e) {
            log.error("Unhandled error", e);
            return ResponseEntity.badRequest()
                                 .build();
        }

    }

    /**
     * Handle {@link Update} request for Telegram Bot Admin
     *
     * @param update instance of {@link Update}
     * @return instance of {@link ResponseEntity}
     */
    @RequestMapping(value = "/callback/admin/update", method = RequestMethod.POST)
    public ResponseEntity<?> onUpdateReceivedAdmin(@RequestBody Update update) {
        try {
            tobaccoAdminBotProcessor.onWebhookUpdateReceived(update);

            return ResponseEntity.ok()
                                 .build();
        } catch (final Exception e) {
            log.error("Unhandled error", e);
            return ResponseEntity.badRequest()
                                 .build();
        }

    }
}
