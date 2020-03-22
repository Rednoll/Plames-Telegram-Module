package com.inwaiders.plames.modules.telegram.domain;

import javax.persistence.Entity;

import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.domain.messenger.impl.MessengerImpl;
import com.inwaiders.plames.modules.telegram.domain.bot.TelegramBot;
import com.inwaiders.plames.modules.telegram.domain.profile.TelegramProfile;

@Entity
public class TelegramMessenger extends MessengerImpl<TelegramProfile>{
	
	@Override
	public String getWebDescription() {
		 
		return "- "+PlamesLocale.getSystemMessage("messenger.discord.description.profiles", TelegramProfile.getCount())+"<br/>- "+PlamesLocale.getSystemMessage("messenger.discord.description.bots", TelegramBot.getCount());
	}

	@Override
	public String getName() {
		
		return "telegram";
	}
	
	@Override
	public String getType() {
		
		return "tg";
	}
}
