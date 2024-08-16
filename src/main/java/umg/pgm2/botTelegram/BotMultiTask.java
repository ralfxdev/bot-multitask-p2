package umg.pgm2.botTelegram;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.List;

public class BotMultiTask extends TelegramLongPollingBot {

    Dotenv dotenv = Dotenv.load();

    String USERNAME = dotenv.get("USERNAME");
    String TOKEN = dotenv.get("TOKEN");

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            System.out.println("Chat ID: " + chatId);
            System.out.println("Nombre: " + update.getMessage().getChat().getFirstName());
            System.out.println("Apellido: " + update.getMessage().getChat().getLastName());
            System.out.println("Username: " + update.getMessage().getChat().getUserName());

            if (messageText.equals("/info")) {
                sendInfo(chatId);
            } else if (messageText.equals("/progra")) {
                sendPrograComment(chatId);
            } else if (messageText.equals("/hola")) {
                sendGreeting(chatId, update.getMessage().getChat().getFirstName());
            } else if (messageText.startsWith("/cambio")) {
                convertCurrency(chatId, messageText);
            } else if (messageText.equals("/ayuda")) {
                sendHelp(chatId);
            } else if (messageText.startsWith("/grupal")) {
                sendGroupMessage(messageText);
            } else {
                sendHelp(chatId);
            }
        }
    }

    private void sendInfo(long chatId) {
        String info = "Información personal:\n" +
                "Número de carnet: 0905-20-8464\n" +
                "Nombre: Randy Alexander López Flores\n" +
                "Semestre: 4to Semestre";
        sendMessage(chatId, info);
    }

    private void sendHelp(long chatId) {
        String info = "BotMultiTask by ralfxdev\n" +
                "/info\n" +
                "/progra\n" +
                "/hola\n" +
                "/cambio <cantidad>\n" +
                "/grupal <mensaje>\n" +
                "Gracias por utilizar :3";
        sendMessage(chatId, info);
    }

    private void sendPrograComment(long chatId) {
        String[] comments = {
                "La clase de programación es muy interesante y desafiante. Estoy aprendiendo mucho.",
                "Estoy disfrutando la práctica con proyectos reales en la clase de programación.",
                "La interacción en las clases hace que aprender programación sea muy entretenido.",
                "Las tareas de programación me ayudan a solidificar mi comprensión de los conceptos."
        };

        Random random = new Random();
        int randomIndex = random.nextInt(comments.length);
        String randomComment = comments[randomIndex];

        sendMessage(chatId, randomComment);
    }

    private void sendGreeting(long chatId, String firstName) {
        String currentDate = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES")).format(new Date());
        String greeting = String.format("Hola, %s, hoy es %s.", firstName, currentDate);
        sendMessage(chatId, greeting);
    }

    private void convertCurrency(long chatId, String messageText) {
        String[] parts = messageText.split(" ");
        if (parts.length == 2) {
            try {
                double euros = Double.parseDouble(parts[1]);
                double rate = getExchangeRate();
                double quetzales = euros * rate;
                sendMessage(chatId, String.format("Son Q. %.2f", quetzales));
            } catch (NumberFormatException e) {
                sendMessage(chatId, "Por favor, introduce un número válido.");
            }
        } else {
            sendMessage(chatId, "Por favor, usa el formato /cambio <cantidad>");
        }
    }

    private double getExchangeRate() {
        return 8.54;
    }

    private void sendGroupMessage(String messageText) {
        List<Long> chatIds = List.of(
                6421826691L,
                6956666969L,
                7070992511L,
                2064783549L,
                6308317056L,
                2064783549L
        );

        String[] parts = messageText.split(" ", 2);
        if (parts.length == 2) {
            String message = parts[1];
            for (Long chatId : chatIds) {
                sendMessage(chatId, message);
            }
        } else {
            sendMessage(chatIds.get(0), "Por favor, usa el formato /grupal <mensaje>");
        }
    }


    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
