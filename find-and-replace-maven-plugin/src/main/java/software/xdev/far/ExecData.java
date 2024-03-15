package software.xdev.far;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;


public class ExecData
{
	private final Log logger;
	private final Path baseDirPath;
	private final boolean recursive;
	private final Pattern findRegex;
	private final String replaceValue;
	private final List<String> fileMasks;
	private final List<Pattern> exclusions;
	private final boolean replaceAll;
	
	public ExecData(final ExecData other)
	{
		this(
			other.getLogger(),
			other.getBaseDirPath(),
			other.isRecursive(),
			other.getFindRegex(),
			other.getReplaceValue(),
			other.getFileMasks(),
			other.getExclusions(),
			other.isReplaceAll());
	}
	
	public ExecData(
		final Log logger,
		final Path baseDirPath,
		final boolean recursive,
		final Pattern findRegex,
		final String replaceValue,
		final List<String> fileMasks,
		final List<Pattern> exclusions,
		final boolean replaceAll)
	{
		this.logger = logger;
		this.baseDirPath = baseDirPath;
		this.recursive = recursive;
		this.findRegex = findRegex;
		this.replaceValue = replaceValue;
		this.fileMasks = fileMasks;
		this.exclusions = exclusions;
		this.replaceAll = replaceAll;
	}
	
	public Log getLogger()
	{
		return this.logger;
	}
	
	public Path getBaseDirPath()
	{
		return this.baseDirPath;
	}
	
	public boolean isRecursive()
	{
		return this.recursive;
	}
	
	public Pattern getFindRegex()
	{
		return this.findRegex;
	}
	
	public String getReplaceValue()
	{
		return this.replaceValue;
	}
	
	public List<String> getFileMasks()
	{
		return this.fileMasks;
	}
	
	public List<Pattern> getExclusions()
	{
		return this.exclusions;
	}
	
	public boolean isReplaceAll()
	{
		return this.replaceAll;
	}
}
