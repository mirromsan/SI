package si;

import org.apache.spark.sql.Row;

import scala.Tuple2;

public class RatioCB {
	private int idMovie1;
	private int idMovie2;
	private int a;
	
	public RatioCB(int idMovie1, int idMovie2, int a) {		
		this.idMovie1 = idMovie1;
		this.idMovie2 = idMovie2;
		this.a = a;
		
	}

	public RatioCB(Tuple2<Row, Integer> x) {
		// TODO Auto-generated constructor stub
	}

	public int getIdMovie1() {
		return idMovie1;
	}

	public void setIdMovie1(int idMovie1) {
		this.idMovie1 = idMovie1;
	}

	public int getIdMovie2() {
		return idMovie2;
	}

	public void setIdMovie2(int idMovie2) {
		this.idMovie2 = idMovie2;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	@Override
	public String toString() {
		return "RatioCB [idMovie1=" + idMovie1 + ", idMovie2=" + idMovie2 + ", a=" + a + "]";
	}
	
	

}
