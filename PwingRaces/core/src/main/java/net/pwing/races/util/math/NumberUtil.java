package net.pwing.races.util.math;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class NumberUtil {

	public static final Random RANDOM = ThreadLocalRandom.current();

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static boolean isRangedInteger(String str) {
		if (!str.contains("-"))
			return false;

		String[] split = str.split("-");
		return isInteger(split[0]) && isInteger(split[1]);
	}

	public static int getInteger(String str) {
		if (!isInteger(str))
			return 0;

		return Integer.parseInt(str);
	}

	public static int getRangedInteger(String str) {
		if (!str.contains("-"))
			return 0;

		String[] split = str.split("-");
		if (isInteger(split[0]) && isInteger(split[1]))
			return 0;

		return RANDOM.nextInt(Integer.parseInt(split[1]) + Integer.parseInt(split[0]));
	}

	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static boolean isRangedDouble(String str) {
		if (!str.contains("-"))
			return false;

		String[] split = str.split("-");
		return isDouble(split[0]) && isDouble(split[1]);
	}

	public static double getDouble(String str) {
		if (!isDouble(str))
			return 0;

		return Double.parseDouble(str);
	}

	public static double getRangedDouble(String str) {
		if (!str.contains("-"))
			return 0.0;

		String[] split = str.split("-");
		if (isDouble(split[0]) && isDouble(split[1]))
			return 0.0;

		double rangeMin = Double.parseDouble(split[0]);
		double rangeMax = Double.parseDouble(split[1]);
		return rangeMin + (rangeMax - rangeMin) * RANDOM.nextDouble();
	}

	public static boolean isFloat(String str) {
		try {
			Float.parseFloat(str);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static boolean isRangedFloat(String str) {
		if (!str.contains("-"))
			return false;

		String[] split = str.split("-");
		return isFloat(split[0]) && isFloat(split[1]);
	}

	public static float getFloat(String str) {
		if (!isFloat(str))
			return 0;

		return Float.parseFloat(str);
	}

	public static double getRangedFloat(String str) {
		if (!str.contains("-"))
			return 0.0;

		String[] split = str.split("-");
		if (isFloat(split[0]) && isFloat(split[1]))
			return 0.0;

		double rangeMin = Float.parseFloat(split[0]);
		double rangeMax = Float.parseFloat(split[1]);
		return rangeMin + (rangeMax - rangeMin) * RANDOM.nextDouble();
	}
}
