package project.signature;

import java.util.HashMap;

/**
 * Класс Signature хранит и управляет коллекцией сигнатур файлов.
 * Сигнатура файла представляет собой последовательность байт в начале файла,
 * которая идентифицирует его тип или формат.
 */
public class Signature {
    /**
     * HashMap для хранения сигнатур файлов.
     * Ключ - расширение файла (например, "exe", "jpg").
     * Значение - сигнатура файла в шестнадцатеричном формате (например, "4D5A", "FFD8").
     */
    private static final HashMap<String, String> extensions = new HashMap<>();

    static {
        addSignature("exe", "4D5A");
    }

    /**
     * Добавляет новую сигнатуру в коллекцию.
     * Если сигнатура для данного расширения уже существует, она будет перезаписана.
     *
     * @param name      Расширение файла (например, "jpg").
     * @param extension Сигнатура файла в шестнадцатеричном формате (например, "FFD8").
     */
    public static void addSignature(String name, String extension) {
        extensions.put(name, extension);
    }

    /**
     * Возвращает HashMap, содержащий все зарегистрированные сигнатуры файлов.
     *
     * @return HashMap с сигнатурами файлов.
     */
    public static HashMap<String, String> getExtensions() {
        return extensions;
    }
}