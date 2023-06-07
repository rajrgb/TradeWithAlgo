package com.algo.trading.models;

import java.util.List;

public class Data {
private List<List<String>> candles;

public Data() {
	super();
	// TODO Auto-generated constructor stub
}

public Data(List<List<String>> candles) {
	super();
	this.candles = candles;
}

public List<List<String>> getCandles() {
	return candles;
}

public void setCandles(List<List<String>> candles) {
	this.candles = candles;
}

@Override
public String toString() {
	return "Data [candles=" + candles + "]";
}

}
