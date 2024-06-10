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
package software.xdev.far.filecontents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;

import software.xdev.far.BaseProcessor;


public class FileContentsProcessor extends BaseProcessor<FileContentsExecData>
{
	public FileContentsProcessor(final FileContentsExecData execData)
	{
		super(execData);
	}
	
	@SuppressWarnings("java:S6300") // Writing to same directory and only temporary
	@Override
	protected void handleFileContents(final File file)
	{
		try
		{
			Path tempFile = null;
			
			if(this.execData.isReplaceLineBased())
			{
				tempFile = this.createTempFile(file);
				try(final FileInputStream fis = new FileInputStream(file);
					final InputStreamReader isr = new InputStreamReader(fis, this.execData.getCharset());
					final BufferedReader fileReader = new BufferedReader(isr))
				{
					try(final FileOutputStream fos = new FileOutputStream(tempFile.toFile());
						final OutputStreamWriter osr = new OutputStreamWriter(fos, this.execData.getCharset());
						final BufferedWriter fileWriter = new BufferedWriter(osr))
					{
						boolean alreadyReplaced = false;
						for(String line = fileReader.readLine(); line != null; line = fileReader.readLine())
						{
							String lineToWrite = line;
							final Matcher matcher = this.execData.getFindRegex().matcher(line);
							if(matcher.find())
							{
								if(this.execData.isReplaceAll())
								{
									lineToWrite = matcher.replaceAll(this.execData.getReplaceValue());
								}
								else if(!alreadyReplaced)
								{
									lineToWrite = matcher.replaceFirst(this.execData.getReplaceValue());
									alreadyReplaced = true;
								}
							}
							fileWriter.write(lineToWrite + System.lineSeparator());
						}
					}
				}
			}
			else
			{
				final String contents = Files.readString(file.toPath(), this.execData.getCharset());
				final Matcher matcher = this.execData.getFindRegex().matcher(contents);
				if(matcher.find())
				{
					tempFile = this.createTempFile(file);
					
					Files.writeString(tempFile, this.execData.isReplaceAll()
						? matcher.replaceAll(this.execData.getReplaceValue())
						: matcher.replaceFirst(this.execData.getReplaceValue()));
				}
			}
			
			if(tempFile != null)
			{
				Files.delete(file.toPath());
				
				if(!tempFile.toFile().renameTo(file))
				{
					throw new IOException(
						"Failed to rename temp file at: " + tempFile + " to " + file.getPath());
				}
			}
		}
		catch(final IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}
	
	protected Path createTempFile(final File original) throws IOException
	{
		return Files.createTempFile(original.getParentFile().toPath(), "tmp", "tmp");
	}
}
