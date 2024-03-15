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
import java.util.regex.Matcher;

import software.xdev.far.BaseProcessor;


public class FileContentsProcessor extends BaseProcessor<FileContentsExecData>
{
	public FileContentsProcessor(final FileContentsExecData execData)
	{
		super(execData);
	}
	
	@Override
	protected void handleFileContents(final File file)
	{
		try
		{
			final File tempFile = File.createTempFile("tmp", "tmp", file.getParentFile());
			
			try(final FileInputStream fis = new FileInputStream(file);
				final InputStreamReader isr = new InputStreamReader(fis, this.execData.getCharset());
				final BufferedReader fileReader = new BufferedReader(isr))
			{
				try(final FileOutputStream fos = new FileOutputStream(tempFile);
					final OutputStreamWriter osr = new OutputStreamWriter(fos, this.execData.getCharset());
					final BufferedWriter fileWriter = new BufferedWriter(osr))
				{
					boolean alreadyReplaced = false;
					
					for(String line = fileReader.readLine(); line != null; line = fileReader.readLine())
					{
						final Matcher matcher = this.execData.getFindRegex().matcher(line);
						if(matcher.find())
						{
							if(this.execData.isReplaceAll())
							{
								line = matcher.replaceAll(this.execData.getReplaceValue());
							}
							else if(!alreadyReplaced)
							{
								line = matcher.replaceFirst(this.execData.getReplaceValue());
								alreadyReplaced = true;
							}
						}
						fileWriter.write(line + System.lineSeparator());
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
		catch(final IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}
}
