package prueba;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.SparkConf;

public class TestSparkSQL {
	

	private static final String NAME = "JavaWordCount";	
	private static final String paths = "Test//SparkSQL.csv";
	

	public static void main(String[] args) {
		System.setProperty("hadoop.home.dir", "c:\\winutil\\");
		System.setProperty("spark.sql.warehouse.dir",
				"file:///${System.getProperty(\"user.dir\")}/spark-warehouse".replaceAll("\\\\", "/"));
		// TODO Auto-generated method stub
		String master = System.getProperty("spark.master");
		// 1. Definir un SparkContext
		SparkConf sconf = new SparkConf().setAppName(NAME).setMaster(master ==null ? "local":master);
		//sconf.set("spark.driver.memory", "1.5g");
		JavaSparkContext ctx = new JavaSparkContext(sconf);
		SQLContext sql = SQLContext.getOrCreate(ctx.sc());
		// 2. Resolver nuestro problema
		Dataset<Row> dataset = sql.read().option("header", true).option("inferSchema", true).csv(paths);
		Dataset<Row> manufacturas = dataset.select(dataset.col("Manufacturer2")).distinct();
		dataset.printSchema();
		manufacturas.show();
		// 3. Liberar recursos
		ctx.stop();
		ctx.close();

	}

}
