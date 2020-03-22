package com.inwaiders.plames.modules.telegram.web.bot.ajax;

import java.util.concurrent.ForkJoinPool;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.inwaiders.plames.api.utils.DescribedFunctionResult;
import com.inwaiders.plames.api.utils.DescribedFunctionResult.Status;
import com.inwaiders.plames.modules.telegram.domain.bot.TelegramBot;

@RestController
@RequestMapping("web/controller/ajax/long_poll/tg/bot")
public class TelegramBotWebAjaxLongPoll {

	@GetMapping(value = "/{id}/init", produces = "text/plain;charset=UTF-8")
	public DeferredResult<ResponseEntity<String>> init(@PathVariable(name="id") long botId) {
	
		DeferredResult<ResponseEntity<String>> output = new DeferredResult<>();
		
		TelegramBot bot = TelegramBot.getById(botId);
		
		if(bot != null) {

			ForkJoinPool.commonPool().submit(()-> {
				
				DescribedFunctionResult result = bot.init();
				
				if(result.getStatus() == Status.OK) {
					
					output.setResult(new ResponseEntity<String>(result.getDescription(), HttpStatus.OK));
				}
				else {
					
					output.setResult(new ResponseEntity<String>(result.getDescription(), HttpStatus.I_AM_A_TEAPOT));
				}
			});
		}
		else {
			
			output.setResult(new ResponseEntity<String>(HttpStatus.NOT_FOUND));
		}
		
		return output;
	}
}
