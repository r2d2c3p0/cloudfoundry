package com.fc.cup.challenge;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

@SpringBootApplication
public class ChallengeApplication {

	public static void main(String[] args) {

		ClientCacheFactory clientCacheFactory = new ClientCacheFactory();
		clientCacheFactory.addPoolLocator("localhost", 10334);
		ClientCache clientCache = clientCacheFactory.create();
		Region region = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY)
				.create("FCCupChallenge");
		System.out.println("Occasion is "+ region.get("FCCupChallenge"));
		region.put("Na", "sodium");
		Object value = region.get("Na");
		System.out.println("Na value is "+ value);
	}
}
