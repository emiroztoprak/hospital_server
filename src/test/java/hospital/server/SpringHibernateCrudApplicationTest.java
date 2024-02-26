package hospital.server;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringHibernateCrudApplication.class)
public class SpringHibernateCrudApplicationTest {
    /*
    @Autowired
    private PatientService patientService;

    @Test
    void testCreateAndFind(){

        patientService.getPatients();
        patientService.save(Patient.builder().
                name("emir").
                lastName("oztoprak").
                sex("male").
                birthDate(new Date()).
                build());
        assertTrue(patientService.findById(1L).isPresent());


     */

    }

