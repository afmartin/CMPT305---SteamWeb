package cmpt305.lab3;

import cmpt305.lab3.exceptions.APIEmptyResponse;
import cmpt305.lab3.stucture.Game;
import cmpt305.lab3.stucture.Genre;
import cmpt305.lab3.stucture.Pair;
import cmpt305.lab3.stucture.User;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class main{
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
		System.out.printf("Total Time: %s\n", formatTime(user.getGameTime()));
		Map<Long, Genre> map = new TreeMap(Collections.reverseOrder());
		for(Genre g : Genre.getKnown()){
			long time = user.getGameTime(g);
			if(time >= .01f){
				map.put(time, g);
			}
		}
		for(Entry<Long, Genre> e : map.entrySet()){
			System.out.printf("\t%-25s: %-15s (%.2f%%)\n", e.getValue().toString(), formatTime(e.getKey()), user.getGameRatio(e.getValue()) * 100);
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

		double totalScore = 0;

		Map<Double, Genre> map = new TreeMap(Collections.reverseOrder());

		for(Genre g : Genre.getKnown()){
			double t1 = u1.getGameRatio(g),
					t2 = u2.getGameRatio(g);
			if(t1 >= .0001f && t2 >= .0001f){
				double score = 2 * (t1 * t2) / (t1 + t2);
				totalScore += score;
				map.put(score, g);
			}
		}
		System.out.printf("\tScore: %.2f%%\n", totalScore * 100);

		for(Entry<Double, Genre> e : map.entrySet()){
			System.out.printf("\t\t%-25s %.2f%%\n", e.getValue().toString() + ":", e.getKey() * 100);
		}
	}

	private static final Pair<String, BigInteger>[] TIME_CONVERSION = new Pair[]{
		new Pair("m", BigInteger.valueOf(60)),
		new Pair("h", BigInteger.valueOf(24)),
		new Pair("d", BigInteger.valueOf(7)),
		new Pair("w", null)
	};

	private static String formatTime(long s){
		List<String> times = new ArrayList();
		BigInteger time = BigInteger.valueOf(s);
		for(Pair<String, BigInteger> p : TIME_CONVERSION){
			if(time.equals(BigInteger.ZERO)){
				break;
			}
			BigInteger cur = BigInteger.ZERO;
			BigInteger by = p.getValue();
			if(by != null){
				while(time.compareTo(by) >= 0){
					cur = cur.add(BigInteger.ONE);
					time = time.subtract(by);
				}
			}
			times.add(String.format("%d%s ", time, p.getKey()));
			time = cur;
		}
		if(times.isEmpty()){
			return "0m";
		}
		StringBuilder out = new StringBuilder();
		for(int i = times.size() - 1; i >= 0; --i){
			out.append(times.get(i));
		}
		return out.toString().trim();
	}

	public static void main(String[] args){
		FileIO.load();

		User user;
		try{
			user = User.getUser(76561198079246791l); //Random User;
		}catch(APIEmptyResponse ex){
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
	}
}
