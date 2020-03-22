package com.inwaiders.plames.modules.telegram.dao.bot;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.inwaiders.plames.modules.telegram.domain.bot.TelegramBot;

public interface TelegramBotRepository extends JpaRepository<TelegramBot, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT b FROM TelegramBot b WHERE b.id = :id AND b.deleted != true")
	public TelegramBot getOne(@Param(value = "id") Long id);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT b FROM TelegramBot b WHERE b.token = :tk AND b.deleted != true")
	public TelegramBot getByToken(@Param(value = "tk") String token);
	
	@Override
	@Query("SELECT b FROM TelegramBot b WHERE b.deleted != true")
	public List<TelegramBot> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM TelegramBot b WHERE b.deleted != true")
	public long count();
}
