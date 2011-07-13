import org.junit.Test;

import junit.framework.Assert;
import de.dini.oanetzwerk.admin.RepositoryValidator;



public class RepositoryValidatorTest {

	@Test public void validateServerTest() {
		RepositoryValidator validator = new RepositoryValidator();
		Assert.assertEquals((Boolean) true, validator.validateServer("HU Berlin Restschnittstelle"));
	}
	
	@Test public void validateLinkTest() {
		RepositoryValidator validator = new RepositoryValidator();
		Assert.assertEquals(
			(String) "passed",
			validator.validateLink("http://oanet.cms.hu-berlin.de/restserver/server/", "Harvester", "retsevrah", "CompleteMetadataEntry/18")
		);
	}
}
