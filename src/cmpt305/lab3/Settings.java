package cmpt305.lab3;

import org.json.JSONObject;

public class Settings{
	private static int maxRetries = 3,
			maxTimeoutMs = 30000;
	private static long retryTimeMs = 60000l;
	private static String gameDatabase = "games.json";
	private static boolean verbose = true;
	private static String apiKey = "";

	public static String getApiKey(){
		return apiKey;
	}

	public static void setApiKey(String str){
		apiKey = str;
		FileIO.saveSettings();
	}

	public static int getMaxRetries(){
		return maxRetries;
	}

	public static void setMaxRetries(int maxRetries){
		Settings.maxRetries = maxRetries;
		FileIO.saveSettings();
	}

	public static int getMaxTimeoutMs(){
		return maxTimeoutMs;
	}

	public static void setMaxTimeoutMs(int maxTimeoutMs){
		Settings.maxTimeoutMs = maxTimeoutMs;
		FileIO.saveSettings();
	}

	public static long getRetryTimeMs(){
		return retryTimeMs;
	}

	public static void setRetryTimeMs(long retryTimeMs){
		Settings.retryTimeMs = retryTimeMs;
		FileIO.saveSettings();
	}

	public static String getGameDatabase(){
		return gameDatabase;
	}

	public static void setGameDatabase(String gameDatabase){
		Settings.gameDatabase = gameDatabase;
		FileIO.saveSettings();
	}

	public static boolean isVerbose(){
		return verbose;
	}

	public static void setVerbose(boolean verbose){
		Settings.verbose = verbose;
		FileIO.saveSettings();
	}
}
