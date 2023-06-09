package com.algo.trading;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.algo.trading.controllers.AuthController;
import com.algo.trading.models.Ohlc;
import com.algo.trading.services.TemplateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class Trade{
	
	private TemplateService ts=new TemplateService();
	
	private String prevSignal="nosignal";
	
	public void printPrices(double arr[]) {
		int size=arr.length;
		for(int i=0;i<size;i++) {
			System.out.print(arr[i]+" ");
		}
		System.out.println("");
	}
	public void placeOrder(String stockSymbol, Double price, String type) throws URISyntaxException{
		String url="https://api-v2.upstox.com/order/place";
		URI uri=new URI(url);
		
		//body
		String requestBody="{\"quantity\": 1,"
				+ "\"product\": \"D\","
				+ "\"validity\": \"DAY\","
				+ "\"price\": "+price+","
				+ "\"tag\": \"sbi\","
				+ "\"instrument_token\": \"NSE_EQ|"+stockSymbol+"\","
				+ "\"order_type\": \"LIMIT\","
				+ "\"transaction_type\": \""+type+"\","
				+ "\"trigger_price\": 580.1,"
				+ "\"is_amo\": "+true+"}";
		//headers
		MultiValueMap<String, String> headers=new LinkedMultiValueMap<>();
		headers.add(HttpHeaders.ACCEPT, "application/json");
		headers.add("Api-Version", "2.0");
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer "+AuthController.getUser().getAccess_token());

		//request
		RequestEntity<String> requestEntity=new RequestEntity<>(requestBody, headers, HttpMethod.POST,uri);
		ResponseEntity<String> responseEntity=ts.exchange(requestEntity, String.class);
		String response=responseEntity.getBody();
		System.out.println(response);
	}
	public void executeLongTrade(String signal, double price, String stockSymbol) throws URISyntaxException {
		if(signal.equals("Buy")&& prevSignal.equals("nosignal")) {
			System.out.println("Long Trade executed at price= "+price);
			placeOrder(stockSymbol,price, "BUY");
			prevSignal="Buy";
		}
		else if(prevSignal.equals("Buy")&&signal.equals("Sell")) {
			placeOrder(stockSymbol, price, "SELL");
			System.out.println("Previous long trade ended at price= "+price);
			prevSignal="nosignal";
		}
	}
	


	
	//fetches current OHLC data:
	public Ohlc fetchCurrentOhlc(URI uri) throws JsonMappingException, JsonProcessingException{
		MultiValueMap<String, String> headers=new LinkedMultiValueMap<>();
		headers.add(HttpHeaders.ACCEPT, "application/json");
		headers.add("Api-Version", "2.0");
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer "+AuthController.getUser().getAccess_token());
		RequestEntity<Void> requestEntity=new RequestEntity<>(headers, HttpMethod.GET,uri);
		ResponseEntity<String> responseEntity=ts.exchange(requestEntity, String.class);
		String response=responseEntity.getBody();
		ObjectMapper objectMapper=new ObjectMapper();
		Ohlc ohlc=objectMapper.readValue(response, Ohlc.class);
		return ohlc;
	}
	
	//starts trading:
	public void startTrade() throws InterruptedException, URISyntaxException, JsonProcessingException{
		while(true) {
			if(AuthController.getUser()!=null) {
				break;
			}
			Thread.sleep(1000);
	        }
		LocalTime currentTime=LocalTime.now();
		LocalTime desiredTime=LocalTime.of(10, 00);
		
		long delay=java.time.Duration.between(currentTime, desiredTime).toMillis();
		
		Thread.sleep(delay);
		//Stock: SBI NSE
		String stockSymbol="INE062A01020";
		String url= "https://api-v2.upstox.com/historical-candle/intraday/NSE_EQ%7C"+stockSymbol+"/1minute";
		URI uri=new URI(url);
		
		
		int multiplier=3;
		int period=22;
		
		ChandelierExit chandelierExit=new ChandelierExit();
		chandelierExit.setPeriod(period);
		chandelierExit.setMultiplier(multiplier);
		int size=period+2;
		double openingPrices[]=new double[size];
		double highPrices[]=new double[size];
		double lowPrices[]=new double[size];
		double closingPrices[]=new double[size];
		double prevChandelierLong=-1;
		double prevChandelierShort=-1;
		String signal="nosignal";
		
		System.out.println("");
		
		
		while(true){
			Ohlc ohlc=fetchCurrentOhlc(uri);
			System.out.println("Data inserted in ohlc object: "+ohlc);
			System.out.println("Time: "+ohlc.getData().getCandles().get(0).get(0));
		
		for(int i=0;i<size;i++) {
			List<String> candle=ohlc.getData().getCandles().get(i);
			openingPrices[i]=Double.parseDouble(candle.get(1));
			highPrices[i]=Double.parseDouble(candle.get(2));
			lowPrices[i]=Double.parseDouble(candle.get(3));
			closingPrices[i]=Double.parseDouble(candle.get(4));
		}

	
		
		chandelierExit.setHighPrices(highPrices);
		chandelierExit.setLowPrices(lowPrices);
		chandelierExit.setClosingPrices(closingPrices);
	
		double chandelierLong=chandelierExit.calChandelierExitLong();
		double chandelierShort=chandelierExit.calChandelierExitShort();
		
		
	
	    
		if(prevChandelierLong!=-1) {
			
			if(prevChandelierShort>closingPrices[1] && closingPrices[0]-chandelierShort>=chandelierShort-openingPrices[0])
				{ 
				   signal="Buy";
				   executeLongTrade(signal,closingPrices[0],stockSymbol);
				}
			if(prevChandelierLong<openingPrices[1] && openingPrices[0]-chandelierLong<= chandelierLong-closingPrices[0] )
				{
				   	signal="Sell";
				   	executeLongTrade(signal, closingPrices[0],stockSymbol);
				}
		}
		
		prevChandelierLong=chandelierLong;
		prevChandelierShort=chandelierShort;
		
	System.out.println(signal+"\n");
	
	long OneMin=60000;
	
	Thread.sleep(OneMin);
		}
	}
}

@SpringBootApplication
public class TradeWithAlgoApplication {
	public static void main(String[] args) throws InterruptedException, URISyntaxException, JsonProcessingException{
		SpringApplication.run(TradeWithAlgoApplication.class, args);
		Trade trade=new Trade();
		trade.startTrade();
		}
	}


