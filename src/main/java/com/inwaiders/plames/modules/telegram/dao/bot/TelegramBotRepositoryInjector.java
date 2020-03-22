package com.inwaiders.plames.modules.telegram.dao.bot;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.telegram.domain.bot.TelegramBot;

@Service
public class TelegramBotRepositoryInjector {

	@Autowired
	private TelegramBotRepository repository;

	@PostConstruct
	private void inject() {
		
		TelegramBot.setRepository(repository);
	}
}
