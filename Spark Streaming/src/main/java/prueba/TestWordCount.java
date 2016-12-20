package prueba;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public final class TestWordCount {
	private static final String NAME = "JavaWordCount";
	private static final Pattern SPACE = Pattern.compile("");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.setProperty("hadoop.home.dir", "c:\\winutil\\");
		System.setProperty("spark.sql.warehouse.dir",
				"file:///${System.getProperty(\"user.dir\")}/spark-warehouse".replaceAll("\\\\", "/"));
		
		//1. Definir un sparkContext
		SparkConf sconf = new SparkConf().setAppName(NAME).setMaster("local[2]");		
		
		//String master = System.getProperty("spark.master");		
		JavaSparkContext ctx = new JavaSparkContext (sconf);
		
		//2. Resolver nuestro problema
		//JavaRDD<String> lines = ctx.textFile("pom.xml");
		JavaRDD<String> lines = ctx.textFile("Test//WordCount.txt");
		
		//Se realiza un flatmap sobre un iterador de listas de palabras
		JavaRDD<String> words = lines.flatMap(x -> ((List<String>) (Arrays.asList(SPACE.split(x)))).iterator());
		
		//Por cada palabra se crea una tupla {palabra:1}
		JavaPairRDD<String, Integer> ones = words.mapToPair(x -> new Tuple2<String, Integer>(x,1));
		
		//Por cada clave que se repita se suman los valores (queda al final un contador de cada palabra)
		JavaPairRDD<String, Integer> counts = ones.reduceByKey((i1, i2) -> i1 + i2);
		
		System.out.println(counts.collectAsMap());
		
		//3. Liberar recursos
		ctx.stop();
		ctx.close();

	}	
		
}
