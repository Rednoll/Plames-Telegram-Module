package com.inwaiders.plames.modules.telegram.dao.profile;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.inwaiders.plames.modules.telegram.domain.profile.TelegramProfile;

public interface TelegramProfileRepository extends JpaRepository<TelegramProfile, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT p FROM TelegramProfile p WHERE p.telegramId = :id AND p.deleted != true")
	public TelegramProfile getByTelegramId(@Param("id") long telegramId);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT p FROM TelegramProfile p WHERE p.id = :id AND p.deleted != true")
	public TelegramProfile getOne(@Param(value = "id") Long id);
	
	@Override
	@Query("SELECT p FROM TelegramProfile p WHERE p.deleted != true")
	public List<TelegramProfile> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM TelegramProfile p WHERE p.deleted != true")
	public long count();
}
