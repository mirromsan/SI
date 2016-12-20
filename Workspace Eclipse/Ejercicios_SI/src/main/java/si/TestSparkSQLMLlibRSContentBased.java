package si;

import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import scala.Tuple2;

public class TestSparkSQLMLlibRSContentBased {

	private static final String NAME = "JavaSQLMLlib2";
	private static final String URL = "jdbc:mysql://localhost:3306/";
	private static final String TABLE = "si_ejercicio1";

	public static void main(String[] args) throws AnalysisException {		
		System.setProperty("hadoop.home.dir", "c:\\winutil\\");
		System.setProperty("spark.sql.warehouse.dir",
				"file:///${System.getProperty(\"user.dir\")}/spark-warehouse".replaceAll("\\\\", "/"));
		
		String master = System.getProperty("spark.master");
		
		// 1. Definir un SparkContext
		SparkConf sconf = new SparkConf().setAppName(NAME).setMaster(master ==null ? "local[*]":master).set("spark.sql.crossJoin.enabled", "true");
		JavaSparkContext ctx = new JavaSparkContext(sconf);		
		
		SQLContext sql = SQLContext.getOrCreate(ctx.sc());
		
		Properties properties = new Properties();
		properties.setProperty("driver", "com.mysql.cj.jdbc.Driver");
		properties.setProperty("user", "root");
		properties.setProperty("password", "root");
		properties.setProperty("allowMultiQueries", "true");
		properties.setProperty("rewriteBatchedStatements", "true");
		
		Dataset<Row> dataset = sql.read().jdbc(URL, TABLE, properties);
		
		JavaRDD<RatioCB> aux = dataset.select(dataset.col("idUser").as("user1"), dataset.col("idMovie").as("movie1"))
				.join(dataset.select(dataset.col("idUser").as("user2"), dataset.col("idMovie").as("movie2")))
				.where("user1 = user2").where("movie1 <> movie2").select("movie1", "movie2").javaRDD()
				.mapToPair(x -> new Tuple2<Row, Integer>(x, 1)).reduceByKey((x, y) -> x + y).map(x -> new RatioCB(x));
		
		sql.createDataFrame(aux, RatioCB.class).write().jdbc(URL, TABLE + "CB", properties);
		
		ctx.stop();
		ctx.close();
	}
}