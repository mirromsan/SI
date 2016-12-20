package si;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.Authorization;
import twitter4j.conf.ConfigurationBuilder;

public class StreamingHelper {
	private static final String TWITTER_PROPS_FILE = "twitter.properties";
	private static Properties props;

	public static void configureTwitterCredentials() throws ClassNotFoundException, IOException {
		props = new Properties();
		InputStream input = StreamingHelper.class.getClassLoader().getResourceAsStream(TWITTER_PROPS_FILE);
		props.load(input);
	}

	public static Authorization getAuthority() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(props.getProperty("consumerKey").trim())
				.setOAuthConsumerSecret(props.getProperty("consumerSecret").trim())
				.setOAuthAccessToken(props.getProperty("accessToken").trim())
				.setOAuthAccessTokenSecret(props.getProperty("accessTokenSecret").trim());
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		return twitter.getAuthorization();
	}

	public static String[] getKeys() {
	return new String[] { "#PropuestaEnMovimiento"};
	}
}
