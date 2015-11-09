package com.ben.mc.AnthingTest;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class NioFileDemo {

	public static void main(String[] args) throws IOException {

		Path listDir = Paths.get("D:/Repository");
		ListTree listTree = new ListTree();
		Files.walkFileTree(listDir, listTree);
		//		Files.w

	}

	static class ListTree extends SimpleFileVisitor<Path> {

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			System.out.println(file.toString());
			return super.visitFile(file, attrs);
		}

	}

}
