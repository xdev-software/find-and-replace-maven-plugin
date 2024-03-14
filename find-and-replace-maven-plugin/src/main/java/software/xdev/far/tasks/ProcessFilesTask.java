/*
 * Copyright © 2024 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.far.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;


public final class ProcessFilesTask
{
	/**
	 * Stupid simple implementation of file walking, renaming, etc.
	 *
	 * @param log                   the maven-plugin log
	 * @param baseDir               the directory to start in
	 * @param isRecursive           whether to recurse further
	 * @param findRegex             the regex to find
	 * @param replaceValue          the value to replace the found regex
	 * @param fileMasks             the file masks to use
	 * @param exclusions            the filename regex to exclude
	 * @param processFileContents   whether to process file contents
	 * @param processFilenames      whether to process file names
	 * @param processDirectoryNames whether to process directory names
	 * @param charset               encoding to be used when reading files
	 */
	public static void process(
		final Log log,
		final Path baseDir,
		final boolean isRecursive,
		final Pattern findRegex,
		final String replaceValue,
		final List<String> fileMasks,
		final List<Pattern> exclusions,
		final boolean processFileContents,
		final boolean processFilenames,
		final boolean processDirectoryNames,
		final boolean replaceAll,
		final Charset charset) throws IOException
	{
		// Load in the files in the base dir
		final File[] baseDirFiles = new File(baseDir.toUri()).listFiles();
		if(baseDirFiles == null)
		{
			throw new IOException(String.format("Unable to list file(s) in baseDir='%s'", baseDir));
		}
		final List<File> filesToProcess = new ArrayList<>(Arrays.asList(baseDirFiles));
		
		ListIterator<File> iterator = filesToProcess.listIterator();
		
		while(iterator.hasNext())
		{
			final File file = iterator.next();
			
			// Remove the file from processing
			iterator.remove();
			
			// Perform dir checks
			if(file.isDirectory())
			{
				iterator = processDirectory(
					iterator,
					isRecursive,
					file,
					processDirectoryNames,
					exclusions,
					findRegex,
					replaceValue,
					replaceAll,
					log);
			}
			
			if(file.isFile())
			{
				processFile(
					log,
					exclusions,
					file,
					fileMasks,
					processFileContents,
					findRegex,
					replaceValue,
					processFilenames,
					replaceAll,
					charset);
			}
		}
	}
	
	private static ListIterator<File> processDirectory(
		final ListIterator<File> iterator,
		final boolean isRecursive,
		File file,
		final boolean processDirectoryNames,
		final List<Pattern> exclusions,
		final Pattern findRegex,
		final String replaceValue,
		final boolean replaceAll,
		final Log log) throws IOException
	{
		
		// Rename the directory
		if(processDirectoryNames && !shouldExcludeFile(exclusions, file))
		{
			file = renameFile(log, file, findRegex, replaceValue, replaceAll);
		}
		
		// If recursive, add child files to iterator
		if(isRecursive)
		{
			final File[] filesToAdd = file.listFiles();
			assert filesToAdd != null;
			for(final File f : filesToAdd)
			{
				iterator.add(f);
				iterator.previous();
			}
		}
		
		return iterator;
	}
	
	private static boolean shouldExcludeFile(final List<Pattern> exclusions, final File file)
	{
		
		for(final Pattern p : exclusions)
		{
			if(p.matcher(file.getName()).find())
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean shouldProcessFile(final List<String> fileMasks, final File file)
	{
		
		if(fileMasks.isEmpty())
		{
			return true;
		}
		
		for(final String fileMask : fileMasks)
		{
			if(file.getName().endsWith(fileMask))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static File renameFile(
		final Log log,
		final File file,
		final Pattern findRegex,
		final String replaceValue,
		final boolean replaceAll)
		throws IOException
	{
		
		final Path filePath = file.toPath();
		final Path parentDir = filePath.getParent();
		final String oldName = file.getName();
		final Matcher matcher = findRegex.matcher(oldName);
		final String newName = replaceAll ? matcher.replaceAll(replaceValue) : matcher.replaceFirst(replaceValue);
		
		if(!newName.equals(oldName))
		{
			final Path targetPath = Paths.get(parentDir.toString(), newName);
			
			log.info(String.format("Renaming %s to %s", oldName, newName));
			
			return new File(Files.move(filePath, targetPath).toUri());
		}
		
		return file;
	}
	
	private static void processFileContents(
		final File file,
		final Pattern findRegex,
		final String replaceValue,
		final boolean replaceAll,
		final Charset charset) throws IOException
	{
		final File tempFile = File.createTempFile("tmp", "tmp", file.getParentFile());
		
		try(final FileInputStream fis = new FileInputStream(file);
			final InputStreamReader isr = new InputStreamReader(fis, charset);
			final BufferedReader fileReader = new BufferedReader(isr))
		{
			try(final FileOutputStream fos = new FileOutputStream(tempFile);
				final OutputStreamWriter osr = new OutputStreamWriter(fos, charset);
				final BufferedWriter fileWriter = new BufferedWriter(osr))
			{
				
				boolean alreadyReplaced = false;
				
				for(String line = fileReader.readLine(); line != null; line = fileReader.readLine())
				{
					final Matcher matcher = findRegex.matcher(line);
					if(matcher.find())
					{
						if(replaceAll)
						{
							line = matcher.replaceAll(replaceValue);
						}
						else if(!alreadyReplaced)
						{
							line = matcher.replaceFirst(replaceValue);
							alreadyReplaced = true;
						}
					}
					try
					{
						fileWriter.write(line + "\n");
					}
					catch(final IOException e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		if(!file.delete())
		{
			throw new IOException("Failed to delete file at: " + file.getPath());
		}
		
		if(!tempFile.renameTo(file))
		{
			throw new IOException("Failed to rename temp file at: " + tempFile.getPath() + " to " + file.getPath());
		}
	}
	
	private static void processFile(
		final Log log,
		final List<Pattern> exclusions,
		final File file,
		final List<String> fileMasks,
		final boolean processFileContents,
		final Pattern findRegex,
		final String replaceValue,
		final boolean processFilenames,
		final boolean replaceAll,
		final Charset charset) throws IOException
	{
		if(shouldExcludeFile(exclusions, file) || !shouldProcessFile(fileMasks, file))
		{
			return;
		}
		
		if(processFileContents)
		{
			processFileContents(file, findRegex, replaceValue, replaceAll, charset);
		}
		
		if(processFilenames)
		{
			renameFile(log, file, findRegex, replaceValue, replaceAll);
		}
	}
	
	private ProcessFilesTask()
	{
	}
}
