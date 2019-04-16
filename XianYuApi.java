import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @Description
 * @Author sally
 * @Date 2019-02-26
 */

public class XianYuApiUtil {
    private static final String JSV = "2.4.5";
    private static final String APP_KEY = "12574478";
    private static final String API = "mtop.taobao.idle.item.detail";
    private static final String VERSION = "";
    private static final String AntiCreep = "true";
    private static final String AntiFlood = "true";
    private static final String PREVENT_FALLBACK = "true";
    private static final String TYPE = "jsonp";
    private static final String DATA_TYPE = "jsonp";
    private static final String CALL_BACK = "mtopjsonp2";
    private static final String API_URL = "http://h5api.m.taobao.com/h5/mtop.taobao.idle.item.detail/4.0";
    private static final String STRING_SPERATOR_1 = "mtopjsonp2(";
    private static final String STRING_SPERATOR_2 = ")";
    private static final String STRING_EMPTY = "";
    private static Map<String, String> params = getParams();

    public static Map<String, String> getParams() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("jsv", JSV);
        params.put("appKey", APP_KEY);
        String timestamp = String.valueOf(new Date().getTime());
        params.put("t", timestamp);
        params.put("sign", "fake");
        params.put("api", API);
        params.put("v", VERSION);
        params.put("AntiCreep", AntiCreep);
        params.put("AntiFlood", AntiFlood);
        params.put("preventFallback", PREVENT_FALLBACK);
        params.put("type", TYPE);
        params.put("dataType", DATA_TYPE);
        params.put("callback", CALL_BACK);
        params.put("data", "");
        return params;
    }

    public static String getSign(String token, String timestamp, String appKey, String data) {
        try {
            String secret = token + "&" + timestamp + "&" + appKey + "&" + data;
            byte[] byteText = secret.getBytes(Charset.forName("UTF-8"));
            String originalString = new String(byteText, "UTF-8");
            String sign = DigestUtils.md5Hex(originalString);
            return sign;
        } catch (UnsupportedEncodingException e) {
            LogUtil.error("获取接口签名失败");
            return null;
        }
    }

    public static String getItemData(String url) {
        String[] queryParams = url.split("&");
        String itemData = "";
        for (String param : queryParams) {
            String paramName = param.split("=")[0];
            String paramValue = param.split("=")[1];
            if (paramName.equals("id")) {
                itemData = "{\"itemId\":\"" + paramValue + "\"}";
                return itemData;
            }
        }
        if (StringUtils.isBlank(itemData)) {
            LogUtil.error("传入url有误");
        }
        return null;
    }


    public static String getSecHandInfoCrawVO(String url) {
        BufferedReader in = null;
        HttpURLConnection secondConn = null;
        try {
            Map<String, String> cookieMap = HttpClientWithProxy.getCookieMap(API_URL, params);
            String token = cookieMap.get("_m_h5_tk").split("_")[0];
            String itemData = getItemData(url);
            String sign = getSign(token, params.get("t"), APP_KEY, itemData);
            params.put("data", URLEncoder.encode(itemData));
            params.put("sign", sign);
            secondConn = HttpClientWithProxy.doHttpUrlConGet(API_URL, params, cookieMap);
            secondConn.connect();
            in = new BufferedReader(new InputStreamReader(secondConn.getInputStream()));
            StringBuffer result = new StringBuffer();
            String content;
            while ((content = in.readLine()) != null) {
                content = content.replace(STRING_SPERATOR_1, STRING_EMPTY).replace(STRING_SPERATOR_2, STRING_EMPTY);
                result.append(content);
            }
            return result.toString();
        } catch (IOException e) {
            LogUtil.error("获取接口内容失败");
            return null;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LogUtil.error(e.getMessage());
            }
            secondConn.disconnect();
        }
    }
}
