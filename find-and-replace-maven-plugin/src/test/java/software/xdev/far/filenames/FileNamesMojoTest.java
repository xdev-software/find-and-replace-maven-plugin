package software.xdev.far.filenames;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import software.xdev.far.BaseMojoTest;


class FileNamesMojoTest extends BaseMojoTest<FileNamesMojo>
{
	public FileNamesMojoTest()
	{
		super(FileNamesMojo::new);
	}
	
	@Test
	void testFilenames() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		final Path nonRecurseFile = Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		
		this.mojo.setFindRegex("_");
		this.mojo.setReplaceValue("-");
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-file-name")));
		assertTrue(Files.exists(nonRecurseFile));
	}
	
	@Test
	void testFilenamesReplaceFirst() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		final Path nonRecurseFile = Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		
		this.mojo.setFindRegex("_");
		this.mojo.setReplaceValue("-");
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-file_name")));
		assertTrue(Files.exists(nonRecurseFile));
	}
	
	@Test
	void testFilenamesRecursive() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		
		this.mojo.setFindRegex("_");
		this.mojo.setReplaceValue("-");
		this.mojo.setRecursive(true);
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-file-name")));
		assertTrue(Files.exists(Paths.get(firstDir.toString(), "some-file-name")));
	}
	
	@Test
	void testFilenamesRecursiveReplaceFirst() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		
		this.mojo.setFindRegex("_");
		this.mojo.setReplaceValue("-");
		this.mojo.setRecursive(true);
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-file_name")));
		assertTrue(Files.exists(Paths.get(firstDir.toString(), "some-file_name")));
	}
	
	@Test
	void testFilenamesRecursiveFileMasks() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path secondDir = Files.createDirectories(Paths.get(firstDir.toString(), "test-sub-dir"));
		final Path unchangedFile1 = Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		final Path unchangedFile2 = Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		final Path unchangedFile3 = Files.createFile(Paths.get(secondDir.toString(), "some_file_name"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name.xml"));
		Files.createFile(Paths.get(firstDir.toString(), "some_file_name.txt"));
		Files.createFile(Paths.get(secondDir.toString(), "some_file_name.yml"));
		
		this.mojo.setFindRegex("_");
		this.mojo.setReplaceValue("-");
		this.mojo.setFileMask(".xml,.txt,.yml");
		this.mojo.setRecursive(true);
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(Files.exists(unchangedFile1));
		assertTrue(Files.exists(unchangedFile2));
		assertTrue(Files.exists(unchangedFile3));
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-file-name.xml")));
		assertTrue(Files.exists(Paths.get(firstDir.toString(), "some-file-name.txt")));
		assertTrue(Files.exists(Paths.get(secondDir.toString(), "some-file-name.yml")));
	}
	
	@Test
	void testFilenamesRecursiveFileMasksReplaceFirst() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path secondDir = Files.createDirectories(Paths.get(firstDir.toString(), "test-sub-dir"));
		final Path unchangedFile1 = Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		final Path unchangedFile2 = Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		final Path unchangedFile3 = Files.createFile(Paths.get(secondDir.toString(), "some_file_name"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name.xml"));
		Files.createFile(Paths.get(firstDir.toString(), "some_file_name.txt"));
		Files.createFile(Paths.get(secondDir.toString(), "some_file_name.yml"));
		
		this.mojo.setFindRegex("_");
		this.mojo.setReplaceValue("-");
		this.mojo.setFileMask(".xml,.txt,.yml");
		this.mojo.setRecursive(true);
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(Files.exists(unchangedFile1));
		assertTrue(Files.exists(unchangedFile2));
		assertTrue(Files.exists(unchangedFile3));
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-file_name.xml")));
		assertTrue(Files.exists(Paths.get(firstDir.toString(), "some-file_name.txt")));
		assertTrue(Files.exists(Paths.get(secondDir.toString(), "some-file_name.yml")));
	}
	
	@Test
	void testFilenamesRecursiveFileMasksWithExclusions() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path secondDir = Files.createDirectories(Paths.get(firstDir.toString(), "test-sub-dir"));
		final Path unchangedFile1 = Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		final Path unchangedFile2 = Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		final Path unchangedFile3 = Files.createFile(Paths.get(secondDir.toString(), "some_file_name"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_xml_file_name.xml"));
		Files.createFile(Paths.get(firstDir.toString(), "some_txt_file_name.txt"));
		final Path unchangedExclusion = Files.createFile(Paths.get(secondDir.toString(), "some_yml_file_name.yml"));
		
		this.mojo.setFindRegex("_");
		this.mojo.setReplaceValue("-");
		this.mojo.setFileMask(".xml,.txt,.yml");
		this.mojo.setExclusions(".yml$");
		this.mojo.setRecursive(true);
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(Files.exists(unchangedFile1));
		assertTrue(Files.exists(unchangedFile2));
		assertTrue(Files.exists(unchangedFile3));
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-xml-file-name.xml")));
		assertTrue(Files.exists(Paths.get(firstDir.toString(), "some-txt-file-name.txt")));
		assertTrue(Files.exists(unchangedExclusion));
	}
	
	@Test
	void testFilenamesRecursiveFileMasksWithExclusionsReplaceFirst() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path secondDir = Files.createDirectories(Paths.get(firstDir.toString(), "test-sub-dir"));
		final Path unchangedFile1 = Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		final Path unchangedFile2 = Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		final Path unchangedFile3 = Files.createFile(Paths.get(secondDir.toString(), "some_file_name"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_xml_file_name.xml"));
		Files.createFile(Paths.get(firstDir.toString(), "some_txt_file_name.txt"));
		final Path unchangedExclusion = Files.createFile(Paths.get(secondDir.toString(), "some_yml_file_name.yml"));
		
		this.mojo.setFindRegex("_");
		this.mojo.setReplaceValue("-");
		this.mojo.setFileMask(".xml,.txt,.yml");
		this.mojo.setExclusions(".yml$");
		this.mojo.setRecursive(true);
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(Files.exists(unchangedFile1));
		assertTrue(Files.exists(unchangedFile2));
		assertTrue(Files.exists(unchangedFile3));
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-xml_file_name.xml")));
		assertTrue(Files.exists(Paths.get(firstDir.toString(), "some-txt_file_name.txt")));
		assertTrue(Files.exists(unchangedExclusion));
	}
}
