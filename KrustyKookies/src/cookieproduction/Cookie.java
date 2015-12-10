package cookieproduction;
import java.util.ArrayList;
import java.util.Map;

public class Cookie {
	private String cookieName;
	private Map<String, Integer> map;
	
	public Cookie(String cookieName, Map<String, Integer> map) {
		this.cookieName = cookieName;
		this.map = map;
	}
	
	public String getCookieName() {
		return cookieName;
	}
	
	public Map<String, Integer> getIngredients() {
		return map;
	}
	
	public ArrayList<String> madeOf() {
		ArrayList<String> list = new ArrayList<String>(map.keySet());
		return list;
	}
}