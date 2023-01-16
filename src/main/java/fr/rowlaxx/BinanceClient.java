package fr.rowlaxx;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;

/**
 * This class represent the Binance Client,
 * abstracting the http communication
 * 
 * @author Théo LINDER
 */
public class BinanceClient {
	
	private static final BinanceClient INSTANCE = new BinanceClient();
	
	public static BinanceClient getDefault() {
		return INSTANCE;
	}
	
	private final HttpClient client;
	
	private BinanceClient() {
		this.client = HttpClient.newHttpClient();
	}
	
	/**
	 * Build the corresponding URI for retrieving the desired candles
	 * @param symbol the symbol
	 * @param interval the interval
	 * @param startDate the startDate
	 * @param endDate the endDate
	 * @param limit the limit
	 * @return the corresponding URI
	 */
	private static URI buildCandleUri(
			String symbol, Intervals interval, Long startDate, Long endDate, Integer limit) {
		
		final StringBuilder sb = new StringBuilder(128);
		final AtomicInteger count = new AtomicInteger(0);
		
		sb.append("https://www.binance.com/api/v3/klines");
		append(sb, "symbol", symbol, count);
		append(sb, "interval", interval, count);
		append(sb, "startDate", startDate, count);
		append(sb, "endDate", endDate, count);
		append(sb, "limit", limit, count);
		
		return URI.create(sb.toString());
	}
	
	/**
	 * Append the parameter to the query
	 * @param sb the StringBuilder
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @param count the parameter index
	 */
	private static void append(
			StringBuilder sb, String name, Object value, AtomicInteger count) {
		
		if (value == null)
			return;

		sb	.append(count.getAndIncrement() == 0 ? '?' : '&')
			.append(name)
			.append('=')
			.append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
	}
	
	/**
	 * Build the corresponding HttpRequest for retrieving the candle
	 * @param symbol the symbol
	 * @param interval the interval
	 * @param startDate the startDate
	 * @param endDate the endDate
	 * @param limit the limit
	 * @return the request
	 */
	private static HttpRequest buildCandleRequest(String symbol, Intervals interval, Long startDate, Long endDate, Integer limit) {
		return HttpRequest.newBuilder()
				.GET()
				.uri(buildCandleUri(symbol, interval, startDate, endDate, limit))
				.timeout(Duration.ofMillis(10_000))
				.build();
	}
	
	/**
	 * Convert a response into a list of candle
	 * @param sd the shared data
	 * @param response the response
	 * @return the list of candles
	 */
	private static List<Candle> getCandles(Candle.SharedData sd, HttpResponse<String> response) {
		final int statusCode = response.statusCode();
		
		if (statusCode < 200 || statusCode >= 300)
			throw RequestException.from(response);
		
		final JSONArray array = new JSONArray(response.body());
		final List<Candle> list = new ArrayList<>(array.length());
		
		for (Object e : array)
			list.add(Candle.from(sd, (JSONArray)e));
		
		return list;
	}
	
	/**
	 * Send an online request to binance.com to get the desired candle
	 * @param symbol
	 * @param limit
	 * @return the future of the candles list
	 */
	public Future<List<Candle>> getCandlesAsync(
			String symbol, Intervals interval) {
		
		return getCandlesAsync(symbol, interval, null, null, null);
	}
	
	/**
	 * Send an online request to binance.com to get the desired candle
	 * @param symbol
	 * @param interval
	 * @param limit
	 * @return the future of the candles list
	 */
	public Future<List<Candle>> getCandlesAsync (
			String symbol, Intervals interval, Integer limit) {
		
		return getCandlesAsync(symbol, interval, null, null, limit);
	}
	
	/**
	 * Send an online request to binance.com to get the desired candle
	 * @param symbol
	 * @param interval
	 * @param startDate
	 * @param endDate
	 * @param limit
	 * @return the future of the candles list
	 */
	public Future<List<Candle>> getCandlesAsync (
			String symbol, Intervals interval, Long startDate, Long endDate, Integer limit) {
		
		final Candle.SharedData sd = new Candle.SharedData(symbol, interval);
		final HttpRequest request = buildCandleRequest(
				symbol, interval, startDate, endDate, limit);
		
		final CompletableFuture<HttpResponse<String>> cfResponse = 
				client.sendAsync(request, BodyHandlers.ofString());
		
		final CompletableFuture<List<Candle>> cf = new CompletableFuture<>();
		
		cfResponse.whenComplete( (response, exception) -> {
			if (response == null)
				cf.completeExceptionally(exception);
			else try {
				cf.complete(getCandles(sd, response));
			} catch(Exception re) {
				cf.completeExceptionally(re);
			}
		});
	
		return cf;
	}
	
	/**
	 * Send an online request to binance.com to get the desired candle
	 * @param symbol
	 * @param interval
	 * @param startDate
	 * @param endDate
	 * @param limit
	 * @return the future of the candles list
	 */
	public List<Candle> getCandles (
			String symbol, Intervals interval, Long startDate, Long endDate, Integer limit)
			throws InterruptedException, IOException {
	
		final Candle.SharedData sd = new Candle.SharedData(symbol, interval);
		final HttpRequest request = buildCandleRequest(
				symbol, interval, startDate, endDate, limit);
		
		return getCandles(sd, client.send(request, BodyHandlers.ofString()));
	}
	
	/**
	 * Send an online request to binance.com to get the desired candle
	 * @param symbol
	 * @param interval
	 * @param limit
	 * @return the future of the candles list
	 */
	public List<Candle> getCandles (
			String symbol, Intervals interval, Integer limit) 
			throws InterruptedException, IOException {
	
		return getCandles(symbol, interval, null, null, limit);
	}
	
	/**
	 * Send an online request to binance.com to get the desired candle
	 * @param symbol
	 * @param interval
	 * @return the future of the candles list
	 */
	public List<Candle> getCandles (
			String symbol, Intervals interval)
			throws InterruptedException, IOException {
	
		return getCandles(symbol, interval, null, null, null);
	}
}
