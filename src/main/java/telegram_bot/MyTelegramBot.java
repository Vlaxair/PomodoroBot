package telegram_bot;

import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import telegram_bot.commands.HelpCommand;
import telegram_bot.commands.BeginCommand;

import java.util.concurrent.TimeUnit;

public class MyTelegramBot extends TelegramLongPollingCommandBot {
    public static final String BOT_TOKEN = "5732086912:AAEGlqqinqW9cIT50j7b6ZA7sTsw_aCdwdE";
    public static final String BOT_USERNAME = "PomodoroVlax_bot";
//    public static final String CHAT_ID = "204002385";

    public static final int WORK = 25;
    public static final int BREAK = 5;
    public static String chatId;

    public MyTelegramBot() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            //регистрация команд
            botsApi.registerBot(this);
            register(new BeginCommand("begin", "Старт"));
            register(new HelpCommand("help", "Помощь"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }
    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    //логика помодоро тут:
    @Override
    public void processNonCommandUpdate(Update update) {
        chatId = update.getMessage().getChatId().toString();
        if (update.hasMessage() && isNumeric(update.getMessage().getText()) && BeginCommand.isStart) {
            String message;
            BeginCommand.isStart = false;
            int count = Integer.parseInt(update.getMessage().getText());    //количество дел
            for (int i = 1; i <= count; i++) {
                message = "Задача № " + i + " началась \n" + "Работаем " + WORK + " минут" ;
                sendMessage(message);
                try {
                    TimeUnit.MINUTES.sleep(WORK);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                message = "Задача № " + i + " завершилась";
                sendMessage(message);
                if (count - i != 0) {
                    message = "Отдых " + BREAK + " минут";
                    sendMessage(message);
                    try {
                        TimeUnit.MINUTES.sleep(BREAK);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    message = "Время работать!";
                    sendMessage(message);
                }
            }
        } else {
            String message = "Непонятная комадна \n" + "Для старта введите /begin";
            sendMessage(message);
        }
    }
    //метод отправки сообщений
    private void sendMessage(String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //метод проверяет что введённое количество дел является числом
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
