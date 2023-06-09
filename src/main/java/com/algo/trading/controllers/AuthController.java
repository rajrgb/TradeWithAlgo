package com.algo.trading.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.algo.trading.models.Credentials;
import com.algo.trading.models.User;
import com.algo.trading.repositories.CredentialsRepository;
import com.algo.trading.services.TemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class AuthController {

@Autowired
private TemplateService ts;
	
@Autowired
private CredentialsRepository	cr;

private static User user;

public static User getUser() {
	return user;
}
@GetMapping("/home")
public String home(Model m) {
	
	List<Credentials> list=cr.findAll();
	m.addAttribute("list",list);	
	return "home";
}

@GetMapping("/getToken")
public String getToken(@RequestParam("code") String code) throws URISyntaxException{
	
	Optional<Credentials> obj=cr.findById(1);
	Credentials c=obj.get();
	String key=c.getApiKey();
	String secret=c.getSecret();
	String RedUri="http://localhost:8080/getToken";
	String url="https://api-v2.upstox.com/login/authorization/token";
	URI uri=new URI(url);
	
	//body
	MultiValueMap<String, String> body=new LinkedMultiValueMap<>();
	body.add("code", code);
	body.add("client_id", key);
	body.add("client_secret", secret);
	body.add("redirect_uri", RedUri);
	body.add("grant_type", "authorization_code");
	
	//headers
	MultiValueMap<String, String> headers=new LinkedMultiValueMap<>();
	headers.add(HttpHeaders.ACCEPT, "application/json");
	headers.add("Api-Version", "2.0");
	headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
	
	//request
	RequestEntity<MultiValueMap<String, String>> requestEntity=new RequestEntity<>(body, headers, HttpMethod.POST,uri);
	ResponseEntity<String> responseEntity=ts.exchange(requestEntity, String.class);
	String response=responseEntity.getBody();
	
	//json string to object
	ObjectMapper objectMapper=new ObjectMapper();
	try {
		user=objectMapper.readValue(response, User.class);
	}catch(Exception e) {
		System.out.println(e.getMessage());
	}
	return "success";
}

@GetMapping("/dashboard")
public String dashboard() {
	if(user==null) return "redirect:home";
	return "dashboard";
}
}
