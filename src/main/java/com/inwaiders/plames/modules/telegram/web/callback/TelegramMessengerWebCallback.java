package com.inwaiders.plames.modules.telegram.web.callback;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.inwaiders.plames.modules.telegram.domain.bot.TelegramBot;
import com.inwaiders.plames.modules.telegram.web.callback.impl.TelegramCallbackTextMessage;

@RestController
@RequestMapping("api/tg")
public class TelegramMessengerWebCallback {
	
	private Map<String, TelegramCallback> callbacks = new HashMap<>();
	
	public TelegramMessengerWebCallback() {

		registerCallback(new TelegramCallbackTextMessage());
	}
	
	@PostMapping(value = "/callback/{token}")
	public ResponseEntity<String> callback(@RequestBody JsonNode json, @PathVariable(name="token") String token) {
	
		if(!json.has("message")) return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		
		TelegramBot bot = TelegramBot.getByToken(token);
	
		if(bot == null) {
			
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
		
		JsonNode message = json.get("message");
		
		TelegramCallback callbackScript = null;
		
		if(message.has("text")) {
			
			callbackScript = callbacks.get("message/text");
		}
		
		if(callbackScript != null) {
			
			String result = callbackScript.run(bot, message);
		
			if(result != null && result.isEmpty()) {
				
				return new ResponseEntity<String>(result, HttpStatus.OK); //Deprecated
			}
		}
		
		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}
	
	public void registerCallback(TelegramCallback callback) {
		
		callbacks.put(callback.getType(), callback);
	}
}