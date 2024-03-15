package software.xdev.far.filecontents;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import software.xdev.far.BaseMojo;
import software.xdev.far.ExecData;


@Mojo(name = "file-contents", defaultPhase = LifecyclePhase.NONE, threadSafe = true)
public class FileContentsMojo extends BaseMojo<FileContentsExecData>
{
	
	/**
	 * Specify file encoding during file-contents replacement
	 * <p>
	 * Default set to Charset.defaultCharset();
	 * </p>
	 */
	@Parameter(property = "encoding")
	String encoding;
	
	public FileContentsMojo()
	{
		super(FileContentsProcessor::new);
	}
	
	@Override
	protected FileContentsExecData enrichData(final ExecData data)
	{
		return new FileContentsExecData(
			data,
			Optional.ofNullable(this.encoding)
				.map(e -> {
					try
					{
						return Charset.forName(e);
					}
					catch(final Exception ex)
					{
						return null;
					}
				})
				.orElseGet(Charset::defaultCharset)
		);
	}
	
	public void setEncoding(final String encoding)
	{
		this.encoding = encoding;
	}
}
