package ru.pobeda18.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.pobeda18.bot.funcs.num_processing.CarNumberProcessing;
import ru.pobeda18.bot.keyBoard.InlineKB;
import ru.pobeda18.db.tables.TempCarNumber;
import ru.pobeda18.service.ClientService;
import ru.pobeda18.client.UserSheetData;
import ru.pobeda18.service.SheetsService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
public class BarrierBot extends TelegramLongPollingCommandBot {

    private final String botUsername;

    private final CarNumberProcessing carNumberProcessing = new CarNumberProcessing();
    private final SheetsService sheetsService;

    private final ClientService clientService;

    private static final String NEW_LINE = System.lineSeparator();
    private static final String CAR_IN_BASE_PATTERN = "Машина c номером {0} есть в базе.";
    private static final String CAR_NOT_IN_BASE_PATTERN = "Машины c номером {0} нет в базе.";
    private static final String HAS_CAR_INFO = "Нажмите на команду /has_car, чтобы проверить номер автомобиля.";
    private static final String NOT_CORRECT_NUMBER = "Неверно введенный номер. Попробуйте еще раз.";
    private static final String OFFER_GET_INFO = "Хотите получить информацию о ней?";
    private static final String NO_COMMAND = "Такой команды нет.";

    public BarrierBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            List<IBotCommand> commandList,
            SheetsService sheetsService,
            ClientService clientService) {
        super(botToken);
        this.botUsername = botUsername;
        this.sheetsService = sheetsService;
        this.clientService = clientService;

        commandList.forEach(this::register);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            try {
                callbackQueryProcessing(update);
            } catch (GeneralSecurityException | IOException | TelegramApiException e) {
                log.error("error");
            }

        } else if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                textMessageProcessing(update);
            } catch (GeneralSecurityException | IOException | TelegramApiException e) {
                log.error("error");
            }
        }
    }

    private void callbackQueryProcessing(Update update) throws TelegramApiException, GeneralSecurityException, IOException {
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callbackData = update.getCallbackQuery().getData();

        removeUserStates(chatId);

        switch (callbackData) {
            case "YES_BUTTON" -> yesButtonProcessing(chatId, messageId);
            case "NO_BUTTON" -> noButtonProcessing(chatId, messageId);
        }
    }

    private void removeUserStates(long chatId) {
        clientService.deleteClientState(chatId);
    }

    private void yesButtonProcessing(long chatId, int messageId) throws GeneralSecurityException, IOException, TelegramApiException {
        UserSheetData userData = getUserSheetData(chatId);
        String textMessage = buildInfoTextMess(userData);
        sendChangedMessage(chatId, messageId, textMessage);
    }

    private UserSheetData getUserSheetData(long chatId) throws GeneralSecurityException, IOException {
        try {

            UserSheetData userSheetData = sheetsService.getInfoByNumber(clientService.findTempCarNumberByClientTgId(chatId).getTempCarNumber());
            clientService.deleteTempCarNumber(chatId);

            return userSheetData;
        } catch (GeneralSecurityException | IOException e) {
            throw e;
        }
    }

    private String buildInfoTextMess(UserSheetData userData) {

        String textMassage = String.format("Информация о владельце машины с номером %s:", userData.getNumber()) + NEW_LINE +
                             String.format("Номер телефона: %s", userData.getPhoneNumber()) + NEW_LINE +
                             String.format("Номер квартиры: %s", userData.getApartmentNumber()) + NEW_LINE + NEW_LINE +
                             HAS_CAR_INFO;
        return textMassage;
    }

    private void sendChangedMessage(long chatId, int messageId, String textMessage) throws TelegramApiException {
        try {
            EditMessageText message = new EditMessageText();
            message.setChatId(chatId);
            message.setText(textMessage);
            message.setMessageId(messageId);
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Send message exception");
            throw e;
        }
    }

    private void noButtonProcessing(long chatId, int messageId) throws TelegramApiException {

        String textMessage = MessageFormat.format(CAR_IN_BASE_PATTERN, clientService.findTempCarNumberByClientTgId(chatId).getTempCarNumber()) + NEW_LINE + NEW_LINE + HAS_CAR_INFO;
        clientService.deleteTempCarNumber(chatId);

        sendChangedMessage(chatId, messageId, textMessage);
    }

    private void textMessageProcessing(Update update) throws GeneralSecurityException, IOException, TelegramApiException {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (clientService.findStateByClientTgId(chatId) != null) {
            String userState = clientService.findStateByClientTgId(chatId).getUserState().toString();

            switch (userState) {
                case "HAS_CAR_COMMAND" -> userCarNumberMessageProcessing(chatId, messageText);
            }
        } else {
            sendMessage(chatId, NO_COMMAND + NEW_LINE + HAS_CAR_INFO);
        }
    }

    private void userCarNumberMessageProcessing(long chatId, String messageText) throws GeneralSecurityException, IOException, TelegramApiException {
        String carNumber = carNumberProcessing.getProcessedCarNumber(messageText);

        if (carNumber == null) {
            sendMessage(chatId, NOT_CORRECT_NUMBER);
        } else {
            isCarInBaseProcessing(chatId, carNumber);
        }
    }

    private void isCarInBaseProcessing(long chatId, String carNumber) throws GeneralSecurityException, IOException, TelegramApiException {

        TempCarNumber tempCarNumber =  new TempCarNumber();
        tempCarNumber.setTempCarNumber(carNumber);
        tempCarNumber.setClientTgId(chatId);
        clientService.saveTempCarNumber(tempCarNumber);


        if (sheetsService.isContainNumber(carNumber)) {
            sendMessage(chatId,
                    MessageFormat.format(CAR_IN_BASE_PATTERN, carNumber) + NEW_LINE + OFFER_GET_INFO,
                    new InlineKB());
        } else {
            sendMessage(chatId,
                    MessageFormat.format(CAR_NOT_IN_BASE_PATTERN, carNumber) + NEW_LINE + NEW_LINE + HAS_CAR_INFO);
            removeUserStates(chatId);
        }
    }

    private void sendMessage(long chatId, String textToSend) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Send message exception");
            throw e;
        }
    }

    private void sendMessage(long chatId, String textToSend, InlineKB kB) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setReplyMarkup(kB.getKB());
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Send message exception");
            throw e;
        }
    }
}