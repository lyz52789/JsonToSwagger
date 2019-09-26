import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONForSwagger {
    private static String SWAGGER ="/**\n" +
            " * @OA\\Post(\n" +
            " *     path=\"/name.php\",\n" +
            " *     summary=\"zdes\",\n" +
            " *     tags={\"ztag\"},\n" +
            "SWAGGER_REQ" +
            "SWAGGER_RES" +
            " */";
    private static String SWAGGER_REQ =
                    " *     @OA\\\\RequestBody(\n" +
                    " *        @OA\\\\MediaType(\n" +
                    " *             mediaType=\"application/x-www-form-urlencoded\",\n" +
                    " *             @OA\\\\Schema(ref=\"#/components/schemas/nameReq\")\n" +
                    " *         ),\n" +
                    " *        @OA\\\\JsonContent(ref=\"#/components/schemas/nameReq\"),\n" +
                    " *     ),\n" ;
    private static String SWAGGER_RES =
                    " *     @OA\\\\Response(\n" +
                    " *          response=200,\n" +
                    " *          description=\"success\",\n" +
                    " *          @OA\\\\JsonContent(ref=\"#/components/schemas/nameResp\")\n" +
                    " *     ),\n" +
                    " * )\n"  ;

    private static String SWAGGER_SCH_ARR = "/**\n" +
                    "* @OA\\Schema(\n" +
                    "*   schema=\"name\",\n" +
                    "*   description=\"手动输入\",\n" +
                    "*   type=\"array\",\n" +
                    "*   @OA\\Items(type=\"array\",@OA\\Items(ref=\"#/components/schemas/name\"),description=\"手动输入\")\n" +
                    "* )\n" +
                    "*/\n"  ;

    private static String SWAGGER_SCH = "/**\n" +
            " * @OA\\Schema(\n" +
            " *   schema=\"name\",\n" +
            " *   description=\"手动输入\",\n" + "zProperty" +
            " * )\n" +
            " */";
    private static Map<String, String> items = new ConcurrentHashMap();
    private static List<String> itemsKey = new ArrayList<>();
    private static Map<String, Object> schemas = new HashMap();
    private static StringBuffer sb = new StringBuffer();
    private static String SWAGGER_PRO = " *       @OA\\\\Property(property=\"name\", type=\"ztype\",example=\"value\",description=\"手动输入\"),\n";
    private static String SWAGGER_ARR = " *       @OA\\\\Property(property=\"name\", type=\"array\",@OA\\\\Items(ref=\"#/components/schemas/znameBean\")),\n";
    private static String SWAGGER_TYPE_ARR = " *       @OA\\\\Property(property=\"name\", type=\"String[]\",example=\"value\",description=\"手动输入\"),\n";
    private static String SWAGGER_REF = " *       @OA\\\\Property(property=\"name\", ref=\"#/components/schemas/znameBean\"),\n";
//     *       @OA\Property(property="groupInfo", ref="#/components/schemas/GroupInfo"),


    public static String toSwagger(String name,String des,String tag) {
        return SWAGGER.replaceFirst("name", name).replaceFirst("ztag", tag).replaceFirst("zdes", des).replaceFirst("SWAGGER_REQ", toRequestBody(name)).replaceFirst("SWAGGER_RES", toResponse(name));
    }

    private static String toRequestBody(String name) {
        return SWAGGER_REQ.replace("name", name);
    }


    private static String toResponse(String name) {
        return SWAGGER_RES.replaceFirst("name", name);
    }

    public static String toSchema(String json, String name,String zname) {

        try{
//
            Map maps = (Map) JSON.parse(json);
            return SWAGGER_SCH.replaceFirst("name", name).replaceFirst("zProperty", toProperty(maps,zname));
        }
        catch (ClassCastException e)
        {
            System.out.println(name+":"+json);
            String key = name+"_item";
//            if (itemsKey.indexOf(key) == -1)
//            {
//                items.put(key, json);
//                itemsKey.add(key);
//
//            }
            return SWAGGER_SCH_ARR.replaceFirst("name", name).replaceFirst("name", key);
        }
    }
    public static String toSchemaArr(String name,String arrname) {
        return SWAGGER_SCH_ARR.replaceFirst("name", name).replaceFirst("name", arrname);
    }

    public static String toSchema(Map maps,String name) {
        return SWAGGER_SCH.replaceFirst("name",name).replaceFirst("zProperty",toProperty(maps,name));
    }

    public static String filterEmoji(String source) {
        if(source != null)
        {
            Pattern emoji = Pattern.compile ("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",Pattern.UNICODE_CASE | Pattern . CASE_INSENSITIVE ) ;
            Matcher emojiMatcher = emoji.matcher(source);
            if ( emojiMatcher.find())
            {
                source = emojiMatcher.replaceAll("*");
                return source ;
            }
            return source;
        }
        return source;
    }

    private static String toProperty(Map<String, Object> map,String name) {
        sb.setLength(0);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof JSONArray) {
                List arr = JSONArray.parseArray(JSON.toJSONString(entry.getValue()));
//                System.out.println(items.size());
                if (arr.isEmpty())
                {
                    return sb.toString();
                }
                try{
//
                    Map maps = (Map) JSON.parse(arr.get(0).toString());

                    items.put(name+"_"+entry.getKey(), arr.get(0).toString());
                    itemsKey.add(name+"_"+entry.getKey());
                    if (entry.getKey().indexOf(name) == -1)
                    {
                        sb.append(SWAGGER_ARR.replaceFirst("name", entry.getKey()).replace("zname", name+"_"+entry.getKey()));
                    }
                    else
                    {
                        sb.append(SWAGGER_ARR.replaceFirst("name", entry.getKey()).replace("zname", entry.getKey()));
                    }
                }
                catch (ClassCastException e)
                {
                    sb.append(SWAGGER_TYPE_ARR.replaceFirst("name", entry.getKey()).replaceFirst("value",arr.toString()));
                }

//                if ("itemList".equals(entry.getKey()))
//                {
//                    System.out.println(items.size());
//                    System.out.println(JSON.toJSON(arr.get(0)));
//                }




            } else if (entry.getValue() instanceof JSONObject) {
                schemas.put(entry.getKey(), entry.getValue());
                if (entry.getKey().indexOf(name) == -1)
                {
                    sb.append(SWAGGER_REF.replaceFirst("name", entry.getKey()).replace("zname", name+"_"+entry.getKey()));
                }
                else
                {
                    sb.append(SWAGGER_REF.replaceFirst("name", entry.getKey()).replace("zname", entry.getKey()));
                }
            } else {
                try {
                    sb.append(SWAGGER_PRO.replaceFirst("name", entry.getKey()).replaceFirst("ztype", getType(entry.getValue().getClass().getTypeName())).replaceFirst("value", filterEmoji(entry.getValue().toString())));
                }catch (NullPointerException e)
                {}
            }
        }

        return sb.toString();
    }

    public static void toSchema(String name){


        for (Map.Entry<String, Object> entry : schemas.entrySet()) {

            if (entry.getKey().indexOf(name) == -1)
            {
                System.out.println(toSchema(entry.getValue().toString(), name+"_"+entry.getKey()+"Bean", name+"_"+entry.getKey()+"Bean"));
            }
            else
            {
                System.out.println(toSchema(entry.getValue().toString(), entry.getKey()+"Bean", entry.getKey()+"Bean"));
            }

        }
        int itemsNum = items.size();
        Iterator<Map.Entry<String, String>> iterator = items.entrySet().iterator();

//        while(iterator.hasNext()){
//            Map.Entry<String, String> entry = iterator.next();
//            if (JSON.parse(entry.getValue()) instanceof JSONArray)
//            {}
//        }

        for (Map.Entry<String, String> entry : items.entrySet()) {
            if (JSON.parse(entry.getValue()) instanceof JSONArray) {

                System.out.println(entry.getValue());
                List arr = JSONArray.parseArray(entry.getValue());
                if (arr.isEmpty())
                {
                    continue;
                }
                if (items.containsKey(entry.getKey()+"Item"))
                {
                    long time = System.nanoTime();
                    items.put(name+"_"+entry.getKey()+"Item"+time, arr.get(0).toString());
                    itemsKey.add(name+"_"+entry.getKey()+"Item"+time);
                    if (entry.getKey().indexOf(name) == -1)
                    {
                        System.out.println(toSchemaArr(entry.getKey()+"Bean",name+"_"+entry.getKey()+"Item"+time+"Bean"));
                    }
                    else
                    {
                        System.out.println(toSchemaArr(entry.getKey()+"Bean",entry.getKey()+"Item"+time+"Bean"));
                    }
                }
                else
                {
                    items.put(name+"_"+entry.getKey()+"Item", arr.get(0).toString());
                    itemsKey.add(name+"_"+entry.getKey()+"Item");

                    if (entry.getKey().indexOf(name) == -1)
                    {
                        System.out.println(toSchemaArr(entry.getKey()+"Bean",name+"_"+entry.getKey()+"ItemBean"));
                    }
                    else
                    {
                        System.out.println(toSchemaArr(entry.getKey()+"Bean",entry.getKey()+"ItemBean"));
                    }
                }
            }
            else
            {
                if (entry.getKey().indexOf(name) == -1)
                {
                    System.out.println(toSchema(entry.getValue(), name+"_"+entry.getKey()+"Bean",name+"_"+entry.getKey()+"Bean"));
                }
                else
                {
                    System.out.println(toSchema(entry.getValue(), entry.getKey()+"Bean",entry.getKey()+"Bean"));
                }

            }
        }
        if (items.size() > itemsNum)
        {
            deal(itemsNum);
        }
    }

    private static void deal(int num){

        int itemsNum = items.size();
        for (int i = items.size() -1; i >= num; i--)
        {
            String value = items.get(itemsKey.get(i));
            String key = itemsKey.get(i)+"Bean";
            System.out.println(toSchema(value, key,key));
        }

        if (items.size() > itemsNum)
        {
            deal(itemsNum);
        }
    }

    private static String getType(String s) {
        switch (s) {
            case "java.lang.Integer":
                s = "integer";
                break;
            case "java.lang.Long":
                s = "long";
                break;
            case "java.lang.Float":
                s = "float";
                break;
            case "java.lang.Double":
                s = "double";
                break;
            default:
                s = "string";
        }
        return s;
    }
}
