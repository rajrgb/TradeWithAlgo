package com.algo.trading.models;

import java.util.Arrays;

public class User {
private String email;
private String exchanges[];
private String products[];
private String broker;
private String user_id;
private String user_name;
private String order_types[];
private String user_type;
private String access_token;
public User() {
	super();
	// TODO Auto-generated constructor stub
}
public User(String email, String[] exchanges, String[] products, String broker, String user_id, String user_name,
		String[] order_types, String user_type, String access_token) {
	super();
	this.email = email;
	this.exchanges = exchanges;
	this.products = products;
	this.broker = broker;
	this.user_id = user_id;
	this.user_name = user_name;
	this.order_types = order_types;
	this.user_type = user_type;
	this.access_token = access_token;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String[] getExchanges() {
	return exchanges;
}
public void setExchanges(String[] exchanges) {
	this.exchanges = exchanges;
}
public String[] getProducts() {
	return products;
}
public void setProducts(String[] products) {
	this.products = products;
}
public String getBroker() {
	return broker;
}
public void setBroker(String broker) {
	this.broker = broker;
}
public String getUser_id() {
	return user_id;
}
public void setUser_id(String user_id) {
	this.user_id = user_id;
}
public String getUser_name() {
	return user_name;
}
public void setUser_name(String user_name) {
	this.user_name = user_name;
}
public String[] getOrder_types() {
	return order_types;
}
public void setOrder_types(String[] order_types) {
	this.order_types = order_types;
}
public String getUser_type() {
	return user_type;
}
public void setUser_type(String user_type) {
	this.user_type = user_type;
}
public String getAccess_token() {
	return access_token;
}
public void setAccess_token(String access_token) {
	this.access_token = access_token;
}
@Override
public String toString() {
	return "User [email=" + email + ", exchanges=" + Arrays.toString(exchanges) + ", products="
			+ Arrays.toString(products) + ", broker=" + broker + ", user_id=" + user_id + ", user_name=" + user_name
			+ ", order_types=" + Arrays.toString(order_types) + ", user_type=" + user_type + ", access_token="
			+ access_token + "]";
}


}
