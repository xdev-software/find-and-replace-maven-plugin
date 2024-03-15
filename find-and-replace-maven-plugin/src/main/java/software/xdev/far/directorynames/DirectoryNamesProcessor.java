package software.xdev.far.directorynames;

import java.io.File;

import software.xdev.far.BaseProcessor;
import software.xdev.far.ExecData;


public class DirectoryNamesProcessor extends BaseProcessor<ExecData>
{
	public DirectoryNamesProcessor(final ExecData execData)
	{
		super(execData);
	}
	
	@Override
	protected File handleDirectory(final File file)
	{
		return this.renameFileDefault(file);
	}
}
