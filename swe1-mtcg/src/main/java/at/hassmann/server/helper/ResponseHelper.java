package at.hassmann.server.helper;

/**
 * Helper Functions of Response Class
 */
public class ResponseHelper {

    public static StringBuilder logLineBreak(String log){
        StringBuilder resString = new StringBuilder();
        for (char lo: log.toCharArray()){
            if(lo == ','){
                resString.append("\n");
            }else if(lo == '['){
                resString.append("\n");
            }else if(lo == ']'){
                resString.append("\n");
            }else {
                resString.append(lo);
            }
        }
        return resString;
    }
}
