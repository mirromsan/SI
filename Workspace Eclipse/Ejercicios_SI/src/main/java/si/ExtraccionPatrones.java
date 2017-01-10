package si;

import java.util.Arrays;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.fpm.AssociationRules;
import org.apache.spark.mllib.fpm.FPGrowth;
import org.apache.spark.mllib.fpm.FPGrowthModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import scala.Tuple2;

public class ExtraccionPatrones {
	private static final String NAME = "JavaSQLMLlib2";
	private static final String URL = "jdbc:mysql://localhost:3306/si_ejercicio1";
	private static final String TABLE = "dates";

	private static final Double MIN_SUPPORT = 0.3;
	private static final Integer NUM_PARTITIONS = 3;
	private static final Double MIN_CONFIDENCE = 0.95;

	public static void main(String[] args) throws Exception {
		System.setProperty("hadoop.home.dir", "c:\\winutil\\");
		System.setProperty("spark.sql.warehouse.dir",
				"file:///${System.getProperty(\"user.dir\")}/spark-warehouse".replaceAll("\\\\", "/"));

		String master = System.getProperty("spark.master");

		// 1. Definir un SparkContext
		SparkConf sconf = new SparkConf().setAppName(NAME).setMaster(master == null ? "local[*]" : master)
				.set("spark.sql.crossJoin.enabled", "true");
		JavaSparkContext ctx = new JavaSparkContext(sconf);

		SQLContext sql = SQLContext.getOrCreate(ctx.sc());

		Properties properties = new Properties();
		properties.setProperty("driver", "com.mysql.jdbc.Driver");
		properties.setProperty("user", "root");
		properties.setProperty("password", "root");
		properties.setProperty("allowMultiQueries", "true");
		properties.setProperty("rewriteBatchedStatements", "true");

		Dataset<Row> dataset = sql.read().jdbc(URL, TABLE, properties);

		Dataset<Row> completa = dataset.select(dataset.col("idUser"), dataset.col("idMovie"));

		JavaRDD<Iterable<String>> aux = completa.javaRDD()
				.mapToPair(x -> new Tuple2<String, String>("" + x.getInt(0), "" + x.getInt(1)))
				.reduceByKey((z, y) -> (String) (z + " " + y)).map(x -> Arrays.asList(x._2.split(" ")));

		FPGrowth fp = new FPGrowth().setMinSupport(MIN_SUPPORT).setNumPartitions(NUM_PARTITIONS);
		FPGrowthModel<String> model = fp.run(aux);

		for (FPGrowth.FreqItemset<String> itemset : model.freqItemsets().toJavaRDD().collect()) {
			System.out.println("[" + itemset.javaItems() + "], " + itemset.freq());
		}

		AssociationRules arules = new AssociationRules().setMinConfidence(MIN_CONFIDENCE);

		JavaRDD<AssociationRules.Rule<String>> results = arules
				.run(new JavaRDD<>(model.freqItemsets(), model.freqItemsets().org$apache$spark$rdd$RDD$$evidence$1));

		for (AssociationRules.Rule<String> rule : results.collect()) {
			System.out.println(rule.javaAntecedent() + " => " + rule.javaConsequent() + ", " + rule.confidence());
		}

		ctx.stop();
		ctx.close();

	}

}
