package umg.pgm2.botTelegram;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import umg.pgm2.model.Cuestionario;
import umg.pgm2.model.User;
import umg.pgm2.model.Respuesta;
import umg.pgm2.service.CuestionarioService;
import umg.pgm2.service.UserService;
import umg.pgm2.service.RespuestaService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class botCuestionario extends TelegramLongPollingBot {
    private final Map<Long, String> estadoConversacion = new HashMap<>();
    private final Map<Long, Integer> indicePregunta = new HashMap<>();
    private final Map<Long, String> seccionActiva = new HashMap<>();
    private final Map<String, String[]> preguntas = new HashMap<>();
    private UserService userService = new UserService();
    private RespuestaService respuestaService = new RespuestaService();
    private User usuarioConectado = null;

    static Dotenv dotenv = Dotenv.load();
    String USERNAME = dotenv.get("USERNAME");
    String TOKEN = dotenv.get("TOKEN");

    public botCuestionario() {
        // Inicializa los cuestionarios con las preguntas.
        preguntas.put("SECTION_1", new String[]{"🤦‍♂️1.1- Estas aburrido?", "😂😂 1.2- Te bañaste hoy?", "🤡🤡 Pregunta 1.3"});
        preguntas.put("SECTION_2", new String[]{"Pregunta 2.1", "Pregunta 2.2", "Pregunta 2.3"});
        preguntas.put("SECTION_3", new String[]{"Pregunta 3.1", "Pregunta 3.2", "Pregunta 3.3"});
        preguntas.put("SECTION_4", new String[]{"Pregunta 4.1", "¿Cuál es tu edad?", "Pregunta 4.3", "Pregunta 4.4"});
    }

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
        long chatId = update.getMessage() != null ? update.getMessage().getChatId() : update.getCallbackQuery().getMessage().getChatId();
        String messageText = update.getMessage() != null ? update.getMessage().getText() : null;
        String callbackData = update.getCallbackQuery() != null ? update.getCallbackQuery().getData() : null;

        try {
            if (update.getMessage() != null && messageText != null) {
                String state = estadoConversacion.getOrDefault(chatId, "");
                usuarioConectado = userService.getUserByTelegramId(chatId);

                if (usuarioConectado == null && state.isEmpty()) {
                    sendText(chatId, "Hola, no tienes un usuario registrado en el sistema. Por favor ingresa tu correo electrónico:");
                    estadoConversacion.put(chatId, "ESPERANDO_CORREO");
                    return;
                }

                if (state.equals("ESPERANDO_CORREO")) {
                    processEmailInput(chatId, messageText);
                    return;
                }

                // Usuario registrado
                if (usuarioConectado != null) {
                    sendText(chatId, "Envía /menu para iniciar el cuestionario.");
                }

                if (messageText.equals("/menu")) {
                    sendMenu(chatId);
                } else if (seccionActiva.containsKey(chatId)) {
                    manejaCuestionario(chatId, messageText);
                }

            } else if (update.getCallbackQuery() != null && callbackData != null) {
                // Maneja el evento de callback
                inicioCuestionario(chatId, callbackData);
            }

        } catch (Exception e) {
            sendText(chatId, "Ocurrió un error al procesar tu mensaje. Por favor intenta de nuevo.");
        }
    }

    private void processEmailInput(long chatId, String email) {
        sendText(chatId, "Recibo su Correo: " + email);
        estadoConversacion.remove(chatId);
        try {
            usuarioConectado = userService.getUserByEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (usuarioConectado == null) {
            sendText(chatId, "El correo no se encuentra registrado en el sistema, por favor contacte al administrador.");
        } else {
            usuarioConectado.setTelegramid(chatId);
            try {
                userService.updateUser(usuarioConectado);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendText(chatId, "Usuario actualizado con éxito! Envía /menu para iniciar el cuestionario.");
        }
    }

    private void sendMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Selecciona una sección:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<Cuestionario> cuestionarios = new CuestionarioService().getAllCuestionarios();

        for(Cuestionario c : cuestionarios) {
            rows.add(crearFilaBoton(c.getNombre(), "SECTION_"+c.getId()));
        }

//        rows.add(crearFilaBoton("Sección 1", "SECTION_1"));
//        rows.add(crearFilaBoton("Sección 2", "SECTION_2"));
//        rows.add(crearFilaBoton("Sección 3", "SECTION_3"));
//        rows.add(crearFilaBoton("Sección 4", "SECTION_4"));

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<InlineKeyboardButton> crearFilaBoton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        return row;
    }

    private void inicioCuestionario(long chatId, String section) {
        seccionActiva.put(chatId, section);
        indicePregunta.put(chatId, 0);
        enviarPregunta(chatId);
    }

    private void enviarPregunta(long chatId) {
        String seccion = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);
        String[] questions = preguntas.get(seccion);

        if (index < questions.length) {
            sendText(chatId, questions[index]);
        } else {
            sendText(chatId, "¡Has completado el cuestionario!");
            seccionActiva.remove(chatId);
            indicePregunta.remove(chatId);
        }
    }

    private void manejaCuestionario(long chatId, String response) {
        String section = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);

        // Verifica si estamos en la segunda pregunta de la cuarta sección
        if (section.equals("SECTION_4") && index == 1) {
            if (!isValidAge(response)) {
                sendText(chatId, "Por favor ingresa una edad válida (entre 0 y 120).");
                return;
            }
        }

        saveRespuesta(chatId, section, index, response);
        sendText(chatId, "Tu respuesta fue: " + response);
        indicePregunta.put(chatId, index + 1);

        enviarPregunta(chatId);
    }

    private boolean isValidAge(String response) {
        try {
            int age = Integer.parseInt(response);
            return age >= 0 && age <= 120;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void saveRespuesta(long chatId, String section, int index, String response) {
        Respuesta respuesta = new Respuesta(chatId, section, index, response);
        respuestaService.saveRespuesta(respuesta);
    }

    private void sendText(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
