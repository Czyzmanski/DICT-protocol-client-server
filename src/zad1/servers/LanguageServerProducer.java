package zad1.servers;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LanguageServerProducer {

	public static void main(String[] args) {

		File[] files = new File(".").listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".txt");
			}

		});

		String classpath = Arrays.stream(((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs())
				.map(URL::getFile).collect(Collectors.joining(File.pathSeparator));
		String javaBinPath = System.getProperty("java.home") + "/bin/java";

		for (File f : files) {
			try {
				ProcessBuilder builder = new ProcessBuilder(javaBinPath, "-cp", classpath,
						LanguageServer.class.getName(), f.getName(), "localhost", "2004");
				builder.inheritIO().start();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}

	}

}
