package net.pwing.races.util.math;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.util.math.EquationOperator;
import net.pwing.races.api.util.math.EquationResult;
import net.pwing.races.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquationUtil {

    public static EquationResult getEquationResult(Player player, String fullString) {
        fullString = MessageUtil.getPlaceholderMessage(player, fullString);
        try {
            return getEquationResult(fullString);
        } catch (ScriptException ex) {
            Bukkit.getLogger().warning("Attribute " + fullString + " failed to solve given equation.");
            ex.printStackTrace();

            return new EquationResult(EquationOperator.ADD, 0);
        }
    }

    public static EquationResult getEquationResult(String fullString) throws ScriptException{
        EquationOperator operator = EquationOperator.getOperator(fullString.charAt(0));
        fullString = fullString.substring(1);
        return new EquationResult(operator, parseEquation(fullString));
    }

    public static double parseEquation(String fullString) throws ScriptException {
        Pattern pattern = Pattern.compile("\\(.*?\\)");
        Matcher matcher = pattern.matcher(fullString);
        if (matcher.find()) {
            return solveEquation(matcher.group().subSequence(1, matcher.group().length() - 1).toString());
        }

        return NumberUtil.getDouble(fullString);
    }

    public static double solveEquation(String equation) throws ScriptException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        String result = engine.eval(equation).toString();
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
