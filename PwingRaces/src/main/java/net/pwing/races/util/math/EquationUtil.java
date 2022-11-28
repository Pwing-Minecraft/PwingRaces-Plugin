package net.pwing.races.util.math;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.pwing.races.api.util.math.EquationOperator;
import net.pwing.races.api.util.math.EquationResult;
import net.pwing.races.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquationUtil {
    private static final Pattern PATTERN = Pattern.compile("\\(.*?\\)");

    public static EquationResult getEquationResult(Player player, String fullString) {
        fullString = MessageUtil.getPlaceholderMessage(player, fullString);
        try {
            return getEquationResult(fullString);
        } catch (Throwable ex) {
            Bukkit.getLogger().warning("Equation for string " + fullString + " was unable to be solved.");
            ex.printStackTrace();

            return new EquationResult(EquationOperator.ADD, 0);
        }
    }

    public static EquationResult getEquationResult(String fullString) {
        // Check first char only since +(value) is a valid number (e.g. +1)
        Optional<EquationOperator> operator = EquationOperator.getOperator(fullString.charAt(0));
        return operator.map(equationOperator -> new EquationResult(equationOperator, parseEquation(fullString.substring(1)))).orElseGet(() -> new EquationResult(EquationOperator.EQUAL, NumberUtil.getDouble(fullString)));
    }

    public static double parseEquation(String fullString) {
        Matcher matcher = PATTERN.matcher(fullString);
        if (matcher.find()) {
            return solveEquation(matcher.group().subSequence(1, matcher.group().length() - 1).toString());
        }

        return NumberUtil.getDouble(fullString);
    }

    public static double solveEquation(String equation) {
        Expression expression = new ExpressionBuilder(equation).build();
        return expression.evaluate();
    }

    public static double getValue(double prevValue, EquationResult result) {
        return switch (result.getOperator()) {
            case ADD -> prevValue + result.getResult();
            case SUBTRACT -> prevValue - result.getResult();
            case MULTIPLY -> prevValue * result.getResult();
            case DIVIDE -> prevValue / result.getResult();
            default -> result.getResult();
        };
    }
}
