package ga.vabe.es;

import ga.vabe.es.model.SlowSql;
import ga.vabe.es.service.EsStatisticService;
import ga.vabe.es.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class Application implements CommandLineRunner {

	@Value("${export_path:./}")
	private String filepath;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HH_mm");


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) {
		long now = System.currentTimeMillis();
		long begin = now - valid(args);
		LocalDateTime _now = LocalDateTime.ofInstant(Instant.ofEpochMilli(now), ZoneId.systemDefault());
		LocalDateTime _begin = LocalDateTime.ofInstant(Instant.ofEpochMilli(begin), ZoneId.systemDefault());
		String filename = filepath + DATE_FORMATTER.format(_begin) + " - " + DATE_FORMATTER.format(_now) + ".xlsx";
		List<SlowSql> slowSql = esService.getSlowSql(begin, now);
		esService.generateXml(slowSql, filename);
		exit();
	}

	private long valid(String[] args) {
		if (args.length > 0) {
			long millis = TimeUtil.parse(args[0]);
			if (millis > 0L) {
				return millis;
			}
		}
		throw new IllegalArgumentException("请输入时间度量，例如 [ 1h30m ] ");
	}

	private static void exit() {
		System.exit(0);
	}

	final private EsStatisticService esService;

}
