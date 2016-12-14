package prueba;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;

import twitter4j.Status;
import twitter4j.auth.Authorization;

public class TestTwitterStreaming {
	private static final String NAME = "StreamingTwitter";
	private static String PATH = "Test//Twitter";
	
	public static void main(String[] args) throws Exception {
		System.setProperty("hadoop.home.dir", "c:\\winutil\\");
		System.setProperty("spark.sql.warehouse.dir",
				"file:///${System.getProperty(\"user.dir\")}/spark-warehouse".replaceAll("\\\\", "/"));
		
		// 1. Definir el objeto configurador de Spark

		String master = System.getProperty("spark.master");
		// 1. Definir un SparkContext
		SparkConf sconf = new SparkConf().setAppName(NAME).setMaster(master ==null ? "local[2]":master);
		
		JavaSparkContext ctx = new JavaSparkContext(sconf);
		
		ctx.setLogLevel("WARN");
		
		// 2. Twitter credentials from twitter.properties
		
		StreamingHelper.configureTwitterCredentials();
		
		Authorization twitter = StreamingHelper.getAuthority();
		
		String[] filters = StreamingHelper.getKeys();
		
		JavaStreamingContext ssc = new JavaStreamingContext(ctx, new Duration(10000));
		JavaReceiverInputDStream<Status> stream = TwitterUtils.createStream(ssc, twitter, filters);
		
		JavaDStream<String> filtered = stream.map(status -> "" + status.getText());
		filtered.foreachRDD(rdd -> {
			LocalDateTime lc = LocalDateTime.now();
			rdd.saveAsTextFile(PATH + Path.SEPARATOR + lc.toEpochSecond(ZoneOffset.UTC));
		});
		// 3. Abrir canal de datos
		ssc.start();
		ssc.awaitTermination();
	}
}
