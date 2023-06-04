package com.example.demo.student;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker; //! v dependency
/* 
  !<dependency>
	!		<groupId>com.github.javafaker</groupId>
	!		<artifactId>javafaker</artifactId>
	!		<version>1.0.2</version>
	!	</dependency>
 */

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class StudentIT {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;
        // private ObjectMapper objectMapper= new ObjectMapper();

        @Autowired
        private StudentRepository studentRepository;

        private final Faker faker = new Faker();

        @Test
        void canRegisterNewStudent() throws Exception {
                // ! given
                String name = String.format("%s %s", faker.name().firstName(), faker.name().lastName());

                Student student = new Student(
                                name,
                                String.format("%s@test.fr", StringUtils.trimAllWhitespace(name.trim().toLowerCase())),
                                Gender.FEMALE);

                // ! when               
                ResultActions resultActions = mockMvc
                                //!The line execute a POST request to the "/api/v1/students" endpoint.
                                .perform(post("/api/v1/students")
                                //!his line sets the content type of the request to "application/json".
                                                .contentType(MediaType.APPLICATION_JSON)
                                                //!This line sets the content of the request body. It converts the provided student object into its JSON representation using the objectMapper.
                                                .content(objectMapper.writeValueAsString(student)));
                // ! then //our expectation
                resultActions.andExpect(status().isOk());
                List<Student> studentsList = studentRepository.findAll();
                assertThat(studentsList)
                                //! It specifies that the comparison should ignore the id field when determining if the objects match. 
                                .usingElementComparatorIgnoringFields("id")
                                //!It checks if at least one element in the list(studentsList) matches the provided student object based on the configured element comparator (ignoring the id field).
                                .contains(student);
        }



        @Test
        void canDeleteStudent() throws Exception {
                // given
                String name = String.format(
                                "%s %s",
                                faker.name().firstName(),
                                faker.name().lastName());

                String email = String.format("%s@amigoscode.edu",
                                StringUtils.trimAllWhitespace(name.trim().toLowerCase()));

                Student student = new Student(
                                name,
                                email,
                                Gender.FEMALE);

                                //!The line execute a POST request to the "/api/v1/students" endpoint.
                mockMvc.perform(post("/api/v1/students")
                                //!his line sets the content type of the request to "application/json".
                                .contentType(MediaType.APPLICATION_JSON)
                                //!This line sets the content of the request body.
                                .content(objectMapper.writeValueAsString(student)))
                                //!thid line verifying that the response status of the performed request is "OK". This is a common assertion to ensure that the request was successful and returned the expected status code.
                                .andExpect(status().isOk());

                MvcResult getStudentsResult = mockMvc.perform(get("/api/v1/students")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())

                                //! this line allows you to obtain or get the result of the performed request and perform additional operations or assertions based on that result.
                                .andReturn();

                String contentAsString = getStudentsResult
                                //! It retrieves the MockHttpServletResponse object associated with the response of the request.
                                .getResponse()
                                //!returns the response body as a string. It allows you to retrieve the content of the response in string format.
                                .getContentAsString();

                List<Student> students = objectMapper
                //!This is a method provided by the ObjectMapper class. It is used to deserialize JSON data into an object.
                                .readValue(
                                        //!This is the JSON string that you want to deserialize into an object
                                        contentAsString,
                                        //!This construct is used to provide type information to the readValue method, as Java's type system erases generic type information at runtime. By using TypeReference, you can specify the desired type to which the JSON string should be deserialized.
                                        new TypeReference<>() {});

                long id = students.stream() //!It returns a Stream object, which represents a sequence of elements from the collection or array.
                                .filter(s -> s.getEmail().equals(student.getEmail()))
                                .map(Student::getId)
                                .findFirst()
                                //!his method is called on an Optional object. It takes a single argument, which is the exception to be thrown if the Optional is empty.
                                .orElseThrow(() -> new IllegalStateException(
                                                "student with email: " + email + " not found"));

                // when
                ResultActions resultActions = mockMvc
                                .perform(delete("/api/v1/students/" + id));

                // then
                resultActions.andExpect(status().isOk());
                boolean exists = studentRepository.existsById(id);
                assertThat(exists).isFalse();
        }
}
