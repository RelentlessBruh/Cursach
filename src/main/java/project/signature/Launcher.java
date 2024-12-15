package project.signature;

/**
 * Класс Launcher предназначен для запуска приложения.
 *  Этот класс обходит проблему с модулями JavaFX, позволяя корректно запустить приложение
 */
public class Launcher {
    /**
     * Точка входа в приложение.
     * Запускает главный класс приложения {@link Main}.
     *
     * @param args Аргументы командной строки, передаваемые в {@link Main#main(String[])}.
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}