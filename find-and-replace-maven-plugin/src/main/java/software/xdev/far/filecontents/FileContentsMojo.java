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
	
	/**
	 * Specify to match line based or otherwise match the whole document.
	 * <p>
	 * When this value is set to <code>false</code> more memory may be required as the whole file is processed at once.
	 * </p>
	 */
	@Parameter(property = "replaceLineBased", defaultValue = "true")
	boolean replaceLineBased = true;
	
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
				.orElseGet(Charset::defaultCharset),
			this.replaceLineBased
		);
	}
	
	public void setEncoding(final String encoding)
	{
		this.encoding = encoding;
	}
	
	public void setReplaceLineBased(final boolean replaceLineBased)
	{
		this.replaceLineBased = replaceLineBased;
	}
}
