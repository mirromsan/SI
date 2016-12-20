package si;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;


public class TestSparkSQLMongoDB {
	private static final String NAME = "JavaSQLMongoDB";
	private static String[] paths = { "Test//sql//users.json","Test//sql//trainings.json" };

	public static void main(String[] args) {
		// 1. Definir un SparkContext
		System.setProperty("hadoop.home.dir", "c:\\winutil\\");
		System.setProperty("spark.sql.warehouse.dir",
				"file:///${System.getProperty(\"user.dir\")}/spark-warehouse".replaceAll("\\\\", "/"));
		
		String master = System.getProperty("spark.master");

		SparkConf sc = new SparkConf().setAppName(NAME).setMaster(master == null ? "local" : master)
				.set("spark.mongodb.input.collection", "spark").set("spark.mongodb.output.collection", "spark")
				.set("spark.mongodb.input.uri", "mongodb://root:root@ds057816.mlab.com:57816/test_spark")
				.set("spark.mongodb.output.uri", "mongodb://root:root@ds057816.mlab.com:57816/test_spark");

		JavaSparkContext ctx = new JavaSparkContext(sc);

		SQLContext sql = SQLContext.getOrCreate(ctx.sc());

		// 2. Resolver nuestro problema

		Dataset<Row> dataset1 = sql.read().option("inferSchema", true).json(paths[0]);
		Dataset<Row> dataset2 = sql.read().option("inferSchema", true).json(paths[1]);

		dataset1.printSchema();
		dataset2.printSchema();

		dataset1.createOrReplaceTempView("users");
		dataset2.createOrReplaceTempView("trainings");

		Dataset<Row> manufacturas = sql.sql("SELECT * FROM users INNER JOIN trainings ON users._id=trainings.idUser");

		manufacturas.show();

		// 3. Liberar recursos

		ctx.stop();
		ctx.close();

	}
		
}
