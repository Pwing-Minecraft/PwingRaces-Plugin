package net.pwing.races.util.math;

import net.pwing.races.api.util.math.EquationOperator;
import net.pwing.races.api.util.math.EquationResult;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquationUtil {

    public static EquationResult getEquationResult(String fullString) {
        EquationOperator operator = EquationOperator.getOperator(fullString.charAt(0));
        fullString = fullString.substring(1);
        return new EquationResult(operator, parseEquation(fullString));
    }

    public static double parseEquation(String fullString) {
        Pattern pattern = Pattern.compile("\\(.*?\\)");
        Matcher matcher = pattern.matcher(fullString);
        if (matcher.find()) {
            return solveEquation(matcher.group().subSequence(1, matcher.group().length() - 1).toString());
        }

        return NumberUtil.getDouble(fullString);
    }

    public static double solveEquation(String equation) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        String result = null;
        try {
            result = engine.eval(equation).toString();
        } catch (ScriptException ex) {
            // Bukkit.getLogger().warning("Failed to sole the equation " + equation + ". Please make sure you entered it correctly.");
        }
        return result == null ? 0 : NumberUtil.getDouble(result);
    }

    public static double getValue(double prevValue, EquationResult result) {
        switch (result.getOperator()) {
            case ADD:
                return prevValue + result.getResult();
            case SUBTRACT:
                return prevValue - result.getResult();
            case MULTIPLY:
                return prevValue * result.getResult();
            case DIVIDE:
                return prevValue / result.getResult();
            case EQUAL:
            default:
                return result.getResult();
        }
    }
}
