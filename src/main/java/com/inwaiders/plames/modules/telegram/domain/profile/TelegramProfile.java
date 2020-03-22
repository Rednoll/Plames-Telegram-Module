package com.inwaiders.plames.modules.telegram.domain.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inwaiders.plames.api.messenger.MessengerException;
import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.domain.messenger.profile.impl.UserProfileBase;
import com.inwaiders.plames.modules.telegram.dao.profile.TelegramProfileRepository;
import com.inwaiders.plames.modules.telegram.domain.bot.TelegramBot;

@Entity
@Table(name = "telegram_profiles")
public class TelegramProfile extends UserProfileBase {

	private static transient TelegramProfileRepository repository;
	
	@Column(name = "telegram_id")
	private long telegramId = -1;

	@Column(name = "username")
	private String username = null;
	
	@ManyToMany(cascade = CascadeType.MERGE, mappedBy = "users", fetch = FetchType.EAGER)
	private List<TelegramBot> bots = new ArrayList<>();
			
	@JoinColumn(name = "priority_bot_id")
	@ManyToOne(cascade = CascadeType.ALL)
	private TelegramBot priorityBot = null;
	
	@Override
	public int hashCode() {
		
		return Objects.hash(getId(), deleted);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TelegramProfile other = (TelegramProfile) obj;
		return telegramId == other.telegramId && getId() == other.getId() && deleted == other.deleted;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void addBot(TelegramBot bot) {
		
		this.bots.add(bot);
		bot.getUsers().add(this);
		
		if(priorityBot == null) {
			
			priorityBot = bot;
		}
	}
	
	@Override
	public boolean receiveMessage(Message message) throws MessengerException {
		
		TelegramBot bot = pickBot();
		
		if(bot != null) {
			
			return bot.sendMessage(message);
		}
		
		return false;
	}
	
	public TelegramBot pickBot() {
		
		if(getPriorityBot() != null) return getPriorityBot();
		
		for(TelegramBot bot : getBots()) {
			
			if(bot.isActive()) {
				
				return bot;
			}
		}
		
		return null;
	}

	public void setTelegramId(long id) {
		
		this.telegramId = id;
	}
	
	public long getTelegramId() {
		
		return this.telegramId;
	}
	
	public List<TelegramBot> getBots() {
	
		return this.bots;
	}
	
	public void setPriorityBot(TelegramBot bot) {
		
		this.priorityBot = bot;
	}
	
	public TelegramBot getPriorityBot() {
		
		return this.priorityBot;
	}
	
	public void setUsername(String un) {
		
		this.username = un;
	}
	
	public String getUsername() {
		
		return this.username;
	}
	
	public String getHumanSign() {
		
		return "[tg] Username: "+getUsername()+" Id: "+getTelegramId();
	}
	
	@Override
	public String getMessengerType() {
		
		return "tg";
	}
	
	public void save() {
		
		if(!deleted) {
			
			repository.save(this);
		}
	}
	
	public void delete() {
		
		deleted = true;
		repository.save(this);
	}
	
	public static TelegramProfile create(long telegramId) {
		
		TelegramProfile profile = new TelegramProfile();
			profile.setTelegramId(telegramId);
		
		profile = repository.saveAndFlush(profile);
	
		return profile;
	}
	
	public static TelegramProfile getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static TelegramProfile getByTelegramId(long id) {
		
		return repository.getByTelegramId(id);
	}
	
	public static List<TelegramProfile> getAll() {
		
		return repository.findAll();
	}
	
	public static long getCount() {
		
		return repository.count();
	}
	
	public static void setRepository(TelegramProfileRepository rep) {
		
		repository = rep;
	}
}
