package com.algo.trading.models;

public class Ohlc {
private String status;
private Data data;
public Ohlc() {
	super();
	// TODO Auto-generated constructor stub
}
public Ohlc(String status, Data data) {
	super();
	this.status = status;
	this.data = data;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public Data getData() {
	return data;
}
public void setData(Data data) {
	this.data = data;
}
@Override
public String toString() {
	return "Ohlc [status=" + status + ", data=" + data + "]";
}

}
