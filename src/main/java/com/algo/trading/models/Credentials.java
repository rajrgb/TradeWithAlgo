package com.algo.trading.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Credentials {
@Id
@GeneratedValue
private int id;
private String name;
private String apiKey;
private String secret;
public Credentials() {
	super();
	// TODO Auto-generated constructor stub
}
public Credentials(int id, String name, String apiKey, String secret) {
	super();
	this.id = id;
	this.name = name;
	this.apiKey = apiKey;
	this.secret = secret;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getApiKey() {
	return apiKey;
}
public void setApiKey(String apiKey) {
	this.apiKey = apiKey;
}
public String getSecret() {
	return secret;
}
public void setSecret(String secret) {
	this.secret = secret;
}
@Override
public String toString() {
	return "Credentials [id=" + id + ", name=" + name + ", apiKey=" + apiKey + ", secret=" + secret + "]";
}


}
