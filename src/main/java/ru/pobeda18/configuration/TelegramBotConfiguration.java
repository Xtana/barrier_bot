package ru.pobeda18.configuration;


import ru.pobeda18.bot.BarrierBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
public class TelegramBotConfiguration {

    @Bean
    TelegramBotsApi telegramBotsApi(BarrierBot barrierBot) {
        TelegramBotsApi botsApi = null;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(barrierBot);
        } catch (TelegramApiException e) {
            log.error("Error occurred while sending message to telegram!", e);
        }
        return botsApi;
    }
}
