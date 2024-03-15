package software.xdev.far.directorynames;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import software.xdev.far.BaseMojo;
import software.xdev.far.ExecData;


@Mojo(name = "directory-names", defaultPhase = LifecyclePhase.NONE, threadSafe = true)
public class DirectoryNamesMojo extends BaseMojo<ExecData>
{
	public DirectoryNamesMojo()
	{
		super(DirectoryNamesProcessor::new);
	}
	
	@Override
	protected ExecData enrichData(final ExecData data)
	{
		return data;
	}
}
