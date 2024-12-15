package project.signature;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для {@link DirScanner}.
 * Содержит набор тестов, проверяющих корректность работы методов класса {@link DirScanner} в различных ситуациях.
 */
class DirScannerTest {

    /**
     * Временная директория, создаваемая JUnit Jupiter перед каждым тестовым методом и удаляемая после его выполнения.
     * Предоставляется аннотацией {@link TempDir}.
     */
    @TempDir
    Path tempDir;

    /**
     * Метод, выполняемый перед каждым тестовым методом.
     * Сбрасывает счетчик найденных файлов и очищает список метаданных в классе {@link DirScanner}.
     */
    @BeforeEach
    void setUp() {
        DirScanner.resetToZeroCnt();
        DirScanner.getMetadata().clear();
    }

    /**
     * Метод, выполняемый после каждого тестового метода.
     * В данном случае не содержит действий, но может использоваться для освобождения ресурсов, если это необходимо.
     */
    @AfterEach
    void tearDown() {
    }

    /**
     * Тестирует метод {@link DirScanner#analyzeDirectory(File)} для случая пустой директории.
     * Ожидается, что {@link DirScanner#getString()} вернет "Пустая папка",
     * счетчик файлов {@link DirScanner#getCnt()} будет равен 0,
     * а список метаданных {@link DirScanner#getMetadata()} будет пустым.
     */
    @Test
    void testAnalyzeDirectory_EmptyDirectory() {
        DirScanner.analyzeDirectory(tempDir.toFile());
        assertEquals("Пустая папка", DirScanner.getString());
        assertEquals(0, DirScanner.getCnt());
        assertTrue(DirScanner.getMetadata().isEmpty());
    }

    /**
     * Тестирует метод {@link DirScanner#analyzeDirectory(File)} для случая передачи null вместо директории.
     * Ожидается, что {@link DirScanner#getString()} вернет "Директория некорректная".
     */
    @Test
    void testAnalyzeDirectory_NullDirectory() {
        DirScanner.analyzeDirectory(null);
        assertEquals("Директория некорректная", DirScanner.getString());
    }

    /**
     * Тестирует метод {@link DirScanner#analyzeDirectory(File)} для случая несуществующей директории.
     * Ожидается, что {@link DirScanner#getString()} вернет "Директория некорректная".
     */
    @Test
    void testAnalyzeDirectory_NonExistentDirectory() {
        File nonExistentDir = new File(tempDir.toFile(), "nonexistent");
        DirScanner.analyzeDirectory(nonExistentDir);
        assertEquals("Директория некорректная", DirScanner.getString());
    }

    /**
     * Тестирует метод {@link DirScanner#analyzeDirectory(File)} для случая передачи файла вместо директории.
     * Ожидается, что {@link DirScanner#getString()} вернет "Директория некорректная".
     *
     * @throws IOException Если возникает ошибка при создании временного файла.
     */
    @Test
    void testAnalyzeDirectory_NotDirectory() throws IOException {
        File file = Files.createFile(tempDir.resolve("file.txt")).toFile();
        DirScanner.analyzeDirectory(file);
        assertEquals("Директория некорректная", DirScanner.getString());
    }

    /**
     * Тестирует метод {@link DirScanner#analyzeFile(File)} для случая пустого файла.
     * Ожидается, что счетчик файлов {@link DirScanner#getCnt()} останется равным 0.
     *
     * @throws IOException Если возникает ошибка при создании временного файла.
     */
    @Test
    void testAnalyzeFile_EmptyFile() throws IOException {
        File emptyFile = Files.createFile(tempDir.resolve("empty.txt")).toFile();
        DirScanner.analyzeFile(emptyFile);
        assertEquals(0, DirScanner.getCnt());
    }
}