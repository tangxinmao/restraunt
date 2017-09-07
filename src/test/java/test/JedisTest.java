package test;

import org.springframework.util.SerializationUtils;
import com.socool.soft.util.JsonUtil;

public class JedisTest {

	public static void main(String[] args) {
//		Jedis jedis = new Jedis("127.0.0.1", 6379);
//		String key = "test3";
//		Integer value = 100;
//		jedis.set(SerializeUtil.serialize(key), SerializeUtil.serialize(value));
//		jedis.incr(SerializeUtil.serialize(key));
//		System.out.println(SerializeUtil.unserialize(jedis.get(SerializeUtil.serialize(key))));
//		jedis.close();
		System.out.println(JsonUtil.toJson(SerializationUtils.serialize("hello world")));
		System.out.println(JsonUtil.toJson(SerializationUtils.serialize("h")));
		System.out.println(JsonUtil.toJson(SerializationUtils.serialize("he")));
		System.out.println(JsonUtil.toJson(SerializationUtils.serialize("hello")));
		System.out.println(JsonUtil.toJson(SerializationUtils.serialize("hello ")));
	}
}
