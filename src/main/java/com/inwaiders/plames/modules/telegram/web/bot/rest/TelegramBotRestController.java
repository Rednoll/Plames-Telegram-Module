package com.inwaiders.plames.modules.telegram.web.bot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.modules.telegram.domain.bot.TelegramBot;

@RestController
@RequestMapping("api/tg/rest")
public class TelegramBotRestController {

	@Autowired
	private ObjectMapper mapper;
	
	@GetMapping(value = "/bots/{id}", produces = "application/json; charset=UTF-8")
	public ObjectNode get(@PathVariable long id) {
		
		TelegramBot bot = TelegramBot.getById(id);
	
		ObjectNode node = mapper.createObjectNode();
		
			node.put("id", bot.getId());
			node.put("name", bot.getName());
			node.put("webhook_address", bot.getWebhookAddress());
			node.put("api_address", bot.getApiAddress());
			node.put("active", bot.isActive());
			node.put("token", bot.getToken());
			
		return node;
	}
	
	@PostMapping(value = "/bots")
	public ObjectNode create(@RequestBody TelegramBot group) {

		group.save();

		return get(group.getId());
	}
	
	@PutMapping(value = "/bots/{id}") 
	public ResponseEntity save(@PathVariable long id, @RequestBody JsonNode node) {
		
		TelegramBot bot = TelegramBot.getById(id);
	
		if(bot == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
		
		if(node.has("name") && node.get("name").isTextual()) {
		
			bot.setName(node.get("name").asText());
		}
		
		if(node.has("webhook_address") && node.get("webhook_address").isTextual()) {
			
			bot.setWebhookAddress(node.get("webhook_address").asText());
		}
		
		if(node.has("api_address") && node.get("api_address").isTextual()) {
			
			bot.setApiAddress(node.get("api_address").asText());
		}
		
		if(node.has("active") && node.get("active").isBoolean()) {
			
			bot.setActive(node.get("active").asBoolean());
		}
		
		if(node.has("token") && node.get("token").isTextual()) {
			
			bot.setToken(node.get("token").asText());
		}
		
		bot.save();
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/bots/{id}")
	public ResponseEntity delete(@PathVariable long id) {
	
		TelegramBot bot = TelegramBot.getById(id);
		
		if(bot != null) {
			
			bot.delete();
		
			return new ResponseEntity<>(HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}