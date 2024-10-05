package service.ms_search_engine.utility;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class JacksonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 將 Map<String, Object> 轉換成指定的 Java 對象
     *
     * @param map   要轉換的 map
     * @param clazz 目標類型
     * @param <T>   類型參數
     * @return 轉換後的 Java 對象
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

    /**
     * 將對象轉換成 JSON 字符串
     *
     * @param obj Java 對象
     * @return JSON 字符串
     */
    public static String objectToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON string", e);
        }
    }

    /**
     * 將 JSON 字符串轉換成指定類型的 Java 對象
     *
     * @param json  JSON 字符串
     * @param clazz 目標類型
     * @param <T>   類型參數
     * @return 轉換後的 Java 對象
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON string to object", e);
        }
    }

    /**
     * 使用 Reflection 將 object 的所有欄位轉成 map, 如果有 JsonIgnore 會忽略
     */
    public static Map<String, Object> objectToMap(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        if (object == null) {
            return map;
        }
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(JsonIgnore.class)) {
                // 取得欄位名稱和對應的值
                String fieldName = field.getName();
                Object value = field.get(object);  // 取得欄位的值
                map.put(fieldName, value);
            }
        }
        return map;
    }
}
