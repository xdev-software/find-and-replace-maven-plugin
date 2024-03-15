package software.xdev.far.filecontents;

import java.nio.charset.Charset;

import software.xdev.far.ExecData;


public class FileContentsExecData extends ExecData
{
	private final Charset charset;
	
	public FileContentsExecData(final ExecData other, final Charset charset)
	{
		super(other);
		this.charset = charset;
	}
	
	public Charset getCharset()
	{
		return this.charset;
	}
}
