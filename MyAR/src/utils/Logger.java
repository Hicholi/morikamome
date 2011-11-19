package utils;

public class Logger {

	public static final boolean logging = true;
	
	public static void log(String text)
	{
		if (logging)
			System.out.println(text);
	}
}
