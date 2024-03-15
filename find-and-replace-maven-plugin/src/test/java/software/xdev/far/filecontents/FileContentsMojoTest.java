package software.xdev.far.filecontents;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import software.xdev.far.BaseMojoTest;


class FileContentsMojoTest extends BaseMojoTest<FileContentsMojo>
{
	public FileContentsMojoTest()
	{
		super(FileContentsMojo::new);
	}
	
	@Test
	void testFileContents()
	{
		this.mojo.setFindRegex("asdf");
		final String replaceValue = "value successfully replaced";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsEmptyReplacementValue()
	{
		this.mojo.setFindRegex("asdf");
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsReplaceFirst()
	{
		this.mojo.setFindRegex("asdf");
		final String replaceValue = "value successfully replaced";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsNonStandardEncodingFindRegex()
	{
		// Replace non-standard with standard
		this.mojo.setFindRegex("ìíîï");
		final String replaceValue = "value successfully replaced";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setEncoding("ISO-8859-1");
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), replaceValue, StandardCharsets.ISO_8859_1));
		assertFalse(this.fileContains(this.nonUtfTestFile.toFile(), "àáâãäåæçèéêëìíîï", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void testFileContentsNonStandardEncodingFindRegexReplaceFirst()
	{
		// Replace non-standard with standard
		this.mojo.setFindRegex("ìíîï");
		final String replaceValue = "value successfully replaced";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setEncoding("ISO-8859-1");
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), replaceValue, StandardCharsets.ISO_8859_1));
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), "àáâãäåæçèéêëìíîï", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void testFileContentsNonStandardReplaceValue()
	{
		// Replace standard with non-standard
		this.mojo.setFindRegex("test");
		final String replaceValue = "çåæ";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setEncoding("ISO-8859-1");
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), replaceValue, StandardCharsets.ISO_8859_1));
		assertFalse(this.fileContains(this.nonUtfTestFile.toFile(), "test", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void testFileContentsNonStandardReplaceValueReplaceFirst()
	{
		// Replace standard with non-standard
		this.mojo.setFindRegex("test");
		final String replaceValue = "çåæ";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setEncoding("ISO-8859-1");
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), replaceValue, StandardCharsets.ISO_8859_1));
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), "test", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void testFileContentsNonStandardFindAndReplaceValue()
	{
		// Replace non-standard with non-standard
		this.mojo.setFindRegex("àáâãäåæçèéêëìíîï");
		final String replaceValue = "çåæìíîïàáâãäå";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setEncoding("ISO-8859-1");
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), replaceValue, StandardCharsets.ISO_8859_1));
		assertFalse(this.fileContains(this.nonUtfTestFile.toFile(), "àáâãäåæçèéêëìíîï", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void testFileContentsNonStandardFindAndReplaceValueReplaceFirst()
	{
		// Replace non-standard with non-standard
		this.mojo.setFindRegex("àáâãäåæçèéêëìíîï");
		final String replaceValue = "çåæìíîïàáâãäå";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setEncoding("ISO-8859-1");
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), replaceValue, StandardCharsets.ISO_8859_1));
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), "àáâãäåæçèéêëìíîï", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void testFileContentsRecursive() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.mojo.setFindRegex("asdf");
		final String replaceValue = "value successfully replaced";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setRecursive(true);
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertFalse(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsRecursiveReplaceFirst() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.mojo.setFindRegex("asdf");
		final String replaceValue = "value successfully replaced";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setRecursive(true);
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsNotRecursive() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.mojo.setFindRegex("asdf");
		final String replaceValue = "value successfully replaced";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setRecursive(false);
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsNotRecursiveReplaceFirst() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.mojo.setFindRegex("asdf");
		final String replaceValue = "value successfully replaced";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setRecursive(false);
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsRecursiveFileMasks() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.mojo.setFindRegex("asdf");
		final String replaceValue = "value successfully replaced";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setRecursive(true);
		this.mojo.setFileMask(".xml,.yml");
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertFalse(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsRecursiveFileMasksReplaceFirst() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.mojo.setFindRegex("asdf");
		final String replaceValue = "value successfully replaced";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setRecursive(true);
		this.mojo.setFileMask(".xml,.yml");
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsRecursiveFileMasksExclusions() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.mojo.setFindRegex("asdf");
		final String replaceValue = "value successfully replaced";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setRecursive(true);
		this.mojo.setFileMask(".xml,.yml");
		this.mojo.setExclusions(".yml$");
		this.mojo.setReplaceAll(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsRecursiveFileMasksExclusionsReplaceFirst() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.mojo.setFindRegex("asdf");
		final String replaceValue = "value successfully replaced";
		this.mojo.setReplaceValue(replaceValue);
		this.mojo.setRecursive(true);
		this.mojo.setFileMask(".xml,.yml");
		this.mojo.setExclusions(".yml$");
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
}
