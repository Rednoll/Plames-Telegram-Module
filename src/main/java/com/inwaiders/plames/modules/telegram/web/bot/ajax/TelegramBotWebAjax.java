package com.inwaiders.plames.modules.telegram.web.bot.ajax;

import java.util.concurrent.ForkJoinPool;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.inwaiders.plames.modules.telegram.domain.bot.TelegramBot;

@RestController
@RequestMapping("web/controller/ajax/tg/bots")
public class TelegramBotWebAjax {

	@PostMapping("/{id}/active")
	public ResponseEntity activeToggle(@RequestBody JsonNode json, @PathVariable(name="id") long botId) {
		
		if(!json.has("active") || !json.get("active").isBoolean()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
	
		boolean active = json.get("active").asBoolean();
	
		TelegramBot bot = TelegramBot.getById(botId);
		
		if(bot != null) {
			
			bot.setActive(active);
			bot.save();
		
			return new ResponseEntity(HttpStatus.OK);
		}
		
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	
	@GetMapping(value = "/{id}/init", produces = "text/plain;charset=UTF-8")
	public ResponseEntity init(@PathVariable(name="id") long botId) {
	
		TelegramBot bot = TelegramBot.getById(botId);
		
		if(bot != null) {
			
			ForkJoinPool.commonPool().submit(()-> {
				
				bot.init();
			});
			
			return new ResponseEntity<String>(HttpStatus.OK);
		}
		
		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	}
	
	@GetMapping(value = "/{id}/description", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> description(@PathVariable(name="id") long botId) {

		TelegramBot bot = TelegramBot.getById(botId);
		
		if(bot != null) {
			
			try {
				
				return new ResponseEntity<String>(bot.getDescription(), HttpStatus.OK);
			}
			catch(Exception e) {
				
				return new ResponseEntity<String>("Ошибка загрузки данных", HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	}
}