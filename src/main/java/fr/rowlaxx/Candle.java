package fr.rowlaxx;

import java.util.Objects;

import org.json.JSONArray;

/**
 * This class represent a single Candle
 * It include the following data : 
 * - Shared Data (symbol & interval)
 * - Open time/price
 * - Close time/price
 * - High/Low
 * - Volume
 * - Number of trades
 * 
 * @See https://binance-docs.github.io/apidocs/spot/en/#kline-candlestick-datas
 */
public record Candle(
		SharedData shared,
		long openTime,//epoch millis
		long closeTime,//epoch millis
		double open,
		double close,
		double low,
		double high,
		double volume,
		int numberOfTrades
) {

	/**
	 * Return a Candle record from a Shared Data and a JSON
	 * @param shared the shared instance
	 * @param json the json array
	 * @return the Candle record
	 */
	public static Candle from(SharedData shared, JSONArray json) {
		return new Candle(
				shared,
				json.getLong(0),
				json.getLong(6),
				json.getDouble(1),
				json.getDouble(4),
				json.getDouble(3),
				json.getDouble(2),
				json.getDouble(5),
				json.getInt(8));
	}
	
	/**
	 * The goal of this class is to reduce the memory consemption by storing the data only once
	 * @author Théo LINDER
	 */
	public static record SharedData(String symbol, Intervals interval) {
		public SharedData {
			Objects.requireNonNull(symbol, "symbol may not be null");
			Objects.requireNonNull(interval, "interval may not be null");
		}
	}
	
	public Candle {
		Objects.requireNonNull(shared, "shared may not be null");
	}
	
	/**
	 * Shortcut method
	 * @return the symbol
	 * @see SharedData.symbol()
	 */
	public String symbol() {
		return shared.symbol;
	}
	
	/**
	 * Shortcut method
	 * @return the interval
	 * @see SharedData.interval()
	 */
	public Intervals interval() {
		return shared.interval;
	}
	
}
