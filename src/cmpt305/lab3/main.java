package cmpt305.lab3;

import cmpt305.lab3.api.API;
import java.util.Map;
import java.util.HashMap;

public class main {

    public static void main(String[] args) {
	API a = API.GetFriendList;
	Map reqs = new HashMap();
	reqs.put(API.Reqs.steamid, "76561197960435530");
	Map data = a.getData(reqs);

	System.out.println(((java.util.ArrayList)((Map)data.get("friendslist")).get("friends")));
    }

}
