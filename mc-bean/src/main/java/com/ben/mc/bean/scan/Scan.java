package com.ben.mc.bean.scan;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ben.mc.bean.util.ExecutorServiceFatory;

public class Scan {

	private static final ExecutorService executorService = ExecutorServiceFatory.getExector();

	public static final Set<String> doScan(String path) throws IOException, InterruptedException, ExecutionException {
		Set<String> result = new HashSet<String>();
		path = path.replace(".", "/");
		path = path.replace(File.separator, "/");
		String[] paths = null;
		paths = path.split(",");
		String[] fullPaths = paths;
		//后期换并发模式
		for (int i = 0, len = paths.length; i < len; i++) {
			Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(paths[i].contains("*") ? paths[i].split("/")[0] : paths[i]);
			Set<URL> scanResult = new LinkedHashSet<URL>();
			while (urls.hasMoreElements()) {
				//			 URL u = urls.nextElement();
				scanResult.add(urls.nextElement());
				//			 System.out.println(u.getFile());
				// System.out.println(u.getFile().replace(File.separator, "/"));
				// System.err.println(u.getFile().substring(u.getFile().indexOf(paths[0])));
				// 第一段完成，遍历所有路径,再正则
				// jar
			}
			result.addAll(classMatchFilter(fullPaths[i], scanResult));
		}
		try {
			return result;
		} finally {
			executorService.shutdown();
		}
	}

	protected static final Set<String> classMatchFilter(String path, Set<URL> paths) throws InterruptedException, ExecutionException {
		final String pathPattern = "^(/.*/|.*/)?" + path.replace("/**", "(/.*)?") + "(/.*)?.class$";
		final int startIndex = (new File(Thread.currentThread().getContextClassLoader().getResource("").getPath())).getPath().replace(File.separator, "/").length() + 1;
		Set<URL> jarClassPaths = new HashSet<URL>();
		Set<URL> fileClassPaths = new HashSet<URL>();
		Set<String> result = new HashSet<String>();
		List<Future<Set<String>>> futures = new ArrayList<Future<Set<String>>>();
		final CountDownLatch countDownLatch = new CountDownLatch(2);
		Iterator<URL> urls = paths.iterator();
		URL u;
		while (urls.hasNext()) {
			u = urls.next();
			if ("jar".equals(u.getProtocol()))
				jarClassPaths.add(u);
			else
				fileClassPaths.add(u);
		}
		//过滤
		futures.add(executorService.submit(new Scan.FileFilter(jarClassPaths, pathPattern, 0, countDownLatch) {
			@Override
			public Set<String> doFilter(Set<URL> url, String pathPattern, int startIndex) throws IOException {
				return jarTypeFilter(pathPattern, url);
			}
		}));
		futures.add(executorService.submit(new Scan.FileFilter(fileClassPaths, pathPattern, startIndex, countDownLatch) {
			@Override
			public Set<String> doFilter(Set<URL> url, String pathPattern, int startIndex) {
				Iterator<URL> it = url.iterator();
				Set<String> result = new HashSet<String>();
				while (it.hasNext())
					result.addAll(fileTypeFilter(new File(it.next().getPath()), pathPattern, startIndex));
				return result;
			}
		}));
		countDownLatch.await();

		result.addAll(futures.get(0).get());
		result.addAll(futures.get(1).get());

		return result;
	}

	protected static final Set<String> jarTypeFilter(String pathPattern, Set<URL> urls) throws IOException {
		Set<String> result = new HashSet<String>();
		Iterator<URL> it = urls.iterator();
		URL u;
		while (it.hasNext()) {
			u = it.next();
			JarFile jarFile = new JarFile(new File(u.getPath().substring(0, u.getPath().lastIndexOf("!")).replaceAll("file:", "")));
			Enumeration<JarEntry> jars = jarFile.entries();
			while (jars.hasMoreElements()) {
				JarEntry jarEntry = jars.nextElement();
				if (jarEntry.getName().matches(pathPattern)) {
					result.add(jarEntry.getName().replace("/", "."));
				}
			}
		}
		return result;
	}

