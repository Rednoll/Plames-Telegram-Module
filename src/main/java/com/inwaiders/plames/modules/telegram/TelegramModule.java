package com.inwaiders.plames.modules.telegram;

import com.inwaiders.plames.api.application.ApplicationAgent;
import com.inwaiders.plames.domain.messenger.impl.MessengerImpl;
import com.inwaiders.plames.domain.module.impl.ModuleBase;
import com.inwaiders.plames.modules.telegram.domain.TelegramMessenger;

public class TelegramModule extends ModuleBase implements ApplicationAgent {

	private static TelegramModule instance = new TelegramModule();
	
	/*
	@Override
	public void init() {
		
		try {
			
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost uploadFile = new HttpPost("http://proxy.inwaiders.enterprises:8080/bot857437797:AAFpQ5zussMHXoVnDxvR67PFDVhqAS0291o/setWebhook");
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.addTextBody("url", "https://proxy.inwaiders.enterprises:8443/api/tg/callback/857437797:AAFpQ5zussMHXoVnDxvR67PFDVhqAS0291o", ContentType.TEXT_PLAIN);
	
				File certificate = new File("proxy.inwaiders.enterprises.crt");
				builder.addBinaryBody("certificate", new FileInputStream(certificate), ContentType.APPLICATION_OCTET_STREAM, certificate.getName());
	
			HttpEntity multipart = builder.build();
			uploadFile.setEntity(multipart);
			CloseableHttpResponse response = httpClient.execute(uploadFile);
		
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
			response.getEntity().writeTo(baos);
			
			System.out.println(new String(baos.toByteArray()));
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
	}
	*/
	
	@Override
	public void init() {
		
		MessengerImpl mess = MessengerImpl.getByType("tg");
	
		if(mess == null) {
			
			TelegramMessenger tgMessenger = new TelegramMessenger();
			
			tgMessenger.save();
		}
	}

	@Override
	public String getName() {
		
		return "Telegram Integration";
	}

	@Override
	public String getVersion() {
		
		return "1V";
	}

	@Override
	public String getDescription() {
		
		return "Интеграция с мессенджером \"Telegram\".";
	}

	@Override
	public String getType() {
		
		return "integration";
	}

	@Override
	public String getLicenseKey() {
		
		return null;
	}

	@Override
	public long getSystemVersion() {
		
		return 0;
	}

	@Override
	public long getId() {
		
		return 33412;
	}
	
	public static TelegramModule getInstance() {
		
		return instance;
	}

	@Override
	public String getDisplayName() {
		
		return "Telegram";
	}

	@Override
	public String getTag() {
		
		return "tg";
	}
}
