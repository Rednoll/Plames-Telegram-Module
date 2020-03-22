package com.inwaiders.plames.modules.telegram.web.callback.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.inwaiders.plames.api.messenger.keyboard.KeyboardButton;
import com.inwaiders.plames.api.messenger.keyboard.MessengerKeyboard;
import com.inwaiders.plames.modules.telegram.domain.bot.TelegramBot;
import com.inwaiders.plames.modules.telegram.domain.profile.TelegramProfile;
import com.inwaiders.plames.modules.telegram.web.callback.TelegramCallback;

public class TelegramCallbackTextMessage extends TelegramCallback{

	@Override
	public String run(TelegramBot bot, JsonNode message) {
	
		JsonNode from = message.get("from");
		
		if(from != null && !from.isEmpty()) {
	
			String text = message.get("text").asText();
			long telegramId = from.get("id").asLong();
		
			TelegramProfile profile = TelegramProfile.getByTelegramId(telegramId);
		
			if(profile == null) {
			
				profile = TelegramProfile.create(telegramId);
					profile.addBot(bot);
			}

			if(profile.getKeyboard() != null) {
				
				MessengerKeyboard keyboard = profile.getKeyboard();
			
				KeyboardButton button = keyboard.getButtonByLabel(text);
			
				if(button != null) {
					
					button.action(profile);
				}
				else {
					
					bot.fromUser(profile, text);
				}
			}
			else {
			
				bot.fromUser(profile, text);
			}
		}
		
		return null;
	}

	@Override
	public String getType() {
		
		return "message/text";
	}
}
