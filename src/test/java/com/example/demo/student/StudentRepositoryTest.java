package com.example.demo.student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.*;

/*
!we don't what that this test should run and take in consideration our local database database, so we will configure 
!our in memory database . the dependency we will use to perform our in memory is h2 :
!      <dependency>
!			<groupId>com.h2database</groupId>
!			<artifactId>h2</artifactId>
!			<scope>test</scope>
!		</dependency>

! after adding your decency add your resource file 
*/


@DataJpaTest //! every time you want to unit test your repository, you need to use @DataJpaTest annotation
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;

    @AfterEach//! mean after each test do something (delete every one)
    void tearDown(){
        underTest.deleteAll();
    }

    @Test
    void itShouldCheckIfStudentEmailExist (){
        //!given
            String email = "jamila@gmail.fr";
            Student student = new Student("jamila", email, Gender.FEMALE);
            underTest.save(student);
        //!when

        boolean expected = underTest.selectExistsEmail(email);

        //!then
        assertThat(expected).isTrue();

    }
    

    @Test
    void itShouldCheckIfStudentEmailDoesNotExist (){
        //!given
            String email = "jamila@gmail.fr";

        //!when

        boolean expected = underTest.selectExistsEmail(email);

        //!then
        assertThat(expected).isFalse();

    }
}
