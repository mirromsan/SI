package prueba;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.Duration;

import scala.Tuple2;

import com.mongodb.spark.MongoSpark;

public class TestAnalisisSentimiento {
	private static final String NAME = "TestAnalisisSentimientos";
	private static String PATH = "Test//Sentimiento";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.setProperty("hadoop.home.dir", "c:\\winutil\\");
		System.setProperty("spark.sql.warehouse.dir",
				"file:///${System.getProperty(\"user.dir\")}/spark-warehouse".replaceAll("\\\\", "/"));
		

		String master = System.getProperty("spark.master");
		// 1. Definir un SparkContext
		SparkConf sconf = new SparkConf().setAppName(NAME).setMaster(master ==null ? "local[2]":master);
		JavaSparkContext ctx = new JavaSparkContext(sconf);		
		ctx.setLogLevel("WARN");
		
		JavaStreamingContext ssc = new JavaStreamingContext(ctx, new Duration(10000));
		JavaDStream<String> stream = ssc.textFileStream(PATH);
		SQLContext sql = SQLContext.getOrCreate(ctx.sc());
		// 2. Resolver nuestro problema
		stream.foreachRDD(rdd -> {
			JavaRDD<Tuple2<String, Double>> data = rdd
					.map(x -> new Tuple2<String, Double>(x, StreamingSenses.getScore(x)));
			Dataset<Tuple2<String, Double>> dataset = sql.createDataset(data.rdd(),
					Encoders.tuple(Encoders.STRING(), Encoders.DOUBLE()));
			MongoSpark.save(dataset);
		});
		// 3. Abrir canal de datos
		ssc.start();
		ssc.awaitTermination();
		ssc.close();

	}

}
