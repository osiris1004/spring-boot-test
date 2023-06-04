package com.example.demo.student;
import com.example.demo.student.exception.StudentNotFoundException;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;


import com.example.demo.student.exception.BadRequestException;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;



// public class StudentServiceTest {
//     /*
//     !since  StudentRepository was already tested, we will then mock at instead */
//     @Mock
//     private StudentRepository studentRepository;
//     private AutoCloseable autoCloseable;
//     private StudentService underTest;

//     @BeforeEach //! mean before each test do some thing 
//     void setUp(){
//         //! this initialize all the mock in this test class
//         autoCloseable =  MockitoAnnotations.openMocks(this);
//         underTest = new StudentService(studentRepository);
//     }

//     @AfterEach //! mean after each test delete every one
//     void tearDown() throws Exception{
//         autoCloseable.close();
//     }


//     @Test
//     //*when getAllStudents() is invoke, the moke was invoke with find all */
//     void canGetAllStudents (){
//         // when 
//         underTest.getAllStudents();
//         //then
//         //! verify is use to verify that this repository(studentRepository) was invoke using the method .findAll()
//         verify(studentRepository).findAll();
//     }

//     @Test
//     @Disabled //! this will disable this test that is it will not run
//     void addStudent (){
     
//     }
//     @Test
//     @Disabled //! this will disable this test that is it will not run
//     void deleteStudent (){
     
//     }
    
// }


@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    /*
    !since  StudentRepository was already tested, we will then mock at it instead */
    @Mock
    private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach //! mean before each test do some thing 
    void setUp(){
        //! this initialize all the mock in this test class
        underTest = new StudentService(studentRepository);
    }

    @Test
    //*when getAllStudents() is invoke, i verify the moke was invoke with find all */
    void canGetAllStudents (){
        //! when 
        underTest.getAllStudents();
        //!then
        //! verify is use to verify that this repository(studentRepository) was invoke using the method .findAll()
        verify(studentRepository).findAll();
    }

    @Test
    void canAddStudent (){
        //! given
        String email = "jamila@gmail.fr";
        Student student = new Student("jamila", email, Gender.FEMALE);

        //!when
        underTest.addStudent(student);

        //!then
        ArgumentCaptor<Student> studentArgumentCaptor  = ArgumentCaptor.forClass(Student.class);

        verify(studentRepository).save(studentArgumentCaptor.capture());//!studentArgumentCaptor.capture() get the argument

        //!captureStudent  is the student ....Repository(student) receive or what the service receive
        Student captureStudent = studentArgumentCaptor.getValue();

                                        //!this v student is the want underTest.addStudent(student); receive
        assertThat(captureStudent).isEqualTo(student);
     
    }

    @Test
    void willThrowWhenEmailIsTaken (){
        //! given
        String email = "jamila@gmail.fr";
        Student student = new Student("jamila", email, Gender.FEMALE);

        /*
         * when(foo.doSomething()).thenReturn(somethingElse); === given(foo.doSomething()).willReturn(somethingElse);
         */
        //! when studentRepository.selectExistsEmail() is executed, i force the return to be true
        given(studentRepository.selectExistsEmail(student.getEmail()))
                                .willReturn(true);

        //!when
        //!then throw exception
       
        assertThatThrownBy( ()->underTest.addStudent(student))
                            .isInstanceOf(BadRequestException.class)
                            .hasMessageContaining( "Email " + student.getEmail() + " taken");

      //! we say that this repository(our mock), will never save any student
      verify(studentRepository, never()).save(any());
     
    }




    @Test
    void canDeleteStudent() {
        // !given
        Long studentId = 1L;

        //!when and then
        //!the method specifies that an exception is expected to be thrown when invoking the deleteStudent method of the underTest object with the given studentId.
        assertThatThrownBy(() -> underTest.deleteStudent(studentId))
                //!This assertion verifies that the exception thrown is an instance of the StudentNotFoundException
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessage("Student with id " + studentId + " does not exists");

        //! we say that this repository(our mock), will never delete a student with id anyLong()
        verify(studentRepository, never()).deleteById(anyLong());

    }


    
}