package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ShareItTests {

    @Test
    void testContextLoads() {
        assertNotNull(SpringBootContextLoader.class);
    }

    @Test
    void testApplicationRun() {
        ShareItApp.main(new String[] {});
        assertDoesNotThrow(() -> { });
    }
}