package oop1.section12;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import java.io.File;
import java.util.List;
import java.util.stream.IntStream;

public record University(String name, List<Faculty> faculties) {

    public record Student(String id, String name, int grade) {
        public static Student parse(Element studentElement) {
            var studentId = studentElement.getAttribute("id");
            var studentName = studentElement.getElementsByTagName("name").item(0).getTextContent();
            var grade = Integer.parseInt(studentElement.getElementsByTagName("grade").item(0).getTextContent());
            return new Student(studentId, studentName, grade);
        }
    }

    public record Department(String name, List<Student> students) {
        public static Department parse(Element departmentElement) {
            var departmentName = departmentElement.getAttribute("name");
            var studentNodes = departmentElement.getElementsByTagName("student");

            var students = IntStream.range(0, studentNodes.getLength())
                    .mapToObj(k -> (Element) studentNodes.item(k))
                    .map(Student::parse)
                    .toList();

            return new Department(departmentName, students);
        }
    }

    public record Faculty(String name, List<Department> departments) {
        public static Faculty parse(Element facultyElement) {
            var facultyName = facultyElement.getAttribute("name");
            var departmentNodes = facultyElement.getElementsByTagName("department");

            var departments = IntStream.range(0, departmentNodes.getLength())
                    .mapToObj(i -> (Element) departmentNodes.item(i))
                    .map(Department::parse)
                    .toList();

            return new Faculty(facultyName, departments);
        }
    }

    public static University parse(String xmlFilePath) throws Exception {
        var xmlFile = new File(xmlFilePath);
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();

        var universityElement = document.getDocumentElement();
        var universityName = universityElement.getAttribute("name");

        var facultyNodes = universityElement.getElementsByTagName("faculty");
        var faculties = IntStream.range(0, facultyNodes.getLength())
                .mapToObj(i -> (Element) facultyNodes.item(i))
                .map(Faculty::parse)
                .toList();

        return new University(universityName, faculties);
    }
}
