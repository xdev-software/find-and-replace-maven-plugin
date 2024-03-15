package software.xdev.far.filecontents;

import java.nio.charset.Charset;

import software.xdev.far.ExecData;


public class FileContentsExecData extends ExecData
{
	private final Charset charset;
	
	private final boolean replaceLineBased;
	
	public FileContentsExecData(final ExecData other, final Charset charset, final boolean replaceLineBased)
	{
		super(other);
		this.charset = charset;
		this.replaceLineBased = replaceLineBased;
	}
	
	public Charset getCharset()
	{
		return this.charset;
	}
	
	public boolean isReplaceLineBased()
	{
		return this.replaceLineBased;
	}
}
