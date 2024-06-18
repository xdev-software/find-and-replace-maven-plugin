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
package software.xdev.far;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.regex.Matcher;


public abstract class BaseProcessor<D extends ExecData>
{
	protected D execData;
	
	protected boolean processDirectoryNames;
	
	protected boolean processFileContents;
	
	protected boolean processFilenames;
	
	protected BaseProcessor(final D execData)
	{
		this(execData, true);
	}
	
	protected BaseProcessor(final D execData, final boolean autoDetectOverrides)
	{
		this.execData = execData;
		if(autoDetectOverrides)
		{
			this.autoDetectOverrides();
		}
		this.run();
	}
	
	protected Class<?> baseProcessorClassForAutoDetectOverrides()
	{
		return BaseProcessor.class;
	}
	
	protected void autoDetectOverrides()
	{
		final Class<?> base = this.baseProcessorClassForAutoDetectOverrides();
		final Class<?> currentClass = this.getClass();
		
		final Predicate<String> isMethodOverridden =
			methodName -> Arrays.stream(currentClass.getDeclaredMethods())
				.filter(m -> methodName.equals(m.getName()))
				.findFirst()
				.map(Method::getDeclaringClass)
				.map(d -> !base.equals(d))
				.orElse(false);
		
		this.processDirectoryNames = isMethodOverridden.test("handleDirectory");
		this.processFilenames = isMethodOverridden.test("handleFile");
		this.processFileContents = isMethodOverridden.test("handleFileContents");
	}
	
	protected void run()
	{
		// Load in the files in the base dir
		final File[] baseDirFiles = new File(this.execData.getBaseDirPath().toUri()).listFiles();
		if(baseDirFiles == null)
		{
			throw new UncheckedIOException(new IOException(String.format(
				"Unable to list file(s) in baseDir='%s'",
				this.execData.getBaseDirPath())));
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
				iterator = this.processDirectory(iterator, file);
			}
			
			if(file.isFile())
			{
				this.processFile(file);
			}
		}
	}
	
	protected ListIterator<File> processDirectory(final ListIterator<File> iterator, final File file)
	{
		File workFile = file;
		
		// Rename the directory
		if(this.processDirectoryNames && !this.shouldExcludeFile(workFile))
		{
			workFile = this.handleDirectory(workFile);
		}
		
		// If recursive, add child files to iterator
		if(this.execData.isRecursive())
		{
			final File[] filesToAdd = workFile.listFiles();
			assert filesToAdd != null;
			for(final File f : filesToAdd)
			{
				iterator.add(f);
				iterator.previous();
			}
		}
		
		return iterator;
	}
	
	protected boolean shouldExcludeFile(final File file)
	{
		return this.execData.getExclusions().stream().anyMatch(p -> p.matcher(file.getName()).find());
	}
	
	protected File handleDirectory(final File file)
	{
		return file;
	}
	
	protected void processFile(final File file)
	{
		if(this.shouldExcludeFile(file) || !this.shouldProcessFile(file))
		{
			return;
		}
		
		if(this.processFileContents)
		{
			this.handleFileContents(file);
		}
		
		if(this.processFilenames)
		{
			this.handleFile(file);
		}
	}
	
	protected boolean shouldProcessFile(final File file)
	{
		if(this.execData.getFileMasks().isEmpty())
		{
			return true;
		}
		
		return this.execData.getFileMasks().stream().anyMatch(fileMask -> file.getName().endsWith(fileMask));
	}
	
	protected File handleFile(final File file)
	{
		return file;
	}
	
	protected File renameFileDefault(final File file)
	{
		final String oldName = file.getName();
		final Matcher matcher = this.execData.getFindRegex().matcher(oldName);
		final String newName = this.execData.isReplaceAll()
			? matcher.replaceAll(this.execData.getReplaceValue())
			: matcher.replaceFirst(this.execData.getReplaceValue());
		
		if(!newName.equals(oldName))
		{
			final Path filePath = file.toPath();
			final Path parentDir = filePath.getParent();
			
			final Path targetPath = Paths.get(parentDir.toString(), newName);
			
			this.execData.getLogger().info(String.format("Renaming %s to %s", oldName, newName));
			
			try
			{
				return new File(Files.move(filePath, targetPath).toUri());
			}
			catch(final IOException e)
			{
				throw new UncheckedIOException(e);
			}
		}
		
		return file;
	}
	
	protected void handleFileContents(final File file)
	{
	}
}
