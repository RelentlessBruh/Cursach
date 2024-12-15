package project.signature;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;


/**
 * Главный класс приложения, реализующий графический интерфейс пользователя (GUI) для сканирования директорий
 * и поиска файлов с определенной сигнатурой.
 * Приложение использует JavaFX для создания GUI.
 */
public class Main extends Application {
    /**
     * Логгер для записи сообщений.1
     */
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Метка для отображения информации пользователю.
     */
    private Label label;

    /**
     * Выбранная пользователем директория.
     */
    private File dir = null;

    /**
     * Точка входа в приложение JavaFX.
     * Инициализирует и отображает основное окно приложения.
     *
     * @param primaryStage Главное окно приложения.
     */
    @Override
    public void start(Stage primaryStage) {
        label = new Label("Выберите каталог:");

        Button button_select = new Button("Выбрать каталог");
        Button button_check = new Button("Сканировать");
        Button button_add = new Button("Добавить в базу данных сигнатуру");


        button_select.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            dir = directoryChooser.showDialog(primaryStage);
            if (dir != null) {
                label.setText("The selected directory: " + dir.getAbsolutePath());
            }
        });

        button_check.setOnAction(event -> {
            if (dir == null || !dir.exists()) {
                label.setText("Каталог не выбран");
                logger.error("Attempt to analyze an unselected directory");
            } else {
                try {
                    DirScanner.analyzeDirectory(dir);


                    switch (DirScanner.getString()) {
                        case "Пустая папка" -> label.setText("Выбранная папка пустая");
                        case "Недостаточно прав для чтения" -> label.setText("Для анализа директории недостаточно прав");
                        case "Директория некорректная" -> label.setText("Директория некорректная");
                        case "Исполняемый(-й) файл(-ы) найден(-ы)" -> label.setText("Исполняемый(-й) файл(-ы) найден(-ы)");
                        default -> label.setText(DirScanner.getString());
                    }
                    if (DirScanner.getCnt() == 0) {
                        logger.info("No executable files found");
                    } else {
                        logger.info("{} executable files found", DirScanner.getCnt());
                        displayMetaData();
                    }
                } catch (RuntimeException e) {
                    logger.error("Unidentified error in the analysis");
                }
            }
        });

        button_add.setOnAction(event -> {
            displaySignatures();
        });

        VBox root = new VBox(10);
        root.getChildren().addAll(label, button_select,
                button_check, button_add);

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Выбор каталога");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Отображает окно с метаданными найденных файлов.
     * Окно содержит прокручиваемую область с метаданными и информацией о количестве найденных файлов.
     */
    public void displayMetaData() {
        Stage stage = new Stage();
        Label label = new Label();

        String str = DirScanner.getMetadata().toString().substring(1,
                DirScanner.getMetadata().toString().length() - 1);
        str = str.replace(',', ' ');

        String counter = "Количество найденных файлов: " + DirScanner.getCnt() + "\n\n";

        label.setText(counter + str);
        label.setWrapText(true);
        stage.setTitle("Метаданные");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(label);
        scrollPane.setFitToHeight(false);

        Scene metadataScene = new Scene(scrollPane, 400, 600);
        stage.setScene(metadataScene);
        logger.info("Displaying a window with metadata of executable files");
        stage.show();
        DirScanner.resetToZeroCnt();
    }

    /**
     * Отображает окно для добавления и просмотра сигнатур.
     * Позволяет пользователю ввести сигнатуру и расширение файла, а также просмотреть
     * существующие сигнатуры в базе данных.
     */
    public void displaySignatures() {
        logger.info("Opening a window to add a signature");
        Stage stage = new Stage();
        Label label = new Label();
        Label label_see = new Label();

        Button button = new Button("Добавить");
        Button button_see = new Button("Просмотреть сигнатуры из базы данных");

        label.setText("Введите название и начало сигнатуры");

        TextField signatureField = new TextField();
        signatureField.setPromptText("Введите сигнатуру (например: FFD8)");

        TextField extensionField = new TextField();
        extensionField.setPromptText("Введите расширение (например: jpg)");

        VBox root = new VBox(10);
        root.getChildren().addAll(label, signatureField, extensionField, button, button_see, label_see);

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Сигнатуры");
        stage.show();

        button.setOnAction(event -> {

            String signature = signatureField.getText().trim();
            String extension = extensionField.getText().trim();

            if (signature.isEmpty() || extension.isEmpty()) {
                label.setText("Введите сигнатуру и расширение");
                logger.error("No signature or extension has been entered");
            } else {

                Signature.addSignature(extension, signature);
                label.setText("Сигнатура добавлена: " + extension + " -> " + signature);
                logger.info("Signature added: {} -> {}", extension, signature);

                signatureField.clear();
                extensionField.clear();
            }
        });

        button_see.setOnAction(event -> {
            String str = Signature.getExtensions().toString().replace('{', ' ').replace('}', ' ').replace(',', '\n');
            label_see.setText(str);
            logger.info("Viewing signatures");
        });
    }

    /**
     * Точка входа в приложение.
     *
     * @param args Аргументы командной строки.
     */
    public static void main(String[] args) {
        logger.info("The beginning of the program");
        launch(args);
    }

    /**
     * Вызывается при завершении работы приложения.
     * Используется для освобождения ресурсов и выполнения завершающих операций.
     */
    public void stop() {
        logger.info("The work is completed");
    }
}