package cmpt305.lab3;

public class Settings{
	public static enum Avatar{
		None(null), Small("avatar"), Medium("avatarmedium"), Large("avatarfull");

		public final String API_KEY;

		private Avatar(String key){
			API_KEY = key;
		}
	}

	private static int maxRetries = 3,
			maxTimeoutMs = 30000;
	private static long retryTimeMs = 60000l;
	private static String gameDatabase = "games.json";
	private static boolean verbose = true;
	private static String apiKey = "";
	private static Avatar avatar = Avatar.None;

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

	public static Avatar getAvatar(){
		return avatar;
	}

	public static void setAvatar(Avatar avatar){
		Settings.avatar = avatar;
		FileIO.saveSettings();
	}
}
