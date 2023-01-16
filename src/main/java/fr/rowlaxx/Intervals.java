package fr.rowlaxx;

import java.util.Objects;

/**
 * This class represent the list of available Intervals in binance
 * @see https://binance-docs.github.io/apidocs/spot/en/#public-api-definitions
 * 
 * @author Théo LINDER
 */
public enum Intervals {

	SECOND_1("1s"),
	MINUTE_1("1m"),
	MINUTE_3("3m"),
	MINUTE_5("5m"),
	MINUTE_15("15m"),
	MINUTE_30("30m"),
	HOUR_1("1h"),
	HOUR_2("2h"),
	HOUR_4("4h"),
	HOUR_6("6h"),
	HOUR_8("8h"),
	HOUR_12("12h"),
    DAY_1("1d"),
    DAY_3("3d"),
    WEEK_1("1w"),
    MONTH_1("1m");
	
	public static Intervals from(String toString) {
		for (Intervals interval : values())
			if (Objects.equals(toString, interval.tostring))
				return interval;
		throw new IllegalArgumentException("Bad interval : " + toString);
	}
    
	private final String tostring;
	
	private Intervals(String tostring) {
		this.tostring = Objects.requireNonNull(tostring);
	}
	
	@Override
	public String toString() {
		return tostring;
	}
}
