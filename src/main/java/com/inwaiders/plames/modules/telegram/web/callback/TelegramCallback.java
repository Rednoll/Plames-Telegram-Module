package com.inwaiders.plames.modules.telegram.web.callback;

import com.fasterxml.jackson.databind.JsonNode;
import com.inwaiders.plames.modules.telegram.domain.bot.TelegramBot;

public abstract class TelegramCallback {

	public abstract String run(TelegramBot bot, JsonNode json);

	public abstract String getType();
}
