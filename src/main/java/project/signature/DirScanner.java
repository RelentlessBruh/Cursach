package project.signature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс DirScanner предназначен для сканирования директорий и поиска файлов с определенной сигнатурой.
 * Класс предоставляет методы для рекурсивного анализа директорий, анализа отдельных файлов,
 * сбора метаданных о найденных файлах и управления счетчиком найденных файлов.
 */
public class DirScanner {

    private static final Logger logger = LogManager.getLogger(DirScanner.class);
    /**
     * Строка, содержащая сообщение о результате последнего анализа.
     * По умолчанию: "Исполняемые файлы не найдены".
     */
    private static String answer = "Исполняемые файлы не найдены";

    /**
     * Счетчик количества найденных файлов с совпадающей сигнатурой.
     */
    private static int cnt;

    /**
     * Список строк, содержащий метаданные найденных файлов.
     */
    private static final List<String> metadata = new ArrayList<>();

    /**
     * Возвращает текущее значение счетчика найденных файлов.
     *
     * @return Количество найденных файлов.
     */
    public static int getCnt() {
        return cnt;
    }

    /**
     * Увеличивает счетчик найденных файлов на 1.
     */
    public static void addCnt() {
        cnt++;
    }

    /**
     * Сбрасывает счетчик найденных файлов до 0.
     */
    public static void resetToZeroCnt() {
        cnt = 0;
    }

    /**
     * Возвращает строку с результатом последнего анализа.
     *
     * @return Строка с результатом анализа.
     */
    public static String getString() {
        return answer;
    }

    /**
     * Устанавливает строку с результатом анализа.
     *
     * @param string Строка с результатом анализа.
     */
    public static void setString(String string) {
        answer = string;
    }

    /**
     * Рекурсивно анализирует указанную директорию и все ее поддиректории на наличие файлов с определенной сигнатурой.
     * Для каждого найденного файла вызывается метод {@link #analyzeFile(File)}.
     * Результат анализа сохраняется в поле {@link #answer}.
     *
     * @param directory Директория для анализа.
     * @throws RuntimeException Если произошла ошибка во время анализа файла.
     */
    public static void analyzeDirectory(File directory) {
        // Устанавливаем значение по умолчанию
        setString("OK");

        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            logger.error("The directory is incorrect");
            setString("Директория некорректная");
            return;
        }

        if (!directory.canRead()) {
            logger.error("Insufficient permissions to read the directory: {}", directory.getAbsolutePath());
            setString("Недостаточно прав для чтения");
            return;
        }

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            setString("Пустая папка");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                try {
                    analyzeFile(file);
                } catch (RuntimeException e) {
                    logger.error("Error analyzing the file: {}", file.getAbsolutePath(), e);
                }
            } else if (file.isDirectory()) {
                analyzeDirectory(file);
            }
        }
    }

    /**
     * Анализирует указанный файл на предмет совпадения первых двух байт с известными сигнатурами.
     * Если сигнатура найдена, увеличивается счетчик {@link #cnt}, и информация о файле добавляется в метаданные.
     *
     * @param file Файл для анализа.
     */
    public static void analyzeFile(File file) {
        if (!file.canRead()) {
            logger.warn("Access to the file is denied: {}", file.getAbsolutePath());
            return; // Пропускаем файл, к которому нет доступа
        }

        try (FileInputStream input = new FileInputStream(file)) {
            byte[] buffer = new byte[2];

            if (input.read(buffer) != -1) {

                String result = bytesToHex(buffer);
                for (String string : Signature.getExtensions().values()) {
                    if (string.equals(result)) {
                        logger.info("The executable file was found: {}", file.getName());
                        addCnt();
                        displayInfo(file);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading the file: {}", file.getAbsolutePath(), e);
        }
    }

    /**
     * Собирает и добавляет метаданные указанного файла в список {@link #metadata}.
     *
     * @param file Файл, для которого необходимо собрать метаданные.
     * @throws IOException Если произошла ошибка при чтении атрибутов файла.
     */
    public static void displayInfo(File file) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

        String result = "→ Название файла -> " + file.getName() + "\n" +
                "Полный путь -> " + file.getAbsolutePath() + "\n" +
                "Дата создания -> " + attr.creationTime() + "\n" +
                "Дата последнего использования -> " + attr.lastAccessTime() + "\n" +
                "Дата последнего изменения -> " + attr.lastModifiedTime() + "\n" +
                "Символическая ссылка -> " + attr.isSymbolicLink() + "\n" +
                "Размер -> " + attr.size() + " байт" + "\n\n";
        addMetadata(result);
    }


    /**
     * Преобразует массив байт в шестнадцатеричную строку.
     *
     * @param bytes Массив байт для преобразования.
     * @return Шестнадцатеричное представление массива байт.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * Возвращает список строк, содержащий метаданные найденных файлов.
     *
     * @return Список строк с метаданными.
     */
    public static List<String> getMetadata() {
        return metadata;
    }

    /**
     * Добавляет строку с метаданными в список {@link #metadata}.
     *
     * @param metadata Строка с метаданными для добавления.
     */
    public static void addMetadata(String metadata) {
        DirScanner.getMetadata().add(metadata);
    }
}