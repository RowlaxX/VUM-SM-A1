import java.util.List;
import java.util.Scanner;

import fr.rowlaxx.BinanceClient;
import fr.rowlaxx.Candle;
import fr.rowlaxx.Intervals;

public class Test {

	public static void main(String[] args) {
		out("Welcome to the binance candles retriever !");
		out("Author : Théo LINDER");
		
		out("==========================================");
		out("Build your request");
		out("==========================================");
		final String symbol = 
		in("Enter the desired symbol (ie BTCUSDT) : ", String.class);
		final Intervals interval = 
		in("Enter the desired interval (1s, 1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 8h, 12h, 1d, 3d, 1w, 1M) : ", Intervals.class);
		final int limit = 
		in("Enter the amount of candles needed : ", Integer.class);
		
		
		out("==========================================");
		out("Sending request");
		out("==========================================");
		final BinanceClient client = BinanceClient.getDefault();
		
		List<Candle> candles = null;
		Throwable cause= null;
		try{
			candles = client.getCandles(symbol, interval, limit);
		}catch(Exception e) {
			cause = e;
		}
		
		out("DONE");
		
		
		out("==========================================");
		out("Result");
		out("==========================================");
		
		if (cause != null)
			cause.printStackTrace();
		else
			candles.forEach(System.out::println);
		
	}
	
	private static void out(String m) {
		System.out.println(m);
	}
	
	private static final Scanner IN = new Scanner(System.in);
	
	@SuppressWarnings("unchecked")
	private static <T> T in(String m, Class<T> clazz) {
		out(m);
		
		if (clazz == Integer.class)
			return (T) Integer.valueOf(IN.nextInt());
		if (clazz == String.class)
			return (T) IN.nextLine();
		if (clazz == Intervals.class)
			return (T) Intervals.from(IN.nextLine());
		
		throw new IllegalArgumentException("Unexpected value: " + clazz);
	}
}
