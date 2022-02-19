package at.hassmann.server.helper;

import at.hassmann.objects.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

/**
 * help functions for Json
 */
public class JsonHelper {

    /**
     * converts Object to Json
     * @param obj Object that should be converted to json
     * @return Json String of Package
     */
    public static String objToJson(Object obj){
        ObjectMapper objectMapper = new ObjectMapper();
        String packageJson = "";
        if(obj != null) {
            try {
                packageJson += objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                packageJson = "Error: JsonProcessingException -> " + e.getMessage();
            }
            return packageJson;
        }else{
            return null;
        }
    }

    /**
     * Json to List
     * @param input Json String
     * @return List from json, which has JsonProcessingException null
     */
    public static List<String> jsonInputLoadToList(String input){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(input, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Json to Map
     * @param input Json String
     * @return Map from json, when JsonProcessingException its null
     */
    public static Map<String, Object> jsonInputLoadloadToMap(String input){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(input, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * converts Object in Json
     * @param user Object that should be converted to json
     * @return Json String of Package
     */
    public static String userToJson(User user){
        return objToJson(user);
    }
}
