import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static Map<String, String> items = new HashMap();
    private static List<String> itemsKey = new ArrayList<>();
    private static Map<String, Object> schemas = new HashMap();
    private static StringBuffer sb = new StringBuffer();
    private static String SWAGGER_PRO = " *       @OA\\\\Property(property=\"name\", type=\"ztype\",example=\"value\",description=\"手动输入\"),\n";
    private static String SWAGGER_ARR = " *       @OA\\\\Property(property=\"name\", type=\"array\",@OA\\\\Items(ref=\"#/components/schemas/nameBean\")),\n";
    private static String SWAGGER_REF = " *       @OA\\\\Property(property=\"name\", ref=\"#/components/schemas/nameBean\")),\n";
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

    public static String toSchema(String json, String name) {
        Map maps = (Map) JSON.parse(json);
        return SWAGGER_SCH.replaceFirst("name", name).replaceFirst("zProperty", toProperty(maps));
    }
    public static String toSchemaArr(String name,String arrname) {
        return SWAGGER_SCH_ARR.replaceFirst("name", name).replaceFirst("name", arrname);
    }

    public static String toSchema(Map maps,String name) {
        return SWAGGER_SCH.replaceFirst("name",name).replaceFirst("zProperty",toProperty(maps));
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

    private static String toProperty(Map<String, Object> map) {
        sb.setLength(0);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof JSONArray) {
                List arr = JSONArray.parseArray(JSON.toJSONString(entry.getValue()));
//                System.out.println(items.size());
                if (arr.isEmpty())
                {
                    return sb.toString();
                }
                items.put(entry.getKey(), arr.get(0).toString());
                itemsKey.add(entry.getKey());
//                if ("itemList".equals(entry.getKey()))
//                {
//                    System.out.println(items.size());
//                    System.out.println(JSON.toJSON(arr.get(0)));
//                }
                sb.append(SWAGGER_ARR.replace("name", entry.getKey()));
            } else if (entry.getValue() instanceof JSONObject) {
                schemas.put(entry.getKey(), entry.getValue());
                sb.append(SWAGGER_REF.replace("name", entry.getKey()));
            } else {
                sb.append(SWAGGER_PRO.replaceFirst("name", entry.getKey()).replaceFirst("ztype", getType(entry.getValue().getClass().getTypeName())).replaceFirst("value", filterEmoji(entry.getValue().toString())));
            }
        }

        return sb.toString();
    }

    public static void toSchema(){
        for (Map.Entry<String, Object> entry : schemas.entrySet()) {
            System.out.println(toSchema(entry.getValue().toString(), entry.getKey()+"Bean"));
        }
        int itemsNum = items.size();
        for (Map.Entry<String, String> entry : items.entrySet()) {
            if (JSON.parse(entry.getValue()) instanceof JSONArray) {
                List arr = JSONArray.parseArray(entry.getValue());
                if (arr.isEmpty())
                {
                    continue;
                }
                if (items.containsKey(entry.getKey()+"Item"))
                {
                    long time = System.nanoTime();
                    items.put(entry.getKey()+"Item"+time, arr.get(0).toString());
                    itemsKey.add(entry.getKey()+"Item"+time);

                    System.out.println(toSchemaArr(entry.getKey()+"Bean",entry.getKey()+"Item"+time+"Bean"));
                }
                else
                {
                    items.put(entry.getKey()+"Item", arr.get(0).toString());
                    itemsKey.add(entry.getKey()+"Item");

                    System.out.println(toSchemaArr(entry.getKey()+"Bean",entry.getKey()+"ItemBean"));
                }
            }
            else
            {
                System.out.println(toSchema(entry.getValue(), entry.getKey()+"Bean"));
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
            System.out.println(toSchema(items.get(itemsKey.get(i)), itemsKey.get(i)+"Bean"));
        }

        if (items.size() > itemsNum)
        {
            deal(itemsNum);
        }
    }

    public static void main(String[] args) {
        String json = "";

        System.out.println(toSwagger("getBubbleList", "获取用户聊天背景", "好友"));
        Map map = new HashMap();
        map.put("gameId","23887032");
        System.out.println(toSchema(map, "getBubbleListReq"));
        System.out.println(toSchema(json, "getBubbleListResp"));
        for (Map.Entry<String, Object> entry : schemas.entrySet()) {
            System.out.println(toSchema(entry.getValue().toString(), entry.getKey()+"Bean"));
        }
        for (Map.Entry<String, String> entry : items.entrySet()) {
            System.out.println(toSchema(entry.getValue(), entry.getKey()+"Bean"));
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
