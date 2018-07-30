package com.gy.mywifi.untils;


import java.util.List;
import java.util.Map;

/**
 * 对象判断
 * Created by Josn on 2017/11/10.
 */

public class ObjectUtils {

    /**
     * 判断字符串是否为空，
     * @param obj
     * @return
     */
    public static final boolean isNull(Object obj) {
        if(obj == null) return true;
        String type = obj.getClass().getSimpleName();
        switch (type) {
            case "String":
                String str = (String) obj;
                if (str == null /*|| str.isEmpty()*/ || str.equals("null") || str.equals(""))
                    return true;
                break;
            case "List":
            case "ArrayList":
            case "LinkedList":
                List list = (List) obj;
                if (list == null /*|| list.isEmpty()*/)
                    return true;
                break;
            case "Map":
            case "HashMap":
            case "LinkedHashMap":
            case "TreeMap":
                Map map = (Map) obj;
                if (map == null /*|| map.isEmpty()*/)
                    return true;
                break;
            default:
                /**
                 * 在判断一次
                 */
                if (null == obj || "".equals(obj)||"null".equals(obj)||"".equals(obj.toString().trim())) {
                    return true;
                }
                break;
        }
        return false;
    }

    public static final boolean isNotNull(Object obj){
        return !isNull(obj);
    }


}
