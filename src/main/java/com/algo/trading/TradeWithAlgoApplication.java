package com.algo.trading;

import java.net.URI;
import java.util.Arrays;
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
import com.algo.trading.models.User;
import com.algo.trading.services.TemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;

class MyThread implements Runnable{
	
	private TemplateService ts=new TemplateService();
	
	private double walletBalance=10000;
	
	private String prevSignal="nosignal";
	
	public void executeLongTrade(String signal, double price) {
		if(signal.equals("Buy")&& prevSignal.equals("nosignal")) {
			System.out.println("Long Trade executed at price= "+price);
			walletBalance=walletBalance-price;
			prevSignal="Buy";
		}
		else if(prevSignal.equals("Buy")&&signal.equals("Sell")) {
			System.out.println("Previous long trade ended at price= "+price);
			walletBalance=walletBalance+price;
			System.out.println("Profit= "+ (walletBalance-10000));
			prevSignal="nosignal";
		}
	}
	

	public void printPrices(double arr[]) {
		int size=arr.length;
		for(int i=0;i<size;i++) {
			System.out.print(arr[i]+" ");
		}
		System.out.println("");
	}
	
	public void run() {

		while(true) {
			if(AuthController.getUser()!=null) {
				break;
			}
			try {
				Thread.sleep(1000);
			}catch(Exception e) 
			{ System.out.println(e.getMessage());}
	}
		//Stock: SBI NSE
		
		String url= "https://api-v2.upstox.com/historical-candle/intraday/NSE_EQ%7CINE062A01020/1minute";
		URI uri=null;
		try {
		uri=new URI(url);
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		MultiValueMap<String, String> headers=new LinkedMultiValueMap<>();
		headers.add(HttpHeaders.ACCEPT, "application/json");
		headers.add("Api-Version", "2.0");
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer "+AuthController.getUser().getAccess_token());
		RequestEntity<Void> requestEntity=new RequestEntity<>(headers, HttpMethod.GET,uri);
		ResponseEntity<String> responseEntity=ts.exchange(requestEntity, String.class);
		String response=responseEntity.getBody();
		Ohlc ohlc=null;
		ObjectMapper objectMapper=new ObjectMapper();
		try {
			ohlc=objectMapper.readValue(response, Ohlc.class);
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Data inserted in ohlc object: "+ohlc);
		int multiplier=3;
		int period=22;
		
		ChandelierExit chandelierExit=new ChandelierExit();
		chandelierExit.setPeriod(period);
		chandelierExit.setMultiplier(multiplier);
		int size=period+2;
		double openPrices[]=new double[size];
		double highPrices[]=new double[size];
		double lowPrices[]=new double[size];
		double closingPrices[]=new double[size];
		double prevChandelierLong=0;
		double prevChandelierShort=0;
		String signal="nosignal";
		
		System.out.println("");
		for(int j=200;j>=0;j--) {
			System.out.println("Time: "+ohlc.getData().getCandles().get(j).get(0));
		
		for(int i=j;i<j+size;i++) {
			List<String> candle=ohlc.getData().getCandles().get(i);
			openPrices[i-j]=Double.parseDouble(candle.get(1));
			highPrices[i-j]=Double.parseDouble(candle.get(2));
			lowPrices[i-j]=Double.parseDouble(candle.get(3));
			closingPrices[i-j]=Double.parseDouble(candle.get(4));
		}

	
		
		chandelierExit.setHighPrices(highPrices);
		chandelierExit.setLowPrices(lowPrices);
		chandelierExit.setClosingPrices(closingPrices);
	
		double chandelierLong=chandelierExit.calChandelierExitLong();
		double chandelierShort=chandelierExit.calChandelierExitShort();
		
		
	
	    
		if(j<200) {
			
			if(prevChandelierShort>closingPrices[1] && chandelierShort<closingPrices[0])
				{ 
				   signal="Buy";
				   executeLongTrade(signal,closingPrices[0]);
				}
			if(prevChandelierLong<openPrices[1]&& chandelierLong>openPrices[0])
				{
				   	signal="Sell";
				   	executeLongTrade(signal, closingPrices[0]);
				}
		}
		
		prevChandelierLong=chandelierLong;
		prevChandelierShort=chandelierShort;
		
	System.out.println(signal+"\n");
		
		}
		System.out.println("Wallet Balance= "+walletBalance);
		System.out.println("Net Profit= "+ (walletBalance-10000));
}

}
@SpringBootApplication
public class TradeWithAlgoApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(TradeWithAlgoApplication.class, args);
		MyThread m1=new MyThread();
		Thread t1=new Thread(m1);
		
		t1.start();
		
		
		
		
		
		
		
		
		
		}
	}


