import java.net.*;

/**
 * @Description
 * @Author sally
 * @Date 2019-03-26
 */

public class ZhuanZhuanUtil {
    private static final String API_URL = "http://zhuan.58.com/zz/transfer/getInfoById?";
    private static final String IMG_HOST = "http://pic5.zhuanstatic.com/zhuanzh/";


    public static String getSecHandInfoCrawVO(String url) {
        try {
            String realUrl = HttpClientWithProxy.getRedirectUrl(url);
            String[] queryParams = realUrl.split("&");
            String metric = "";
            String infoId = "";
            for (String param:queryParams){
                if (param.contains("infoId"))
                    infoId = param.split("=")[1];
                else if (param.contains("metric"))
                    metric = param.split("=")[1];
            }
            String queryStr = "metric=" + metric + "&" + "infoId=" + infoId;
            URL apiUrl = new URL(API_URL + queryStr);
            String result = HttpClientWithProxy.get(apiUrl.toString(), null, false, null);
            return result;
        } catch (MalformedURLException e) {
            LogUtil.error(e.getMessage());
            return null;
        }
    }

}
