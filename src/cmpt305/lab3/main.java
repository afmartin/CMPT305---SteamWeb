package cmpt305.lab3;

import cmpt305.lab3.exceptions.APIEmptyResponse;
import cmpt305.lab3.gui.GUI;
import cmpt305.lab3.stucture.Game;
import cmpt305.lab3.stucture.Genre;
import cmpt305.lab3.stucture.User;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class main {
    public static void printGames(Map<Game, Long> games){
        System.out.printf("Owns %d games:\n", games.size());
	for(Entry<Game, Long> game : games.entrySet()){
	    System.out.printf("\t%s | Time: %ss\n\t\tGenres: ", game.getKey(), game.getValue());
	    for(Genre g : game.getKey().genres){
		System.out.print(g + ", ");
	    }
	    System.out.println();
	}
    }
    public static void printGameTime(User user){
        System.out.printf("Total Time: %ds\n", user.getGameTime());
        Map<Long, Genre> map = new TreeMap(Collections.reverseOrder());
        long totaltime = 0;
        for(Genre g : Genre.KNOWN){
            long time = user.getGameTime(g);
            totaltime+=time;
            if(time != 0)
                map.put(time, g);
        }
        for(Entry<Long, Genre> e : map.entrySet()){
            System.out.printf("\t%-25s: %-10s (%.2f%%)\n", e.getValue().toString(), e.getKey() + "s", (float)e.getKey()/totaltime*100);
        }
    }
    public static void printFriends(User user){
        Map<User, Long> friends = user.getFriends(); //Get the user's friends, and friend since info (unix timestamp long)
	System.out.printf("Friends with %d people:\n", friends.size());
	for(Entry<User, Long> friend : friends.entrySet()){
	    System.out.printf("\t%s\n\t\tSince: %s\n", friend.getKey(), new Date(friend.getValue() * 1000));
	}
    }
    public static void printComparison(User u1, User u2){
        System.out.printf("%s -> %s\n", u1.getName(), u2.getName());

        float totalScore = 0;

        Map<Float, Genre> map = new TreeMap(Collections.reverseOrder());

        for(Genre g : Genre.KNOWN){
            float
                    t1 = u1.getGameRatio(g),
                    t2 = u2.getGameRatio(g);
            totalScore += t1 * t2;
            if(t1 >= .01f && t2 >= .01f)
                map.put(t1 * t2, g);
        }
        System.out.printf("\tScore: %.2f%%\n", totalScore*100);

        for(Entry<Float, Genre> e : map.entrySet()){
            System.out.printf("\t\t%-25s %.2f%%\n", e.getValue().toString() + ":", e.getKey()*100);
        }
    }
    public static void main(String[] args) {
        Game.load();
	
	User user;
	try {
	    user = User.getUser(76561198122982968l); //Random User;
	} catch (APIEmptyResponse ex) {
	    System.err.println("User not found.");
	    return;
	}
	System.out.println(user);
        printGameTime(user);
        for(User u : user.getFriends().keySet()){
            System.out.println(u);
            printGameTime(u);
        }
        for(User u : user.getFriends().keySet()){
            printComparison(user, u);
        }

	//printGames(user.getGames()); //Get the user's games, and playtime in seconds
    }
}
