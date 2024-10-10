package service.ms_search_engine.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JacksonUtilsTest {

    private TestObject testObject;

    @BeforeEach
    void setUp() {
        testObject = new TestObject("TestName", 25);
    }

    @Test
    void testObjectToJson() {
        String jsonString = JacksonUtils.objectToJson(testObject);
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("TestName"));
        assertTrue(jsonString.contains("25"));
    }

    @Test
    void testJsonToObject() {
        String jsonString = "{\"name\":\"TestName\",\"age\":25}";
        TestObject resultObject = JacksonUtils.jsonToObject(jsonString, TestObject.class);
        assertNotNull(resultObject);
        assertEquals("TestName", resultObject.getName());
        assertEquals(25, resultObject.getAge());
    }

    @Test
    void testMapToObject() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "TestName");
        map.put("age", 25);

        TestObject resultObject = JacksonUtils.mapToObject(map, TestObject.class);
        assertNotNull(resultObject);
        assertEquals("TestName", resultObject.getName());
        assertEquals(25, resultObject.getAge());
    }

    @Test
    void testObjectToJson_withInvalidObject() {
        Object invalidObj = new Object() {
            public String getName() throws JsonProcessingException {
                throw new JsonProcessingException("Invalid property") {};
            }
        };

        Exception exception = assertThrows(RuntimeException.class, () -> JacksonUtils.objectToJson(invalidObj));
        assertTrue(exception.getMessage().contains("Error converting object to JSON string"));
    }

    @Test
    void testJsonToObject_withInvalidJson() {
        String invalidJson = "{invalid json}";

        Exception exception = assertThrows(RuntimeException.class, () -> JacksonUtils.jsonToObject(invalidJson, TestObject.class));
        assertTrue(exception.getMessage().contains("Error converting JSON string to object"));
    }

    // 測試用的 Java 對象
    @Getter
    static class TestObject {
        private String name;
        private int age;

        public TestObject() {
        }

        public TestObject(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}