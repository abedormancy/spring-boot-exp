package ga.vabe.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * 53 bits unique id:
 *
 * |--------|--------|--------|--------|--------|--------|--------|--------|
 * |00000000|00011111|11111111|11111111|11111111|11111111|11111111|11111111|
 * |--------|---xxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxx-----|--------|--------|
 * |--------|--------|--------|--------|--------|---xxxxx|xxxxxxxx|xxx-----|
 * |--------|--------|--------|--------|--------|--------|--------|---xxxxx|
 *
 * Maximum ID = 11111_11111111_11111111_11111111_11111111_11111111_11111111
 *
 * Maximum TS = 11111_11111111_11111111_11111111_111
 *
 * Maximum NT = ----- -------- -------- -------- ---11111_11111111_111 = 65535
 *
 * Maximum SH = ----- -------- -------- -------- -------- -------- ---11111 = 31
 *
 * It can generate 64k unique id per IP and up to 2106-02-07T06:28:15Z.
 *
 * 时间戳减去一个固定值，此方案最高可支持到2106年。
 *
 * 如果每秒6.5万个序列号不够怎么办？没关系，可以继续递增时间戳，向前“借”下一秒的6.5万个序列号。
 *
 * 同时还解决了时间回拨的问题。
 *
 * 机器标识采用简单的主机名方案，只要主机名符合host-1，host-2就可以自动提取机器标识，无需配置。
 *
 * 最后，为什么采用最多53位整型，而不是64位整型？这是因为考虑到大部分应用程序是Web应用，
 * 如果要和JavaScript打交道，由于JavaScript支持的最大整型就是53位，超过这个位数，JavaScript将丢失精度。
 * 因此，使用53位整数可以直接由JavaScript读取，而超过53位时，就必须转换成字符串才能保证JavaScript处理正确，
 * 这会给API接口带来额外的复杂度。这也是为什么新浪微博的API接口会同时返回id和idstr的原因。
 *
 *  -- from 廖雪峰
 */
public final class IdUtil {

	// private static final Pattern PATTERN_LONG_ID = Pattern.compile("^([0-9]{15})([0-9a-f]{32})([0-9a-f]{3})$");

	private static final Pattern PATTERN_HOSTNAME = Pattern.compile("^.*\\D+([0-9]+)$");

	private static final long OFFSET = LocalDate.of(2000, 1, 1).atStartOfDay(ZoneId.of("Z")).toEpochSecond();

	private static final long MAX_NEXT = 0b11111_11111111_111L;

	private static final long SHARD_ID = getServerIdAsLong();

	private static long offset = 0L;

	private static long lastEpoch = 0L;

	private final static char[] DIGITS = {
			'0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b',
			'c', 'd', 'e', 'f', 'g', 'h',
			'j', 'k', 'm', 'n', 'p', 'q',
			'r', 's', 'u', 'u', 'v', 'w',
			'x', 'y'
	};

	public static long nextId() {
		return nextId(System.currentTimeMillis() / 1000L);
	}

	public static String hexId() {
		return toUnsignedString(nextId(), 4);
	}

	private static String toUnsignedString(long i, int shift) {
		char[] buf = new char[64];
		int charPos = 64;
		long mask = -1 ^ (-1 << shift);
		do {
			buf[--charPos] = DIGITS[(int) (i & mask)];
			i >>>= shift;
		} while (i != 0);
		return new String(buf, charPos, (64 - charPos));
	}

	private static synchronized long nextId(long epochSecond) {
		if (epochSecond < lastEpoch) {
			// warning: clock is turn back:
			// logger.warn("clock is back: " + epochSecond + " from previous:" + lastEpoch);
			epochSecond = lastEpoch;
		}
		if (lastEpoch != epochSecond) {
			lastEpoch = epochSecond;
			reset();
		}
		offset++;
		long next = offset & MAX_NEXT;
		if (next == 0) {
			// logger.warn("maximum id reached in 1 second in epoch: " + epochSecond);
			return nextId(epochSecond + 1);
		}
		return generateId(epochSecond, next, SHARD_ID);
	}

	private static void reset() {
		offset = 0L;
	}

	private static long generateId(long epochSecond, long next, long shardId) {
		return ((epochSecond - OFFSET) << 21) | (next << 5) | shardId;
	}

	private static long getServerIdAsLong() {
		try {
			String hostname = InetAddress.getLocalHost().getHostName();
			Matcher matcher = PATTERN_HOSTNAME.matcher(hostname);
			if (matcher.matches()) {
				long n = Long.parseLong(matcher.group(1));
				return n & 7;
				// if (n >= 0 && n < 8) {
					// logger.info("detect server id from host name {}: {}.", hostname, n);
					// return n;
				// }
			}
		} catch (UnknownHostException e) {
			// logger.warn("unable to get host name. set server id = 0.");
		}
		return 0;
	}

	public static void main(String[] args) {
		IntStream.rangeClosed(1, 10).forEach(i -> {
			long id = nextId();
			System.out.println(id + " / " + toUnsignedString(id, 4) + " / " + toUnsignedString(id, 5));
		});

	}


}
