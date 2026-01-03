package com.thisjowi.note.Utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EncryptionUtilSpringTest {

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Test
    public void testEncryptionWithSpringContext() {
        assertNotNull(encryptionUtil);
        
        String original = "Spring Secret Test";
        String encrypted = EncryptionUtil.encrypt(original);
        String decrypted = EncryptionUtil.decrypt(encrypted);
        
        assertEquals(original, decrypted);
    }
}
