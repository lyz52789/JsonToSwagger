import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;

public class Main {

    private static final CloseableHttpClient httpclient = HttpClients.createDefault();
    private static String URL = "http://coder.53site.com/WerewolfJP/PHPApi/temp/v/name.php";
//    private static String URL = "http://coder.53site.com/Werewolf/v/name.php";
    public static void main(String[] args) {
        String name = "groupGetProps";
        System.out.println(JSONForSwagger.toSwagger(name, "获取圣典匙石列表", "圣典"));
        Map map = new HashMap();
        map.put("userNo","48");
//        map.put("itemId","1");
//        map.put("itemNum","1");
//        map.put("checkUserNo","46");
//        map.put("canonNo","3");
//        map.put("type","0");
//        map.put("lastUserNo","7803405");
        map.put("cookie","/FPCQ076dl0oibELuO68f/Fj6Rfi7nIZckvdRiBZMEZeg=D");
        System.out.println(JSONForSwagger.toSchema(map, name+"Req"));
        String result = doPost(name,map);
        System.out.println("////"+result);
        System.out.println(JSONForSwagger.toSchema(doPost(name,map), name+"Resp",name));
        JSONForSwagger.toSchema(name);

    }
    public static String sendPost(String url) {
        HttpPost httppost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        String result = null;
        try {
            result = EntityUtils.toString(entity);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String doPost(String name,Map<String,String> map){

        HttpPost httpPost = null;
        String result = null;
        try{
            httpPost = new HttpPost(URL.replaceFirst("name",name));
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
            }
            if(list.size() > 0){
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,"utf-8");
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpclient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,"utf-8");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

}
