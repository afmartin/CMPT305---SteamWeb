package cmpt305.lab3;

import cmpt305.lab3.exceptions.APIEmptyResponse;
import cmpt305.lab3.stucture.Game;
import cmpt305.lab3.stucture.Genre;
import cmpt305.lab3.stucture.User;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
	User user;
	try {
	    user = User.getUser(76561198122982968l); //Random User;
	} catch (APIEmptyResponse ex) {
	    System.err.println("User not found.");
	    return;
	}
	System.out.println(user);
	Map<User, Long> friends = user.getFriends(); //Get the user's friends, and friend since info (unix timestamp long)
	System.out.printf("Friends with %d people:\n", friends.size());
	for(Entry<User, Long> friend : friends.entrySet()){
	    System.out.printf("\t%s\n\t\tSince: %s\n", friend.getKey(), new Date(friend.getValue() * 1000));
	}
	System.out.print("\nPress enter to continue...\n");
	(new Scanner(System.in)).nextLine();
	System.out.println(user);
	Map<Game, Long> games = user.getGames(); //Get the user's games, and playtime (in seconds?)
	//Note: it takes a while to analyze what each game is...
	//But once it knows the appid, it remembers it.
	//Session dependent
	System.out.printf("Owns %d games:\n", games.size());
	for(Entry<Game, Long> game : games.entrySet()){
	    System.out.printf("\t%s | Hours: %ss\n\t\tGenres: ", game.getKey(), game.getValue());
	    for(Genre g : game.getKey().genres){
		System.out.print(g + ", ");
	    }
	    System.out.println();
	}
    }

}
