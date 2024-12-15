package project.signature;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для {@link Signature}.
 * Содержит набор тестов, проверяющих корректность работы методов класса {@link Signature}.
 */
class SignatureTest {

    /**
     * Тестирует метод {@link Signature#addSignature(String, String)}.
     * Проверяет, что сигнатура успешно добавляется в коллекцию и может быть получена с помощью {@link Signature#getExtensions()}.
     */
    @Test
    void testAddSignature() {
        Signature.addSignature("jpg", "FFD8");
        assertEquals("FFD8", Signature.getExtensions().get("jpg"));
    }

    /**
     * Тестирует метод {@link Signature#getExtensions()}.
     * Проверяет, что метод возвращает корректную коллекцию сигнатур,
     * включая предустановленные и добавленные в процессе теста.
     */
    @Test
    void testGetExtensions() {
        Signature.addSignature("png", "8950");
        assertTrue(Signature.getExtensions().containsKey("exe"));
        assertTrue(Signature.getExtensions().containsKey("png"));
        assertEquals("4D5A", Signature.getExtensions().get("exe"));
        assertEquals("8950", Signature.getExtensions().get("png"));
    }

    /**
     * Тестирует метод {@link Signature#addSignature(String, String)} на случай добавления дублирующей сигнатуры.
     * Проверяет, что при повторном добавлении сигнатуры для одного и того же расширения, старое значение перезаписывается новым.
     */
    @Test
    void test_doubleAddSignatures() {
        Signature.addSignature("png", "8950");
        Signature.addSignature("png", "8951");
        assertEquals("8951", Signature.getExtensions().get("png"));
    }
}