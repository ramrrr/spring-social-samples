package org.springframework.social.movies.netflix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.social.oauth1.ProtectedResourceClientFactory;
import org.springframework.web.client.RestTemplate;

public class NetFlixTemplate implements NetFlixApi {
	
	private final RestTemplate restTemplate;

	private final String userBaseUrl;

	public NetFlixTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		this.restTemplate = ProtectedResourceClientFactory.create(apiKey, apiSecret, accessToken, accessTokenSecret);
		this.userBaseUrl = getUserBaseUrl();
	}

	public List<QueueItem> getDiscQueue() {
		Map<String, Object> resultMap = restTemplate.getForObject(userBaseUrl + QUEUE_PATH, Map.class);

		Map<String, Object> queueMap = (Map<String, Object>) resultMap.get("queue");
		List<Map<String, Object>> queueItemMaps = (List<Map<String, Object>>) queueMap.get("queue_item");
		List<QueueItem> queueItems = new ArrayList<QueueItem>();
		for (Map<String, Object> queueItemMap : queueItemMaps) {
			String id = String.valueOf(queueItemMap.get("id"));
			String title = String.valueOf(((Map) queueItemMap.get("title")).get("regular"));
			String releaseYear = String.valueOf(queueItemMap.get("release_year"));
			queueItems.add(new QueueItem(id, title, releaseYear));
		}
		return queueItems;
	}

	private String getUserBaseUrl() {
		Map<String, Map<String, Map<String, String>>> result = restTemplate.getForObject(CURRENT_USER_URL, Map.class);
		return result.get("resource").get("link").get("href");
	}

	private static final String CURRENT_USER_URL = "http://api.netflix.com/users/current?output=json";
	private static final String QUEUE_PATH = "/queues/disc?output=json";
}