	protected static final Set<String> fileTypeFilter(File file, String pathPattern, int startIndex) {
		//Map<String, String> result = new WeakHashMap<String, String>();
		Set<String> result = new HashSet<String>();
		if (file.isFile()) {
			if (file.getPath().replace(File.separator, "/").matches(pathPattern))
				//result.put(file.getName(), file.getPath().substring(startIndex).replace(".class", "").replace(File.separator, "."));
				//文件添加返回
				result.add(file.getPath().substring(startIndex).replace(".class", "").replace(File.separator, "."));
			return result;
		}
		else if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				//目录递归
				result.addAll(fileTypeFilter(f, pathPattern, startIndex));
			}
		}
		return result;
	}

	static abstract class FileFilter implements Callable<Set<String>> {

		private String pathPattern;
		private Set<URL> urls;
		private int startIndex;
		private final CountDownLatch countDownLatch;

		public FileFilter(Set<URL> urls, String pathPattern, final CountDownLatch countDownLatch) {
			super();
			this.pathPattern = pathPattern;
			this.urls = urls;
			this.countDownLatch = countDownLatch;
		}

		public FileFilter(Set<URL> urls, String pathPattern, int startIndex, final CountDownLatch countDownLatch) {
			super();
			this.pathPattern = pathPattern;
			this.startIndex = startIndex;
			this.urls = urls;
			this.countDownLatch = countDownLatch;
		}

		public abstract Set<String> doFilter(Set<URL> url, String pathPattern, int startIndex) throws Exception;

		public Set<String> call() throws Exception {
			Set<String> result = doFilter(urls, pathPattern, startIndex);
			if (null != countDownLatch)
				countDownLatch.countDown();
			return result;
		}

	}

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

		// ClassPathXmlApplicationContext c=new ClassPathXmlApplicationContext();
		// c.getBean("a");

		// doScan("antlr/actions");
		//		Set<String> result = doScan("org/**/orm/hibernate4");
		//		Set<String> result = doScan("com");
		Set<String> result = doScan("javassist/**/annotation,com");
		Iterator<String> it = result.iterator();
		while (it.hasNext())
			System.err.println(it.next());

		if (true)
			return;
		WeakHashMap<String, List<String>> weakHashMap = new WeakHashMap<String, List<String>>();
		WeakHashMap<String, String> weakHashMap1 = new WeakHashMap<String, String>();
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		weakHashMap.put("1", list);
		weakHashMap1.put("x", "xxxxxxxxxx");
		list = null;
		System.gc();
		System.out.println(weakHashMap.get("1").get(2));
		System.out.println(weakHashMap1.get("x"));

		String a = "org/a/b/c/aspectj/UnlockSignatureImpl.class";
		String a1 = "org/a/aspectj/UnlockSignatureImpl.class";

		System.out.println(a.matches("^org/[^/]*/aspectj/*.*"));
		System.out.println(a.matches("^org/*.*/aspectj/*.*"));

		String b = "asdf/*/asdf/**";
		b = b.replace("/*/", "/[^/]*/");
		b = b.replace("**", ".*");

		System.out.println(b);
		System.out.println("com/ben/net/factory".matches("^com/*.*/net/*.*"));
		System.out.println("org/aspectj/runtime/reflect/UnlockSignatureImpl.class".matches("^org/*.*/reflect/*.*"));
		System.out.println("com/a/ben".replace("/", "B"));
		System.out.println("com/a/ben".matches("^[com]/[a]/*.*"));
		System.out.println("com/a/ben".matches("^+com/+a/*.*"));

		Pattern p = Pattern.compile("com/a/*.*");
		Matcher m = p.matcher("com/aadsf/ben");
		System.out.println(m.matches());

		String XM = "(^//.|^/)?:(/x)?$";
		String XM2 = "^/c/a/v/v?+.*/.*.class$";
		String XM3 = "^/c/a(/.*)?/x(/.*)?.class$";
		System.err.println("c/a/v/c/".matches(XM));
		System.err.println("XX:" + "/c/a/v/v/xxx/**.class".matches(XM2));
		System.err.println("XX3:" + "/c/a/asd/asdf/x1/v/x/asdfsa/asdfsadf/df/123.class".matches(XM3));

		String stact = "^asdf\\**\\**\\abc";

		stact = stact.replace(File.separator, "/");
		stact = stact.replace("/**", "(/.*)?");

		System.err.println("AX:" + "asdf/dddd/dd/dd/dd/dd/ddd/abc".matches(stact));
		System.out.println(stact);

		System.out.println("MBA:" + "E:/javaProject/Eclipse/BenDemo/javassistTest/target/classes/org/springframework/orm/hibernate4/testAA/xxxx.class".matches("^(/.*/|.*/)?org(/.*)?/orm/hibernate4(/.*)?.class$"));
	}
}
