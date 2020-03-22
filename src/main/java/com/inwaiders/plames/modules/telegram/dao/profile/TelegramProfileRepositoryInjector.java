package com.inwaiders.plames.modules.telegram.dao.profile;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.telegram.domain.profile.TelegramProfile;

@Service
public class TelegramProfileRepositoryInjector {

	@Autowired
	private TelegramProfileRepository repository;

	@PostConstruct
	private void inject() {
		
		TelegramProfile.setRepository(repository);
	}
}
