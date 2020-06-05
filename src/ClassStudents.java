
public class ClassStudents {
	private Student student;

	public ClassStudents(Student student) {
		this.student = student;
	}
	
	public String changeName(String name) {
		this.student.setName(name);
		
		return this.student.getName();
	}
	
	public int updateStudentInfo(String name) {
		if (student == null) {
			return 0;
		}
		
		student.setAge(22);
		student.setName("Test Name");
		
		return 1;
	}
}
