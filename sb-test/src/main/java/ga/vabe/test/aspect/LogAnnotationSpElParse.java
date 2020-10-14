package ga.vabe.test.aspect;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义日志注解 SPEL 表达式解析
 */
public class LogAnnotationSpElParse {

    private static Pattern PATTERN = Pattern.compile("\\$\\(\\s*(#.+?)\\s*\\)");

    /**
     * SPEL表达式, key -> SPEL模板， value SPEL表达式<br>
     * 其中key中的值以如下字符串开头逻辑：<br>
     * ^( -> 方法参数对象<br>
     * $( -> 返回结束对象
     */
    private Map<String, Expression> spEls = new HashMap<>();


    public String getValue(String strTemplate , EvaluationContext context) {
        Map<String, String> strMap = new HashMap<>();
        StringSubstitutor sub = new StringSubstitutor(strMap, "$(", ")", '$');
        for (Map.Entry<String, Expression> next : spEls.entrySet()) {
            Object data = next.getValue().getValue(context);
            // result = replaceStr(result, next.getKey(), String.valueOf(data));
            strMap.put(next.getValue().getExpressionString(), String.valueOf(data));
        }
        return sub.replace(strTemplate);
    }

    public LogAnnotationSpElParse(String strTemplate) {
        if (strTemplate == null) {
            strTemplate = "";
        }
        Matcher matcher = PATTERN.matcher(strTemplate);
        while (matcher.find()) {
            String spElStr = matcher.group(1);
            SpelExpressionParser parser = new SpelExpressionParser();
            //获取表达式
            Expression expression = parser.parseExpression(spElStr);
            spEls.put(matcher.group(), expression);
            System.out.println(matcher.group() + ": " + matcher.group(1));
        }
    }

}