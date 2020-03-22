package com.inwaiders.plames.modules.telegram.web.bot;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.inwaiders.plames.modules.telegram.domain.bot.TelegramBot;

@Controller("TelegramBotsPageController")
public class BotsPageController {

	@GetMapping("/tg/bots")
	public String mainPage(Model model) {
		
		List<TelegramBot> bots = TelegramBot.getAll();
		
		model.addAttribute("bots", bots);
		
		return "tg_bots";
	}
}