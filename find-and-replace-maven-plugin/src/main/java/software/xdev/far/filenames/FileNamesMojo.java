package software.xdev.far.filenames;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import software.xdev.far.BaseMojo;
import software.xdev.far.ExecData;


@Mojo(name = "file-names", defaultPhase = LifecyclePhase.NONE, threadSafe = true)
public class FileNamesMojo extends BaseMojo<ExecData>
{
	public FileNamesMojo()
	{
		super(FileNamesProcessor::new);
	}
	
	@Override
	protected ExecData enrichData(final ExecData data)
	{
		return data;
	}
}
