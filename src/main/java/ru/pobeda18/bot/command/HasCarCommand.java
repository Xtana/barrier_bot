package ru.pobeda18.bot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.pobeda18.service.ClientService;
import ru.pobeda18.db.tables.ClientState;
import ru.pobeda18.db.tables.states.UserState;

@Service
@Slf4j
@RequiredArgsConstructor
public class HasCarCommand implements IBotCommand {

    private final ClientService clientService;

    @Override
    public String getCommandIdentifier() {
        return "has_car";
    }

    @Override
    public String getDescription() {
        return "Возвращает информацию о нахождении автомобиля по конкретному номеру в базе данных";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        try {
            answer.setText("Введине номер машины");
            absSender.execute(answer);
            setUserState(message.getChatId());
        } catch (Exception e) {
            log.error("Ошибка возникла /has_car методе", e);
        }
    }

    private void setUserState(long chatId) {
//        BarrierBot.getUserStates().put(chatId, UserState.HAS_CAR_COMMAND);
        if (clientService.findStateByClientTgId(chatId) != null) {
            return;
        }
        ClientState clientState = new ClientState();
        clientState.setUserState(UserState.HAS_CAR_COMMAND);
        clientState.setClientTgId(chatId);
        clientService.saveClientState(clientState);
    }

}
