package com.inwaiders.plames.modules.telegram.domain.bot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.api.messenger.keyboard.KeyboardButton;
import com.inwaiders.plames.api.messenger.keyboard.MessengerKeyboard;
import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.api.utils.DescribedFunctionResult;
import com.inwaiders.plames.api.utils.DescribedFunctionResult.Status;
import com.inwaiders.plames.modules.telegram.dao.bot.TelegramBotRepository;
import com.inwaiders.plames.modules.telegram.domain.profile.TelegramProfile;

@Cache(region = "messengers-additionals-cache-region", usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "telegram_bots")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TelegramBot {

	private static transient TelegramBotRepository repository;
	
	private static transient ObjectMapper mapper = new ObjectMapper();
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id = null;
	
	@Column(name = "name")
	private String name = null;
	
	@Column(name = "token")
	private String token = null;
	
	@Column(name = "active")
	private boolean active = true;
	
	@JsonAlias("webhook_address")
	@Column(name = "webhook_address")
	private String webhookAddress = null;
	
	@JsonAlias("api_address")
	@Column(name = "api_address")
	private String apiAddress = null;
	
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "telegram_profiles_bots_mtm", joinColumns = @JoinColumn(name = "telegram_bot_id"), inverseJoinColumns = @JoinColumn(name = "telegram_profile_id"))
	private Set<TelegramProfile> users = new HashSet<>();
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	@Override
	public int hashCode() {
		
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TelegramBot other = (TelegramBot) obj;
		return active == other.active && Objects.equals(id, other.id) && Objects.equals(token, other.token);
	}
	
	public DescribedFunctionResult init() {
		
		DescribedFunctionResult result = new DescribedFunctionResult(Status.OK, "Bot successful inited!");
		DescribedFunctionResult subResult = null;
		
		subResult = initWebhook();
		
		if(subResult.getStatus() == Status.ERROR) {
			
			result = subResult;
		}
		
		return result;
	}
	
	private String parseKeyboard(MessengerKeyboard keyboard) {
	
		if(keyboard == null) return null;
		
		ObjectNode root = mapper.createObjectNode();
			root.put("resize_keyboard", true);
			root.put("one_time_keyboard", keyboard.isOnetime());
			
			ArrayNode jsonRows = mapper.createArrayNode();
			
			for(List<KeyboardButton> rows : keyboard.getButtonsMatrix()) {
			
				ArrayNode jsonButtons = mapper.createArrayNode();
				
				for(KeyboardButton button : rows) {
					
					jsonButtons.add(button.getLabel());
				}
				
				jsonRows.add(jsonButtons);
			}
			
			root.put("keyboard", jsonRows);
			
		return root.toString();
	}
	
	public boolean sendMessage(Message message) {
		
		if(!isActive()) return false;
		
		TelegramProfile target = (TelegramProfile) message.getReceiver();
		String text = message.getDisplayText();
		String keyboard = parseKeyboard(message.getKeyboard());
		
		return sendMessage(target.getTelegramId(), text, keyboard);
	}
	
	private boolean sendMessage(long chatId, String text, String keyboard) {
		
		ObjectNode jsonData = mapper.createObjectNode();
			jsonData.put("chat_id", chatId);
			jsonData.put("text", text);
			
			if(keyboard != null && !keyboard.isEmpty()) {
				
				jsonData.put("reply_markup", keyboard);
			}
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(methodUrl("sendMessage"));
			
			post.setEntity(new StringEntity(jsonData.toString(), ContentType.APPLICATION_JSON));
		
		try {
			
			CloseableHttpResponse response = httpClient.execute(post);
		
			return response.getStatusLine().getStatusCode() == 200;
		}
		catch(ClientProtocolException e) {
			
			e.printStackTrace();
		}
		catch(IOException e) {
			
			e.printStackTrace();
		}
			
		return false;
	}
	
	public DescribedFunctionResult initWebhook() {
			
		DescribedFunctionResult result = null;
		
		try {
			
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost uploadFile = new HttpPost(methodUrl("setWebhook"));
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.addTextBody("url", createWebhookUrl(), ContentType.TEXT_PLAIN);

				File certificate = new File("telegram-ssl.crt");
				builder.addBinaryBody("certificate", new FileInputStream(certificate), ContentType.APPLICATION_OCTET_STREAM, certificate.getName());

			HttpEntity multipart = builder.build();
			uploadFile.setEntity(multipart);
			
			CloseableHttpResponse response = httpClient.execute(uploadFile);
			response.close();
			
			boolean verifyResult = verifyWebhook();
			
			if(verifyResult) {
			
				result = new DescribedFunctionResult(Status.OK, "Webhook successful set");
			}
			else {

				result = new DescribedFunctionResult(Status.ERROR, "Webhook verification error");
			}
			
			return result;
		}
		catch(IOException e) {
		
			e.printStackTrace();
		}
		
		result = new DescribedFunctionResult(Status.ERROR, "Webhook set exception");
		
		return result;
	}
	
	public boolean verifyWebhook() {
		
		try {
			
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet get = new HttpGet(methodUrl("getWebhookInfo"));
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
						
			CloseableHttpResponse response = httpClient.execute(get);

				response.getEntity().writeTo(baos);

			response.close();
				
			String rawData = new String(baos.toByteArray());
		
			ObjectNode data = mapper.readValue(rawData, ObjectNode.class);
			
			ObjectNode dResult = (ObjectNode) data.get("result");

			if(dResult == null) return false;
			
			if(!dResult.has("url") || !dResult.get("url").asText().equals(createWebhookUrl())) return false;

			return true;
		}
		catch(ClientProtocolException e) {
			
			e.printStackTrace();
		}
		catch(IOException e) {
			
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean fromUser(TelegramProfile profile, String text) {
		
		if(!isActive()) return false;
	
		profile.fromUser(text);
		
		return true;
	}
	
	public String getDescription() {
		
		String result = "";
		
		result += PlamesLocale.getSystemMessage("telegram_bot.description.token", shortyToken());
		result += "<br/>";
		result += PlamesLocale.getSystemMessage("telegram_bot.description.web_hook_address", getWebhookAddress());
		result += "<br/>";
		result += PlamesLocale.getSystemMessage("telegram_bot.description.api_address", getApiAddress());
		
		return result;
	}
	
	public String shortyToken() {
		
		return token.length() > 12 ? token.substring(0, 6)+"..."+token.substring(token.length()-6) : token;
	}
	
	public String createWebhookUrl() {
		
		return getWebhookAddress()+"/api/tg/callback/"+getToken();
	}
	
	private String methodUrl(String methodName) {
		
		return getApiAddress()+"/bot"+getToken()+"/"+methodName;
	}

	public void setActive(boolean active) {
		
		this.active = active;
	}
	
	public boolean isActive() {
		
		return this.active;
	}
	
	public void setApiAddress(String address) {
		
		this.apiAddress = address;
	}
	
	public String getApiAddress() {
		
		return this.apiAddress;
	}
	
	public void setWebhookAddress(String address) {
		
		this.webhookAddress = address;
	}
	
	public String getWebhookAddress() {
		
		return this.webhookAddress;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return this.name;
	}
	
	public void setToken(String token) {
		
		this.token = token;
	}
	
	public String getToken() {
		
		return this.token;
	}
	
	public Set<TelegramProfile> getUsers() {
		
		return this.users;
	}
	
	public Long getId() {
		
		return this.id;
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
	
	public static TelegramBot create() {
		
		TelegramBot profile = new TelegramBot();
		
		profile = repository.saveAndFlush(profile);
	
		return profile;
	}
	
	public static TelegramBot getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static TelegramBot getByToken(String token) {
		
		return repository.getByToken(token);
	}
	
	public static List<TelegramBot> getAll() {
		
		return repository.findAll();
	}
	
	public static long getCount() {
		
		return repository.count();
	}
	
	public static void setRepository(TelegramBotRepository rep) {
		
		repository = rep;
	}
}
