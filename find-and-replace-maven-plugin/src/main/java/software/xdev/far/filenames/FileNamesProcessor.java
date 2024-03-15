package software.xdev.far.filenames;

import java.io.File;

import software.xdev.far.BaseProcessor;
import software.xdev.far.ExecData;


public class FileNamesProcessor extends BaseProcessor<ExecData>
{
	public FileNamesProcessor(final ExecData execData)
	{
		super(execData);
	}
	
	@Override
	protected File handleFile(final File file)
	{
		return this.renameFileDefault(file);
	}
}
