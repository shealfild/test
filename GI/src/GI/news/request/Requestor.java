package GI.news.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class Requestor {

	public void requestTest() {
		   String clientId = "DNFYtxuiNqJPaKyJJfAn"; 
	        String clientSecret = "P76MRuhXSI"; 

	        String text = null;
	        String sort = null;
	        int start = 10;
	        int display = 100;
	        String keyword = "동성화인텍";
	        try {
	            text = URLEncoder.encode(keyword, "UTF-8");
	            sort = URLEncoder.encode("sim", "UTF-8");
	        } catch (UnsupportedEncodingException e) {
	            throw new RuntimeException("인코딩 에러",e);
	        }

	        String apiURL = "https://openapi.naver.com/v1/search/news.json?query=" + text + "&sort=" + sort + "&start=" + start;    
	        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml ���

	        Map<String, String> requestHeaders = new HashMap<>();
	        requestHeaders.put("X-Naver-Client-Id", clientId);
	        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
	        String responseBody = get(apiURL,requestHeaders);
	        
        	JSONObject newsObject = JSONObject.fromObject(responseBody);
        	String lastBuildDate = newsObject.getString("lastBuildDate");
        	int total = newsObject.getInt("total");
        	JSONArray newsItems = newsObject.getJSONArray("items");
        	System.out.println("search date : " + lastBuildDate);
        	System.out.println("search total : " + total + "\n");
        	for (int i = 0; i < newsItems.size(); i++) {
        		JSONObject newsItemObject = newsItems.getJSONObject(i);
        		String newsTitle = newsItemObject.getString("title");
        		if (newsTitle.indexOf(keyword) < 0) continue;
        		String newsDesc = newsItemObject.getString("description");
        		String newsLink = newsItemObject.getString("link");
        		String newsOriginLink = newsItemObject.getString("originallink");
        		String newsData = newsItemObject.getString("pubDate");
        		System.out.println(newsData + "\n" + newsTitle + "\n" + newsDesc + "\n" + newsLink + "\n" + newsOriginLink);
        		
        		try {
        			//사이트별 뉴스 콘텐츠 영역 정보 id가 필요 할듯 함 
        			Document doc = Jsoup.connect(newsOriginLink).get();
        			if (newsOriginLink.indexOf("www.itooza.com") >= 0) {
	        			Element contentsElement = doc.getElementById("article-body");
	        			String newsText = contentsElement.text();
	        			System.out.println(newsText + "\n\n");
        			} else {
	        			String newsText = doc.text();
	        			System.out.println(newsText + "\n\n");
        			}
				} catch (IOException e) {
				}
        	}
	}
	
	 private String get(String apiUrl, Map<String, String> requestHeaders){
	        HttpURLConnection con = connect(apiUrl);
	        try {
	            con.setRequestMethod("GET");
	            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
	                con.setRequestProperty(header.getKey(), header.getValue());
	            }

	            int responseCode = con.getResponseCode();
	            if (responseCode == HttpURLConnection.HTTP_OK) { // ���� ȣ��
	                return readBody(con.getInputStream());
	            } else { // ���� �߻�
	                return readBody(con.getErrorStream());
	            }
	        } catch (IOException e) {
	            throw new RuntimeException("API ��û�� ���� ����", e);
	        } finally {
	            con.disconnect();
	        }
	    }

	    private HttpURLConnection connect(String apiUrl){
	        try {
	            URL url = new URL(apiUrl);
	            return (HttpURLConnection)url.openConnection();
	        } catch (MalformedURLException e) {
	            throw new RuntimeException("API URL�� �߸��Ǿ����ϴ�. : " + apiUrl, e);
	        } catch (IOException e) {
	            throw new RuntimeException("������ �����߽��ϴ�. : " + apiUrl, e);
	        }
	    }

	    private String readBody(InputStream body){
	        InputStreamReader streamReader = null;
			try {
				streamReader = new InputStreamReader(body, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
			}
			
	        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
	            StringBuilder responseBody = new StringBuilder();

	            String line;
	            while ((line = lineReader.readLine()) != null) {
	                responseBody.append(line);
	            }

	            return responseBody.toString();
	        } catch (IOException e) {
	            throw new RuntimeException("API ������ �дµ� �����߽��ϴ�.", e);
	        }
	    }
}
