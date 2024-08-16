package umg.pgm2;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import umg.pgm2.botTelegram.BotMultiTask;

public class Main {
    public static void main(String[] args) {
        try{
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            BotMultiTask bot = new BotMultiTask();
            botsApi.registerBot(bot);
            System.out.println("/help");
            System.out.println("Bot registered!");
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}